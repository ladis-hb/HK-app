package lads.dev.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import lads.dev.R;
import lads.dev.biz.LocalData;
import lads.dev.dto.DevUpsDto;
import lads.dev.entity.BroadcastArguments;
import lads.dev.entity.DevEntity;
import lads.dev.entity.ViewEntity;
import lads.dev.utils.FormatDevInfo;
import lads.dev.utils.MyDatabaseHelper;
import lads.dev.viewadapter.WarningAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DevHomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DevHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DevHomeFragment extends Fragment {
    private String TAG="DevHomeFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DevHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DevHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DevHomeFragment newInstance(String param1, String param2) {
        DevHomeFragment fragment = new DevHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    TextView txtAC,txtEm,txtTh;
    MyDatabaseHelper dbHelper;
    String newLine;
    TextView txtBasicInfo;
    TextView txtBatInfo;
    TextView txtIoInfo;
    DevUpsDto dto;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private LocalBroadcastManager localBroadcastManager;
    IntentFilter intentFilter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.d(TAG, "~~~DevHomeFragment.onCreate");

        dbHelper = new MyDatabaseHelper(getContext(), "lads.db", null, 2);
        initWarnings("所有");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        newLine = System.getProperty("line.separator");
        newLine+=newLine;
        // Inflate the layout for this fragment
        Log.d(TAG, "~~~DevHomeFragment.onCreateView");
        View view = inflater.inflate(R.layout.fragment_dev_home, container, false);
        final RecyclerView recyclerView=(RecyclerView) view.findViewById(R.id.recyclerview_warn);;


        Spinner spinnerDevType = view.findViewById(R.id.spinnerDevTypeChoose);
        spinnerDevType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "select "+position);
                String str = "所有";
                switch (position){
                    case 1:
                        str="UPS";
                        break;
                    case 2:
                        str="空调";
                        break;
                    case 3:
                        str="电量仪";
                        break;

                    case 4:
                        str="温湿度";
                        break;
                        default:
                            str = "所有";
                            break;
                }

                initWarnings(str);
                WarningAdapter adapter = new WarningAdapter(warninglist);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        WarningAdapter adapter = new WarningAdapter(warninglist);
        recyclerView.setAdapter(adapter);

        txtAC = (TextView) view.findViewById(R.id.txt_acdata_frag_dev_home);
        txtEm = (TextView) view.findViewById(R.id.txt_emdata_frag_dev_home);
        txtTh = (TextView) view.findViewById(R.id.txt_thdata_frag_dev_home);


        txtBasicInfo = (TextView) view.findViewById(R.id.txt_ups_basicinfo_fragment_dev_home);
        txtBatInfo = (TextView) view.findViewById(R.id.txt_ups_batinfo_fragment_dev_home);
       txtIoInfo = (TextView) view.findViewById(R.id.txt_ups_ioinfo_fragment_dev_home);

       // timer.schedule(task, 1000,1000);

        //定义广播接受器
        intentFilter = new IntentFilter();
        //添加监听事件
        intentFilter.addAction(BroadcastArguments.getUps());
        intentFilter.addAction(BroadcastArguments.getTh());
        intentFilter.addAction(BroadcastArguments.getEm());
        intentFilter.addAction(BroadcastArguments.getAc());
        //挂载广播器实例
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        //注册监听到实例
        localBroadcastManager.registerReceiver(broadcastReceiver,intentFilter);
        return view;
    }


    //构造事件处理函数
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String devid = intent.getStringExtra("devid");
            switch (action){
                case "lads.dev.update.Ups":
                    getDevInfo("ups");
                    break;
                case "lads.dev.update.Ac":
                    getDevInfo("ac");
                    break;

                case "lads.dev.update.Em":
                    getDevInfo("em");
                    break;
                case "lads.dev.update.Th":
                    getDevInfo("th");
                    break;
            }

        }
    };

    private void getDevInfo(String devType){
        String AcInfo = "";
        String EmInfo = "";
        String ThInfo = "";
        for(String code:LocalData.Cache_all_devlist.keySet()){
            DevEntity entity = LocalData.Cache_all_devlist.get(code);
            String type = entity.getTypeCode();
            if(entity.getTypeCode().equals(devType)){
                String devid = entity.getCode();
                Map<String,ViewEntity> devMap = LocalData.devDataMap.get(devid);
                if(devMap == null) return;
                switch (devType){
                    case "ups":
                        Map<String,String> map = FormatDevInfo.ShortUps(devid);
                        txtBasicInfo.setText(map.get("BasicInfo"));
                        txtBatInfo.setText(map.get("BatInfo"));
                        txtIoInfo.setText(map.get("IoInfo"));
                        /*StringBuffer sb = new StringBuffer();
                        sb.append("UPS名称："+entity.getName()+newLine);
                        if(devMap.containsKey("工作模式")) sb.append("工作模式："+devMap.get("工作模式").getValue()+newLine);
                        if(devMap.containsKey("相位")) sb.append("相位："+devMap.get("相位").getValue()+newLine);
                        txtBasicInfo.setText(sb.toString());
                        sb = new StringBuffer();
                        if(devMap.containsKey("电池电压")) sb.append("电池电压："+devMap.get("电池电压").getValue()+newLine);
                        if (devMap.containsKey("负电池电压"))sb.append("负电池电压："+devMap.get("负电池电压").getValue()+newLine);
                        if(devMap.containsKey("剩余容量")) sb.append("剩余容量："+devMap.get("剩余容量").getValue()+newLine);
                        txtBatInfo.setText(sb.toString());
                        sb=new StringBuffer();
                        if(devMap.containsKey("输入电压")) sb.append("输入电压："+devMap.get("输入电压").getValue()+newLine);
                        if(devMap.containsKey("输入频率")) sb.append("输入频率："+devMap.get("输入频率").getValue()+newLine);
                        if(devMap.containsKey("输出电压")) sb.append("输出电压："+devMap.get("输出电压").getValue()+newLine);
                        if(devMap.containsKey("输出频率")) sb.append("输出频率："+devMap.get("输出频率").getValue()+newLine);
                        txtIoInfo.setText(sb.toString());*/
                        break;
                    case "ac":
                        AcInfo +="设备名称:"+entity.getName()+"\n"+ FormatDevInfo.ShortAC(devid)+"\n";
                        break;
                    case "em":
                        EmInfo +="设备名称:"+entity.getName()+"\n"+ FormatDevInfo.ShortEM(devid)+"\n";
                        break;
                    case "th":
                        ThInfo += "设备名称:"+entity.getName()+"\n"+ FormatDevInfo.ShortTH(devid)+"\n";
                        break;
                }
            }
        }
        if(!AcInfo.equals("")) txtAC.setText(AcInfo);
        if(!EmInfo.equals("")) txtEm.setText(EmInfo);
        if(!ThInfo.equals("")) txtTh.setText(ThInfo);
    }

    List<String> warninglist = new ArrayList<>();
    private void initWarnings(String type) {

       /* warninglist.clear();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("warning",null,null,null,null,null,null);
        if(cursor.moveToFirst()) {
            do {
                String devType = cursor.getString(cursor.getColumnIndex("dev_type"));
                String warnTitle = cursor.getString(cursor.getColumnIndex("warn_title"));
                String warnContent = cursor.getString(cursor.getColumnIndex("warn_content"));
                String createTime = cursor.getString(cursor.getColumnIndex("create_time"));
                if(type.equals("所有")) {
                    warninglist.add("["+devType+"] "+warnContent+" "+createTime);
                } else if(type.equals(devType)) {
                    warninglist.add("["+devType+"] "+warnContent+" "+createTime);
                }

            } while (cursor.moveToNext());
        }
        cursor.close();

        SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddHHmmssSSS");
       for(int i=0;i<20;i++) {
            //Date now = new Date();
            warninglist.add(sdf.format(new Date().toString())+"反反复复付付付付付付付付付付付付付付付付付付");
        }*/
    }

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
