package lads.dev.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import lads.dev.R;
import lads.dev.activity.AddUpsActivity;
import lads.dev.activity.HisDataActivity;
import lads.dev.activity.SpConfigActivity;
import lads.dev.biz.DbDataService;
import lads.dev.biz.LocalData;
import lads.dev.dto.DevUpsDto;
import lads.dev.entity.DevEntity;
import lads.dev.entity.DevOptEntity;
import lads.dev.entity.DevOptHisEntity;
import lads.dev.entity.KeyValueEntity;
import lads.dev.entity.ProtocolEntity;
import lads.dev.entity.ViewEntity;
import lads.dev.entity.WarnHisEntity;
import lads.dev.utils.MyDatabaseHelper;
import lads.dev.utils.MyUtil;
import lads.dev.viewadapter.DevAdapter;
import lads.dev.viewadapter.HisDataAdapter;
import lads.dev.viewadapter.StrSingleAdapter;
import lads.dev.viewadapter.UpsAdapter;
import lads.dev.viewadapter.WarningAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DevUpsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DevUpsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DevUpsFragment extends Fragment {
    MyDatabaseHelper dbHelper;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DevUpsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DevUpsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DevUpsFragment newInstance(String param1, String param2) {
        DevUpsFragment fragment = new DevUpsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private String deviceCode;
    DevAdapter devAdapter;
    RecyclerView recyclerView;
    String newLine;
    Button btnRefresh,btnHistory,btnOperate;
    DbDataService dbDataService;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        newLine = System.getProperty("line.separator");
        newLine+=newLine;
        dbHelper = new MyDatabaseHelper(getContext(), 2);
        dbDataService = new DbDataService(dbHelper.getDb());
        View view = inflater.inflate(R.layout.fragment_dev_ups, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_ups);
        btnRefresh = (Button) view.findViewById(R.id.devUpsFragment_btn_refresh) ;
        btnHistory = (Button) view.findViewById(R.id.devUpsFragment_btn_his) ;
        btnOperate = (Button) view.findViewById(R.id.devUpsFragment_btn_opt) ;

        initWarnings();
        RecyclerView recyclerView2 = (RecyclerView) view.findViewById(R.id.recyclerview_warn_fragment_dev_ups);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        recyclerView2.setLayoutManager(layoutManager2);
        WarningAdapter adapter = new WarningAdapter(warninglist);
        recyclerView2.setAdapter(adapter);

        txtBasicInfo = (TextView) view.findViewById(R.id.txt_ups_basicinfo_fragment_dev_ups);
        txtBatInfo = (TextView) view.findViewById(R.id.txt_ups_batinfo_fragment_dev_ups);
        txtIoInfo = (TextView) view.findViewById(R.id.txt_ups_ioinfo_fragment_dev_ups);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUps();
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MyUtil.isStringEmpty(deviceCode)) {
                    Toast.makeText(getContext(), "choose device", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("deviceCode", deviceCode);
                Intent intent = new Intent(getActivity().getApplicationContext(), HisDataActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

//        View view2 = inflater.inflate(R.layout.content_devopt, container, false);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        AlertDialog alertDialog = builder.setTitle("Operation")
//                .setView(view2)
//                .create();
//        Button btnOpen = view2.findViewById(R.id.content_devopt_btn_open);
//        Button btnClose = view2.findViewById(R.id.content_devopt_btn_close);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        btnOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                DevSettingFragment.devBizHandler1.addDevOpt();

                //alertDialog.show();
                if(MyUtil.isStringEmpty(deviceCode)) {
                    Toast.makeText(getContext(), "choose device", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<DevOptEntity> list = getOptByDev(deviceCode);
                if(MyUtil.isListEmpty(list)) {
                    Toast.makeText(getContext(), "no operation settings", Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] items = new String[list.size()];
                for(int i=0;i<list.size();i++) {
                    items[i] = list.get(i).getOptName();
                }
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(getContext(), i,Toast.LENGTH_SHORT).show();
                        Log.d("aaa", i+"");
                        if(DevSettingFragment.devBizHandler1 == null) {
                            Toast.makeText(getContext(), "setting first", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String strCmd = list.get(i).getOptValue();
                        DevEntity devEntity = dbDataService.getDevByCode(deviceCode);
                        String spNo = devEntity.getSpNo();
                        DevOptHisEntity devOptHisEntity = new DevOptHisEntity();
                        devOptHisEntity.setDevName(devEntity.getName());
                        devOptHisEntity.setDevCode(devEntity.getCode());
                        devOptHisEntity.setOptName(list.get(i).getOptName());
                        devOptHisEntity.setOptValue(list.get(i).getOptValue());
                        if(spNo.equals("1")) {
                            DevSettingFragment.devBizHandler1.addDevOpt(devOptHisEntity);
                        } else if(spNo.equals("2")) {
                            DevSettingFragment.devBizHandler2.addDevOpt(devOptHisEntity);
                        } else if(spNo.equals("3")) {
                            DevSettingFragment.devBizHandler3.addDevOpt(devOptHisEntity);
                        } else if(spNo.equals("4")) {
                            DevSettingFragment.devBizHandler4.addDevOpt(devOptHisEntity);
                        }
                        Toast.makeText(getContext(), "succeed", Toast.LENGTH_SHORT).show();
                    }

                });
                builder.create().show();
            }
        });

        loadUps();
        timer.schedule(task, 1000,1000);
        return view;
    }

    private List<DevOptEntity> getOptByDev(String deviceCode) {
        DevEntity devEntity = dbDataService.getDevByCode(deviceCode);
        if(devEntity == null) {
            return null;
        }
        String protocolCode=devEntity.getProtocolCode();
        List<DevOptEntity> list = dbDataService.getDevOptByProtocol(protocolCode);
        return list;
    }

    private void loadUps() {
        List<KeyValueEntity> list = new ArrayList<>();
        for(DevEntity entity : LocalData.devlist) {
            if(entity.getTypeCode().equals("ups")) {
                KeyValueEntity kv = new KeyValueEntity(entity.getCode(), entity.getName());
                showDevInfo(entity.getCode());
                list.add(kv);
            }
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        devAdapter = new DevAdapter(getContext(), list);
        devAdapter.setDevAdapterCallback(new DevAdapter.DevAdapterCallback() {
            @Override
            public void itemClick(String devCode) {
                deviceCode=devCode;
            }

            @Override
            public void itemMenuClick(String devCode) {

            }
        });
        devAdapter.setOnItemClickListener(new DevAdapter.OnItemClickListener() {
            @Override
            public void setOnClickItemListener(View view, int position, String key) {
                deviceCode = key;
            }
        });
        recyclerView.setAdapter(devAdapter);
    }

    private void showDevInfo(String devCode) {
        if(MyUtil.isStringEmpty(devCode)) {
            return;
        }
        Map<String, ViewEntity> map = LocalData.devDataMap.get(devCode);
        if(map==null) {
            return;
        }
        StringBuffer sb1 = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        StringBuffer sb3 = new StringBuffer();
        for(String key : map.keySet()) {
            ViewEntity ve = map.get(key);
            if(ve.getColumnIndex()==1) {
                sb1.append(key+":"+ve.getValue()+"\n");
            } else if(ve.getColumnIndex()==2) {
                sb2.append(key+":"+ve.getValue()+"\n");
            } else if (ve.getColumnIndex()==3) {
                sb3.append(key+":"+ve.getValue()+"\n");
            }
        }
        txtBasicInfo.setText(sb1.toString());
        txtBatInfo.setText(sb2.toString());
        txtIoInfo.setText(sb3.toString());
    }

    private void showDevHis(String devCode) {
        Toast.makeText(getContext(),"showDevHis",Toast.LENGTH_SHORT).show();
    }

    TextView txtBasicInfo;
    TextView txtBatInfo;
    TextView txtIoInfo;
    DevUpsDto dto;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
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
            showDevInfo(deviceCode);
        }
    };
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    List<String> warninglist = new ArrayList<>();
    private void initWarnings() {

        warninglist.clear();
        List<WarnHisEntity> list = new ArrayList<>();
        if(MyUtil.isStringEmpty(deviceCode)) {
            list = dbDataService.getWarnHisByType("ups");
        } else {
            list = dbDataService.getWarnHisByDev(deviceCode);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                //reloadDev();
                break;
            default:
        }
    }

    public void reloadDev() {
        loadUps();
        //devlist.clear();
        //devlist.add("aa");
//        upsAdapter = new UpsAdapter(DevUpsFragment.this, dbHelper.getWritableDatabase(), devlist);
//        upsAdapter.setUpslist(devlist);
//        recyclerView.setAdapter(upsAdapter);
    }

    List<String> devlist = new ArrayList<>();

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
