package lads.dev.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import lads.dev.R;
import lads.dev.activity.AddThActivity;
import lads.dev.activity.HisDataActivity;
import lads.dev.biz.DbDataService;
import lads.dev.biz.LocalData;
import lads.dev.entity.BroadcastArguments;
import lads.dev.entity.DevEntity;
import lads.dev.entity.KeyValueEntity;
import lads.dev.entity.ViewEntity;
import lads.dev.utils.MyApplication;
import lads.dev.utils.MyDatabaseHelper;
import lads.dev.utils.MyUtil;
import lads.dev.viewadapter.DevAdapter;
import lads.dev.viewadapter.ThAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DevTHFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DevTHFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DevTHFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DevIOFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DevTHFragment newInstance(String param1, String param2) {
        DevTHFragment fragment = new DevTHFragment();
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
    String TAG = "DevTH";
    String deviceCode;
    MyDatabaseHelper dbHelper;
    DevAdapter devAdapter;
    RecyclerView recyclerView;
    String newLine;
    Button btnRefresh,btnHistory;
    TextView txtInfo;
    DbDataService dbDataService;
    //list
    List<DevEntity> devlist= new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        newLine = System.getProperty("line.separator");
        newLine+=newLine;
        dbHelper = new MyDatabaseHelper(getContext(), 2);
        dbDataService = new DbDataService(dbHelper.getDb());
        View view = inflater.inflate(R.layout.fragment_dev_th, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_th);
        btnRefresh = (Button) view.findViewById(R.id.devThFragment_btn_refresh) ;
        btnHistory = (Button) view.findViewById(R.id.devThFragment_btn_his) ;

        txtInfo = view.findViewById(R.id.devThFragment_txt_info);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTh();
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
        loadTh();
        showDevInfo();
        //定义广播接受器
        LocalBroadcastManager localBroadcastManager;
        IntentFilter intentFilter;
        intentFilter = new IntentFilter();
        //添加监听事件
        intentFilter.addAction(BroadcastArguments.getTh());
        //构造事件处理函数
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getContext(),intent.getStringExtra("devid"),Toast.LENGTH_LONG).show();
                showDevInfo();
            }
        };
        //挂载广播器实例
        localBroadcastManager = LocalBroadcastManager.getInstance(MyApplication.getContext());
        //注册监听到实例
        localBroadcastManager.registerReceiver(broadcastReceiver,intentFilter);
        return view;
    }

    private void loadTh() {
        List<KeyValueEntity> list = new ArrayList<>();
        for(DevEntity entity : LocalData.devlist) {
            if(entity.getTypeCode().equals("th")) {
                devlist.add(entity);
                KeyValueEntity kv = new KeyValueEntity(entity.getCode(), entity.getName());
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

    private void showDevInfo() {
        String str="";
        for (DevEntity entity:devlist){
            Map<String, ViewEntity> map = LocalData.devDataMap.get(entity.getCode());
            if(map!=null) {
                StringBuffer sb = new StringBuffer();
                sb.append("设备名称: "+entity.getName()+"\n");
                for(String key : map.keySet()) {
                    ViewEntity ve = map.get(key);
                    sb.append(key+":"+ve.getValue()+", ");
                }
                str+=sb.toString()+newLine+"\n";
            }
        }
        txtInfo.setText(str);
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
