package lads.dev.biz;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lads.dev.dto.DevUpsDto;
import lads.dev.entity.DevEntity;
import lads.dev.entity.DevOptEntity;
import lads.dev.entity.FieldDisplayEntity;
import lads.dev.entity.InstructionEntity;
import lads.dev.entity.ProtocolEntity;
import lads.dev.entity.ResultEntity;
import lads.dev.entity.SpEntity;
import lads.dev.entity.SysParamEntity;
import lads.dev.entity.TypeEntity;
import lads.dev.entity.ViewEntity;
import lads.dev.entity.WarnCfgEntity;

public class LocalData {
    public static DevUpsDto devUpsDto;

    public static List<TypeEntity> typelist;
    public static List<ProtocolEntity> protocollist;
    public static List<DevEntity> devlist;
    public static List<InstructionEntity> instructionlist;
    public static List<ResultEntity> resultlist;
    public static List<FieldDisplayEntity> fieldDisplaylist;
    public static List<WarnCfgEntity> warnCfglist;
    public static List<SpEntity> splist;
    public static List<SysParamEntity> sysparamlist;
    public static List<DevOptEntity> devoptlist;
    public static String url = "";
    public static Map<String, Map<String, ViewEntity>> devDataMap = new HashMap<>();

    public static Map<String,SpEntity> Cache_splist;
    public static  Map<String,TypeEntity> Cache_typelist;

    //缓存设备列表
    //string SerialPort Num ,list devlist
    public static  Map<String,List<DevEntity>> Cache_devlist;
    //string devcode ,list devlist
    public static  Map<String,DevEntity> Cache_all_devlist;
    //缓存协议列表
    public static Map<String,ProtocolEntity> Cache_protocollist;
    //缓存指令列表
    public static Map<String,List<InstructionEntity>> Cache_instructionlist;
    //缓存解析
    public  static Map<String,List<ResultEntity>> Cache_resultlist;
    //缓存显示的数据
    public static Map<String,List<FieldDisplayEntity>> Cache_fieldDisplaylist;
    //缓存显示的数据
    public static Map<String,FieldDisplayEntity> Cache_all_fieldDisplaylist;
    //缓存sys配置
    public static  Map<String,SysParamEntity> Cache_sysparamlist;

}
