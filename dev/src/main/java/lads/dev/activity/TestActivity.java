package lads.dev.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.example.x6.serial.SerialPort;
import lads.dev.R;
import lads.dev.biz.DbDataService;
import lads.dev.biz.LocalData;
import lads.dev.biz.RuleEnum;
import lads.dev.dto.RecvDataDto;
import lads.dev.entity.DevEntity;
import lads.dev.entity.FieldDisplayEntity;
import lads.dev.entity.InstructionEntity;
import lads.dev.entity.ProtocolEntity;
import lads.dev.entity.ResultEntity;
import lads.dev.entity.ViewEntity;
import lads.dev.entity.WarnHisEntity;
import lads.dev.utils.ChangeTool;
import lads.dev.utils.MyDatabaseHelper;
import lads.dev.utils.SerialPortUtils;

public class TestActivity extends AppCompatActivity {

    EditText txtSend,txtRecv,txtBaudrate;
    Button btn1,btn2,btn3,btn4,btnTest2;
    SerialPortUtils serialPortUtils3;
    SerialPort serialPort3;
    MyDatabaseHelper dbHelper;
    DbDataService dataService;


    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    f1();
                    break;
            }
            super.handleMessage(msg);
        }

        void f1() {

            bizHandle();

            //Log.d(TAG, "~~~~~~~~~~~~~~~~~~~~~,"+new Date().toString());
        }
    };

    class c1 implements Runnable{
        private byte[] bytes;
        private String devCode;
        private int step;
        private String readType;
        private String protocol;
        List<InstructionEntity> instructionlist;
        public c1(byte[]bytes,String devCode, int step,String readType, String protocol, List<InstructionEntity> instructionlist){
            this.bytes=bytes;
            this.devCode=devCode;
            this.step=step;
            this.readType=readType;
            this.protocol=protocol;
            this.instructionlist = instructionlist;
        }

        @Override
        public void run(){
            readSp(bytes,devCode,step,readType, protocol, instructionlist);
        }

    }
    private int readSp(byte[] bytes, String devCode, int step, String readType, String protocol, List<InstructionEntity> instructionlist) {
        int ret=0;
        RecvDataDto recvDataDto = new RecvDataDto();
        //ret = serialPortUtils3.readOvertime(bytes, devCode, readType, recvDataDto).getRet();
        handleRecvData(recvDataDto.getBytes(), recvDataDto.getCount(), step, devCode, protocol, instructionlist);
//        serialPortUtils3.sendData(bytes,step,readType);
//        serialPortUtils3.recvDataWithOvertime();
        return ret;
    }

    private String spNo;
    private String spCode;
    private String deviceCode="";
    private String devname="";
    private String devtype="";
    private String readType="";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Map<String, ViewEntity> resultDisplay = new HashMap<>();
    private void bizHandle() {

        //Log.d(TAG, "operating thread running,bizHandle2");
        List<DevEntity> devlist = new ArrayList<>();
        for(DevEntity entity : LocalData.devlist) {
            if(entity.getSpNo().equals("3")) {
                devlist.add(entity);
            }
        }
        //Log.d(TAG,"#####（1)设备数:"+devlist.size());
        // Log.d(TAG, "operating thread running,bizHandle3");
        for(DevEntity entity : devlist) {
            deviceCode = entity.getCode();
            devname = entity.getName();
            devtype = entity.getTypeCode();
            String protocol = entity.getProtocolCode();
            for(ProtocolEntity protocolEntity : LocalData.protocollist) {
                if(protocolEntity.getCode().equals(protocol)) {
                    readType=protocolEntity.getReadType();
                    break;
                }
            }

            List<InstructionEntity> instructionlist = new ArrayList<>();
            for(InstructionEntity a : LocalData.instructionlist) {
                if(a.getProtocolCode().equals(protocol)) {
                    instructionlist.add(a);
                }
            }

            for(InstructionEntity instructionEntity : instructionlist) {
                //Log.d(TAG, "operating thread running,bizHandle6");
                int step = instructionEntity.getSeq();
                String instructionStr = instructionEntity.getStr();
                //Log.d(TAG, "====---===deviceCode:"+deviceCode+",instructionStr:"+instructionStr);
                byte[] bytes = ChangeTool.HexToByteArr(instructionStr);
                try{
                    Thread.sleep(1500);
                } catch (Exception e) {
                    Log.d(TAG,"#################################");
                }
                new Thread(new c1(bytes,deviceCode,step,readType,protocol, instructionlist)).start();
                //readSp(bytes,deviceCode,step,readType,protocol);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        dbHelper = new MyDatabaseHelper(this, 2);
        dataService = new DbDataService(dbHelper.getDb());
        SQLiteDatabase db = dbHelper.getDb();
//        db.execSQL("delete from dev");
//        db.execSQL("insert into dev(type_code,protocol_code,name,code,sp_no,sp_code,seq) values('th','th_ladis_01','th1','mac00103001','3','SerialPort3',1)");
//        db.execSQL("insert into dev(type_code,protocol_code,name,code,sp_no,sp_code,seq) values('th','th_ladis_02','th2','mac00103002','3','SerialPort3',2)");
//        db.execSQL("insert into dev(type_code,protocol_code,name,code,sp_no,sp_code,seq) values('th','th_ladis_03','th3','mac00103003','3','SerialPort3',3)");
//        db.execSQL("insert into dev(type_code,protocol_code,name,code,sp_no,sp_code,seq) values('em','em1_ladis_01','em1','mac00104001','4','SerialPort4',1)");
        dataService.initContextData();


        serialPortUtils3 = new SerialPortUtils();

        serialPort3 = serialPortUtils3.openSerialPort(3, 19200); //test th
        //serialPort3 = serialPortUtils3.openSerialPort(4, 9600); //test em
        serialPortUtils3.setOnDataReceiveListener(new SerialPortUtils.OnDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] buffer, int size, int ste) {
                //Log.d(TAG, "~~~~~~~~~~~~~setOnDataReceiveListener handle recv data,"+new Date().toString());
            }
        });

        bizHandle();

        //timer.schedule(task, 2000, 20000);


    }

    private String TAG="TEST_ACTIVITY";

    private void handleRecvData(byte[] buffer, int size, int step, String devCode, String protocol1, List<InstructionEntity> instructionlist1)  {
        //Log.d(TAG,"####        onDataReceive             （1)设置数据，deviceCode:"+deviceCode);
        resultDisplay = LocalData.devDataMap.get(devCode);
        if(resultDisplay == null) {
            resultDisplay = new HashMap<>();
        }

        InstructionEntity instructionEntity = new InstructionEntity();
        for(InstructionEntity a : instructionlist1) {
            if(a.getProtocolCode().equals(protocol1) && a.getSeq()==step) {
                instructionEntity = a;
                break;
            }
        }
        String instructionCode = instructionEntity.getCode();
        String str = instructionEntity.getStr();
        String rule = instructionEntity.getRule();
        String encoding = instructionEntity.getEncoding();
        String[] arr = rule.split("\\|");

        Map<String, String> resultMap = new HashMap<>();

        List<ResultEntity> resultItemList = new ArrayList<>();
        for(ResultEntity a : LocalData.resultlist) {
            if(a.getInstructionCode().equals(instructionCode)) {
                resultItemList.add(a);
            }
        }

        Map<String, String> map = new HashMap<>();
        for(String s : arr) {
            String[] arr2 = s.split("=");
            map.put(arr2[0],arr2[1]);
        }
        String resultType = map.get("1");
        if(resultType.equals(RuleEnum.RuleStep1.ASCII.toString())) {
            try {
                handleData_str(instructionCode, arr, rule, resultItemList, buffer, size, encoding, resultMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(resultType.equals(RuleEnum.RuleStep1.NUMBER.toString())) {
            //String[] arr2 = arr[1].split("_");
            String trimConfigStr = arr[1];
            String[] arr3 = trimConfigStr.split("=")[1].split("_");
            int leftTrim = Integer.parseInt(arr3[0].substring(4));
            int rightTrim = Integer.parseInt(arr3[1].substring(5));
            //int leftTrim = Integer.parseInt(arr2[0].substring(4));
            byte[] bytes = new byte[size-leftTrim-rightTrim];
            System.arraycopy(buffer, leftTrim, bytes, 0, size-leftTrim-rightTrim);
            try {
                handleData_number(resultItemList, bytes, resultMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(resultType.equals(RuleEnum.RuleStep1.MIX.toString())) {

        }

        List<FieldDisplayEntity> fieldDisplayList = new ArrayList<>();
        for(FieldDisplayEntity a: LocalData.fieldDisplaylist) {
            if(a.getProtocolCode().equals(protocol1)) {
                fieldDisplayList.add(a);
            }
        }

        StringBuffer sb = new StringBuffer();
        for(FieldDisplayEntity e : fieldDisplayList) {
            String fieldName = e.getFieldName();
            String displayName = e.getDisplayName();
            int columnIndex = e.getColumnIndex();
            String fieldValue = resultMap.get(fieldName);
            sb.append(displayName+"："+fieldValue+"，");

            if(fieldValue != null) {
                ViewEntity viewEntity = new ViewEntity(fieldValue, columnIndex);
                resultDisplay.put(displayName, viewEntity);
            }
        }
        //Log.d(TAG,"####（2)设置数据，devCode:"+devCode);
        LocalData.devDataMap.put(devCode, resultDisplay);
        Log.d(TAG, "+++++++"+devCode+"："+sb.toString());
    }

    private void handleData_number(List<ResultEntity> resultItemList, byte[] bytes, Map<String, String> resultMap) throws Exception {
        Date now = new Date();
        for(int i=0;i<resultItemList.size(); i++) {
            ResultEntity resultItemEntity = resultItemList.get(i);
            String pFieldName = resultItemEntity.getFieldName();
            String pDisplayName = resultItemEntity.getDisplayName();
            String pPrefix = resultItemEntity.getPrefix();
            pPrefix=pPrefix==null?"":pPrefix;
            String pSuffix = resultItemEntity.getSuffix();
            pSuffix=pSuffix==null?"":pSuffix;
            int pStartAddr = resultItemEntity.getStartAddr();
            int pLen = resultItemEntity.getLen();
            int pRatio = resultItemEntity.getRatio();
            String pDataType = resultItemEntity.getDataType();
            String pDataType2 = resultItemEntity.getDataType2();
            String pWarnType = resultItemEntity.getWarnType();

            float fVal=0f;
            String pVal="";
            if(pDataType2.equals(RuleEnum.DataType2.INT.toString())) {
                if(pLen != 4) {
                    throw new Exception("int length configuration wrong for "+pFieldName);
                }
                int iVal = ChangeTool.bytesToInt(bytes, pStartAddr);
                fVal = ((float)iVal)/pRatio;
                pVal = pPrefix + String.valueOf(fVal) + pSuffix;
            } else if (pDataType2.equals(RuleEnum.DataType2.SHORT.toString())) {
                if(pLen != 2) {
                    throw new Exception("short length configuration wrong for "+pFieldName);
                }
                int iVal = ChangeTool.bytesToShort(bytes, pStartAddr);
                fVal = ((float)iVal)/pRatio;
                pVal = pPrefix + String.valueOf(fVal) + pSuffix;
            } else if (pDataType2.equals(RuleEnum.DataType2.USHORT.toString())) {
                if(pLen != 2) {
                    throw new Exception("unsigned short length configuration wrong for "+pFieldName);
                }
                int iVal = ChangeTool.bytesToUnsignedShort(bytes, pStartAddr);
                fVal = ((float)iVal)/pRatio;
                pVal = pPrefix + String.valueOf(fVal) + pSuffix;
            } else if (pDataType2.equals(RuleEnum.DataType2.FLOAT.toString())) {
                if(pLen != 4) {
                    throw new Exception("float length configuration wrong for "+pFieldName);
                }
                fVal = ChangeTool.bytesToInt(bytes, pStartAddr);
                pVal = pPrefix + String.valueOf(fVal) + pSuffix;
            }
            resultMap.put(pFieldName, pVal);

            //warn
            if (pWarnType.equals(RuleEnum.WarnType.UPPER_LOWER_LIMITS.toString())) {
                float upperLimit = Float.parseFloat(resultItemEntity.getUpperLimit());
                float lowerLimit = Float.parseFloat(resultItemEntity.getLowerLimit());
                String warnDesc = "";
                if(fVal<lowerLimit || fVal>upperLimit) {
                    now = new Date();
                    if(fVal<lowerLimit) {
                        warnDesc = pFieldName+" above upper limit";
                    } else if(fVal>upperLimit) {
                        warnDesc = pFieldName+" below lower limit";
                    }
                    WarnHisEntity warnHisEntity = new WarnHisEntity();
                    warnHisEntity.setWarnTitle(warnDesc);
                    warnHisEntity.setWarnContent("Device name: "+devname+", "+warnDesc+" "+sdf.format(now));
                    warnHisEntity.setDevCode(deviceCode);
                    warnHisEntity.setDevName(devname);
                    warnHisEntity.setDevType(devtype);
                    warnHisEntity.setCreateTime(now);
                    dataService.addWarn(warnHisEntity);
                }
            }
        }
    }

    private void handleData_str(String instructionCode, String[] arr, String rule, List<ResultEntity> resultItemList, byte[] bytes, int size, String encoding, Map<String, String> resultMap) throws Exception {
        String[] arr3;
        // 1.get string
        if(arr.length != 3) {
            throw new Exception("format error: "+ rule);
        }
        String resultStr = "";
        try {
            resultStr = new String(bytes, 0,  size, encoding);
        } catch (Exception e) {
            throw new Exception("new string error: ");
        }
        // 2.trim string, config str like "left1_right0"
        String trimConfigStr = arr[1];
        arr3 = trimConfigStr.split("=")[1].split("_");
        int leftTrim = Integer.parseInt(arr3[0].substring(4));
        int rightTrim = Integer.parseInt(arr3[1].substring(5));
        resultStr = resultStr.substring(leftTrim, resultStr.length()-rightTrim);

        // 3.1 split string and get result.
        String splitType = arr[2].split("=")[1];

        if(splitType.equals(RuleEnum.RuleStep3.LEN.toString())) {
            handleData_str_len(instructionCode, resultStr, resultItemList, resultMap);
        } else if(splitType.contains(RuleEnum.RuleStep3.SPLIT.toString())) {
            String splitSymbol = "";
            arr3 = splitType.split("_");
            for(RuleEnum.RuleStep3SplitSymbol e : RuleEnum.RuleStep3SplitSymbol.values()) {
                if (e.getName().equals(arr3[1])) {
                    splitSymbol = e.getValue();
                    break;
                }
            }
            String[] arrResult = resultStr.split(splitSymbol);
            if(resultItemList.size() != arrResult.length) {
                //throw new Exception("result item count unequals to result length");
                Log.d(TAG, "result item count unequals to result length");
                return;
            }
            //load result data
            for(int i=0; i<resultItemList.size(); i++) {

                String fName = resultItemList.get(i).getFieldName();
                String fValue = arrResult[i];
                resultMap.put(fName, fValue);
                ResultEntity e = resultItemList.get(i);
                // *** handle parseable field value
                if(e.getParseFlag().equals(RuleEnum.ParseFlag.PARSE.toString())) {
                    String parseStr = e.getParseStr();
                    String[] arrtmp = parseStr.split(";");
                    Map<String, String> maptmp = new HashMap<>();
                    for(String s : arrtmp) {
                        String[] arrtmp2 = s.split("=");
                        maptmp.put(arrtmp2[0], arrtmp2[1]);
                    }
                    String fValueDesc = maptmp.get(fValue);
                    if(fValueDesc != null) {
                        resultMap.put(fName, fValueDesc);
                    }
                }
            }
        }
    }

    private void handleData_str_len(String instructionCode, String resultStr, List<ResultEntity> resultItemList, Map<String, String> resultMap) {
        for(int i=0;i<resultItemList.size();i++) {
            ResultEntity resultItemEntity = resultItemList.get(i);
            String pFieldName = resultItemEntity.getFieldName();
            String pDisplayName = resultItemEntity.getDisplayName();
            String pPrefix = resultItemEntity.getPrefix();
            String pSuffix = resultItemEntity.getSuffix();
            int pStartAddr = resultItemEntity.getStartAddr();
            int pLen = resultItemEntity.getLen();
            String pValue = resultStr.substring(pStartAddr, pStartAddr+pLen);
            resultMap.put(pFieldName, pValue);
            if(resultItemEntity.getParseFlag().equals(RuleEnum.ParseFlag.PARSE.toString())) {
                String parseStr = resultItemEntity.getParseStr();
                String[] arrtmp = parseStr.split(";");
                Map<String, String> maptmp = new HashMap<>();
                for(String s : arrtmp) {
                    String[] arrtmp2 = s.split("=");
                    maptmp.put(arrtmp2[0], arrtmp2[1]);
                }
                String fValueDesc = maptmp.get(pValue);
                if(fValueDesc != null) {
                    resultMap.put(pFieldName, fValueDesc);
                }
            }

            //handleWarn_str(instructionCode, resultItemEntity, pValue);
        }
    }

}
