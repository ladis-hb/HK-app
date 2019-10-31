package lads.dev.biz;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.x6.serial.SerialPort;
import lads.dev.dto.DevUpsDto;
import lads.dev.utils.SerialPortUtils;

/**
 * Created by Administrator on 2019-07-08
 */
public class UpsBizHandler implements Runnable  {
    private static final String TAG = "UpsBizHandler";

    private String devname;
    private SerialPort serialPort;
    //private byte[] buffer;
    private SerialPortUtils serialPortUtils = new SerialPortUtils();

    String devType,phase,workState;
    String inputVoltage,inputFrequency,outputVoltage,outputFrequency,outputCurrent,outputLoadPercent,positiveBusVoltage,negativeBusVoltage,pBatteryVoltage,nBatteryVoltage,maxTemperature,upsState;

//    public void setBuffer(byte[] buffer) {
//        this.buffer = buffer;
//    }

    public void setSerialPortUtils(SerialPortUtils serialPortUtils) {
        this.serialPortUtils = serialPortUtils;
    }

    //UpsDto upsDto = null;
    DevUpsDto dto = null;
    String lastB7Value="";
    String lastB6Value="";
    String lastB5Value="";
    String lastB4Value="";
    String lastB3Value="";
    String lastB2Value="";
    String lastB1Value="";
    String lastB0Value="";
    String lastA0Value="";
    String lastA1Value="";
    SQLiteDatabase db;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public UpsBizHandler() {
        dto = new DevUpsDto();

        serialPortUtils.setOnDataReceiveListener(new SerialPortUtils.OnDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] buffer, int size, int step) {
                String msg = "";

                try {
                    msg = new String(buffer, 0, size, "UTF8");
                    msg = msg.substring(1, msg.length()-1); //clear first character '(' and last character '\r'
                } catch (Exception e) {

                }
                String[] arr=null;
                if(step == 0) { //QPI
                    Log.d(TAG, "step0,"+msg);
                    devType = msg;
                    dto.setQpi(devType);
                } else if(step==1) { //QMD (###########C6KS ###6000 80 1/1 230 230 20 12.0 (TTTTTTTTTTTTTTT WWWWWWW KK P/P MMM NNN RR BB.B <cr>
                    Log.d(TAG, "step1,"+msg);
                    arr = msg.split(" ");
                    dto.setQmd_ww(arr[1].replace("#",""));
                    dto.setQmd_kk(arr[2]);
                    dto.setQmd_pp(arr[3]);
                    dto.setQmd_mm(arr[4]);
                    dto.setQmd_nn(arr[5]);
                    dto.setQmd_rr(arr[6]);
                } else if (step==2) { //QMOD
                    workState = getWorkStateStr(msg);
                    dto.setQmod(workState);
                } else if (step==3) { //QGS (000.0 00.0 219.5 50.0 000.1 000 357.0 357.0 202.3 ---.- 022.6 101000000000 (MMM.M HH.H LLL.L NN.N QQQ.Q DDD KKK.K VVV.V SSS.S XXX.X TTT.T b9b8b7b6b5b4b3b2b1b0<cr>
                    arr = msg.split(" ");
                    dto.setQgs_mm(arr[0]);
                    dto.setQgs_hh(arr[1]);
                    dto.setQgs_ll(arr[2]);
                    dto.setQgs_nn(arr[3]);
                    dto.setQgs_dd(arr[5]);
                    dto.setQgs_ss(arr[8]);
                    dto.setQgs_xx(arr[9]);
                    dto.setQgs_tt(arr[10]);
                    String str = arr[11];
                    try{
                        handleWarn(str);
                    } catch (Exception e) {
                        dto.setTxtError(e.getMessage());
                    }
                } else if(step==4) { //QBV
                    arr = msg.split(" ");
                    dto.setQbv_tt(arr[4]);
                } else if(step==5) { //QRI
                    arr = msg.split(" ");
                    dto.setQri_qq(arr[1]);
                    dto.setQri_rr(arr[3]);
                }

                LocalData.devUpsDto = dto;
                //onChangeViewDataListener.onChangeViewData(dto);
            }
        });
    }

    private void handleWarn(String msg) {
        Date now = new Date();
        String b7Value = msg.substring(2, 2); //judge poweroff
        if(!b7Value.equals(lastB7Value)) { //1=电池模式；0=市电模式
            if(b7Value.equals("1")) { //warn, electricity abnormal
                db.execSQL("insert into warning(dev_type,warn_title,warn_content,create_time) values(?,?,?,?)",
                        new String[]{"UPS","市电异常",devname+"UPS输入市电异常",sdf.format(now)});
            } else if(b7Value.equals("0")) { // warn, ele recover
                db.execSQL("insert into warning(dev_type,warn_title,warn_content,create_time) values(?,?,?,?)",
                        new String[]{"UPS","市电恢复",devname+"UPS输入市电恢复",sdf.format(now)});
            }
            lastB7Value=b7Value;
        }
        String b6Value = msg.substring(3,3);
        if(!b6Value.equals(lastB6Value) && b6Value.equals("1")) {
            db.execSQL("insert into warning(dev_type,warn_title,warn_content,create_time) values(?,?,?,?)",
                    new String[]{"UPS","电压低",devname+"UPS电池电压低",sdf.format(now)});
            lastB6Value=b6Value;
        }
        String b5Value = msg.substring(4,4);
        if(!b5Value.equals(lastB5Value) && b5Value.equals("1")) {
            db.execSQL("insert into warning(dev_type,warn_title,warn_content,create_time) values(?,?,?,?)",
                    new String[]{"UPS","旁路供电",devname+"UPS旁路供电中",sdf.format(now)});
            lastB5Value=b5Value;
        }
        String b4Value = msg.substring(5,5);
        if(!b4Value.equals(lastB4Value) && b4Value.equals("1")) {
            // send QFS
            /*byte[] bytes = new byte[]{0x51, 0x46, 0x53, 0x0d, 0x0a};
            serialPortUtils.sendData(bytes, 4);
            serialPortUtils.recvData();*/
            db.execSQL("insert into warning(dev_type,warn_title,warn_content,create_time) values(?,?,?,?)",
                    new String[]{"UPS","故障",devname+"UPS故障",sdf.format(now)});
            lastB4Value=b4Value;
        }
        
        String b3Value = msg.substring(6,6);
        if(!b3Value.equals(lastB3Value) && b3Value.equals("1")) {
            db.execSQL("insert into warning(dev_type,warn_title,warn_content,create_time) values(?,?,?,?)",
                    new String[]{"UPS","EPO",devname+"UPS紧急关断启用（EPO）",sdf.format(now)});
            lastB3Value=b3Value;
        }

        String b2Value = msg.substring(7,7);
        if(!b2Value.equals(lastB2Value) && b2Value.equals("1")) {
            db.execSQL("insert into warning(dev_type,warn_title,warn_content,create_time) values(?,?,?,?)",
                    new String[]{"UPS","UPS测试中",devname+"UPS测试中",sdf.format(now)});
            lastB2Value=b2Value;
        }

        String b1Value = msg.substring(8,8);
        if(!b1Value.equals(lastB1Value) && b1Value.equals("1")) {
            db.execSQL("insert into warning(dev_type,warn_title,warn_content,create_time) values(?,?,?,?)",
                    new String[]{"UPS","UPS关机中",devname+"UPS关机中",sdf.format(now)});
            lastB1Value=b1Value;
        }

        String b0Value = msg.substring(9,9);
        if(!b0Value.equals(lastB0Value) && b0Value.equals("1")) {
            db.execSQL("insert into warning(dev_type,warn_title,warn_content,create_time) values(?,?,?,?)",
                    new String[]{"UPS","未检测到UPS电池",devname+"未检测到UPS电池",sdf.format(now)});
            lastB0Value=b0Value;
        }

        String a0Value = msg.substring(10,10);
        if(!a0Value.equals(lastA0Value) && a0Value.equals("1")) {
            db.execSQL("insert into warning(dev_type,warn_title,warn_content,create_time) values(?,?,?,?)",
                    new String[]{"UPS","UPS电池测试失败",devname+"UPS电池测试失败",sdf.format(now)});
            lastA0Value=a0Value;
        }

        String a1Value = msg.substring(11,11);
        if(!a1Value.equals(lastA1Value) && a1Value.equals("1")) {
            db.execSQL("insert into warning(dev_type,warn_title,warn_content,create_time) values(?,?,?,?)",
                    new String[]{"UPS","UPS电池测试成功",devname+"UPS电池测试成功",sdf.format(now)});
            lastA1Value=a1Value;
        }
    }

    @Override
    public void run() {
        try {
            serialPort = serialPortUtils.serialPort;
            if(serialPort == null) {
                //serialPort = serialPortUtils.openSerialPort(4);
                if(serialPort == null) {
                    Log.e(TAG, "打开串口失败");
                    onThrowErrorListener.OnThrowError("打开串口失败");
                    return;
                }
            }
        } catch (Exception e) {
            onThrowErrorListener.OnThrowError("打开串口失败，"+e.getMessage());
            return;
        }
        try{
            bizHandle();
        } catch (Exception e) {
            onThrowErrorListener.OnThrowError("业务处理失败，"+e.getMessage());
        }

    }


    private void bizHandle() {
//        byte[] bytes = new byte[]{0x51, 0x50, 0x49, 0x0d, 0x0a}; //QPI
//        serialPortUtils.sendData(bytes, 0);
//        serialPortUtils.recvData();
//        bytes = new byte[]{0x51,0x4d,0x44,0x0d,0x0a}; //QMD
//        serialPortUtils.sendData(bytes, 1);
//        serialPortUtils.recvData();
//        bytes = new byte[]{0x51,0x4d,0x4f,0x44,0x0d,0x0a}; //QMOD
//        serialPortUtils.sendData(bytes, 2);
//        serialPortUtils.recvData();
//        bytes = new byte[]{0x51,0x47,0x53,0x0d,0x0a}; //QGS
//        serialPortUtils.sendData(bytes, 3);
//        serialPortUtils.recvData();
//        bytes = new byte[]{0x51,0x42,0x56,0x0d,0x0a}; //QBV
//        serialPortUtils.sendData(bytes, 4);
//        serialPortUtils.recvData();
//        bytes = new byte[]{0x51,0x52,0x49,0x0d,0x0a}; //QRI
//        serialPortUtils.sendData(bytes, 5);
//        serialPortUtils.recvData();
//        bytes = new byte[]{0x51,0x57,0x53,0x0d,0x0a}; //QWS
//        serialPortUtils.sendData(bytes, 5);
//        serialPortUtils.recvData();
    }

    private String getWorkStateStr(String workState) {
        String strRet;
        switch (workState) {
            case "P":
                strRet="Power on";
                break;
            case "S":
                strRet="Standy";
                break;
            case "Y":
                strRet="Bypass";
                break;
            case "L":
                strRet="Line";
                break;
            case "B":
                strRet="Battery";
                break;
            case "T":
                strRet="Battery test";
                break;
            case "F":
                strRet="Fault";
                break;
            case "E":
                strRet="ECO";
                break;
            case "C":
                strRet="Converter";
                break;
            case "D":
                strRet="Shutdown";
                break;
            default:
                strRet="Unknown";
                break;
        }
        return strRet;
    }

    private String getUpsRunningStatusBeforeFault(String str) {
        StringBuffer sb = new StringBuffer();
        String[] arr = {"DCTODC on", "PFC on", "INVERTER on", "", "input relay on", "O/P relay on","",""};
        for(int i=0; i<8; i++) {
            if(str.charAt(i)=='1') {
                sb.append(arr[i]+",");
            }
        }
        String strRet = sb.toString();
        if(strRet.length() > 0) {
            strRet = strRet.substring(0, strRet.length()-1);
        }
        return strRet;
    }

    private List<String> getWarn(String str) { //param : 0000000000000000000000000000000000000000000000000000000000000000
        List<String> listRet = new ArrayList<>();
        String[] arr = {
                "Battery open","IP N loss","IP site fail","Line phase error","Bypass phase error","Bypass frequency unstable","Battery over charge","Battery low","Overload warning","Fan lock warning",
                "EPO active","Turn on abnormal","Over temperature","Charger fail","Remote shut down","L1 IP fuse fail","L2 IP fuse fail","L3 IP fuse fail","L1 PFC positive error","L1 PFC negative error",
                "L2 PFC positive error","L2 PFC negative error","L3 PFC positive error","L3 PFC negative error","CAN communication error","Synchronization line error","Synchronization pulse error","Host line error","Male connection error","Female connection error",
                "Parallel line connection error","Battery connect different","Line connect different","Bypass connect different","Mode type different","Parallel Capacity setting different","Parallel Auto Start Enable setting different","Parallel Bypass Enable setting different","Parallel Bat Protected Enable setting different","Parallel Bat Open Check Enable setting different",
                "Parallel Bypass Forbidden setting different","Parallel Converter Enable setting different","Parallel Bypass Freq High loss setting different","Parallel Bypass Freq Low loss setting different","Parallel Bypass Volt High loss setting different","Parallel Bypass Volt Low Loss setting different","Parallel Line Freq High Loss setting different","Parallel Line Freq Low Loss setting different","Parallel Line Volt High Loss setting different","Parallel Line Volt Low Loss setting different",
                "Locked in bypass after overload 3 times in 30min","Warning for three-phase AC input current unbalance","Battery fuse broken","Inverter inter-current unbalance","P1 cut off pre-alarm","Warning for Battery replace","Warning for input phase error for LV 6-10K UPS","Cover of maintain switch is open","Phase Auto Adapt Failed","",
                "","EEPROM operation error","",""
        };
        for(int i=0;i<64;i++) {
            char c = str.charAt(i);
            if(c=='1') {
                listRet.add(arr[i]);
            }
        }
        return listRet;
    }

    static Map<String, String> mapFaultKind = new HashMap<>();
    static {
        mapFaultKind.put("01", "Bus start fail");
        mapFaultKind.put("02", "Bus volt over");
        mapFaultKind.put("03", "Bus volt under");
        mapFaultKind.put("04", "Bus volt unbalance");
        mapFaultKind.put("05", "Bus short");
        mapFaultKind.put("06", "PFC over current");

        mapFaultKind.put("11","Inverter soft fail" );
        mapFaultKind.put("12","Inverter volt high" );
        mapFaultKind.put("13","Inverter volt low" );
        mapFaultKind.put("14","L1 inverter short" );
        mapFaultKind.put("15","L2 inverter short" );
        mapFaultKind.put("16","L3 inverter short" );
        mapFaultKind.put("17","L1L2 inverter short" );
        mapFaultKind.put("18","L2L3 inverter short" );
        mapFaultKind.put("19","L3L1 inverter short" );
        mapFaultKind.put("1A","L1 inverter negative power" );
        mapFaultKind.put("1B","L2 inverter negative power" );
        mapFaultKind.put("1C","L3 inverter negative power" );

        mapFaultKind.put("21", "Bat SCR fault");
        mapFaultKind.put("22", "Line SCR fault");
        mapFaultKind.put("23", "Inverter relay open fault");
        mapFaultKind.put("24", "Inverter relay short fault");
        mapFaultKind.put("25", "Wiring fault");
        mapFaultKind.put("26", "Battery reverse fault");
        mapFaultKind.put("27", "Battery too high");
        mapFaultKind.put("28", "Battery too low");
        mapFaultKind.put("29", "Battery Fuse Open-Circuit Fault");

        mapFaultKind.put("31", "CAN communication fault");
        mapFaultKind.put("32", "Host line fault");
        mapFaultKind.put("33", "Synchronization line fault");
        mapFaultKind.put("34", "Synchronization pulse line fault");
        mapFaultKind.put("35", "Parallel communication line loss");
        mapFaultKind.put("36", "Output circuit fault");

        mapFaultKind.put("41", "Over temperature");
        mapFaultKind.put("42", "CPU communication fault");
        mapFaultKind.put("43", "Overload fault");
        mapFaultKind.put("44", "Fan fault");
        mapFaultKind.put("45", "Charger fault");
    }



    public OnChangeViewDataListener onChangeViewDataListener;
    public static interface OnChangeViewDataListener{
        public void onChangeViewData(DevUpsDto upsDto);
    }
    public void setOnChangeViewDataListener(OnChangeViewDataListener onChangeViewDataListener) {
        this.onChangeViewDataListener = onChangeViewDataListener;
    }

    public OnThrowErrorListener onThrowErrorListener;
    public static interface OnThrowErrorListener{
        public void OnThrowError(String str);
    }
    public void setOnThrowErrorListener(OnThrowErrorListener onThrowErrorListener) {
        this.onThrowErrorListener = onThrowErrorListener;
    }

    public String getDevname() {
        return devname;
    }

    public void setDevname(String devname) {
        this.devname = devname;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

}
