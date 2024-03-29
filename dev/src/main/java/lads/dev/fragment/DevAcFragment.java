package lads.dev.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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


import java.nio.channels.NonWritableChannelException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import lads.dev.R;
import lads.dev.activity.AddAcActivity;
import lads.dev.activity.HisDataActivity;
import lads.dev.biz.DbDataService;
import lads.dev.biz.LocalData;
import lads.dev.dto.DevUpsDto;
import lads.dev.entity.BroadcastArguments;
import lads.dev.entity.DevEntity;
import lads.dev.entity.DevOptEntity;
import lads.dev.entity.DevOptHisEntity;
import lads.dev.entity.KeyValueEntity;
import lads.dev.entity.ViewEntity;
import lads.dev.utils.DevOperate;
import lads.dev.utils.MyBroadCast;
import lads.dev.utils.MyDatabaseHelper;
import lads.dev.utils.MyUtil;
import lads.dev.viewadapter.AcAdapter;
import lads.dev.viewadapter.DevAdapter;
import lads.dev.viewadapter.WarningAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DevAcFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DevAcFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DevAcFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DevAcFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DevAcFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DevAcFragment newInstance(String param1, String param2) {
        DevAcFragment fragment = new DevAcFragment();
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

    String deviceCode;
    MyDatabaseHelper dbHelper;
    DevAdapter devAdapter;
    RecyclerView recyclerView;
    String newLine;
    Button btnRefresh,btnHistory,btnOperate;
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
        View view = inflater.inflate(R.layout.fragment_dev_ac, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_ac);
        btnRefresh = (Button) view.findViewById(R.id.devAcFragment_btn_refresh) ;
        btnHistory = (Button) view.findViewById(R.id.devAcFragment_btn_his) ;
        btnOperate = (Button) view.findViewById(R.id.devAcFragment_btn_opt) ;

        txtInfo = view.findViewById(R.id.devAcFragment_txt_info);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAc();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        btnOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                        String strCmd = list.get(i).getOptValue();
                        DevEntity devEntity = dbDataService.getDevByCode(deviceCode);
                        String spNo = devEntity.getSpNo();
                        DevOptHisEntity devOptHisEntity = new DevOptHisEntity();
                        devOptHisEntity.setDevName(devEntity.getName());
                        devOptHisEntity.setDevCode(devEntity.getCode());
                        devOptHisEntity.setOptName(list.get(i).getOptName());
                        devOptHisEntity.setOptValue(list.get(i).getOptValue());
                        DevOperate.addDevOpt(devOptHisEntity);

                        Toast.makeText(getContext(), "succeed", Toast.LENGTH_SHORT).show();
                    }

                });
                builder.create().show();
            }
        });
        loadAc();
        showDevInfo();
        MyBroadCast.Recv(BroadcastArguments.getAc(), new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Toast.makeText(getContext(),intent.getStringExtra("devid"),Toast.LENGTH_LONG).show();
                showDevInfo();
            }
        });

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

    private void loadAc() {
        List<KeyValueEntity> list = new ArrayList<>();
        for(DevEntity entity : LocalData.devlist) {
            if(entity.getTypeCode().equals("ac")) {
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
