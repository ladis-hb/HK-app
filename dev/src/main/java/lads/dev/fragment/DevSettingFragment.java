package lads.dev.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.AndroidException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import org.json.JSONObject;

import java.net.URI;
import java.security.cert.TrustAnchor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import com.example.x6.serial.SerialPort;
import lads.dev.R;
import lads.dev.activity.SpConfigActivity;
import lads.dev.activity.TestActivity;
import lads.dev.biz.DbDataService;
import lads.dev.biz.DevBizHandler;
import lads.dev.biz.LocalData;
import lads.dev.biz.RuleEnum;
import lads.dev.biz.TestDevBizHandler;
import lads.dev.biz.UpsBizHandler;
import lads.dev.entity.DataHisEntity;
import lads.dev.entity.DevEntity;
import lads.dev.entity.DevOptEntity;
import lads.dev.entity.DevOptHisEntity;
import lads.dev.entity.ProtocolEntity;
import lads.dev.entity.SpEntity;
import lads.dev.entity.SysParamEntity;
import lads.dev.entity.TypeEntity;
import lads.dev.entity.ViewEntity;
import lads.dev.utils.ChangeTool;
import lads.dev.utils.HttpUtil;
import lads.dev.utils.MyDatabaseHelper;
import lads.dev.utils.MyUtil;
import lads.dev.utils.MyWebSocketClient;
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
    EditText txtBaudrate1,txtBaudrate2,txtBaudrate3,txtBaudrate4,txtSql,txtUrl,txtWebsocket;
    Button btnSave1,btnSave2,btnSave3,btnSave4,btnOpenPort1,btnOpenPort2,btnOpenPort3,btnOpenPort4,btnSql,btnUrl,btnConfig,btnTest ;
    public static DevBizHandler devBizHandler1,devBizHandler2,devBizHandler3,devBizHandler4;
    String strProtocol1,strProtocol2,strProtocol3,strProtocol4,strType1,strType2,strType3,strType4;
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
        btnSql = (Button) view.findViewById(R.id.btn111);
        btnUrl = (Button) view.findViewById(R.id.btnUrl);
        btnConfig = (Button) view.findViewById(R.id.frg_setting_btn_config);
        btnTest = view.findViewById(R.id.test_page);

        txtSql = (EditText) view.findViewById(R.id.txt_sql_fragment_dev_setting);
        txtUrl = (EditText) view.findViewById(R.id.txtUrl);
        txtWebsocket = (EditText) view.findViewById(R.id.txtWebsocket);
        cbx = view.findViewById(R.id.frag_setting_cbx_connect);



        dbHelper = new MyDatabaseHelper(getContext(), 2);
        dbDataService = new DbDataService(dbHelper.getDb());



        Button btnInitData = (Button) view.findViewById(R.id.btn_initdata_fragment_dev_setting);
        btnInitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyDatabaseHelper(getContext(), 1); //first clear db
                dbHelper = new MyDatabaseHelper(getContext(), 2);
                dbDataService = new DbDataService(dbHelper.getDb());
                dbDataService.initData();
                Toast.makeText(getContext(), "Init data finish", Toast.LENGTH_SHORT).show();
/*
                try {
                    dbDataService.initContextData();
                    loaddata();
                    Toast.makeText(getContext(), "Refresh data finish", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }*/
            }
        });
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


        btnSave1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtBaudrate1.setText("2400");
                String strBaudrate = txtBaudrate1.getText().toString();
                try {
                    baudrate1 = Integer.parseInt(strBaudrate);

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Input integer", Toast.LENGTH_SHORT).show();
                    return;
                }
                dbDataService.updateBaudrate(baudrate1, 1);
                Toast.makeText(getContext(), "Succeed", Toast.LENGTH_SHORT).show();
            }
        });
        btnSave2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strBaudrate = txtBaudrate2.getText().toString();
                try {
                    baudrate2 = Integer.parseInt(strBaudrate);

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Input integer", Toast.LENGTH_SHORT).show();
                    return;
                }
                dbDataService.updateBaudrate(baudrate2, 2);
                Toast.makeText(getContext(), "Succeed", Toast.LENGTH_SHORT).show();
            }
        });
        btnSave3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strBaudrate = txtBaudrate3.getText().toString();
                try {
                    baudrate3 = Integer.parseInt(strBaudrate);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Input integer", Toast.LENGTH_SHORT).show();
                    return;
                }
                dbDataService.updateBaudrate(baudrate3, 3);
                Toast.makeText(getContext(), "Succeed", Toast.LENGTH_SHORT).show();
            }
        });
        btnSave4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strBaudrate = txtBaudrate4.getText().toString();
                try {
                    baudrate4 = Integer.parseInt(strBaudrate);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Input integer", Toast.LENGTH_SHORT).show();
                    return;
                }
                dbDataService.updateBaudrate(baudrate4, 4);
                Toast.makeText(getContext(), "Succeed", Toast.LENGTH_SHORT).show();
            }
        });


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
                    //runflag1=true;
                    devBizHandler1.setFlagSpRun(true);
                } else {
                    btnOpenPort1.setText(R.string.OPEN);
                    //runflag1=false;
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
                    //runflag2=true;
                    devBizHandler2.setFlagSpRun(true);
                } else {
                    btnOpenPort2.setText(R.string.OPEN);
                    //runflag2=false;
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


        btnSql.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    JSONObject json = new JSONObject();
                    json.put("name", "zhangsan");
                    json.put("age", "18");
                    Log.d("TEST", json.toString());
                } catch (Exception e) {

                }

                Cursor query;
                String str = txtSql.getText().toString();
                String[] arr = str.replace("\r", "").replace("\n", "").split(";");
                try {
                    for(String s : arr) {
                        if(s.equals("")) {
                            continue;
                        } else if (s.contains("select") || s.contains("SELECT")) {
                           query = dbHelper.getDb().rawQuery(s, null);
                           //query.ge
                        } else {
                           dbHelper.getDb().execSQL(s);
                        }
                    }
                    Toast.makeText(getContext(), "exec sql finish", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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

        cbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    String strUrl = txtUrl.getText().toString();
                    if(!MyUtil.isStringEmpty(strUrl)) {
                        LocalData.url = strUrl;
                    }
                    String s = txtWebsocket.getText().toString().trim();
                    if(!MyUtil.isStringEmpty(s)) {
                        URI uri = URI.create(s);
                        initMyWebsocket(uri);
                    }
                } else {

                }
            }
        });

        EditText txt1 = (EditText) view.findViewById(R.id.txtTest_setting);
        Button btn1 = (Button) view.findViewById(R.id.btnTest_setting);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String str = txt1.getText().toString();
                    byte[] bytes = ChangeTool.HexToByteArr(str);
                    TestDevBizHandler testDevBizHandler = new TestDevBizHandler("ups_ladis_01", getContext(), "devname111", "ups");
                    testDevBizHandler.onDataReceive(bytes, bytes.length, 2);
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getApplicationContext(), SpConfigActivity.class));
            }
        });

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext().getApplicationContext(), TestActivity.class));
            }
        });

        handlerData.postDelayed(runnableData, 10*60*1000); //timer 10min, send http and save data

        //timer.schedule(task, 1000,5000);

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

    MyWebSocketClient client = null;
    private void initMyWebsocket(URI uri) {
        if(client == null) {
            client = new MyWebSocketClient(uri) {
                @Override
                public void onMessage(String message) {

                    /**
                     * 命令格式：{DeviceCode:"xxxx",OptCode:"StartUps",OptType:"operate/query"}
                     */
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
                            if(spNo.equals("1")) {
                                devBizHandler1.addDevOpt(devOptHisEntity);
                            } else if(spNo.equals("2")) {
                                devBizHandler2.addDevOpt(devOptHisEntity);
                            } else if(spNo.equals("3")) {
                                devBizHandler3.addDevOpt(devOptHisEntity);

                            } else if(spNo.equals("4")) {
                                devBizHandler4.addDevOpt(devOptHisEntity);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }
    }

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    //send http data and save device data
    Handler handlerData = new Handler();
    Runnable runnableData = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG,"~~~~~~~~~~~~~~~");
            for(String key : LocalData.devDataMap.keySet()) {
                String deviceCode = key;
                Map<String,ViewEntity> map1 = LocalData.devDataMap.get(deviceCode);
                DevEntity devEntity = null;
                for(DevEntity e : LocalData.devlist) {
                    if(e.getCode().equals(deviceCode)) {
                        devEntity = e;
                        break;
                    }
                }
                if(devEntity == null) {
                    continue;
                }
                try {
                    //send http
                    JSONObject json = new JSONObject();
                    for(String key2 : map1.keySet()) {
                        ViewEntity entity = map1.get(key2);
                        json.put(key2, entity.getValue());
                    }
                    if(!MyUtil.isStringEmpty(LocalData.url)) {
                        HttpUtil.httpPost(LocalData.url, json.toString());
                    }

                    //save data
                    DataHisEntity dataHisEntity = new DataHisEntity();
                    dataHisEntity.setDevCode(deviceCode);
                    dataHisEntity.setDevName(devEntity.getName());
                    dataHisEntity.setSpCode(devEntity.getSpCode());
                    dataHisEntity.setMsg(json.toString());
                    dataHisEntity.setCreateTime(new Date());
                    dbDataService.addDataHis(dataHisEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            handlerData.postDelayed(this, 10*60*1000);
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
                HttpUtil.httpPost(LocalData.url, json.toString());
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
            if(entity.getSeq()==1) {
                txtBaudrate1.setText(String.valueOf(entity.getBaudrate()));
            } else if(entity.getSeq()==2) {
                txtBaudrate2.setText(String.valueOf(entity.getBaudrate()));
            } else if(entity.getSeq()==3) {
                txtBaudrate3.setText(String.valueOf(entity.getBaudrate()));
            } else if(entity.getSeq()==4) {
                txtBaudrate4.setText(String.valueOf(entity.getBaudrate()));
            }
        }
        for(SysParamEntity entity : LocalData.sysparamlist) {
            if(entity.getParamName().equals("http_uri")) {
                if(entity.getParamValue()!=null) {
                    txtUrl.setText(entity.getParamValue());
                }
            } else if(entity.getParamName().equals("websocket_uri")) {
                if(entity.getParamValue()!=null) {
                    txtWebsocket.setText(entity.getParamValue());
                }
            }
        }
    }

    //        devBizHandler1.setOnThrowErrorListener(new DevBizHandler.OnThrowErrorListener() {
//            String str;
//            @Override
//            public void OnThrowError(String msg) {
//                str=msg;
//                handler.post(runnable);
//            }
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getContext(), str, Toast.LENGTH_LONG).show();
//                }
//            };
//        });




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
