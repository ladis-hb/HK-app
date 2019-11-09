package lads.dev.utils;

import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.example.x6.serial.SerialPort;
import lads.dev.biz.RuleEnum;
import lads.dev.dto.RecvDataDto;

/**
 * Created by Administrator on 2019-07-01
 */
public class SerialPortUtils {
    private final String TAG = "SerialPortUtils";
    //private String path = "/dev/ttyS1";
    //private String path = "/dev/ttyAMA3";
    private String path = "/dev/";
    //private int baudrate = 115200;
    public boolean serialPortStatus = false; //是否打开串口标志
    public String data_;
    public boolean threadStatus; //线程状态，为了安全终止线程

    public SerialPort serialPort = null;
    public InputStream inputStream = null;
    public OutputStream outputStream = null;
    public ChangeTool changeTool = new ChangeTool();
    int flag = -1;
    String readType="";

    /**
     * 打开串口
     * @return serialPort串口对象
     */
//    public SerialPort openSerialPort(){
//        try {
//            serialPort = new SerialPort(new File(path),baudrate,0);
//            this.serialPortStatus = true;
//            threadStatus = false; //线程状态
//
//            //获取打开的串口中的输入输出流，以便于串口数据的收发
//            inputStream = serialPort.getInputStream();
//            outputStream = serialPort.getOutputStream();
//
//            //new ReadThread().start(); //开始线程监控是否有数据要接收
//        } catch (IOException e) {
//            Log.e(TAG, "openSerialPort: 打开串口异常：" + e.toString());
//            return serialPort;
//        }
//        Log.d(TAG, "openSerialPort: 打开串口");
//        return serialPort;
//    }
    public SerialPort openSerialPort(int n, int baudrate){
        try {
            String s = "";
            switch (n){
                case 1:
                    s="ttyS0";
                    break;

                case 2:
                    s="ttyS1";
                    break;
                case 3:
                    s="ttyS2";
                    break;

                case 4:
                    s="ttyS3";
                    break;
            }
            /*if(n==1) {
//                s="ttyAMA0";

            } else if(n==2) {
//                s="ttyAMA1";

            } else if(n==3) {
//                s="ttyAMA2";

            } else if(n==4) {
//                s="ttyAMA3";

            }*/
            path+=s;
            serialPort = new SerialPort(new File(path),baudrate,0);
            this.serialPortStatus = true;
            threadStatus = false; //线程状态

            //获取打开的串口中的输入输出流，以便于串口数据的收发
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();

            //new ReadThread().start(); //开始线程监控是否有数据要接收
        } catch (IOException e) {
            Log.e(TAG, "openSerialPort: 打开串口异常：" + e.toString());
            return serialPort;
        }
        Log.d(TAG, "openSerialPort: 打开串口"+path+",braudrate:"+baudrate);
        return serialPort;
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort(){
        try {
            inputStream.close();
            outputStream.close();

            this.serialPortStatus = false;
            this.threadStatus = true; //线程状态
            serialPort.close();
        } catch (IOException e) {
            Log.e(TAG, "closeSerialPort: 关闭串口异常："+e.toString());
            return;
        }
        Log.d(TAG, "closeSerialPort: 关闭串口成功");
    }

    /**
     * 发送串口指令（字符串）
     * @param data String数据指令
     */
    public void sendSerialPort(String data){
        Log.d(TAG, "sendSerialPort: 发送数据");
        try {
            byte[] sendData = ChangeTool.HexToByteArr(data); //string转byte[]
            if (sendData.length > 0) {
                outputStream.write(sendData);
                outputStream.flush();
                Log.d(TAG, "sendSerialPort: 串口数据发送成功，"+data);
            }
        } catch (IOException e) {
            Log.e(TAG, "sendSerialPort: 串口数据发送失败："+e.toString());
        }

    }

   /* public RecvDataDto readOvertime(byte[] sendData, String devCode, String readType, RecvDataDto recvData){
        int ret=0;
        RecvDataDto result = new RecvDataDto();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Callable<RecvDataDto> readTask = new Callable<RecvDataDto>() {
            @Override
            public RecvDataDto call() throws Exception {
               return readData(sendData, devCode, readType, recvData);
            }
        };
        Future<RecvDataDto> future = executorService.submit(readTask);
        try {
            future.get(5000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            ret=-1;
        } finally {
            future.cancel(true);
            executorService.shutdown();

            try {
                if(future.get() == null){
                   result.setRet(-1);
                }else {
                    result = future.get();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            result.setRet(ret);
            return result;
        }
    }*/
    //serialPort发送指令，等待数据返回
    public RecvDataDto readData(byte[] sendData, String readType) {
        RecvDataDto recvData = new RecvDataDto();
        try {
            Log.e(TAG, "1 ===send data, size:"+sendData.length+";data:"+ChangeTool.ByteArrToHex(sendData));
            outputStream.write(sendData);

            byte[] buffer = new byte[512];
            byte[] tmp = new byte[64];
            int size = -1;
            int totalCount = 0;
            if(readType.equals(RuleEnum.ReadType.LADSUPS.toString())) {
                //Log.e(TAG, devCode+"1 ===send data, size:"+sendData.length+";data:"+ChangeTool.ByteArrToHex(sendData));
                while(true) {
                    size = inputStream.read(tmp);
                    System.arraycopy(tmp, 0, buffer, totalCount, size);
                    totalCount+=size;
                    if(tmp[size-1]==(byte)0x0d) {
                        break;
                    }
                }
                //Log.d(TAG, "2 === "+devCode+" receive data, size:" + totalCount+", "+ChangeTool.ByteArrToHex(buffer));

            } else if(readType.equals(RuleEnum.ReadType.MODBUS.toString())) {
                size = inputStream.read(tmp);
                System.arraycopy(tmp, 0, buffer, totalCount, size);
                if(size<3) {
                    Log.d(TAG, "recv data incomplete");
                    return null;
                }
                String hexStr = ChangeTool.ByteArrToHex(tmp, 2, 1);
                int i = ChangeTool.HexToInt(hexStr);
                totalCount = size;
                while(totalCount < i+5) { //接收总长度=数据长度+5
                    size = inputStream.read(tmp);
                    System.arraycopy(tmp, 0, buffer, totalCount, size);
                    totalCount+=size;
                }
            } else {
                Log.d(TAG, "Unsupported query type");
            }
            Log.d(TAG, "2 ===  receive data, size:" + totalCount+", "+ChangeTool.ByteArrToHex(buffer));
            recvData.setCount(totalCount);
            recvData.setBytes(buffer);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return recvData;
    }

    public OnDataReceiveListener onDataReceiveListener = null;
    public static interface OnDataReceiveListener {
        public void onDataReceive(byte[] buffer, int size, int ste);
    }
    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }
}
