package lads.dev.utils;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.example.x6.serial.SerialPort;

import lads.dev.biz.LocalData;

/**
 * Created by Administrator on 2019-07-01
 */
public class SerialPortUtils {
    private final String TAG = "SerialPortUtils";
    private boolean serialPortStatus = false; //是否打开串口标志
    public String data_;
    private boolean threadStatus; //线程状态，为了安全终止线程
    public SerialPort serialPort;
    private InputStream inputStream ;
    private OutputStream outputStream;

    public SerialPortUtils(String spNo, int bauxite){
        String path = "/dev/";
        try {
            String s = "";
            switch (spNo){
                case "1":
                    s="ttyS0";
                    break;

                case "2":
                    s="ttyS1";
                    break;
                case "3":
                    s="ttyS2";
                    break;

                case "4":
                    s="ttyS3";
                    break;
            }

            path+=s;
            if(LocalData.SerialPort.containsKey(spNo)){
                serialPort = LocalData.SerialPort.get(spNo);
            }else {
                serialPort = new SerialPort(new File(path),bauxite,0);
                LocalData.SerialPort.put(spNo,serialPort);
            }
            //获取打开的串口中的输入输出流，以便于串口数据的收发
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "openSerialPort: 打开串口异常：" + e.toString());
        }
    }

    public SerialPortUtils(){

    }

    /**
     * 打开串口
     * @return serialPort串口对象
     */
    public SerialPort openSerialPort(int n, int baudrate){
        String path = "/dev/";
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

    public void closeSerialPort(Boolean disconnect) {
        try {
            inputStream = null;
            outputStream = null;
            serialPort = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    //serialPort发送指令，等待数据返回
    //public RecvDataDto readData(byte[] sendData, String readType) {
   public byte[] readData(byte[] sendData, String readType) {
       byte[] result = new byte[0];
       int conut = 0;
       try {
           Log.e(TAG, "1 ===send data, size:"+sendData.length+";data:"+ChangeTool.ByteArrToHex(sendData));
           outputStream.write(sendData);
           int islet = inputStream.available();
           switch (readType){
               //485and232协议处理方式不同，
               // 485是一次接受所有数据，但会有延时，所以做超时等待
               //232马上会有数据返回，但回传方式为单字节，需要持续接受，值至接受到0d字符
               case "1":
                   //485
                   //等待响应100mills,50ms查询一次结果
                   while (inputStream.available() == 0 && conut <= 20) {
                       //Log.e(TAG, "1 ===wait data, data length is 0,wait mills:"+conut);
                       Thread.sleep(50);
                       conut++;
                   }
                   islet = inputStream.available();
                   result = new byte[islet];
                   inputStream.read(result, 0, islet);
                   break;
               case "2"://ups
                   byte[] tmp = new byte[512];
                   //等待1000ms
                   while (inputStream.available() == 0 && conut <= 20) {
                       //Log.e(TAG, "1 ===wait data ups, data length is 0,wait mills:"+conut);
                       Thread.sleep(50);
                       conut++;
                   }
                   //如果还未收到数据退出
                   if(inputStream.available() == 0) return result;
                   //收到数据长度
                   islet = inputStream.available();
                   //获取数据内容
                   inputStream.read(tmp,0,islet);
                   //如果尾部没有od循环等待数据输入
                   //Log.e(TAG, "length："+islet+"::"+ChangeTool.Byte2Hex(tmp[islet-1]));
                   while (tmp[islet-1]!=(byte)0x0d){
                       //Log.e(TAG, "已收到数据，但数据不齐，等待数据流："+ChangeTool.ByteArrToHex(tmp));
                       //判断是否有新数据
                       if(inputStream.available() == 0) continue;
                       //Log.e(TAG, "重新赋值"+inputStream.available());
                       int i = inputStream.available()+islet;
                       inputStream.read(tmp,islet,i);
                       islet =i;
                       //Log.e(TAG, "length："+islet+"::"+ChangeTool.Byte2Hex(tmp[islet-1]));
                       //Thread.sleep(50);
                       /*if(conut > 20 && inputStream.available() == islet){
                           return result;
                       }else if(conut > 20 && inputStream.available() != 0){
                           conut = 0;
                       }
                       conut++;*/
                   }
                   Log.e(TAG, "已收到全部数据："+ChangeTool.ByteArrToHex(tmp));
                   result = new byte[islet];
                   System.arraycopy(tmp,0,result,0,islet);
                   break;
           }
           Log.d(TAG, "1 ===send data, size:"+sendData.length+";data:"+ChangeTool.ByteArrToHex(sendData)+"2 ===  receive data, size:" + result.length+", "+ChangeTool.ByteArrToHex(result));
       } catch (IOException | InterruptedException e) {
           e.printStackTrace();
       }
       return result;

       /*byte[] buffer = new byte[512];
        byte[] tmp = new byte[64];
        int size;
        int totalCount = 0;
        try {
            Log.e(TAG, "1 ===send data, size:"+sendData.length+";data:"+ChangeTool.ByteArrToHex(sendData));
            //发送指令
            outputStream.write(sendData);
            //判断指令是232还是485
            if(readType.equals(RuleEnum.ReadType.LADSUPS.toString())) {//232
                while(true) {
                    //获取接受的byte字数
                    size = inputStream.read(tmp);
                    //把接受的字符存入buffer
                    System.arraycopy(tmp, 0, buffer, totalCount, size);
                    //递增tc
                    totalCount+=size;
                    //流会一直接受字符，如果接受的字符0d，则接受完毕，退出循环
                    if(tmp[size-1]==(byte)0x0d) break;
                }
            } else if(readType.equals(RuleEnum.ReadType.MODBUS.toString())) {//485
                size = inputStream.read(tmp);
                //485会一次性接受所有字符,
                if(size<3) {
                    Log.d(TAG, "recv data incomplete");
                    return null;
                }
                System.arraycopy(tmp, 0, buffer, totalCount, size);
                //转化为16进制
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
        } catch (Exception e) {
            e.printStackTrace();
        }
       byte[] result = new byte[totalCount];
       System.arraycopy(buffer,0,result,0,totalCount);

       Log.d(TAG, "1 ===send data, size:"+sendData.length+";data:"+ChangeTool.ByteArrToHex(sendData)+"2 ===  receive data, size:" + result.length+", "+ChangeTool.ByteArrToHex(result));
        return result;*/
    }

    public OnDataReceiveListener onDataReceiveListener = null;
    public static interface OnDataReceiveListener {
        public void onDataReceive(byte[] buffer, int size, int ste);
    }
    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }
}
