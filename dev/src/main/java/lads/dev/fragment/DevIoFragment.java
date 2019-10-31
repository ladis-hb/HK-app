package lads.dev.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.x6.gpioctl.GpioUtils;

import java.util.ArrayList;
import java.util.List;

import lads.dev.R;
import lads.dev.activity.AddEmActivity;
import lads.dev.viewadapter.EmAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DevIoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DevIoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DevIoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DevIoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DevEmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DevIoFragment newInstance(String param1, String param2) {
        DevIoFragment fragment = new DevIoFragment();
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

    private char group='E';
    int io0,io1,io2,io3;
    int high=1,low=0;
    static GpioUtils gpioUtils;

    Button btnO1High,btnO1Low,btnO3High,btnO3Low,btnI0Get,btnI2Get;
    TextView txtI0,txtI2;
    String TAG="DevIoFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dev_io, container, false);
        btnI0Get = view.findViewById(R.id.frag_io_btn_i0_get);
        btnI2Get = view.findViewById(R.id.frag_io_btn_i2_get);
        btnO1High = view.findViewById(R.id.frag_io_btn_o1_sethigh);
        btnO1Low = view.findViewById(R.id.frag_io_btn_o1_setlow);
        btnO3High = view.findViewById(R.id.frag_io_btn_o3_sethigh);
        btnO3Low = view.findViewById(R.id.frag_io_btn_o3_setlow);
        txtI0 = view.findViewById(R.id.frag_io_txt_i0_msg);
        txtI2 = view.findViewById(R.id.frag_io_txt_i2_msg);

        /**
         *  设备0、2输入，1、3输出
         */

        gpioUtils = GpioUtils.getGpioUtils();
        io0 = gpioUtils.getGpioPin(group, 0);
        io1 = gpioUtils.getGpioPin(group, 1);
        io2=gpioUtils.getGpioPin(group, 2);
        io3 = gpioUtils.getGpioPin(group, 3);
        GpioUtils.setGpioDirection(io0, 1);
        GpioUtils.setGpioDirection(io2, 1);
        GpioUtils.setGpioDirection(io1, 0);
        GpioUtils.setGpioDirection(io3, 0);
        btnI0Get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ret = GpioUtils.gpioGetValue(io0);
                if(ret==0) {
                    Log.d(TAG, "低电平");
                    txtI0.setText("低电平");
                } else if(ret==1) {
                    Log.d(TAG, "高电平");
                    txtI0.setText("高电平");
                } else {
                    Log.d(TAG, "其他");
                    txtI0.setText("获取电平信号失败");
                }
            }
        });
        btnI2Get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ret=GpioUtils.gpioGetValue(io2);
                if(ret==0) {
                    Log.d(TAG, "低电平");
                    txtI2.setText("低电平");
                } else if(ret==1) {
                    Log.d(TAG, "高电平");
                    txtI2.setText("高电平");
                } else {
                    Log.d(TAG, "其他");
                    txtI2.setText("获取电平信号失败");
                }
            }
        });
        btnO1High.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GpioUtils.gpioSetValue(io1, high);
            }
        });
        btnO1Low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GpioUtils.gpioSetValue(io1, low);
            }
        });
        btnO3High.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GpioUtils.gpioSetValue(io3, high);
            }
        });
        btnO3Low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GpioUtils.gpioSetValue(io3, low);
            }
        });

        return view;
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
