package lads.dev.utils;

import android.content.Context;

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
            //获取Url
            String uri = LocalData.Cache_sysparamlist.get("http_uri").getParamValue();
            //send http
            if(!MyUtil.isStringEmpty(uri)) {
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
}
