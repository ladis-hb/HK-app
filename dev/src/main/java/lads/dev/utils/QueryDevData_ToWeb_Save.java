package lads.dev.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lads.dev.biz.DbDataService;
import lads.dev.biz.LocalData;
import lads.dev.entity.DataHisEntity;
import lads.dev.entity.DevEntity;
import lads.dev.entity.FieldDisplayEntity;
import lads.dev.entity.InstructionEntity;
import lads.dev.entity.ResultEntity;
import lads.dev.entity.ViewEntity;

public class QueryDevData_ToWeb_Save {
    private static MyDatabaseHelper dbHelper = new MyDatabaseHelper(MyApplication.getContext(), 2);
    private static DbDataService dbDataService = new DbDataService(dbHelper.getDb());
    private static String uri = LocalData.Cache_sysparamlist.get("http_uri").getParamValue();
    private static String TAG = "QueryDevData_ToWeb_Save";
    public static void QueryDevData_ToWeb_SaveTo(String deviceCode){
        //get 设备信息
        DevEntity devEntity = LocalData.Cache_all_devlist.get(deviceCode);
        //查看是否是注册设备
        if(devEntity == null) return;
        //获取设备result协议列表
        String protocolCode = devEntity.getProtocolCode();
        //获取inst列表，协议-协议子集
        List<String> instructionList = new ArrayList<>();
        for(InstructionEntity instructionEntity:LocalData.Cache_instructionlist.get(protocolCode)){
            instructionList.add(instructionEntity.getCode());
        }
        Map<String,String> resultList = new HashMap<>();
        for(String string:instructionList){
            for( ResultEntity resultEntity :LocalData.Cache_resultlist.get(string)){
                resultList.put(resultEntity.getFieldName(),resultEntity.getDisplayName());
            }
        }
        //获取fielddisplay
        Map<String,String> fieldDisplayList = new HashMap<>();
        for (FieldDisplayEntity fieldDisplayEntity:LocalData.Cache_fieldDisplaylist.get(protocolCode)){
            fieldDisplayList.put(fieldDisplayEntity.getDisplayName(),fieldDisplayEntity.getFieldName());
        }
        //读取设备数据
        Map<String, ViewEntity> map = LocalData.devDataMap.get(deviceCode);


        try {

            JSONObject data = new JSONObject();
            for(String key : map.keySet()) {
                data.put(resultList.get(fieldDisplayList.get(key)), map.get(key).getValue());
            }
            //组装body
            JSONObject Body = new JSONObject();
            Body.put("deviceCode",deviceCode);
            Body.put("date",new Date().toString());
            Body.put("data",data);
            Body.put("devType",LocalData.Cache_all_devlist.get(deviceCode).getTypeCode());
            Body.put("name",LocalData.Cache_all_devlist.get(deviceCode).getName());

            //send http
            if(!MyUtil.isStringEmpty(uri)) {
                Log.e(TAG,"发送"+uri+"设备数据信息");
                HttpUtil.AsyncHttpPost(uri+"/dev", Body.toString());

            }

            //save data
            DataHisEntity dataHisEntity = new DataHisEntity();
            dataHisEntity.setDevCode(deviceCode);
            dataHisEntity.setDevName(devEntity.getName());
            dataHisEntity.setSpCode(devEntity.getSpCode());
            dataHisEntity.setMsg(data.toString());
            dataHisEntity.setCreateTime(new Date());
            dbDataService.addDataHis(dataHisEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Alarm(String dev_name,String dev_type,String warn_title,String warn_content,String create_time){
        Map<String,String> devlist = new HashMap<>();
        for(String code:LocalData.Cache_all_devlist.keySet()){
            devlist.put(LocalData.Cache_all_devlist.get(code).getName(),code);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("DeviceId",devlist.get(dev_name));
            jsonObject.put("Dev_name",dev_name);
            jsonObject.put("Alarm_device",dev_type);
            jsonObject.put("Alarm_type",warn_title);
            jsonObject.put("Alarm_msg",warn_content);
            jsonObject.put("Alarm_time",create_time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //send http
        if(!MyUtil.isStringEmpty(uri)) {
            Log.e(TAG,"发送"+uri+"报警信息");
            HttpUtil.AsyncHttpPost(uri+"/Alarm", jsonObject.toString());
        }

    }
}
