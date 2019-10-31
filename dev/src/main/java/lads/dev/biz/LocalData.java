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
}
