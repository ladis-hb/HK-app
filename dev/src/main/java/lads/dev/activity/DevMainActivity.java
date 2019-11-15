package lads.dev.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;

import lads.dev.R;
import lads.dev.biz.DbDataService;
import lads.dev.biz.DevQueryOprate;
import lads.dev.biz.LocalData;
import lads.dev.entity.DevEntity;
import lads.dev.entity.DevOptEntity;
import lads.dev.entity.DevOptHisEntity;
import lads.dev.fragment.DevAcFragment;
import lads.dev.fragment.DevEmFragment;
import lads.dev.fragment.DevHomeFragment;
import lads.dev.fragment.DevIoFragment;
import lads.dev.fragment.DevSettingFragment;
import lads.dev.fragment.DevTHFragment;
import lads.dev.fragment.DevUpsFragment;
import lads.dev.utils.DevOperate;
import lads.dev.utils.MyApplication;
import lads.dev.utils.MyDatabaseHelper;
import lads.dev.utils.MyTimeTask;
import lads.dev.utils.QueryDevData_ToWeb_Save;
import lads.dev.utils.SocketIO;


public class DevMainActivity extends AppCompatActivity implements DevHomeFragment.OnFragmentInteractionListener,
        DevUpsFragment.OnFragmentInteractionListener,DevAcFragment.OnFragmentInteractionListener,DevEmFragment.OnFragmentInteractionListener,
        DevTHFragment.OnFragmentInteractionListener,DevSettingFragment.OnFragmentInteractionListener, DevIoFragment.OnFragmentInteractionListener {

    String TAG="DevMainActivity";

    MyDatabaseHelper dbHelper;
    DbDataService dbDataService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_main);

        try {

            dbHelper = new MyDatabaseHelper(DevMainActivity.this, 2);
            dbDataService = new DbDataService(dbHelper.getDb());
            dbDataService.initContextData();
        } catch (Exception e) {
            Toast.makeText(this, "数据异常，刷新数据", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            new MyDatabaseHelper(DevMainActivity.this, 1);
            dbHelper = new MyDatabaseHelper(DevMainActivity.this, 2);
            dbDataService = new DbDataService(dbHelper.getDb());
            dbDataService.initContextData();
        }

        //开启定时任务
        setInit();
    }

    private void setInit(){

        DevQueryOprate devQueryOprate = new DevQueryOprate();
        //初始化dev查询
        new MyTimeTask(10 * 1000, 10 * 1000, new TimerTask() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //检查操作缓存里是否有待处理操作，有的话取出操作，遍历执行，
                        Set<DevOptHisEntity> optrate = new HashSet<>(LocalData.Cache_OptOprate);
                        if(optrate.size()>0){

                            //清空操作缓存
                            LocalData.Cache_OptOprate.clear();
                            //遍历缓存
                            for(DevOptHisEntity devOptEntity:optrate){
                                Boolean optOk = DevOperate.addDevOpt(devOptEntity);
                                if(optOk){
                                    //Toast.makeText(MyApplication.getContext(), devOptEntity.getDevName()+devOptEntity.getOptName()+"操作执行成功",Toast.LENGTH_LONG).show();
                                }else {
                                    //执行失败
                                    //重新把任务加入执行队列
                                    LocalData.Cache_OptOprate.add(devOptEntity);
                                    String info = devOptEntity.getDevName()+devOptEntity.getOptName()+"操作执行失败，已存入待执行队列，下一循环重试";
                                    Log.e(TAG,info);
                                    //Toast.makeText(MyApplication.getContext(),info,Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        //如果串口被占用则跳出本次循环，避免污染全局变量，导致程序崩溃
                        if(LocalData.Cache_Open_SpNo.size()>0){
                            Log.e(TAG,"有串口被占用则跳出本次循环，避免污染全局变量，导致程序崩溃");
                            return;
                        }
                        devQueryOprate.StartQuery();
                    }
                }).start();
            }
        }).start();
        //两个小时刷新一次在线设备
        new MyTimeTask(2 * 60 * 60 * 1000,20000, new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG,"两个小时刷新一次在线设备");
                dbDataService.getDev();
            }
        }).start();
        //定时发送http
        new MyTimeTask(1000 * 10*60*10,20000, new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG,"定时发送http");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(String deviceCode : LocalData.devDataMap.keySet()) {
                            QueryDevData_ToWeb_Save.QueryDevData_ToWeb_SaveTo(deviceCode);
                        }
                    }
                }).start();
            }
        }).start();
        //定时检查socket连接
        new MyTimeTask(60 * 1000,20000, new TimerTask() {
            @Override
            public void run() {
                if(SocketIO.mSocket == null) initMyWebsocket();
            }
        }).start();
    }

    //----------------------------------------
    public void initMyWebsocket() {
        try {
            //监听操作指令
            SocketIO.listen("operate",onNewMessage);
            //监听掉线事件
            SocketIO.listen("disconnect",onNewMessage);
            //发送注册事件
            SocketIO.register();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Socket解析器
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            DevMainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //回传参数{Type：xx, ...arg:xxx},
                    Log.e(TAG,args[0].toString());
                    if(args[0].equals("")) return;
                    JSONObject data = (JSONObject) args[0];
                    //Log.e(TAG,data.toString());
                    if(!data.has("Type")) return;
                    String Type = null;
                    try {
                        Type = data.getString("Type");
                        switch (Type){
                            //响应掉线重连
                            case "disconnect":
                                SocketIO.register();
                                break;
                            //响应注册成功事件
                            case "Register":
                                String SocketId = data.getString("socketID");
                                Log.e(TAG,"socket服务器连接成功,唯一ID:"+SocketId);
                                Toast.makeText(MyApplication.getContext(),"socket服务器连接成功,唯一ID:"+SocketId,Toast.LENGTH_LONG).show();
                                dbDataService.updateParamValue("SocketID",SocketId);
                                break;
                            //响应操作
                            case "Operate":
                                Log.i(TAG,"收到服务器操作指令"+data.toString());
                                String OptType = data.getString("OptType");
                                String deviceid = data.getString("DeviceCode");
                                String OptCode = data.getString("OptCode");
                                switch (OptType){
                                    //下发指令操作
                                    case "operate":
                                        Log.i(TAG,"收到"+OptType+"指令");
                                        //如果设备数据缓存里面没有这个设备，退出操作
                                        if(!LocalData.Cache_all_devlist.containsKey(deviceid) || LocalData.Cache_all_devlist.get(deviceid) == null) return;
                                        //获取设备数据
                                        DevEntity devEntity = LocalData.Cache_all_devlist.get(deviceid);
                                        //获取配置操作指令
                                        DevOptEntity devOptEntity = LocalData.Cache_devoptlist.get(devEntity.getProtocolCode()+OptCode);
                                        if(devOptEntity == null) return;
                                        //组装DevOptHisEntity
                                        DevOptHisEntity devOptHisEntity = new DevOptHisEntity();
                                        devOptHisEntity.setDevCode(deviceid);
                                        devOptHisEntity.setDevName(devEntity.getName());
                                        devOptHisEntity.setOptName(devOptEntity.getOptName());
                                        devOptHisEntity.setOptValue(devOptEntity.getOptValue());
                                        LocalData.Cache_OptOprate.add(devOptHisEntity);

                                        break;
                                    //下发查询操作
                                    case "query":
                                        QueryDevData_ToWeb_Save.QueryDevData_ToWeb_SaveTo(deviceid);
                                        break;
                                }
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d(TAG, "onFragmentInteraction");
    }

}