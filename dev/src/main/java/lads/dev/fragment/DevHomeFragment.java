package lads.dev.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lads.dev.R;
import lads.dev.biz.LocalData;
import lads.dev.dto.DevUpsDto;
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



    MyDatabaseHelper dbHelper;
    String newLine;

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
                if(position==1){ str="UPS";} else if(position==2){str="空调";}
                else if(position==3){str="电量仪";}
                else if(position==4){str="温湿度";}
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

        String str="";

        TextView txtAC = (TextView) view.findViewById(R.id.txt_acdata_frag_dev_home);
      str = "蒸发器温度：30℃"+newLine+"室内温度：20℃"+newLine+"室内湿度：37%";
        txtAC.setText(str);
        TextView txtEM = (TextView) view.findViewById(R.id.txt_emdata_frag_dev_home);
       str="输入电压：180V"+newLine+"输入电流：99A"+newLine+"输入频率：40Hz"+newLine+"有功功率：80kW"+newLine+"无功功率：60kW"+newLine+"功率因素：1";
        txtEM.setText(str);

        TextView txtIO = (TextView) view.findViewById(R.id.txt_thdata_frag_dev_home);
        str="温度1：23℃"+newLine+"湿度1：33.4%"+newLine+"温度2：26℃"+newLine+"湿度2：36%";
        txtIO.setText(str);

        txtBasicInfo = (TextView) view.findViewById(R.id.txt_ups_basicinfo_fragment_dev_home);
        txtBatInfo = (TextView) view.findViewById(R.id.txt_ups_batinfo_fragment_dev_home);
       txtIoInfo = (TextView) view.findViewById(R.id.txt_ups_ioinfo_fragment_dev_home);

        timer.schedule(task, 1000,1000);

        return view;
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
            //Log.d("",sdf.format(new Date()));
            dto = LocalData.devUpsDto;
            if(dto == null) {
                dto = new DevUpsDto();
            }
            StringBuffer sb = new StringBuffer();
            sb.append("UPS名称："+newLine);
            sb.append("工作模式："+dto.getQmod()+newLine);
           sb.append("相位："+dto.getQmd_pp()+newLine);
            txtBasicInfo.setText(sb.toString());
            sb = new StringBuffer();
            sb.append("电池电压："+dto.getQgs_ss()+"V"+newLine);
            sb.append("负电池电压："+dto.getQgs_xx()+"V"+newLine);
            sb.append("剩余容量："+dto.getQbv_tt()+"%"+newLine);
            txtBatInfo.setText(sb.toString());
            sb=new StringBuffer();
            sb.append("输入电压："+dto.getQgs_mm()+"V"+newLine);
            sb.append("输入频率："+dto.getQgs_hh()+"Hz"+newLine);
            sb.append("输出电压："+dto.getQgs_ll()+"V"+newLine);
            sb.append("输出频率："+dto.getQgs_nn()+"Hz"+newLine);
            txtIoInfo.setText(sb.toString());
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
