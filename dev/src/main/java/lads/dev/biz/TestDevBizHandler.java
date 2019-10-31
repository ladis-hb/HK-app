package lads.dev.biz;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.stream.Collectors;

import android_serialport_api.SerialPort;
import lads.dev.dto.DevUpsDto;
import lads.dev.entity.FieldDisplayEntity;
import lads.dev.entity.InstructionEntity;
import lads.dev.entity.LastWarnValueEntity;
import lads.dev.entity.ResultEntity;
import lads.dev.entity.ViewEntity;
import lads.dev.entity.WarnCfgEntity;
import lads.dev.entity.WarnHisEntity;
import lads.dev.utils.ChangeTool;
import lads.dev.utils.HttpUtil;
import lads.dev.utils.MyDatabaseHelper;
import lads.dev.utils.SerialPortUtils;

/**
 * Created by Administrator on 2019-07-08
 */
public class TestDevBizHandler implements Runnable  {
    private static final String TAG = "TestDevBizHandler";

    private String devname;
    private String devtype;
    private String protocol;
    private List<InstructionEntity> instructionlist;
    Map<String, ViewEntity> resultDisplay = new HashMap<>();
    List<LastWarnValueEntity> lastWarnList = new ArrayList<>();

    MyDatabaseHelper dbHelper;
    DbDataService dataService;
    Context mContext;
    List<LastWarnValueEntity> lastWarnValueList;



    //SQLiteDatabase db;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public TestDevBizHandler(String protocol, Context ctx, String devname, String devtype) {
        this.devname = devname;
        this.devtype = devtype;
        this.protocol = protocol;
        this.mContext = ctx;
        dbHelper = new MyDatabaseHelper(ctx, 2);
        dataService = new DbDataService(dbHelper.getDb());


    }

    public void onDataReceive(byte[] buffer, int size, int step) {
        //instructionlist = LocalData.instructionlist.stream().filter(a->a.getProtocolCode().equals(protocol)).sorted(Comparator.comparing(InstructionEntity::getSeq)).collect(Collectors.toList());
        instructionlist = new ArrayList<>();
        for(InstructionEntity a : LocalData.instructionlist) {
            if(a.getProtocolCode().equals(protocol)) {
                instructionlist.add(a);
            }
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
                e.printStackTrace();
            }
        } else if(resultType.equals(RuleEnum.RuleStep1.NUMBER.toString())) {
            String[] arr2 = arr[1].split("_");
            int leftTrim = Integer.parseInt(arr2[0].substring(4));
            byte[] bytes = new byte[size-leftTrim];
            System.arraycopy(buffer, leftTrim, bytes, 0, size-leftTrim);
            try {
                handleData_number(resultItemList, bytes, resultMap);
            } catch (Exception e) {
                e.printStackTrace();
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
        Toast.makeText(mContext, fieldDisplayList.size()+"", Toast.LENGTH_SHORT).show();

        try {
            JSONObject json = new JSONObject();
            for(String key : resultDisplay.keySet()) {
                ViewEntity entity = resultDisplay.get(key);
                json.put(key, entity.getValue());
            }
            HttpUtil.httpPost("https://www.baidu.com", json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleData_number(List<ResultEntity> resultItemList, byte[] bytes, Map<String, String> resultMap) throws Exception {
        Date now = new Date();
        for(int i=0;i<resultItemList.size(); i++) {
            ResultEntity resultItemEntity = resultItemList.get(i);
            String pFieldName = resultItemEntity.getFieldName();
            String pDisplayName = resultItemEntity.getDisplayName();
            String pPrefix = resultItemEntity.getPrefix();
            String pSuffix = resultItemEntity.getSuffix();
            int pStartAddr = resultItemEntity.getStartAddr();
            int pLen = resultItemEntity.getLen();
            int pRatio = resultItemEntity.getRatio();
            String pDataType = resultItemEntity.getDataType();
            String pDataType2 = resultItemEntity.getDataType2();

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
            if (pDataType.equals(RuleEnum.WarnType.UPPER_LOWER_LIMITS.toString())) {
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
                throw new Exception("result item count unequals to result length");
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
                        warnHisEntity.setDevName(devname);
                        warnHisEntity.setDevType(devtype);
                        warnHisEntity.setCreateTime(now);
                        dataService.addWarn(warnHisEntity);
                    }
                }

            }
        }
    }

    @Override
    public void run() {

        try{
            bizHandle();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void bizHandle() {
        if(instructionlist == null) {
            //instructionlist = LocalData.instructionlist.stream().filter(a->a.getProtocolCode().equals(protocol)).sorted(Comparator.comparing(InstructionEntity::getSeq)).collect(Collectors.toList());
            instructionlist = new ArrayList<>();
            for(InstructionEntity a : LocalData.instructionlist) {
                if(a.getProtocolCode().equals(protocol)) {
                    instructionlist.add(a);
                }
            }
        }
        for(InstructionEntity instructionEntity : instructionlist) {
            int step = instructionEntity.getSeq();
            String instructionStr = instructionEntity.getStr();
            byte[] commandbytes = ChangeTool.HexToByteArr(instructionStr);
        }
    }


    public String getDevname() {
        return devname;
    }

    public void setDevname(String devname) {
        this.devname = devname;
    }

//    public SQLiteDatabase getDb() {
//        return db;
//    }
//
//    public void setDb(SQLiteDatabase db) {
//        this.db = db;
//    }

    public String getDevtype() {
        return devtype;
    }

    public void setDevtype(String devtype) {
        this.devtype = devtype;
    }
}
