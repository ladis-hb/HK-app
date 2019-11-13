package lads.dev.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lads.dev.R;
import lads.dev.activity.SpConfigActivity;
import lads.dev.activity.TestActivity;
import lads.dev.biz.DbDataService;
import lads.dev.biz.LocalData;
import lads.dev.entity.SpEntity;
import lads.dev.utils.HttpUtil;
import lads.dev.utils.MyDatabaseHelper;
import lads.dev.utils.MyUtil;
import lads.dev.viewadapter.WarningAdapter;

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
    EditText txtBaudrate1,txtBaudrate2,txtBaudrate3,txtBaudrate4,txtUrl,txtWebsocket;
    Button btnSave1,btnSave2,btnSave3,btnSave4,btnOpenPort1,btnOpenPort2,btnOpenPort3,btnOpenPort4,btnUrl,btnConfig,btnTest,btn_refresh_devInfo;
     private int baudrate1,baudrate2,baudrate3,baudrate4;
    CheckBox cbx;
    TextView txt_Socket_stat_info,txt_Web_stat_info,txt_mac,txt_SocketID;
    RecyclerView li_devlist_all,li_devlist_online;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "DevSettingFragment.onCreateView");
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_dev_setting, container, false);

        txt_mac = view.findViewById(R.id.txt_mac);
        txt_SocketID = view.findViewById(R.id.txt_socketID);

        li_devlist_all = view.findViewById(R.id.li_devlist_all);
        li_devlist_online = view.findViewById(R.id.li_devlist_online);

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

        btn_refresh_devInfo = view.findViewById(R.id.btn_refresh_devInfo);

        //http保存按钮
        btnUrl = (Button) view.findViewById(R.id.btnUrl);
        //txt_Socket_stat_info
        txt_Web_stat_info = view.findViewById(R.id.txt_Web_stat_info);
        //串口配置按钮
        btnConfig = (Button) view.findViewById(R.id.frg_setting_btn_config);
        //测试页面
        btnTest = view.findViewById(R.id.test_page);

        txtUrl = (EditText) view.findViewById(R.id.txtUrl);
        txtWebsocket = (EditText) view.findViewById(R.id.txtWebsocket);
        cbx = view.findViewById(R.id.frag_setting_cbx_connect);

        //超时

        dbHelper = new MyDatabaseHelper(getContext(), 2);
        dbDataService = new DbDataService(dbHelper.getDb());
        //
        btn_refresh_devInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbDataService.getSp();
                dbDataService.getSysParam();
                loaddata();
                loadDevList();

            }
        });

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
        //重新载入数据库
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
                    dbDataService.updateParamValue("webConnect","true");
                    CheckUrl();
                }else {
                    dbDataService.updateParamValue("webConnect","false");
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
                dbDataService.updateBaudrate(Integer.parseInt(txtBaudrate1.getText().toString()), 1);
            }
        });
        btnSave2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbDataService.updateBaudrate(Integer.parseInt(txtBaudrate2.getText().toString()), 2);
            }
        });
        btnSave3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbDataService.updateBaudrate(Integer.parseInt(txtBaudrate3.getText().toString()), 3);
            }
        });
        btnSave4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbDataService.updateBaudrate(Integer.parseInt(txtBaudrate4.getText().toString()), 4);
            }
        });

        //打开端口
        btnOpenPort1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String strBaudrate =txtBaudrate1.getText().toString();
                    try {
                        baudrate1 = Integer.parseInt(strBaudrate);

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Input baudrate", Toast.LENGTH_SHORT).show();
                        return;
                    }

                if(btnOpenPort1.getText().toString().equals("打开端口")) {
                    btnOpenPort1.setText(R.string.Close);
                    dbDataService.updateSerialPortState(1,1);
                } else {
                    btnOpenPort1.setText(R.string.OPEN);
                    dbDataService.updateSerialPortState(0,1);
                }
                dbDataService.getSp();
            }
        });

        btnOpenPort2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    String strBaudrate = txtBaudrate2.getText().toString();
                    try {
                        baudrate2 = Integer.parseInt(strBaudrate);

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Input baudrate", Toast.LENGTH_SHORT).show();
                        return;
                    }

                if(btnOpenPort2.getText().toString().equals("打开端口")) {
                    btnOpenPort2.setText(R.string.Close);
                    dbDataService.updateSerialPortState(1,2);
                } else {
                    btnOpenPort2.setText(R.string.OPEN);
                    dbDataService.updateSerialPortState(0,2);
                }
                dbDataService.getSp();
            }
        });
        btnOpenPort3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    String strBaudrate = txtBaudrate3.getText().toString();
                    try {
                        baudrate3 = Integer.parseInt(strBaudrate);

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Input baudrate", Toast.LENGTH_SHORT).show();
                        return;
                    }


                if(btnOpenPort3.getText().toString().equals("打开端口")) {
                    btnOpenPort3.setText(R.string.Close);
                    dbDataService.updateSerialPortState(1,3);
                } else {
                    btnOpenPort3.setText(R.string.OPEN);
                    dbDataService.updateSerialPortState(0,3);
                }
                dbDataService.getSp();
            }
        });
        btnOpenPort4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    String strBaudrate = txtBaudrate4.getText().toString();
                    try {
                        baudrate4 = Integer.parseInt(strBaudrate);

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Input baudrate", Toast.LENGTH_SHORT).show();
                        return;
                    }


                if(btnOpenPort4.getText().toString().equals("打开端口")) {
                    btnOpenPort4.setText(R.string.Close);
                    dbDataService.updateSerialPortState(1,4);
                } else {
                    btnOpenPort4.setText(R.string.OPEN);
                    dbDataService.updateSerialPortState(0,4);
                }
                dbDataService.getSp();
            }
        });
        dbDataService.getSysParam();
        loaddata();
        return view;
    }


    private void loadDevList(){
        Set<String> dev_all = new HashSet<>();
        for(String code:LocalData.Cache_all_devlist.keySet()){
            dev_all.add(LocalData.Cache_all_devlist.get(code).getName());
        }

        //List<String> list_all = new ArrayList<>(dev_all);
        Set<String> dev_online = new HashSet<>();
        for(String spNo:LocalData.Cache_devlist.keySet()){
           for(String code:LocalData.Cache_devlist.get(spNo).keySet()){
               dev_online.add(LocalData.Cache_devlist.get(spNo).get(code).getName());
           }
        }
        li_devlist_all.setLayoutManager(new LinearLayoutManager(getContext()));
        li_devlist_all.setAdapter(new WarningAdapter(new ArrayList<>(dev_all)));

        li_devlist_online.setLayoutManager(new LinearLayoutManager(getContext()));
        li_devlist_online.setAdapter(new WarningAdapter(new ArrayList<>(dev_online)));
    }


    //检测Web连接
    private void CheckUrl(){
        Log.d(TAG,"检测Web连接");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Boolean connect = HttpUtil.checkUrl(txtWebsocket.getText().toString(),1000);
                Message message = new Message();
                if(connect) message.what = 1;
                else message.what = 0;
                handler_check_web.sendMessage(message);
            }
        }).start();
    }
    //创建发送hanlder
    private Handler handler_check_web = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    txt_Web_stat_info.setText("数据上传服务器连接成功");
                    txt_Web_stat_info.setTextColor(0xFF006400);
                    break;
                case 0:
                    txt_Web_stat_info.setText("数据上传服务器连接失败");
                    txt_Web_stat_info.setTextColor(0xFFA52A2A);
                    break;
            }
        }
    };

    private void loaddata() {
        for(SpEntity entity : LocalData.splist) {
            switch (entity.getSeq()){
                case 1:
                    txtBaudrate1.setText(String.valueOf(entity.getBaudrate()));
                    if(entity.getState() == 1 && btnOpenPort1.getText().equals("打开端口")) btnOpenPort1.performClick();
                    break;
                case 2:
                    txtBaudrate2.setText(String.valueOf(entity.getBaudrate()));
                    if(entity.getState() == 1&& btnOpenPort2.getText().equals("打开端口")) btnOpenPort2.performClick();
                    break;
                case 3:
                    txtBaudrate3.setText(String.valueOf(entity.getBaudrate()));
                    if(entity.getState() == 1&& btnOpenPort3.getText().equals("打开端口")) btnOpenPort3.performClick();
                    break;
                case 4:
                    txtBaudrate4.setText(String.valueOf(entity.getBaudrate()));
                    if(entity.getState() == 1&& btnOpenPort4.getText().equals("打开端口")) btnOpenPort4.performClick();
                    break;
            }
        }
        for(String key:LocalData.Cache_sysparamlist.keySet()){
            String value=LocalData.Cache_sysparamlist.get(key).getParamValue();
            switch (key){
                case "http_uri":
                    txtUrl.setText(value);
                    break;
                case "websocket_uri":
                    txtWebsocket.setText(value);
                    break;
                case "webConnect":
                    if(value.equals("true")) cbx.setChecked(true);
                    break;
                case "MacStr":
                    txt_mac.setText(value);
                    break;
                case "SocketID":
                    txt_SocketID.setText(value);
                    break;
            }
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
        dbDataService.initContextData();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
