package lads.dev.biz;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lads.dev.dto.RecvDataDto;
import lads.dev.entity.BroadcastArguments;
import lads.dev.entity.DevEntity;
import lads.dev.entity.FieldDisplayEntity;
import lads.dev.entity.InstructionEntity;
import lads.dev.entity.LastWarnValueEntity;
import lads.dev.entity.ResultEntity;
import lads.dev.entity.SpEntity;
import lads.dev.entity.ViewEntity;
import lads.dev.entity.WarnCfgEntity;
import lads.dev.entity.WarnHisEntity;
import lads.dev.utils.ChangeTool;
import lads.dev.utils.MyApplication;
import lads.dev.utils.MyBroadCast;
import lads.dev.utils.MyDatabaseHelper;
import lads.dev.utils.SerialPortUtils;

public class DevQueryOprate {
    private static final String TAG = "DevQueryOprate";
    //存放返回数据
    private Map<String, ViewEntity> resultDisplay = new HashMap<>();
    //错误列表
    private List<LastWarnValueEntity> lastWarnList = new ArrayList<>();
    //
    private MyDatabaseHelper dbHelper;
    private DbDataService dataService;
    //DevSettingFragment实例
    private Context mContext;
    //
    private List<LastWarnValueEntity> lastWarnValueList;
    //格式化字符串
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //缓存

    private static int state = 0;

    public DevQueryOprate(){
        this.mContext = MyApplication.getContext();
        this.dbHelper = new MyDatabaseHelper(this.mContext, 2);
        this.dataService = new DbDataService(dbHelper.getDb());
    }

    public void StartQuery(){
        Log.e(TAG,"开始迭代设备，查询参数");
        for(SpEntity spEntity:LocalData.splist){
            if(spEntity != null && spEntity.getState() == 1){
                Log.e(TAG,"打开的串口："+spEntity.getCode());
                bizHandle(String.valueOf(spEntity.getSeq()));
            }
        }
    }

    private void bizHandle(String spNo) {
        //判定串口是否被使用，使用状态则跳过查询
        if(LocalData.Cache_Open_SpNo.contains(spNo)){
            Log.e(TAG,spNo+"串口还未被释放，将跳过本查询");
            return;
        }
        //如果串口没有设备直接退出
        if(!LocalData.Cache_devlist.containsKey(spNo)) return;
        //获取设备列表，不能直接使用LocalData.Cache_devlist.get(spNo).keySet()赋值，会因为删除设备导致引用失效
        Set<String> devlist = new HashSet<>(LocalData.Cache_devlist.get(spNo).keySet());
        //如果没有设备退出
        if(devlist.size() == 0) return;
        Log.d(TAG,"串口:"+spNo+"#####（1)设备数:"+devlist.size());
        //把串口标示为占用
        LocalData.Cache_Open_SpNo.add(spNo);
        //初始化SerialPort
        SerialPortUtils serialPortUtils = new SerialPortUtils(spNo,LocalData.Cache_splist.get(spNo).getBaudrate());
        //进入第一个循环，迭代一个串口上挂载的所有设备
        for(String devcode:devlist) {
            DevEntity entity = LocalData.Cache_devlist.get(spNo).get(devcode);
            List<InstructionEntity> instructionlist = LocalData.Cache_instructionlist.get(entity.getProtocolCode());
            Log.d(TAG,"设备名称："+entity.getName()+"设备id:"+entity.getCode()+"#####（1)指令集条数:"+instructionlist.size());
            //进入第二个循环，迭代协议指令集，每条指令集发送查询
            for(InstructionEntity instructionEntity : instructionlist) {
                //优化流程，直接传递deventity
                readSp(instructionEntity,entity,spNo,serialPortUtils);
                //Log.d(TAG,"设备名称："+entity.getName()+"设备id:"+entity.getCode()+"#####（1)指令集:"+instructionEntity.getCode());
            }
        }
        //close SerialPort
        serialPortUtils.closeSerialPort(true);
        //串口使用完毕，清除标示
        LocalData.Cache_Open_SpNo.remove(spNo);
    }
    //发送查询指令
    private Boolean readSp(InstructionEntity instructionEntity,DevEntity entity,String spNo,SerialPortUtils serialPortUtils) {
        //获取指令数据字符串
        String instructionStr = instructionEntity.getStr();
        //格式化指令为16进制
        byte[] bytes= ChangeTool.HexToByteArr(instructionStr);
        //获取协议号
        String protocol=entity.getProtocolCode();
        //获取设备id
        String devCode=entity.getCode();
        //获取指令读取类型，ups2，其余1
        String readType=LocalData.Cache_protocollist.get(protocol).getReadType();
        //申明RecvDataDto
        RecvDataDto result = new RecvDataDto();
        //
        byte[] r = serialPortUtils.readData(bytes, readType);
        if(r.length <1){
            result.setRet(-1);
        }else {
            result.setBytes(r);
            result.setCount(r.length);
        }
        //判断ret，如果=-1，且次数达到5次，加入错误列表，如果没有，重置错误计数器，继续流程
        if(result.getRet() == -1) {
            //设备查询超时次数
            int LostTimes;
            try {
                LostTimes = LocalData.Cache_devlist.get(spNo).get(devCode).getLostTimes();
            }catch (Exception e){
                return false;
            }
            //5次连接不上就判断设备掉线
            if (LostTimes > LocalData.Cache_instructionlist.get(protocol).size() * 5) {
                //删除掉线的设备
                Log.e(TAG, entity.getName() + " 超过" + LostTimes + "次未响应,判定设备故障或不在线，将设备剔除缓存设备列表，两小时候重新刷新缓存");
                LocalData.Cache_devlist.get(spNo).remove(devCode);
                WarnHisEntity warnHisEntity = new WarnHisEntity();
                warnHisEntity.setCreateTime(new Date());
                warnHisEntity.setDevCode(entity.getCode());
                warnHisEntity.setDevName(entity.getName());
                warnHisEntity.setDevType(entity.getTypeCode());
                warnHisEntity.setWarnTitle("设备掉线");
                warnHisEntity.setWarnContent("设备掉线");
                dataService.addWarn_DeviceOffline(warnHisEntity);
                Log.e(TAG, LocalData.Cache_devlist.get(spNo).size() + "");
            } else {
                LocalData.Cache_devlist.get(spNo).get(devCode).setLostTimes(LostTimes + 1);
                Log.e(TAG, entity.getCode() + "、查询超时，超时时间3秒,超时次数：" + LostTimes);
            }
            return false;
        }
        else{
            if(LocalData.Cache_devlist.get(spNo).get(devCode).getLostTimes() !=0) LocalData.Cache_devlist.get(spNo).get(devCode).setLostTimes(0);
            //解析数据recvDataDto，
            RecvDataDto finalResult = result;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handleRecvData(finalResult,instructionEntity,entity);
                }
            }).start();
        }
        return true;
    }
    //解析数据recvDataDto，
    private void handleRecvData(RecvDataDto recvDataDto,InstructionEntity instructionEntity,DevEntity entity){//byte[] buffer, int size, int step, String devCode, String protocol1, List<InstructionEntity> instructionlist1)  {
        //获取设备id
        String devCode=entity.getCode();
        //recvData
        byte[] buffer = recvDataDto.getBytes();
        int size = recvDataDto.getCount();
        //指令code
        String instructionCode = instructionEntity.getCode();
        //指令分解规则
        String rule = instructionEntity.getRule();
        //指令分解规则
        String[] arr = rule.split("\\|");
        //获取分解指令集的方法
        List<ResultEntity> resultItemList = LocalData.Cache_resultlist.get(instructionCode);
        //存放解析后的数据
        Map<String, String> resultMap = new HashMap<>();
        //判断解析类型'1=ascii|2=left1_right1|3=split_whitespace'
        switch (arr[0].split("=")[1]){
            case "ascii":
                try {
                    resultMap= handleData_str(instructionCode, arr, rule, resultItemList, buffer, size, instructionEntity.getEncoding(),entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "number":
                //left3,right2
                String[] arr3 = arr[1].split("=")[1].split("_");
                //3
                int leftTrim = Integer.parseInt(arr3[0].substring(4));
                //2
                int rightTrim = Integer.parseInt(arr3[1].substring(5));
                //485协议数据字节前3位后两位为标识，中间为数据
                //17-2-3,总的字节长度-头地址码1-功能码1-长度码1-校验码2,默认-5
                int end = size-leftTrim-rightTrim;
                //申请一个byte数组
                byte[] bytes = new byte[end];
                //填充byte数组
                System.arraycopy(buffer, leftTrim, bytes, 0, end);
                try {
                    resultMap=  handleData_number(resultItemList, bytes,entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        //如果没有解析到数据，退出
        if(resultMap.size() <1) return;
        //存放指令查询到的数据,判断devMap有devcode，没有就新建
        resultDisplay = new HashMap<>();
        //StringBuffer sb = new StringBuffer();
        for(String key:resultMap.keySet()){
            try {
                //获取协议的field，map结构
                String k = instructionEntity.getProtocolCode()+key;
                if (!LocalData.Cache_all_fieldDisplaylist.containsKey(k)) {
                    Log.e(TAG,"协议："+instructionEntity.getProtocolCode()+"的"+key+"被屏蔽");
                    continue;
                }
                FieldDisplayEntity e = LocalData.Cache_all_fieldDisplaylist.get(k);
                //String fieldName = e.getFieldName();
                String displayName = e.getDisplayName();
                int columnIndex = e.getColumnIndex();
                String fieldValue = resultMap.get(key);
                //sb.append(displayName+"："+fieldValue+"，");

                ViewEntity viewEntity = new ViewEntity(fieldValue, columnIndex);
                resultDisplay.put(displayName, viewEntity);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        switch (entity.getTypeCode()){
            case "ups":
                if(LocalData.devDataMap.containsKey(devCode)){
                    LocalData.devDataMap.get(devCode).putAll(resultDisplay);
                }else {
                    LocalData.devDataMap.put(devCode, resultDisplay);
                }
                break;
            default:
                LocalData.devDataMap.put(devCode, resultDisplay);
                break;
        }
        //根据不同的类型发送广播
        Intent intent = new Intent(BroadcastArguments.getUpdate());
        switch (entity.getTypeCode()){
            case "ups":
                intent = new Intent(BroadcastArguments.getUps());
                break;
            case "ac":
                intent = new Intent(BroadcastArguments.getAc());
                break;
            case "em":
                intent = new Intent(BroadcastArguments.getEm());
                break;
            case "th":
                intent = new Intent(BroadcastArguments.getTh());
                break;
        }
        //广播携带设备id
        Log.e(TAG,"广播指令一发送，id:"+devCode);
        intent.putExtra("devid",devCode);
        MyBroadCast.Send(intent);
    }

    private Map<String, String> handleData_number(List<ResultEntity> resultItemList, byte[] bytes,DevEntity entity) throws Exception {
        //新建结果Map
        Map<String, String> resultMap = new HashMap<>();
        //迭代解析规则'ac_ladis_01_q','q_2','EvaporatorTemperature','℃',4,2,10,2,'short',0,1)"
        for(ResultEntity resultItemEntity:resultItemList) {
            //'q_2'
            String pFieldName = resultItemEntity.getFieldName();
            //'EvaporatorTemperature'
            String pDisplayName = resultItemEntity.getDisplayName();
            //""
            String pPrefix = resultItemEntity.getPrefix()==null?"":resultItemEntity.getPrefix();
            //'℃'
            String pSuffix = resultItemEntity.getSuffix()==null?"":resultItemEntity.getSuffix();
            //0
            int pStartAddr = resultItemEntity.getStartAddr();
            //2
            int pLen = resultItemEntity.getLen();
            //10
            int pRatio = resultItemEntity.getRatio();
            //2
            String pDataType = resultItemEntity.getDataType();
            //'short'
            String pDataType2 = resultItemEntity.getDataType2();
            //0
            String pWarnType = resultItemEntity.getWarnType();
            //空float
            float fVal=0f;
            //值
            String pVal="";

            try{
                switch (pDataType2){
                    case "int":
                       // if(pLen != 4) throw new Exception("int length configuration wrong for "+pFieldName);
                        //数据的前几位转int
                        //int iVal = ChangeTool.bytesToInt(bytes, pStartAddr);
                        //获取实际值
                        fVal = ((float)ChangeTool.bytesToInt(bytes, pStartAddr))/pRatio;
                        //组装成字符串
                        pVal = pPrefix + fVal + pSuffix;
                        break;
                    case "short":
                        //if(pLen != 2) throw new Exception("short length configuration wrong for "+pFieldName);
                        //int iVal = ChangeTool.bytesToShort(bytes, pStartAddr);
                        fVal = ((float)ChangeTool.bytesToShort(bytes, pStartAddr))/pRatio;
                        pVal = pPrefix + fVal + pSuffix;
                        break;
                    case "ushort":
                        //if(pLen != 2) throw new Exception("unsigned short length configuration wrong for "+pFieldName);
                        //int iVal = ChangeTool.bytesToUnsignedShort(bytes, pStartAddr);
                        fVal = ((float)ChangeTool.bytesToUnsignedShort(bytes, pStartAddr))/pRatio;
                        pVal = pPrefix + fVal + pSuffix;
                        break;
                    case "float":
                        //if(pLen != 4) throw new Exception("float length configuration wrong for "+pFieldName);
                        fVal = ChangeTool.bytesToInt(bytes, pStartAddr);
                        pVal = pPrefix + fVal + pSuffix;
                        break;
                }

                /*if(pDataType2.equals(RuleEnum.DataType2.INT.toString())) {
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
                    //注释报错，有报错会直接退出循环
                        *//*if(pLen != 4) {
                            throw new Exception("float length configuration wrong for "+pFieldName);
                        }*//*
                    fVal = ChangeTool.bytesToInt(bytes, pStartAddr);
                    pVal = pPrefix + String.valueOf(fVal) + pSuffix;
                }*/
                resultMap.put(pFieldName, pVal);

                //warn
                if (pWarnType.equals(RuleEnum.WarnType.UPPER_LOWER_LIMITS.toString())) {
                    //0
                    float upperLimit = Float.parseFloat(resultItemEntity.getUpperLimit());
                    //0
                    float lowerLimit = Float.parseFloat(resultItemEntity.getLowerLimit());
                    String warnDesc = "";
                    if(fVal<lowerLimit || fVal>upperLimit) {
                        Date now = new Date();
                        if(fVal<lowerLimit) {
                            warnDesc = pFieldName+" above upper limit";
                        } else if(fVal>upperLimit) {
                            warnDesc = pFieldName+" below lower limit";
                        }
                        WarnHisEntity warnHisEntity = new WarnHisEntity();
                        warnHisEntity.setWarnTitle(warnDesc);
                        warnHisEntity.setWarnContent("Device name: "+entity.getName()+", "+warnDesc+" "+sdf.format(now));
                        warnHisEntity.setDevCode(entity.getCode());
                        warnHisEntity.setDevName(entity.getName());
                        warnHisEntity.setDevType(entity.getTypeCode());
                        warnHisEntity.setCreateTime(now);
                        dataService.addWarn(warnHisEntity);
                    }
                }
            }catch (Exception e){
                Log.e(TAG,e.getMessage());
            }
        }
        return resultMap;
    }

    private Map<String, String> handleData_str(String instructionCode, String[] arr, String rule, List<ResultEntity> resultItemList, byte[] bytes, int size, String encoding,DevEntity entity) throws Exception {
        if(arr.length != 3) throw new Exception("format error: "+ rule);
        Map<String, String> resultMap = new HashMap<>();
        //// arr 1=ascii,2=left1_right1,3=split_whitespace
        String[] arr3 = arr[1].split("=")[1].split("_");
       /* //1
        int leftTrim = Integer.parseInt(arr3[0].substring(4));
        //1
        int rightTrim = Integer.parseInt(arr3[1].substring(5));*/
        //去掉0d
        String resultStr;
        try {
            //掐头去尾，获取数据字节
            resultStr = new String(bytes, Integer.parseInt(arr3[0].substring(4)),  size-Integer.parseInt(arr3[1].substring(5)), encoding);//.substring(leftTrim, resultStr.length()-rightTrim);
        } catch (Exception e) {
            throw new Exception("new string error: ");
        }
        //resultStr = resultStr.substring(leftTrim, resultStr.length()-rightTrim);

        // split_whitespace
        String splitType = arr[2].split("=")[1];
         if(splitType.contains(RuleEnum.RuleStep3.SPLIT.toString())) {
            String splitSymbol = "";
            arr3 = splitType.split("_");
            for(RuleEnum.RuleStep3SplitSymbol e : RuleEnum.RuleStep3SplitSymbol.values()) {
                //whitespace
                if (e.getName().equals(arr3[1])) {
                    splitSymbol = e.getValue();
                    break;
                }
            }
            //
            String[] arrResult = resultStr.split(splitSymbol);
            if(resultItemList.size() != arrResult.length) {
                //throw new Exception("result item count unequals to result length");
            }
            //load result data
            for(int i=0; i<resultItemList.size(); i++) {
                //(instruction_code,field_name,display_name,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qpi','qpi_nn','DevType',1,0,0,1)");
                ResultEntity e = resultItemList.get(i);
                //qpi_nn
                String fName = e.getFieldName();
                //p101
                String fValue = arrResult[i];
                resultMap.put(fName, fValue);

                // *** handle parseable field value
                //全是0，不会进入if
                if(e.getParseFlag().equals(RuleEnum.ParseFlag.PARSE.toString())) {
                    Map<String, String> maptmp = new HashMap<>();
                    for(String s : e.getParseStr().split(";")) {
                        String[] arrtmp2 = s.split("=");
                        maptmp.put(arrtmp2[0], arrtmp2[1]);
                    }
                    String fValueDesc = maptmp.get(fValue);
                    if(fValueDesc != null) {
                        resultMap.put(fName, fValueDesc);
                    }
                }
                // handle warn data('ups_ladis_01_qgs','qgs_bbb','Warning',1,0,2,12)");
                //e.getWarnType() ==1 or 2
                if(e.getWarnType().equals(RuleEnum.WarnType.UPPER_LOWER_LIMITS.toString()) || e.getWarnType().equals(RuleEnum.WarnType.WARN_CONFIG.toString())) {
                    handleWarn_str(instructionCode, e, arrResult[i],entity);
                    //new Thread(new Warn_query(instructionCode, e, arrResult[i])).start();
                }
            }
        }else
            //没有len,不会进入这个if
            if(splitType.equals(RuleEnum.RuleStep3.LEN.toString())) {
             //handleData_str_len(instructionCode, resultStr, resultItemList, resultMap,entity);
             for(ResultEntity resultItemEntity:resultItemList) {
                 //('ups_ladis_01_qpi','qpi_nn','DevType',1,0,0,1)")
                 //qpi_nn
                 String pFieldName = resultItemEntity.getFieldName();
                 //1
                 int pStartAddr = resultItemEntity.getStartAddr();
                 //0
                 int pLen = resultItemEntity.getLen();
                 //
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

                 handleWarn_str(instructionCode, resultItemEntity, pValue,entity);
             }
         }
        return resultMap;
    }

    private void handleData_str_len(String instructionCode, String resultStr, List<ResultEntity> resultItemList, Map<String, String> resultMap,DevEntity entity) {
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

            handleWarn_str(instructionCode, resultItemEntity, pValue,entity);
        }
    }

    private void handleWarn_str(String instructionCode, ResultEntity e, String fieldValue,DevEntity entity) {

        Map<String, String> map = new HashMap<>();
        String[] arr;
        Date now = new Date();
        String fieldName = e.getFieldName();
        String warnType = e.getWarnType();
        String warnDesc = "";

        WarnHisEntity warnHisEntity = new WarnHisEntity();

        if(warnType.equals(RuleEnum.WarnType.UPPER_LOWER_LIMITS.toString())) {//1
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
                warnHisEntity.setWarnContent("Device name: "+entity.getName()+", "+warnDesc+" "+sdf.format(now));
                warnHisEntity.setDevCode(entity.getCode());
                warnHisEntity.setDevName(entity.getName());
                warnHisEntity.setDevType(entity.getTypeCode());
                warnHisEntity.setCreateTime(now);
                dataService.addWarn(warnHisEntity);
            }
        } else if (warnType.equals(RuleEnum.WarnType.WARN_CONFIG.toString())) {//2
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
                        warnHisEntity.setWarnContent("Device name: "+entity.getName()+", "+warnDesc+" "+sdf.format(now));
                        warnHisEntity.setDevCode(entity.getCode());
                        warnHisEntity.setDevName(entity.getName());
                        warnHisEntity.setDevType(entity.getTypeCode());
                        warnHisEntity.setCreateTime(now);
                        dataService.addWarn(warnHisEntity);
                    }
                }
            }
        }
    }
}
