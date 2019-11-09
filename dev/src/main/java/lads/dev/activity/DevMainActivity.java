package lads.dev.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lads.dev.R;
import lads.dev.biz.DbDataService;
import lads.dev.biz.DevBizHandler;
import lads.dev.biz.LocalData;
import lads.dev.entity.DataHisEntity;
import lads.dev.entity.DevEntity;
import lads.dev.entity.DevOptEntity;
import lads.dev.entity.DevOptHisEntity;
import lads.dev.entity.FieldDisplayEntity;
import lads.dev.entity.InstructionEntity;
import lads.dev.entity.ResultEntity;
import lads.dev.entity.ViewEntity;
import lads.dev.fragment.DevAcFragment;
import lads.dev.fragment.DevEmFragment;
import lads.dev.fragment.DevHomeFragment;
import lads.dev.fragment.DevIoFragment;
import lads.dev.fragment.DevSettingFragment;
import lads.dev.fragment.DevTHFragment;
import lads.dev.fragment.DevUpsFragment;
import lads.dev.utils.DevOperate;
import lads.dev.utils.HttpUtil;
import lads.dev.utils.MyDatabaseHelper;
import lads.dev.utils.MyUtil;
import lads.dev.utils.QueryDevData_ToWeb_Save;
import lads.dev.utils.SocketUtil;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;


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
        //两个小时刷新一次在线设备
        handlerData.postDelayed(runnableDataUpdateDevList, 2*60*60*1000);
        //定时发送http
        handlerData.postDelayed(runnableDataSendDataToWenAndSave, 1000*10);
        initMyWebsocket();
        //初始化查询
        new Thread(new DevBizHandler()).start();
    }


    //----------------------------------------
    public void initMyWebsocket() {
        Log.d(TAG,"初始化socket连接");
        //if(!LocalData.Cache_sysparamlist.containsKey("webConnect") || LocalData.Cache_sysparamlist.get("webConnect").getParamValue().equals("false")) return;
        if(!LocalData.Cache_sysparamlist.containsKey("webConnect") ){
            Log.e(TAG,"连接参数未定义");
            return;
        }
        if( LocalData.Cache_sysparamlist.get("webConnect").getParamValue().equals("false")){
            Log.e(TAG,"连接参数false");
            return;
        }
        Socket mSocket;
        try {
            //获取socket实例

            mSocket = IO.socket(LocalData.Cache_sysparamlist.get("websocket_uri").getParamValue());
            //连接socket服务器
            mSocket.connect();
            //构造注册信息
            JSONObject register = new JSONObject();
            register.put("user","mac001");
            //触发注册
            Log.e(TAG,"注册socket");
            mSocket.emit("AppRegister",register);
            //注册监听
            mSocket.once("operate",onNewMessage);


        } catch (URISyntaxException e) {
            e.printStackTrace();
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
                    JSONObject data = (JSONObject) args[0];
                    if(!data.has("Type")) return;
                    String Type = null;
                    try {
                        Type = data.getString("Type");
                        switch (Type){
                            //响应注册成功事件
                            case "Register":
                                Log.e(TAG,"socket服务器连接成功,唯一ID:"+data.getString("socketID"));
                                //Toast.makeText(MyApplication.getContext(),"socket服务器连接成功,唯一ID:"+data.getString("socketID"),Toast.LENGTH_LONG).show();
                                //txt_Socket_stat_info.setText("socket服务器连接成功,唯一ID:"+data.getString("socketID"));
                                //txt_Socket_stat_info.setTextColor(0xFF006400);
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
                                        DevOperate.addDevOpt(devOptHisEntity);

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

    //send http data and save device data
    Handler handlerData = new Handler();
    Runnable runnableDataSendDataToWenAndSave = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG,"~~~~~~~~~~~~~~~");
            for(String deviceCode : LocalData.devDataMap.keySet()) {
                QueryDevData_ToWeb_Save.QueryDevData_ToWeb_SaveTo(deviceCode);
            }
            handlerData.postDelayed(this, 1000*60*10);
        }
    };
    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d(TAG, "onFragmentInteraction");
    }

    //2小时获取更新一次设备
    Runnable runnableDataUpdateDevList = new Runnable() {
        @Override
        public void run() {
            dbDataService.getDev();
            handlerData.postDelayed(this, 2*60*60*1000);
        }
    };
}