package lads.dev.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.SerialPort;
import lads.dev.dto.DevUpsDto;
import lads.dev.dto.RecvDataDto;
import lads.dev.entity.DevEntity;
import lads.dev.entity.DevOptHisEntity;
import lads.dev.entity.FieldDisplayEntity;
import lads.dev.entity.InstructionEntity;
import lads.dev.entity.LastWarnValueEntity;
import lads.dev.entity.ProtocolEntity;
import lads.dev.entity.ResultEntity;
import lads.dev.entity.SpEntity;
import lads.dev.entity.ViewEntity;
import lads.dev.entity.WarnCfgEntity;
import lads.dev.entity.WarnHisEntity;
import lads.dev.utils.ChangeTool;
import lads.dev.utils.MyDatabaseHelper;
import lads.dev.utils.MyUtil;
import lads.dev.utils.SerialPortUtils;

/**
 * Created by Administrator on 2019-07-08
 */
public class DevBizHandler implements Runnable  {
    private static final String TAG = "DevBizHandler";


    //private SerialPort serialPort;
    private SerialPortUtils serialPortUtils = new SerialPortUtils();
    //存放返回数据
    Map<String, ViewEntity> resultDisplay = new HashMap<>();
    //错误列表
    List<LastWarnValueEntity> lastWarnList = new ArrayList<>();
    //
    MyDatabaseHelper dbHelper;
    DbDataService dataService;
    //DevSettingFragment实例
    Context mContext;
    //
    List<LastWarnValueEntity> lastWarnValueList;
    //
    List<DevOptHisEntity> devOptList = new ArrayList<>();
    //每个查询线程等待秒数，查询过快容易丢失返回数据
    int handle_wait_slim = 1500;

    /**
     * synchronize timer reading task and device operating task
     * 0=initial,1=timer task,2=operating task
     */
    private int flagTimerRead = 0;
    //flagSpRun true运行串口
    private boolean flagSpRun = false; //serialport service running flag

    public DevBizHandler(Context ctx, SerialPortUtils serialPortUtils, String spNo) {
        this.serialPortUtils = serialPortUtils;
        this.spNo = spNo;
        this.mContext = ctx;
        dbHelper = new MyDatabaseHelper(ctx, 2);
        dataService = new DbDataService(dbHelper.getDb());
        spCode = LocalData.Cache_splist.get(spNo).getCode();
/*
        List<SpEntity> spEntityList = LocalData.splist;
        for(SpEntity spEntity : spEntityList) {
            if(String.valueOf(spEntity.getSeq()).equals(spNo)) {
                spCode = spEntity.getCode();
                break;
            }
        }*/

        /*serialPortUtils.setOnDataReceiveListener(new SerialPortUtils.OnDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] buffer, int size, int step) {
                Log.d(TAG,"####        onDataReceive             （1)设置数据，deviceCode:"+deviceCode);
                //Log.d(TAG, "~~~~~~~~~~~~~ handle recv data,"+new Date().toString());
                String protocol="";
                List<InstructionEntity> instructionlist = new ArrayList<>();

                resultDisplay = LocalData.devDataMap.get(deviceCode);
                if(resultDisplay == null) {
                    resultDisplay = new HashMap<>();
                }

                //InstructionEntity instructionEntity = instructionlist.stream().filter(a->a.getProtocolCode().equals(protocol) && a.getSeq()==step).collect(Collectors.toList()).get(0);
                InstructionEntity instructionEntity = new InstructionEntity();
                for(InstructionEntity a : instructionlist) {
                    if(a.getProtocolCode().equals(protocol) && a.getSeq()==step) {
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

                //List<ResultEntity> resultItemList = LocalData.resultlist.stream().filter(a->a.getInstructionCode().equals(instructionCode)).sorted(Comparator.comparing(ResultEntity::getSeq)).collect(Collectors.toList());
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
                        //onThrowErrorListener.OnThrowError(e.getMessage());
                        Log.d(TAG, "error"+e.getMessage());
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
                        onThrowErrorListener.OnThrowError(e.getMessage());
                    }
                } else if(resultType.equals(RuleEnum.RuleStep1.MIX.toString())) {

                }

                //List<FieldDisplayEntity> fieldDisplayList = LocalData.fieldDisplaylist.stream().filter(a->a.getProtocolCode().equals(protocol)).sorted(Comparator.comparing(FieldDisplayEntity::getSeq)).collect(Collectors.toList());
                List<FieldDisplayEntity> fieldDisplayList = new ArrayList<>();
                for(FieldDisplayEntity a: LocalData.fieldDisplaylist) {
                    if(a.getProtocolCode().equals(protocol)) {
                        fieldDisplayList.add(a);
                    }
                }

                for(FieldDisplayEntity e : fieldDisplayList) {
                    String fieldName = e.getFieldName();
                    String displayName = e.getDisplayName();
                    int columnIndex = e.getColumnIndex();
                    String fieldValue = resultMap.get(fieldName);

                    if(fieldValue != null) {
                        ViewEntity viewEntity = new ViewEntity(fieldValue, columnIndex);
                        resultDisplay.put(displayName, viewEntity);
                    }
                }
                String ids = deviceCode;
                Log.d(TAG,"####（2)设置数据，deviceCode:"+deviceCode);
                LocalData.devDataMap.put(deviceCode, resultDisplay);

                //Toast.makeText(mContext, fieldDisplayList.size()+"", Toast.LENGTH_SHORT).show();

                //save data history, send data to http server
                //move to timer
//                try {
//                    DataHisEntity dataHisEntity = new DataHisEntity();
//                    dataHisEntity.setDevCode(deviceCode);
//                    dataHisEntity.setDevName(devname);
//                    dataHisEntity.setSpCode(spCode);
//                    dataHisEntity.setCreateTime(new Date());
//                    dataService.addDataHis(dataHisEntity);
//
//                    JSONObject json = new JSONObject();
//                    for(String key : resultDisplay.keySet()) {
//                        ViewEntity entity = resultDisplay.get(key);
//                        json.put(key, entity.getValue());
//                    }
//                    HttpUtil.httpPost(LocalData.url, json.toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }
        });*/
    }

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            if(flagSpRun) {
                bizHandle();
                Log.d(TAG, spNo+", ~~~~~~~~~~~~~~~~~~~~~,"+new Date().toString());
            }
            //Message message = new Message();
            //message.what = 1;
            //handler.sendMessage(message);
        }
    };
    /*Handler handler = new Handler() {
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
            if(flagSpRun) {
                bizHandle();

//                String qmd = "514d440d0a";
//                byte[] bytes = ChangeTool.HexToByteArr(qmd);
//                new Thread(new c1(bytes,2,"2")).start();

                Log.d(TAG, spNo+", ~~~~~~~~~~~~~~~~~~~~~,"+new Date().toString());
            }
        }
    };*/

 /*   public void setSerialPortUtils(SerialPortUtils serialPortUtils) {
        this.serialPortUtils = serialPortUtils;
    }*/
    @Override
    public void run() {
        try{
            timer.schedule(task, 1000, 30000);
        } catch (Exception e) {
            onThrowErrorListener.OnThrowError("biz error，"+e.getMessage());
        }
    }

    public void addDevOpt(DevOptHisEntity devOptHisEntity) {
        devOptList.add(devOptHisEntity);

        if(flagTimerRead == 0) {
            flagTimerRead=2;
            int count = devOptList.size();
            for(int i=count-1;i>0;i--) {
                DevOptHisEntity e = devOptList.get(i);
                String devCode = devOptHisEntity.getDevCode();
                String protocolCode="";
                String readType="";
                for(DevEntity devEntity : LocalData.devlist) {
                    if(devEntity.getCode().equals(devCode)) {
                        protocolCode=devEntity.getProtocolCode();
                        break;
                    }
                }
                if(MyUtil.isStringEmpty(protocolCode)) {
                    return;
                }

                List<InstructionEntity> instructionlist = new ArrayList<>();
                for(InstructionEntity a : LocalData.instructionlist) {
                    if(a.getProtocolCode().equals(protocolCode)) {
                        instructionlist.add(a);
                    }
                }
                for(ProtocolEntity protocolEntity : LocalData.protocollist) {
                    if(protocolEntity.getCode().equals(protocolCode)) {
                        readType = protocolEntity.getReadType();
                        break;
                    }
                }
                if(MyUtil.isStringEmpty(readType)) {
                    return;
                }
                String msg = e.getOptValue();
                byte[] bytes = ChangeTool.HexToByteArr(msg);
                int ret = readSp(bytes, devCode,99,readType,protocolCode,instructionlist); //define opt 99
                if(ret == -1) {
                    break;
                }
                devOptList.remove(i); // remove command
            }
            flagTimerRead=0;
        }
    }
    //串口
    private String spNo;
    //SerialPort*
    private String spCode;
    //设备编码
    private String deviceCode="";
    //设备名
    private String devname="";
    //设备类型
    private String devtype="";
    //设备协议
//    private String protocol;
    //
    private String readType="";
    //格式化字符串
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //
    private void bizHandle() {
        if(flagTimerRead != 0) {
            try {
                //Log.d(TAG, "operating thread running, waiting ...");
                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //Log.d(TAG, "operating thread running,bizHandle2");
        flagTimerRead=1;
        //List<DevEntity> devlist = dataService.getDev(spNo);

        /*List<DevEntity> devlist = new ArrayList<>();

        for(DevEntity entity : LocalData.devlist) {
            if(entity.getSpNo().equals(spNo)) {
                devlist.add(entity);
            }
        }*/
        //如果串口没有设备直接退出
        if(!LocalData.Cache_devlist.containsKey(spNo)) return;
        List<DevEntity> devlist = LocalData.Cache_devlist.get(spNo);
        Log.d(TAG,devlist.get(0).getTypeCode()+"#####（1)设备数:"+devlist.size());

        //进入第一个循环，迭代一个串口上挂载的所有设备
        for(DevEntity entity : devlist) {
            //mac00100101
            deviceCode = entity.getCode();
            //ups1
            devname = entity.getName();
            //ups
            devtype = entity.getTypeCode();
            String protocol = entity.getProtocolCode();
            //Log.d(TAG, "operating thread running,bizHandle31");
            /*for(ProtocolEntity protocolEntity : LocalData.protocollist) {
                if(protocolEntity.getCode().equals(protocol)) {
                    readType=protocolEntity.getReadType();
                    break;
                }
            }*/
            //判断协议是否正确，可以省略，获取协议读取类型
            if(LocalData.Cache_protocollist.containsKey(protocol)){
                readType=LocalData.Cache_protocollist.get(protocol).getReadType();
            }

            /*List<InstructionEntity> instructionlist = new ArrayList<>();
            for(InstructionEntity a : LocalData.instructionlist) {
                if(a.getProtocolCode().equals(protocol)) {
                    instructionlist.add(a);
                }
            }*/
            //获取协议指令集
            List<InstructionEntity> instructionlist = LocalData.Cache_instructionlist.get(protocol);

            //进入第二个循环，迭代协议指令集，每条指令集发送查询
            for(InstructionEntity instructionEntity : instructionlist) {
                //执行指令前等待，执行过快会丢失数据
                try{
                    Thread.sleep(handle_wait_slim);
                } catch (Exception e) {
                    Log.d(TAG,"handle_wait_slim error#################################");
                }
                //一般不会进入这个if
                if(devOptList.size()>0) { //handle instant device setting
                    int count = devOptList.size();
                    for(int i=count-1;i>0;i--) {
                        DevOptHisEntity e = devOptList.get(i);
                        String msg = e.getOptValue();
                        byte[] bytes = ChangeTool.HexToByteArr(msg);
                        int ret = readSp(bytes,deviceCode, 99,readType, protocol, instructionlist); //define opt 99
                        if(ret == -1) {
                            break;
                        }
                        devOptList.remove(i); // remove command
                    }
                }
                //获取指令集步进，ups1-6，其它都是1
                int step = instructionEntity.getSeq();
                //获取指令数据字符串
                String instructionStr = instructionEntity.getStr();
                Log.d(TAG, "====---===deviceCode:"+deviceCode+",instructionStr:"+instructionStr);
                //格式化指令为16进制
                byte[] bytes = ChangeTool.HexToByteArr(instructionStr);
                //优化流程，直接传递deventity
                new Thread(new c1(bytes,deviceCode,step,readType,protocol, instructionlist)).start();
            }
        }
        flagTimerRead = 0;
    }



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
            //Log.e(TAG,deviceCode+"read Data send --------------------------"+sdf.format(new Date()));
        }

    }
    private int readSp(byte[] bytes, String devCode, int step, String readType, String protocol, List<InstructionEntity> instructionlist) {

        int ret=0;
        RecvDataDto recvDataDto = new RecvDataDto();
        ret = serialPortUtils.readOvertime(bytes, devCode, readType, recvDataDto);
        if(ret == -1) {
            Log.d(TAG, devname+" offline");
            //remove offline device
           for(DevEntity e : LocalData.devlist) {
                if(e.getCode().equals(deviceCode)) {
                    e.setLostTimes(e.getLostTimes()+1);
                    if(e.getLostTimes()>=5) { //5次连接不上就判断设备掉线
                        LocalData.devlist.remove(e);
                        //循环遍历cache，删除dev
                        /*for(String brand:LocalData.Cache_devlist.keySet()){
                            for (DevEntity devEntity:LocalData.Cache_devlist.get(brand)){
                                if(devEntity.getCode().equals(deviceCode)){
                                    LocalData.Cache_devlist.get(brand).remove(devEntity);
                                    break;
                                }
                            }
                        }*/
                        LocalData.Cache_devlist.get(spNo).remove(e);
                        //设备掉线告警
                        WarnHisEntity warnHisEntity = new WarnHisEntity();
                        warnHisEntity.setCreateTime(new Date());
                        warnHisEntity.setDevCode(e.getCode());
                        warnHisEntity.setDevName(e.getName());
                        warnHisEntity.setDevType(e.getTypeCode());
                        warnHisEntity.setWarnTitle("设备掉线");
                        warnHisEntity.setWarnContent("设备掉线");
                        dataService.addWarn_DeviceOffline(warnHisEntity);
                    }

                    break;
                }
            }
        } else {
            for(DevEntity e : LocalData.devlist) {
                if(e.getCode().equals(devCode)) {
                    e.setLostTimes(0);
                    break;
                }
            }
            /*for(DevEntity e : LocalData.Cache_devlist.get(spNo)) {
                if(e.getCode().equals(devCode)) {
                    e.setLostTimes(0);
                    break;
                }
            }*/

            //Log.e(TAG,deviceCode+"read Data recd --------------------------"+sdf.format(new Date()));
            handleRecvData(recvDataDto.getBytes(), recvDataDto.getCount(), step, devCode, protocol, instructionlist);
        }
        //Log.d(TAG, "operating thread running,readSp"+ret);
        return ret;
    }

    private void handleRecvData(byte[] buffer, int size, int step, String devCode, String protocol1, List<InstructionEntity> instructionlist1)  {
        //Log.d(TAG,"####        onDataReceive             （1)设置数据，deviceCode:"+deviceCode);
        //判断devMap有devcode，没有就新建
        resultDisplay = LocalData.devDataMap.get(devCode);
        if(resultDisplay == null) {
            resultDisplay = new HashMap<>();
        }
        //迭代指令集，
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

        /*List<ResultEntity> resultItemList = new ArrayList<>();
        for(ResultEntity a : LocalData.resultlist) {
            if(a.getInstructionCode().equals(instructionCode)) {
                resultItemList.add(a);
            }
        }*/
        List<ResultEntity> resultItemList = LocalData.Cache_resultlist.get(instructionCode);
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
        //Log.e(TAG,devCode+"read Data end --------------------------"+sdf.format(new Date()));
    }


    //SQLiteDatabase db;



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
                // handle warn data
                if(e.getWarnType().equals(RuleEnum.WarnType.UPPER_LOWER_LIMITS.toString()) || e.getWarnType().equals(RuleEnum.WarnType.WARN_CONFIG.toString())) {
                    handleWarn_str(instructionCode, e, arrResult[i]);
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

            handleWarn_str(instructionCode, resultItemEntity, pValue);
        }
    }

    private void handleWarn_str(String instructionCode, ResultEntity e, String fieldValue) {
        Map<String, String> map = new HashMap<>();
        String[] arr;
        Date now = new Date();
        String fieldName = e.getFieldName();
        String warnType = e.getWarnType();
        String warnDesc = "";

        WarnHisEntity warnHisEntity = new WarnHisEntity();

        if(warnType.equals(RuleEnum.WarnType.UPPER_LOWER_LIMITS.toString())) {
            String lowerLimit = e.getLowerLimit();
            String upperLimit = e.getUpperLimit();
            float fLower = Float.parseFloat(lowerLimit);
            float fUpper = Float.parseFloat(upperLimit);
            float fValue = Float.parseFloat(fieldValue);
            if(fValue<fLower || fValue>fUpper) {
                if(fValue<fLower) {
                    warnDesc = fieldName+" above upper limit";
                } else if(fValue>fUpper) {
                    warnDesc = fieldName+" below lower limit";
                }
                warnHisEntity.setWarnTitle(warnDesc);
                warnHisEntity.setWarnContent("Device name: "+devname+", "+warnDesc+" "+sdf.format(now));
                warnHisEntity.setDevCode(deviceCode);
                warnHisEntity.setDevName(devname);
                warnHisEntity.setDevType(devtype);
                warnHisEntity.setCreateTime(now);
                dataService.addWarn(warnHisEntity);
            }
        } else if (warnType.equals(RuleEnum.WarnType.WARN_CONFIG.toString())) {
            //List<WarnCfgEntity> warnCfgList = LocalData.warnCfglist.stream().filter(a->a.getInstructionCode().equals(instructionCode) && a.getFieldName().equals(fieldName)).sorted(Comparator.comparing(WarnCfgEntity::getSeq)).collect(Collectors.toList());
            List<WarnCfgEntity> warnCfgList = new ArrayList<>();
            for(WarnCfgEntity a : LocalData.warnCfglist) {
                if(a.getInstructionCode().equals(instructionCode) && a.getFieldName().equals(fieldName)) {
                    warnCfgList.add(a);
                }
            }
            for(WarnCfgEntity warnCfgEntity : warnCfgList) {
                int startAddr = warnCfgEntity.getStartAddr();
                int len = warnCfgEntity.getLen();
                int seq = warnCfgEntity.getSeq();
                String warnEnum = warnCfgEntity.getWarnEnum();
                String warnStr = fieldValue.substring(startAddr, startAddr+len);
                //lastWarnValueList = lastWarnList.stream().filter(a->a.getInstructionCode().equals(instructionCode) && a.getFieldName().equals(fieldName) && a.getSeq()==seq).collect(Collectors.toList());
                lastWarnValueList = new ArrayList<>();
                for(LastWarnValueEntity a : lastWarnList) {
                    if(a.getInstructionCode().equals(instructionCode) && a.getFieldName().equals(fieldName) && a.getSeq()==seq) {
                        lastWarnValueList.add(a);
                    }
                }
                if(lastWarnValueList.size() == 0) {
                    LastWarnValueEntity lastWarnValueEntity = new LastWarnValueEntity();
                    lastWarnValueEntity.setInstructionCode(instructionCode);
                    lastWarnValueEntity.setFieldName(fieldName);
                    lastWarnValueEntity.setSeq(seq);
                    lastWarnValueEntity.setValue(warnStr);
                    lastWarnValueList.add(lastWarnValueEntity);
                } else {
                    if(!lastWarnValueList.get(0).getValue().equals(warnStr)) { // warn if not equal to old value
                        lastWarnValueList.get(0).setValue(warnStr);
                        arr = warnEnum.split(";");
                        for(String s : arr) {
                            String[] arr2 = s.split("=");
                            map.put(arr2[0], arr2[1]);
                        }
                        warnDesc = map.get(warnStr); // warning desc
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

    public boolean isFlagSpRun() {
        return flagSpRun;
    }

    public void setFlagSpRun(boolean flagSpRun) {
        this.flagSpRun = flagSpRun;
    }
}
