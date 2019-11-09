package lads.dev.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lads.dev.biz.LocalData;
import lads.dev.entity.ViewEntity;

public class FormatDevInfo {
    public static Set<String> UpsBasicInfo = new HashSet<>();
    public static Set<String> UpsBatInfo = new HashSet<>();
    public static Set<String> UpsIoInfo = new HashSet<>();

    public static Set<String> AC = new HashSet<>();
    public static Set<String> Em = new HashSet<>();
    public static Set<String> Th = new HashSet<>();

    public static Set<String> getUpsBasicInfo(){
     UpsBasicInfo.add("协议类型");
        UpsBasicInfo.add("Ups Mode");
        UpsBasicInfo.add("Ups型号");
        UpsBasicInfo.add("相位");
        UpsBasicInfo.add("输出额定VA");
        UpsBasicInfo.add("最高温度");
        UpsBasicInfo.add("电池电压");
        UpsBasicInfo.add("额定输出电压");
        UpsBasicInfo.add("额定输出电流");
        UpsBasicInfo.add("额定电压");
        UpsBasicInfo.add("额定输出频率");
        UpsBasicInfo.add("输出额定 power factor");
        UpsBasicInfo.add("Positive BUS voltage");
        UpsBasicInfo.add("Negative BUS voltage");
     return UpsBasicInfo;
    }

    public static  Set<String> getUpsBatInfo(){
        UpsBatInfo.add("电池容量");
        UpsBatInfo.add("蓄电池组数");
        UpsBatInfo.add("电池片数量");
        UpsBatInfo.add("电池保持时间");
        UpsBatInfo.add("每单位电池标准电压");
        UpsBatInfo.add("输出负载百分比");
        UpsBatInfo.add("Nominal I/P Voltage");
        UpsBatInfo.add("Nominal O/P Voltage");
        return UpsBatInfo;
    }
    public static Set<String> getUpsIoInfo(){
        UpsIoInfo.add("输入电压");
        UpsIoInfo.add("输入频率");
        UpsIoInfo.add("输出电压");
        UpsIoInfo.add("输出频率");
        UpsIoInfo.add("输出电流");
        UpsIoInfo.add("P 电池电压");
        UpsIoInfo.add("N 电池电压");
        return UpsIoInfo;
    }

    public static Set<String> getAC(){
        AC.add("蒸发器温度");
        AC.add("室内温度");
        AC.add("室内湿度");
        return AC;
    }

    public static Set<String> getEm(){
        Em.add("有效电流");
        Em.add("有效电压");
        Em.add("有效功率");
        return Em;
    }

    public static Set<String> getTh(){
        Th.add("温度");
        Th.add("湿度");
        return Th;
    }

    public static Map<String,String> ShortUps(String devid){
     Map<String,String> map = new HashMap<>();
     String BasicInfo = "";
     String BatInfo = "";
     String IoInfo = "";

     for(String key: LocalData.devDataMap.get(devid).keySet()){
         if(getUpsBasicInfo().contains(key)){
             BasicInfo +=key +":"+ LocalData.devDataMap.get(devid).get(key).getValue()+"\n";
             break;
         }
         if(getUpsBatInfo().contains(key)) {
             BatInfo +=key +":"+  LocalData.devDataMap.get(devid).get(key).getValue()+"\n";
             break;
         }
         if(getUpsIoInfo().contains(key)){
             IoInfo +=key +":"+  LocalData.devDataMap.get(devid).get(key).getValue()+"\n";
             break;
         }
     }
     map.put("BasicInfo",BasicInfo);
     map.put("BatInfo",BatInfo);
     map.put("IoInfo",IoInfo);
     return map;
    }

    public  static  String ShortAC(String devid){
        String string = "";
        for(String key:getAC()){
            if(LocalData.devDataMap.get(devid).containsKey(key)){
                string += key +":"+LocalData.devDataMap.get(devid).get(key).getValue()+"\n";
            }
        }
        return string;
    }
    public  static  String ShortEM(String devid){
        String string = "";
        for(String key:getEm()){
            if(LocalData.devDataMap.get(devid).containsKey(key)){
                string += key +":"+LocalData.devDataMap.get(devid).get(key).getValue()+"\n";
            }
        }
        return string;
    }
    public  static  String ShortTH(String devid){
        String string = "";
        for(String key:getTh()){
            if(LocalData.devDataMap.get(devid).containsKey(key)){
                string += key +":"+LocalData.devDataMap.get(devid).get(key).getValue()+"\n";
            }
        }
        return string;
    }
}
