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
import java.util.concurrent.Callable;
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
            if(n==1) {
//                s="ttyAMA0";
                s="ttyS0";
            } else if(n==2) {
//                s="ttyAMA1";
                s="ttyS1";
            } else if(n==3) {
//                s="ttyAMA2";
                s="ttyS2";
            } else if(n==4) {
//                s="ttyAMA3";
                s="ttyS3";
            }
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
            byte[] sendData = data.getBytes(); //string转byte[]
            this.data_ = new String(sendData); //byte[]转string
            if (sendData.length > 0) {
                outputStream.write(sendData);
                //outputStream.write('\n');
                outputStream.write('\r'+'\n');
                outputStream.flush();
                Log.d(TAG, "sendSerialPort: 串口数据发送成功，"+this.data_);
            }
        } catch (IOException e) {
            Log.e(TAG, "sendSerialPort: 串口数据发送失败："+e.toString());
        }

    }

    public int readOvertime(byte[] sendData, String devCode, String readType, RecvDataDto recvData) {
        int ret=0;
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Callable<Integer> readTask = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                readData(sendData, devCode, readType, recvData);
                return 1;
            }
        };
        Future<Integer> future = executorService.submit(readTask);
        try {
            future.get(3000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            ret=-1;
        } finally {
            future.cancel(true);
            executorService.shutdown();
            return ret;
        }
    }

    public void readData(byte[] sendData, String devCode, String readType, RecvDataDto recvData) {

        try {
            //Log.e(TAG, "1 ===send data, size:"+sendData.length+";data:"+ChangeTool.ByteArrToHex(sendData));
            outputStream.write(sendData);

            byte[] buffer = new byte[512];
            byte[] tmp = new byte[64];
            int size = -1;
            int totalCount = 0;
            if(readType.equals(RuleEnum.ReadType.LADSUPS.toString())) {
                while(true) {
                    size = inputStream.read(tmp);
                    System.arraycopy(tmp, 0, buffer, totalCount, size);
                    totalCount+=size;
                    if(tmp[size-1]==(byte)0x0d) {
                        break;
                    }
                }

            } else if(readType.equals(RuleEnum.ReadType.MODBUS.toString())) {
                size = inputStream.read(tmp);
                System.arraycopy(tmp, 0, buffer, totalCount, size);
                if(size<3) {
                    Log.d(TAG, "recv data incomplete");
                    return;
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
            //Log.d(TAG, "2 ===["+sdf.format(new Date())+"] "+devCode+" receive data, size:" + totalCount+", "+ChangeTool.ByteArrToHex(buffer));
            recvData.setCount(totalCount);
            recvData.setBytes(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void sendData(byte[] data, int step, String readType) {



        Log.e(TAG, "1.1 ~～~send data, size:"+data.length+";data:"+ChangeTool.ByteArrToHex(data));
        if(data.length==0) {
            Log.d(TAG, "1.~~~send data length 0, return");
            return;
        }
        try {
            outputStream.write(data);
            this.flag = step;
            this.readType = readType;
            Log.d(TAG, "1.2 ~~~"+sdf.format(new Date())+"send data readType," + readType);
        } catch (IOException e) {
            Log.e(TAG, "~~~send data error," + e.getStackTrace());
        }
    }
*/
    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /*public int recvData() {
        Log.d(TAG,"2.1 ===recvData start,"+new Date().toString());
        byte[] buffer = new byte[512];
        byte[] tmp = new byte[64];
        int size = -1;
        int totalCount = 0;
        try {

            //Log.v(TAG,"recvData data:"+inputStream.read(tmp));
            if(readType.equals(RuleEnum.ReadType.LADSUPS.toString())) {

                while(true) {
                    size = inputStream.read(tmp);
                    System.arraycopy(tmp, 0, buffer, totalCount, size);
                    totalCount+=size;
                    if(tmp[size-1]==(byte)0x0d) {
                        break;
                    }
                }

                *//*
                 try {
                Thread.sleep(3000);
            } catch (Exception e){
                Log.e(TAG, "~~~receive data error," + e.getStackTrace());
            }
                size = 10;
                totalCount = 10;*//*
                Log.v(TAG,"2.2 ===recvData ;readType"+readType);
            } else if(readType.equals(RuleEnum.ReadType.MODBUS.toString())) {
                size = inputStream.read(tmp);
                System.arraycopy(tmp, 0, buffer, totalCount, size);
                if(size<3) {
                    Log.d(TAG, "recv data incomplete");
                    return 0;
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

            }
            Log.d(TAG, "2.3 ===["+sdf.format(new Date())+"]receive data, size:" + totalCount);
            Log.v(TAG,"2.4 ===recvData data:"+ChangeTool.ByteArrToHex(buffer));

            if(totalCount > 0) {
                onDataReceiveListener.onDataReceive(buffer,totalCount, flag);
                //Log.d(TAG,"*###### no callback");
            }

        } catch (IOException e) {
            Log.e(TAG, "~~~receive data error," + e.getStackTrace());
        }
        Log.d(TAG,"2.5 ===recvData end,"+new Date().toString());
        int rets = totalCount;
        return totalCount;

    }*/

    /*public int recvDataWithOvertime() {
        int ret = 0;
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Callable<Integer> readTask = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
//                Looper.prepare();
//
//                //new code
//                MessageQueue queue = Looper.myQueue();
//                queue.addIdleHandler(new MessageQueue.IdleHandler() {
//                    int mReqCount = 0;
//                    @Override
//                    public boolean queueIdle() {
//                       if(++mReqCount == 2){
//                           Looper.myLooper().quit();
//                           return false;
//                       }else {
//                           return true;
//                       }
//                    }
//                });
                //
                int i = recvData();
//                Looper.loop();
                return i;
            }
        };
        Future<Integer> future = executorService.submit(readTask);
        try {
            future.get(3000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
          ret=-1;
        } finally {
            future.cancel(true);
            executorService.shutdown();
            return ret;
        }
    }
*/
    /**
     * 单开一线程，来读数据
     */
    /*private class ReadThread extends Thread{
        @Override
        public void run() {
            super.run();
            //判断进程是否在运行，更安全的结束进程
            while (!threadStatus){
                Log.d(TAG, "进入线程run,"+this.hashCode());
                //64   1024
                byte[] buffer = new byte[512];
                byte[] tmp = new byte[64];
                int size=-1; //读取数据的大小
                int totalCount=0;
                try {
//                    while(size==0) {
//                        size=inputStream.available();
//                        if(size>0) {
//                            Log.d(TAG, "count:"+size);
//                        }
//                    }
//                    buffer = new byte[size];
//                    inputStream.read(buffer);

                    while(true) {
                        Log.d(TAG, size+","+totalCount);

                        size = inputStream.read(tmp);
                        System.arraycopy(tmp, 0, buffer, totalCount, size);
                        totalCount+=size;
                        if(tmp[size-1]==(byte)0x0d) {
                            break;
                        }
                    }

                    //test start
//                    byte[] buffer1 = new byte[512];
//                    byte[] buffer2 = new byte[512];
//                    int size1 = inputStream.read(buffer1);
//                    int size2 = inputStream.read(buffer2);
                    //test end
                    Log.d(TAG, "======="+size+","+totalCount);
                    if (totalCount > 0){
                        Log.d(TAG, "run: 接收到了数据：" + changeTool.ByteArrToHex(buffer));
                        Log.d(TAG, "run: 接收到了数据大小：" + String.valueOf(totalCount));
                        onDataReceiveListener.onDataReceive(buffer,totalCount, flag);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "run: 数据读取异常：" +e.toString());
                }
            }

        }
    }
*/
    public OnDataReceiveListener onDataReceiveListener = null;
    public static interface OnDataReceiveListener {
        public void onDataReceive(byte[] buffer, int size, int ste);
    }
    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }
}
