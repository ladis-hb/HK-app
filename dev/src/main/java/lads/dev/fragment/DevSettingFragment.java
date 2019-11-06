package lads.dev.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.x6.serial.SerialPort;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import lads.dev.R;
import lads.dev.activity.SpConfigActivity;
import lads.dev.activity.TestActivity;
import lads.dev.biz.DbDataService;
import lads.dev.biz.DevBizHandler;
import lads.dev.biz.LocalData;
import lads.dev.entity.DataHisEntity;
import lads.dev.entity.DevEntity;
import lads.dev.entity.FieldDisplayEntity;
import lads.dev.entity.InstructionEntity;
import lads.dev.entity.ResultEntity;
import lads.dev.entity.SpEntity;
import lads.dev.entity.ViewEntity;
import lads.dev.utils.HttpUtil;
import lads.dev.utils.MyDatabaseHelper;
import lads.dev.utils.MyUtil;
import lads.dev.utils.SerialPortUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DevSettingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DevSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DevSettingFragment extends Fragment {
    String TAG="DevSettingFragment";
    MyDatabaseHelper dbHelper;
    //SQLiteDatabase db;

    DbDataService dbDataService;

//    private Handler handler;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DevSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DevSettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DevSettingFragment newInstance(String param1, String param2) {
        DevSettingFragment fragment = new DevSettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "DevSettingFragment.onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    EditText txtBaudrate1,txtBaudrate2,txtBaudrate3,txtBaudrate4,txtUrl,txtWebsocket,editText_wait_ups,editText_wait_query;
    Button btnSave1,btnSave2,btnSave3,btnSave4,btnOpenPort1,btnOpenPort2,btnOpenPort3,btnOpenPort4,btnUrl,btnConfig,btnTest ,btn_all_start,btn_wait_query;
    public static DevBizHandler devBizHandler1,devBizHandler2,devBizHandler3,devBizHandler4;
    SerialPort serialPort1,serialPort2,serialPort3,serialPort4 ;
    SerialPortUtils serialPortUtils1,serialPortUtils2,serialPortUtils3,serialPortUtils4;
    private int baudrate1,baudrate2,baudrate3,baudrate4;
    CheckBox cbx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "DevSettingFragment.onCreateView");
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_dev_setting, container, false);

        txtBaudrate1 = (EditText) view.findViewById(R.id.devSettingFragment_txt_baudrate1);
        txtBaudrate2 = (EditText) view.findViewById(R.id.devSettingFragment_txt_baudrate2);
        txtBaudrate3 = (EditText) view.findViewById(R.id.devSettingFragment_txt_baudrate3);
        txtBaudrate4 = (EditText) view.findViewById(R.id.devSettingFragment_txt_baudrate4);
        btnSave1 = (Button) view.findViewById(R.id.devSettingFragment_btn_save1);
        btnSave2 = (Button) view.findViewById(R.id.devSettingFragment_btn_save2);
        btnSave3 = (Button) view.findViewById(R.id.devSettingFragment_btn_save3);
        btnSave4 = (Button) view.findViewById(R.id.devSettingFragment_btn_save4);
        btnOpenPort1 = (Button) view.findViewById(R.id.devSettingFragment_btn_open1);
        btnOpenPort2 = (Button) view.findViewById(R.id.devSettingFragment_btn_open2);
        btnOpenPort3 = (Button) view.findViewById(R.id.devSettingFragment_btn_open3);
        btnOpenPort4 = (Button) view.findViewById(R.id.devSettingFragment_btn_open4);

        //http保存按钮
        btnUrl = (Button) view.findViewById(R.id.btnUrl);
        //串口配置按钮
        btnConfig = (Button) view.findViewById(R.id.frg_setting_btn_config);
        //测试页面
        btnTest = view.findViewById(R.id.test_page);
        //一键载入数据打开所有端口
        btn_all_start = view.findViewById(R.id.btn_all_start);

        txtUrl = (EditText) view.findViewById(R.id.txtUrl);
        txtWebsocket = (EditText) view.findViewById(R.id.txtWebsocket);
        cbx = view.findViewById(R.id.frag_setting_cbx_connect);

        //超时
        editText_wait_ups = view.findViewById(R.id.editText_wait_ups);
        editText_wait_query = view.findViewById(R.id.editText_wait_query);
        btn_wait_query = view.findViewById(R.id.btn_wait_query);

        dbHelper = new MyDatabaseHelper(getContext(), 2);
        dbDataService = new DbDataService(dbHelper.getDb());

        //初始化数据库
        Button btnInitData = (Button) view.findViewById(R.id.btn_initdata_fragment_dev_setting);
        btnInitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyDatabaseHelper(getContext(), 1); //first clear db
                dbHelper = new MyDatabaseHelper(getContext(), 2);
                dbDataService = new DbDataService(dbHelper.getDb());
                dbDataService.initData();
                Toast.makeText(getContext(), "Init data finish", Toast.LENGTH_SHORT).show();
            }
        });
        //载入数据库
        Button btnRefreshData = (Button) view.findViewById(R.id.btn_refreshdata_fragment_dev_setting);
        btnRefreshData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    dbDataService.initContextData();
                    loaddata();
                    Toast.makeText(getContext(), "Refresh data finish", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        //一键载入数据打开所有端口
        btn_all_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRefreshData.performClick();
                btnSave1.performClick();
                btnSave2.performClick();
                btnSave3.performClick();
                btnSave4.performClick();
                btnOpenPort1.performClick();
                btnOpenPort2.performClick();
                btnOpenPort3.performClick();
                btnOpenPort4.performClick();
            }
        });

        //保存网络配置
        btnUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strUrl = txtUrl.getText().toString().trim();
                String strWebsocket = txtWebsocket.getText().toString().trim();
                if(MyUtil.isStringEmpty(strUrl) || MyUtil.isStringEmpty(strWebsocket)) {
                    Toast.makeText(getContext(), "url and uri required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(btnUrl.getText().toString().toUpperCase().equals("SAVE")) {
                    btnUrl.setText("modify");
                    txtUrl.setEnabled(false);
                    txtWebsocket.setEnabled(false);
                    dbDataService.updateParamValue("http_uri", strUrl);
                    dbDataService.updateParamValue("websocket_uri", strWebsocket);
                } else {
                    btnUrl.setText("save");
                    txtUrl.setEnabled(true);
                    txtWebsocket.setEnabled(true);
                }
            }
        });
        //打开socket
        cbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    String strUrl = txtUrl.getText().toString();
                    if(!MyUtil.isStringEmpty(strUrl)) {
                        //device设备上传地址/api/dev
                        LocalData.url = strUrl+"/dev";
                    }
                    String s = txtWebsocket.getText().toString().trim();
                    if(!MyUtil.isStringEmpty(s)) {
                        URI uri = URI.create(s);
                        initMyWebsocket(uri);
                    }
                }
            }
        });
        //保存超时
        btn_wait_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int wait_ups = Integer.parseInt(editText_wait_ups.getText().toString());
                    int wait_query = Integer.parseInt(editText_wait_query.getText().toString());
                    if(wait_ups < 500){
                        Toast.makeText(getContext(),"ups查询最小设置400",Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(wait_query < 5000){
                        Toast.makeText(getContext(),"主查询最小设置值5000,建议值10000",Toast.LENGTH_LONG).show();
                        return;
                    }
                    dbDataService.updateParamValue("main_query", String.valueOf(wait_query));
                    dbDataService.updateParamValue("handle_wait_slim", String.valueOf(wait_ups));
                    Toast.makeText(getContext(),"Save Success",Toast.LENGTH_LONG).show();


                }catch (Exception e){
                    Log.e(TAG,e.getMessage());
                }

            }
        });
        //打开串口配置
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getApplicationContext(), SpConfigActivity.class));
            }
        });
        //打开测试页面
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext().getApplicationContext(), TestActivity.class));
            }
        });
        //保存波特率配置
        btnSave1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strBaudrate = txtBaudrate1.getText().toString();
                SaveBaudrate(strBaudrate,1);
            }
        });
        btnSave2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strBaudrate = txtBaudrate2.getText().toString();
                SaveBaudrate(strBaudrate,2);
            }
        });
        btnSave3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strBaudrate = txtBaudrate3.getText().toString();
                SaveBaudrate(strBaudrate,3);
            }
        });
        btnSave4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strBaudrate = txtBaudrate4.getText().toString();
                SaveBaudrate(strBaudrate,4);
            }
        });

        //打开端口
        btnOpenPort1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String strBaudrate =txtBaudrate1.getText().toString();
                    try {
                        baudrate1 = Integer.parseInt(strBaudrate);

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Input baudrate", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(serialPort1==null) {
                        serialPort1 = serialPortUtils1.openSerialPort(1, baudrate1);
                        if(serialPort1 == null) {
                            Toast.makeText(getContext(), "open serialport1 failed", Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            Toast.makeText(getContext(), "open serialport1 ttyS0 success", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(getContext(), "open serialport1 failed,"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(btnOpenPort1.getText().toString().equals("打开端口")) {
                    btnOpenPort1.setText(R.string.Close);
                    devBizHandler1.setFlagSpRun(true);
                } else {
                    btnOpenPort1.setText(R.string.OPEN);
                    devBizHandler1.setFlagSpRun(false);
                }
            }
        });

        btnOpenPort2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String strBaudrate = txtBaudrate2.getText().toString();
                    try {
                        baudrate2 = Integer.parseInt(strBaudrate);

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Input baudrate", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(serialPort2==null) {
                        serialPort2 = serialPortUtils2.openSerialPort(2, baudrate2);
                        if(serialPort2 == null) {
                            Toast.makeText(getContext(), "open serialport1 failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(getContext(), "open serialport2 failed,"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(btnOpenPort2.getText().toString().equals("打开端口")) {
                    btnOpenPort2.setText(R.string.Close);
                    devBizHandler2.setFlagSpRun(true);
                } else {
                    btnOpenPort2.setText(R.string.OPEN);
                    devBizHandler2.setFlagSpRun(false);
                }
            }
        });
        btnOpenPort3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String strBaudrate = txtBaudrate3.getText().toString();
                    try {
                        baudrate3 = Integer.parseInt(strBaudrate);

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Input baudrate", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(serialPort3==null) {
                        serialPort3 = serialPortUtils3.openSerialPort(3, baudrate3);
                        if(serialPort3 == null) {
                            Toast.makeText(getContext(), "open serialport3 failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(getContext(), "open serialport3 failed,"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(btnOpenPort3.getText().toString().equals("打开端口")) {
                    btnOpenPort3.setText(R.string.Close);
                    devBizHandler3.setFlagSpRun(true);
                } else {
                    btnOpenPort3.setText(R.string.OPEN);
                    devBizHandler3.setFlagSpRun(false);
                }
            }
        });
        btnOpenPort4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String strBaudrate = txtBaudrate4.getText().toString();
                    try {
                        baudrate4 = Integer.parseInt(strBaudrate);

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Input baudrate", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(serialPort4==null) {
                        serialPort4 = serialPortUtils4.openSerialPort(4, baudrate4);
                        if(serialPort4 == null) {
                            Toast.makeText(getContext(), "open serialport4 failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(getContext(), "open serialport4 failed,"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(btnOpenPort4.getText().toString().equals("打开端口")) {
                    btnOpenPort4.setText(R.string.Close);
                    devBizHandler4.setFlagSpRun(true);
                } else {
                    btnOpenPort4.setText(R.string.OPEN);
                    devBizHandler4.setFlagSpRun(false);
                }
            }
        });
        //定时发送http
        handlerData.postDelayed(runnableData, 1000*60);
        //初始化查询
        serialPortUtils1 = new SerialPortUtils();
        devBizHandler1 = new DevBizHandler(getContext(), serialPortUtils1,"1" );
        new Thread(devBizHandler1).start();
        serialPortUtils2 = new SerialPortUtils();
        devBizHandler2 = new DevBizHandler(getContext(), serialPortUtils2, "2" );
        new Thread(devBizHandler2).start();
        serialPortUtils3 = new SerialPortUtils();
        devBizHandler3 = new DevBizHandler(getContext(), serialPortUtils3, "3");
        new Thread(devBizHandler3).start();
        serialPortUtils4 = new SerialPortUtils();
        devBizHandler4 = new DevBizHandler(getContext(), serialPortUtils4, "4" );
        new Thread(devBizHandler4).start();

        return view;
    }
    //Socket解析器
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.d(TAG,data.toString());
                }
            });
        }
    };
    //初始化socket，注册app，监听服务器操作
    private void initMyWebsocket(URI uri) {
        Log.d(TAG,"初始化socket连接");
        Socket mSocket;
        try {
            //获取socket实例
            mSocket = IO.socket(uri.toString());
            //连接socket服务器
            mSocket.connect();
            //构造注册信息
            JSONObject register = new JSONObject();
            register.put("user","mac001");
            //触发注册
            mSocket.emit("AppRegister",register);
            //注册监听
            mSocket.on("operate",onNewMessage);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*MyWebSocketClient client = new MyWebSocketClient(uri) {
                @Override
                public void onMessage(String message) {
                    Log.d(TAG,"初始化socket onMessage 监听");
                    *//**
                     * 命令格式：{DeviceCode:"xxxx",OptCode:"StartUps",OptType:"operate/query"}
                     *//*
                    try {
                        JSONObject json = new JSONObject(message);
                        String devCode = json.getString("DeviceCode");
                        String optType = json.getString("OptType");
                        String optCode = json.getString("OptCode");
                        if(optType.equals(RuleEnum.OptType.QUERY.toString())) {
                            queryData(devCode);
                        } else if(optType.equals(RuleEnum.OptType.OPERATE.toString())) {
                            Map<String, ViewEntity> map = LocalData.devDataMap.get(devCode);
                            if(map == null) {
                                Log.d(TAG, devCode+" offline");
                                Toast.makeText(getContext(), devCode+" offline", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            DevEntity devEntity = null;
                            String spNo = "";
                            String devName="";
                            for(DevEntity entity : LocalData.devlist) {
                                if(entity.getCode().equals(devCode)) {
                                    devEntity = entity;
                                    spNo = entity.getSpNo();
                                    devName = entity.getName();
                                    break;
                                }
                            }

                            String optValue = "";
                            String optName="";
                            for(DevOptEntity entity : LocalData.devoptlist) {
                                if(entity.getOptCode().equals(optCode)) {
                                    optValue = entity.getOptValue();
                                    optName = entity.getOptName();
                                    break;
                                }
                            }
                            DevOptHisEntity devOptHisEntity = new DevOptHisEntity();
                            devOptHisEntity.setDevCode(devCode);
                            devOptHisEntity.setDevName(devName);
                            devOptHisEntity.setOptName(optName);
                            devOptHisEntity.setOptValue(optValue);
                            switch (spNo){
                                case "1":
                                    devBizHandler1.addDevOpt(devOptHisEntity);
                                    break;
                                case "2":
                                    devBizHandler2.addDevOpt(devOptHisEntity);
                                    break;
                                case "3":
                                    devBizHandler3.addDevOpt(devOptHisEntity);
                                    break;
                                case "4":
                                    devBizHandler4.addDevOpt(devOptHisEntity);
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };*/
    }


    //send http data and save device data
    Handler handlerData = new Handler();
    Runnable runnableData = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG,"~~~~~~~~~~~~~~~");
            for(String deviceCode : LocalData.devDataMap.keySet()) {
                //get 设备信息
                DevEntity devEntity = LocalData.Cache_all_devlist.get(deviceCode);
                //查看是否是注册设备
                if(devEntity == null) return;
                //获取设备result协议列表
                String protocolCode = devEntity.getProtocolCode();
                //获取inst列表，协议-协议子集
                List<String> instructionList = new ArrayList<>();
                for(InstructionEntity instructionEntity:LocalData.Cache_instructionlist.get(protocolCode)){
                    instructionList.add(instructionEntity.getCode());
                }
                Map<String,String> resultList = new HashMap<>();
                for(String string:instructionList){
                    for( ResultEntity resultEntity :LocalData.Cache_resultlist.get(string)){
                        resultList.put(resultEntity.getFieldName(),resultEntity.getDisplayName());
                    }
                }
                //获取fielddisplay
                Map<String,String> fieldDisplayList = new HashMap<>();
                for (FieldDisplayEntity fieldDisplayEntity:LocalData.Cache_fieldDisplaylist.get(protocolCode)){
                    fieldDisplayList.put(fieldDisplayEntity.getDisplayName(),fieldDisplayEntity.getFieldName());
                }
                //读取设备数据
                Map<String,ViewEntity> map = LocalData.devDataMap.get(deviceCode);


                try {

                    JSONObject data = new JSONObject();
                    for(String key : map.keySet()) {
                        data.put(resultList.get(fieldDisplayList.get(key)), map.get(key).getValue());
                    }
                    //组装body
                    JSONObject Body = new JSONObject();
                    Body.put("deviceCode",deviceCode);
                    Body.put("date",new Date().toString());
                    Body.put("data",data);
                    Body.put("devType",LocalData.Cache_all_devlist.get(deviceCode).getTypeCode());
                    Body.put("name",LocalData.Cache_all_devlist.get(deviceCode).getName());
                    //获取Url
                    String uri = LocalData.Cache_sysparamlist.get("http_uri").getParamValue();
                    //send http
                    if(!MyUtil.isStringEmpty(uri)) {
                        HttpUtil.httpPost(uri+"/dev", Body.toString());
                    }

                    //save data
                    DataHisEntity dataHisEntity = new DataHisEntity();
                    dataHisEntity.setDevCode(deviceCode);
                    dataHisEntity.setDevName(devEntity.getName());
                    dataHisEntity.setSpCode(devEntity.getSpCode());
                    dataHisEntity.setMsg(data.toString());
                    dataHisEntity.setCreateTime(new Date());
                    dbDataService.addDataHis(dataHisEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
           // handlerData.postDelayed(this, 20000);
        }
    };

    private void queryData(String deviceCode) {
        try {
            Map<String,ViewEntity> map1 = LocalData.devDataMap.get(deviceCode);
            JSONObject json = new JSONObject();
            for(String key2 : map1.keySet()) {
                ViewEntity entity = map1.get(key2);
                json.put(key2, entity.getValue());
            }
            if(!MyUtil.isStringEmpty(LocalData.url)) {
                //HttpUtil.httpPost(LocalData.url, json.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Query error", Toast.LENGTH_SHORT).show();
        }

    }


    private void loaddata() {
        dbDataService.getSp();
        dbDataService.getSysParam();
        for(SpEntity entity : LocalData.splist) {
            switch (entity.getSeq()){
                case 1:
                    txtBaudrate1.setText(String.valueOf(entity.getBaudrate()));
                    break;
                case 2:
                    txtBaudrate2.setText(String.valueOf(entity.getBaudrate()));
                    break;
                case 3:
                    txtBaudrate3.setText(String.valueOf(entity.getBaudrate()));
                    break;
                case 4:
                    txtBaudrate4.setText(String.valueOf(entity.getBaudrate()));
                    break;
            }
        }
        txtUrl.setText(LocalData.Cache_sysparamlist.get("http_uri").getParamValue());
        txtWebsocket.setText(LocalData.Cache_sysparamlist.get("websocket_uri").getParamValue());
        editText_wait_ups.setText(LocalData.Cache_sysparamlist.get("handle_wait_slim").getParamValue());
        editText_wait_query.setText(LocalData.Cache_sysparamlist.get("main_query").getParamValue());

    }
    //保存Baudrate
   public void SaveBaudrate(String strBaudrate,int seq){
        try {
            baudrate1 = Integer.parseInt(strBaudrate);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Input integer", Toast.LENGTH_SHORT).show();
            return;
        }
        dbDataService.updateBaudrate(baudrate1, seq);
        Toast.makeText(getContext(), "Succeed", Toast.LENGTH_SHORT).show();
    }
/*


    boolean runflag1 = false;
    boolean runflag2 = false;
    boolean runflag3 = false;
    boolean runflag4 = false;


    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    update();
                    break;
            }
            super.handleMessage(msg);
        }
        void update() {
            if(runflag1) {
                try {
                    //devBizHandler1 = new DevBizHandler(strProtocol1, getContext(), serialPortUtils1,txtDevname1.getText().toString().trim(),strType1 );
                    devBizHandler1 = new DevBizHandler(getContext(), serialPortUtils1,"1" );
                    Thread thread = new Thread(devBizHandler1);
                    thread.start();

                } catch (Exception e) {
                    runflag1 = false;
                    btnOpenPort1.setText("open");
                    Toast.makeText(getContext(), "error, "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            if(runflag2) {
                try {
                    devBizHandler2 = new DevBizHandler(getContext(), serialPortUtils2, "2" );
                    Thread thread = new Thread(devBizHandler2);
                    thread.start();
                } catch (Exception e) {
                    runflag2 = false;
                    btnOpenPort2.setText("open");
                    Toast.makeText(getContext(), "error, "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            if(runflag3) {
                try {
                    devBizHandler3 = new DevBizHandler(getContext(), serialPortUtils3, "3");
                    Thread thread = new Thread(devBizHandler3);
                    thread.start();
                } catch (Exception e) {
                    runflag3 = false;
                    btnOpenPort3.setText("open");
                    Toast.makeText(getContext(), "error, "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            if(runflag4) {
                try {
                    devBizHandler4 = new DevBizHandler(getContext(), serialPortUtils4, "4" );
                    Thread thread = new Thread(devBizHandler4);
                    thread.start();
                } catch (Exception e) {
                    runflag4 = false;
                    btnOpenPort4.setText("open");
                    Toast.makeText(getContext(), "error, "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

*/



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
