package lads.dev.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.FileDescriptor;

import android_serialport_api.SerialPort;
import lads.dev.R;
import lads.dev.biz.UpsBizHandler;
import lads.dev.dto.DevUpsDto;
import lads.dev.utils.MyByteUtil;
import lads.dev.utils.SerialPortUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SerialPortUtils serialPortUtils = new SerialPortUtils();
    UpsBizHandler upsBizHandler = new UpsBizHandler();
    private SerialPort serialPort;

    private Handler handler;
    private byte[] mBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnOpen = (Button)findViewById(R.id.btn_open);
        Button btnClose = (Button)findViewById(R.id.btn_close);
        Button btnSend = (Button)findViewById(R.id.btnSend);
        Button btnStatus=(Button)findViewById(R.id.btnStatus);
        Button btnTest = (Button)findViewById(R.id.btnTest);
        final EditText txtSend = (EditText)findViewById(R.id.txtSend);
        final TextView txtView = (TextView)findViewById(R.id.txtView);

        handler = new Handler(); //创建主线程的handler  用于更新UI

        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //serialPort = serialPortUtils.openSerialPort(4);
                if (serialPort == null){
                    Log.e(TAG, "串口打开失败");
                    Toast.makeText(MainActivity.this,"串口打开失败",Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(TAG, "串口已打开");
                Toast.makeText(MainActivity.this,"串口已打开",Toast.LENGTH_SHORT).show();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serialPortUtils.closeSerialPort();
                Log.d(TAG, "串口已关闭");
                Toast.makeText(MainActivity.this,"串口关闭成功",Toast.LENGTH_SHORT).show();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serialPortUtils.sendSerialPort(txtSend.getText().toString());
                txtView.setText("串口发送指令：" + serialPortUtils.data_);
                Toast.makeText(MainActivity.this,"发送指令："+txtSend.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });

        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileDescriptor fileDescriptor = serialPort.mFd;
                String result = fileDescriptor.toString();
                txtView.setText(result);
            }
        });

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //UpsBizHandler upsBizHandler = new UpsBizHandler();
//                //upsBizHandler.setSerialPortUtils(serialPortUtils);
//                Thread thread = new Thread(upsBizHandler);
//                thread.start();

                String hexStr = txtSend.getText().toString();
                byte[] bytes = MyByteUtil.hexStringToBytes(hexStr);
                String str = new String(bytes);
                str = str.substring(0, str.length()-1);
                txtView.setText(str);
                Log.d(TAG, str);
            }
        });

        upsBizHandler.setOnChangeViewDataListener(new UpsBizHandler.OnChangeViewDataListener() {
            DevUpsDto dto;
            @Override
            public void onChangeViewData(DevUpsDto upsDto) {
                dto=upsDto;
                handler.post(runnable);
            }

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    //txtView.setText(dto.getDevType());
                }
            };
        });

        serialPortUtils.setOnDataReceiveListener(new SerialPortUtils.OnDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] buffer, int size, int flag) {
                Log.d(TAG, "进入数据监听事件中。。。" + new String(buffer, 0,size-1));
                //Log.d(TAG, "进入数据监听事件中。。。");
                //
                //在线程中直接操作UI会报异常：ViewRootImpl$CalledFromWrongThreadException
                //解决方法：handler
                //
                mBuffer = buffer;
                handler.post(runnable);
            }
            //开线程更新UI
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    txtView.setText("size："+ String.valueOf(mBuffer.length)+"数据监听："+ new String(mBuffer));
                }
            };
        });
    }
}
