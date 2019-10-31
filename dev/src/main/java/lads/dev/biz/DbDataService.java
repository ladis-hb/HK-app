package lads.dev.biz;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiConfiguration;
import android.util.Log;
import android.widget.GridLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lads.dev.entity.DataHisEntity;
import lads.dev.entity.DevEntity;
import lads.dev.entity.DevOptEntity;
import lads.dev.entity.FieldDisplayEntity;
import lads.dev.entity.InstructionEntity;
import lads.dev.entity.ProtocolEntity;
import lads.dev.entity.ResultEntity;
import lads.dev.entity.SpEntity;
import lads.dev.entity.SysParamEntity;
import lads.dev.entity.TypeEntity;
import lads.dev.entity.WarnCfgEntity;
import lads.dev.entity.WarnHisEntity;
import lads.dev.utils.MyDatabaseHelper;

public class DbDataService {
    MyDatabaseHelper dbHelper;
    SQLiteDatabase db;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String TAG = "DbDataService";

    public DbDataService(SQLiteDatabase db) {
        this.db = db;
    }

    public void initData() {
        initDbData();
        initContextData();
    }

    public void initContextData() {
//        LocalData.typelist = getDevType();
//        LocalData.protocollist = getProtocol();
//        LocalData.instructionlist = getInstruction();
//        LocalData.resultlist = getResult();
//        LocalData.fieldDisplaylist = getFieldDisplay();
//        LocalData.warnCfglist = getWarnCfg();
//        LocalData.devlist = getDev();
//        LocalData.splist = getSp();
        getDevType();
        getProtocol();
        getInstruction();
        getResult();
        getFieldDisplay();
        getWarnCfg();
        getDev();
        getSp();
        getSysParam();
        getDevOpt();
    }

    public List<SpEntity> getSp() {
        List<SpEntity> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from sp order by seq", null);
        if(cursor.moveToFirst()) {
            do{
                SpEntity entity = new SpEntity();
                entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                entity.setCode(cursor.getString(cursor.getColumnIndex("code")));
                entity.setBaudrate(cursor.getInt(cursor.getColumnIndex("baud_rate")));
                entity.setSeq(cursor.getInt(cursor.getColumnIndex("seq")));
                list.add(entity);
            } while (cursor.moveToNext());
        }
        LocalData.splist = list;
        return list;
    }


    public List<TypeEntity> getDevType() {
        List<TypeEntity> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from type", null);
        if(cursor.moveToFirst()) {
            do{
                TypeEntity entity = new TypeEntity();
                entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                entity.setName(cursor.getString(cursor.getColumnIndex("name")));
                entity.setCode(cursor.getString(cursor.getColumnIndex("code")));
                list.add(entity);
            } while (cursor.moveToNext());
        }
        LocalData.typelist = list;
        return list;
    }

    public List<ProtocolEntity> getProtocol() {
        List<ProtocolEntity> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from protocol", null);
        if(cursor.moveToFirst()) {
            do{
                ProtocolEntity entity = new ProtocolEntity();
                entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                entity.setTypeCode(cursor.getString(cursor.getColumnIndex("type_code")));
                entity.setName(cursor.getString(cursor.getColumnIndex("name")));
                entity.setCode(cursor.getString(cursor.getColumnIndex("code")));
                entity.setReadType(cursor.getString(cursor.getColumnIndex("read_type")));
                list.add(entity);
            } while (cursor.moveToNext());
        }
        LocalData.protocollist = list;
        return list;
    }

    public List<DevEntity> getDev() {
        List<DevEntity> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from dev order by sp_code,seq", null);
        if(cursor.moveToFirst()) {
            do{
                DevEntity entity = new DevEntity();
                entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                entity.setTypeCode(cursor.getString(cursor.getColumnIndex("type_code")));
                entity.setProtocolCode(cursor.getString(cursor.getColumnIndex("protocol_code")));
                entity.setName(cursor.getString(cursor.getColumnIndex("name")));
                entity.setCode(cursor.getString(cursor.getColumnIndex("code")));
                entity.setSpNo(cursor.getString(cursor.getColumnIndex("sp_no")));
                entity.setSpCode(cursor.getString(cursor.getColumnIndex("sp_code")));
                entity.setOnlineFlag(cursor.getString(cursor.getColumnIndex("online_flag")));
                entity.setSeq(cursor.getInt(cursor.getColumnIndex("seq")));
                entity.setLostTimes(0);
                list.add(entity);
            } while (cursor.moveToNext());
        }
        LocalData.devlist = list;
        return list;
    }

    public DevEntity getDevByName(String devName) {
        DevEntity entity = null;
        Cursor cursor = db.rawQuery("select * from dev where name=? order by sp_code,seq", new String[]{devName});
        if(cursor.moveToFirst()) {
            entity = new DevEntity();
            entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
            entity.setTypeCode(cursor.getString(cursor.getColumnIndex("type_code")));
            entity.setProtocolCode(cursor.getString(cursor.getColumnIndex("protocol_code")));
            entity.setName(cursor.getString(cursor.getColumnIndex("name")));
            entity.setCode(cursor.getString(cursor.getColumnIndex("code")));
            entity.setSpNo(cursor.getString(cursor.getColumnIndex("sp_no")));
            entity.setSpCode(cursor.getString(cursor.getColumnIndex("sp_code")));
            entity.setOnlineFlag(cursor.getString(cursor.getColumnIndex("online_flag")));
            entity.setSeq(cursor.getInt(cursor.getColumnIndex("seq")));
        }
        cursor.close();
        return entity;
    }
    public DevEntity getDevByCode(String devCode) {
        DevEntity entity = null;
        Cursor cursor = db.rawQuery("select * from dev where code=? order by sp_code,seq", new String[]{devCode});
        if(cursor.moveToFirst()) {
            entity = new DevEntity();
            entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
            entity.setTypeCode(cursor.getString(cursor.getColumnIndex("type_code")));
            entity.setProtocolCode(cursor.getString(cursor.getColumnIndex("protocol_code")));
            entity.setName(cursor.getString(cursor.getColumnIndex("name")));
            entity.setCode(cursor.getString(cursor.getColumnIndex("code")));
            entity.setSpNo(cursor.getString(cursor.getColumnIndex("sp_no")));
            entity.setSpCode(cursor.getString(cursor.getColumnIndex("sp_code")));
            entity.setOnlineFlag(cursor.getString(cursor.getColumnIndex("online_flag")));
            entity.setSeq(cursor.getInt(cursor.getColumnIndex("seq")));
        }
        cursor.close();
        return entity;
    }


    public List<InstructionEntity> getInstruction() {
        List<InstructionEntity> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from instruction order by seq", null);
        if(cursor.moveToFirst()) {
            do{
                InstructionEntity entity = new InstructionEntity();
                entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                entity.setProtocolCode(cursor.getString(cursor.getColumnIndex("protocol_code")));
                entity.setCode(cursor.getString(cursor.getColumnIndex("code")));
                entity.setStr(cursor.getString(cursor.getColumnIndex("str")));
                entity.setRule(cursor.getString(cursor.getColumnIndex("rule")));
                entity.setEncoding(cursor.getString(cursor.getColumnIndex("encoding")));
                entity.setSeq(cursor.getInt(cursor.getColumnIndex("seq")));
                list.add(entity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        LocalData.instructionlist = list;
        return list;
    }

    public List<ResultEntity> getResult() {
        List<ResultEntity> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from result order by seq", null);
        if(cursor.moveToFirst()) {
            do{
                ResultEntity entity = new ResultEntity();
                entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                entity.setInstructionCode(cursor.getString(cursor.getColumnIndex("instruction_code")));
                entity.setFieldName(cursor.getString(cursor.getColumnIndex("field_name")));
                entity.setDisplayName(cursor.getString(cursor.getColumnIndex("display_name")));
                entity.setPrefix(cursor.getString(cursor.getColumnIndex("prefix")));
                entity.setSuffix(cursor.getString(cursor.getColumnIndex("suffix")));
                entity.setStartAddr(cursor.getInt(cursor.getColumnIndex("start_addr")));
                entity.setLen(cursor.getInt(cursor.getColumnIndex("len")));
                entity.setRatio(cursor.getInt(cursor.getColumnIndex("ratio")));
                entity.setDataType(cursor.getString(cursor.getColumnIndex("data_type")));
                entity.setDataType2(cursor.getString(cursor.getColumnIndex("data_type_2")));
                entity.setParseFlag(cursor.getString(cursor.getColumnIndex("parse_flag")));
                entity.setParseStr(cursor.getString(cursor.getColumnIndex("parse_str")));
                entity.setWarnType(cursor.getString(cursor.getColumnIndex("warn_type")));
                entity.setLowerLimit(cursor.getString(cursor.getColumnIndex("lower_limit")));
                entity.setUpperLimit(cursor.getString(cursor.getColumnIndex("upper_limit")));
                entity.setSeq(cursor.getInt(cursor.getColumnIndex("seq")));
                list.add(entity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        LocalData.resultlist = list;
        return list;
    }

    public List<FieldDisplayEntity> getFieldDisplay() {
        List<FieldDisplayEntity> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from field_display order by seq", null);
        if(cursor.moveToFirst()) {
            do{
                FieldDisplayEntity entity = new FieldDisplayEntity();
                entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                entity.setProtocolCode(cursor.getString(cursor.getColumnIndex("protocol_code")));
                entity.setFieldName(cursor.getString(cursor.getColumnIndex("field_name")));
                entity.setDisplayName(cursor.getString(cursor.getColumnIndex("display_name")));
                entity.setColumnIndex(cursor.getInt(cursor.getColumnIndex("column_index")));
                entity.setSeq(cursor.getInt(cursor.getColumnIndex("seq")));
                list.add(entity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        LocalData.fieldDisplaylist = list;
        return list;
    }

    public List<WarnCfgEntity> getWarnCfg() {
        List<WarnCfgEntity> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from warn_cfg order by seq", null);
        if(cursor.moveToFirst()) {
            do{
                WarnCfgEntity entity = new WarnCfgEntity();
                entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                entity.setStartAddr(cursor.getInt(cursor.getColumnIndex("start_addr")));
                entity.setLen(cursor.getInt(cursor.getColumnIndex("len")));
                entity.setWarnEnum(cursor.getString(cursor.getColumnIndex("warn_enum")));
                entity.setSeq(cursor.getInt(cursor.getColumnIndex("seq")));
                list.add(entity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        LocalData.warnCfglist = list;
        return list;
    }

    public List<WarnHisEntity> getWarnHisByType(String type) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<WarnHisEntity> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from warn_his where dev_type=?", new String[]{type});
        if(cursor.moveToFirst()) {
            do{
                WarnHisEntity entity = new WarnHisEntity();
                entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                entity.setDevName(cursor.getString(cursor.getColumnIndex("dev_name")));
                entity.setDevType(cursor.getString(cursor.getColumnIndex("dev_type")));
                entity.setWarnTitle(cursor.getString(cursor.getColumnIndex("warn_title")));
                entity.setWarnContent(cursor.getString(cursor.getColumnIndex("warn_content")));
                String strCreateTime = cursor.getString(cursor.getColumnIndex("create_time"));
                try {
                    Date createTime = sdf.parse(strCreateTime);
                    entity.setCreateTime(createTime);
                } catch (Exception e) {
                    Log.e(TAG, "time format", e);
                }
                list.add(entity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<WarnHisEntity> getWarnHisByDev(String devCode) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<WarnHisEntity> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from warn_his where dev_code=?", new String[]{devCode});
        if(cursor.moveToFirst()) {
            do{
                WarnHisEntity entity = new WarnHisEntity();
                entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                entity.setDevName(cursor.getString(cursor.getColumnIndex("dev_name")));
                entity.setDevType(cursor.getString(cursor.getColumnIndex("dev_type")));
                entity.setWarnTitle(cursor.getString(cursor.getColumnIndex("warn_title")));
                entity.setWarnContent(cursor.getString(cursor.getColumnIndex("warn_content")));
                String strCreateTime = cursor.getString(cursor.getColumnIndex("create_time"));
                try {
                    Date createTime = sdf.parse(strCreateTime);
                    entity.setCreateTime(createTime);
                } catch (Exception e) {
                    Log.e(TAG, "time format", e);
                }
                list.add(entity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<SysParamEntity> getSysParam() {
        List<SysParamEntity> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from sys_param", null);
        if(cursor.moveToFirst()) {
            do{
                SysParamEntity entity = new SysParamEntity();
                entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                entity.setParamName(cursor.getString(cursor.getColumnIndex("param_name")));
                entity.setParamValue(cursor.getString(cursor.getColumnIndex("param_value")));
                list.add(entity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        LocalData.sysparamlist = list;
        return list;
    }

    public List<DevOptEntity> getDevOpt() {
        List<DevOptEntity> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from dev_opt", null);
        if(cursor.moveToFirst()) {
            do{
                DevOptEntity entity = new DevOptEntity();
                entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                entity.setProtocolCode(cursor.getString(cursor.getColumnIndex("protocol_code")));
                entity.setOptCode(cursor.getString(cursor.getColumnIndex("opt_code")));
                entity.setOptName(cursor.getString(cursor.getColumnIndex("opt_name")));
                entity.setOptValue(cursor.getString(cursor.getColumnIndex("opt_value")));
                list.add(entity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        LocalData.devoptlist = list;
        return list;
    }

    public List<DevOptEntity> getDevOptByProtocol(String protocol) {
        List<DevOptEntity> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from dev_opt where protocol_code=?", new String[]{protocol});
        if(cursor.moveToFirst()) {
            do{
                DevOptEntity entity = new DevOptEntity();
                entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                entity.setProtocolCode(cursor.getString(cursor.getColumnIndex("protocol_code")));
                entity.setOptCode(cursor.getString(cursor.getColumnIndex("opt_code")));
                entity.setOptName(cursor.getString(cursor.getColumnIndex("opt_name")));
                entity.setOptValue(cursor.getString(cursor.getColumnIndex("opt_value")));
                list.add(entity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void addWarn(WarnHisEntity warnHisEntity) {
        db.execSQL("insert into warn_his(dev_name,dev_type,warn_title,warn_content,create_time) values(?,?,?,?,?)",
                new String[]{warnHisEntity.getDevName(),warnHisEntity.getDevType(),warnHisEntity.getWarnTitle(),warnHisEntity.getWarnContent(),sdf.format(warnHisEntity.getCreateTime())});
    }

    //如果最近一条告警记录不是"设备掉线"，则添加
    public void addWarn_DeviceOffline(WarnHisEntity warnHisEntity) {
        Cursor cursor = db.rawQuery("select * from warn_his where dev_code=? order by create_time desc", new String[]{warnHisEntity.getDevCode()});
        if(cursor.moveToFirst()) {
            String warnTitle = cursor.getString(cursor.getColumnIndex("warn_title"));
            if(!warnTitle.equals("设备掉线")) {
                db.execSQL("insert into warn_his(dev_name,dev_type,warn_title,warn_content,create_time) values(?,?,?,?,?)",
                        new String[]{warnHisEntity.getDevName(),warnHisEntity.getDevType(),warnHisEntity.getWarnTitle(),warnHisEntity.getWarnContent(),sdf.format(warnHisEntity.getCreateTime())});
            }
        } else {
            db.execSQL("insert into warn_his(dev_name,dev_type,warn_title,warn_content,create_time) values(?,?,?,?,?)",
                    new String[]{warnHisEntity.getDevName(),warnHisEntity.getDevType(),warnHisEntity.getWarnTitle(),warnHisEntity.getWarnContent(),sdf.format(warnHisEntity.getCreateTime())});
        }
        cursor.close();
    }

    public int addDevice(DevEntity entity) {
        Cursor cursor = db.rawQuery("select * from dev where name='"+entity.getName()+"'", null);
        if(cursor.moveToFirst()) {
            cursor.close();
            return 0;
        } else {
            db.execSQL("insert into dev(type_code,protocol_code,name,code,sp_no,sp_code,seq) values(?,?,?,?,?,?,?)",
                    new String[]{entity.getTypeCode(),entity.getProtocolCode(),entity.getName(),entity.getCode(),entity.getSpNo(),entity.getSpCode(),entity.getSeq()+""});
            return 1;
        }
    }

    public void addDataHis(DataHisEntity entity) {
        db.execSQL("insert into data_his(sp_code,dev_code,dev_name,msg,create_time) values(?,?,?,?,?)",
                new String[]{entity.getSpCode(),entity.getDevCode(),entity.getDevName(),entity.getMsg(),sdf.format(entity.getCreateTime())});
    }

    public void deleteDevice(String deviceCode) {
        db.execSQL("delete from dev where code='"+deviceCode+"'");
    }

    public void updateBaudrate(int baudrate, int seq) {
        db.execSQL("update sp set baud_rate="+baudrate+" where seq="+seq);
    }

    public void updateParamValue(String paramName, String paramValue) {
        db.execSQL("update sys_param set param_value='"+paramValue+"' where param_name='"+paramName+"'");
    }

    public void updateDevOnlineFlag(String deviceCode, RuleEnum.OnOffFlag onOffFlag) {
        db.execSQL("update dev set online_flag='"+onOffFlag.toString()+"' where code='"+deviceCode+"'");
    }

    public List<DataHisEntity> getHisDataByDevcode(String devcode) {
        List<DataHisEntity> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from data_his where dev_code=? order by create_time desc", new String[]{devcode});
        if(cursor.moveToFirst()) {
            do{
                DataHisEntity entity = new DataHisEntity();
                entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                entity.setSpCode(cursor.getString(cursor.getColumnIndex("sp_code")));
                entity.setDevCode(cursor.getString(cursor.getColumnIndex("dev_code")));
                entity.setDevName(cursor.getString(cursor.getColumnIndex("dev_name")));
                entity.setMsg(cursor.getString(cursor.getColumnIndex("msg")));
                String strCreateTime = cursor.getString(cursor.getColumnIndex("create_time"));
                try {
                    Date createTime = sdf.parse(strCreateTime);
                    entity.setCreateTime(createTime);
                } catch (Exception e) {
                    Log.e(TAG, "time format", e);
                }
                list.add(entity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private void initDbData() {

        //sp
        db.execSQL("insert into sp(code,seq,baud_rate) values ('SerialPort1','1','2400')");
        db.execSQL("insert into sp(code,seq,baud_rate) values ('SerialPort2','2','9600')");
        db.execSQL("insert into sp(code,seq,baud_rate) values ('SerialPort3','3','9600')");
        db.execSQL("insert into sp(code,seq,baud_rate) values ('SerialPort4','4','9600')");

        //type
        db.execSQL("insert into type(name,code) values ('UPS','ups')");
        db.execSQL("insert into type(name,code) values ('AC','ac')");
        db.execSQL("insert into type(name,code) values ('EM','em')");
        db.execSQL("insert into type(name,code) values ('TH','th')");

        //protocol
        //ups
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('ups','UPS_Ladis_01','ups_ladis_01','2')");
        //ac 6
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('ac','空调01','ac_ladis_01','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('ac','空调02','ac_ladis_02','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('ac','空调03','ac_ladis_03','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('ac','空调04','ac_ladis_04','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('ac','空调05','ac_ladis_05','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('ac','空调06','ac_ladis_06','1')");
        //em 2
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('em','单项电量仪01','em1_ladis_01','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('em','单项电量仪02','em1_ladis_02','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('em','三项电量仪01','em3_ladis_01','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('em','三项电量仪02','em3_ladis_02','1')");
        //th 16
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('th','温湿度01','th_ladis_01','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('th','温湿度02','th_ladis_02','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('th','温湿度03','th_ladis_03','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('th','温湿度04','th_ladis_04','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('th','温湿度05','th_ladis_05','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('th','温湿度06','th_ladis_06','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('th','温湿度07','th_ladis_07','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('th','温湿度08','th_ladis_08','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('th','温湿度09','th_ladis_09','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('th','温湿度10','th_ladis_10','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('th','温湿度11','th_ladis_11','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('th','温湿度12','th_ladis_12','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('th','温湿度13','th_ladis_13','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('th','温湿度14','th_ladis_14','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('th','温湿度15','th_ladis_15','1')");
        db.execSQL("insert into protocol(type_code,name,code,read_type) values ('th','温湿度16','th_ladis_16','1')");


        //dev
        //db.execSQL("insert into dev(type_code,protocol_code,name,serial_port_no) values ('UPS','ups_ladis_01','main ups device')");

        //instruction
        //ups
        db.execSQL("insert into instruction(protocol_code,code,str,rule,encoding,seq) values ('ups_ladis_01','ups_ladis_01_qpi','5150490d0a','1=ascii|2=left1_right1|3=split_whitespace','UTF-8',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,encoding,seq) values ('ups_ladis_01','ups_ladis_01_qmd','514d440d0a','1=ascii|2=left1_right1|3=split_whitespace','UTF-8',2)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,encoding,seq) values ('ups_ladis_01','ups_ladis_01_qmod','514d4f440d0a','1=ascii|2=left1_right1|3=split_whitespace','UTF-8',3)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,encoding,seq) values ('ups_ladis_01','ups_ladis_01_qgs','5147530d0a','1=ascii|2=left1_right1|3=split_whitespace','UTF-8',4)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,encoding,seq) values ('ups_ladis_01','ups_ladis_01_qbv','5142560d0a','1=ascii|2=left1_right1|3=split_whitespace','UTF-8',5)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,encoding,seq) values ('ups_ladis_01','ups_ladis_01_qri','5152490d0a','1=ascii|2=left1_right1|3=split_whitespace','UTF-8',6)");
        //ac
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('ac_ladis_01','ac_ladis_01_q','0103000000670420','1=number|2=left3_right2',1)");
        //db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('ac_ladis_01','ac_ladis_01_q','010300060002240A','1=number|2=left3_right2',1)");

        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('ac_ladis_02','ac_ladis_02_q','0203000000670413','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('ac_ladis_03','ac_ladis_03_q','03030000006705C2','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('ac_ladis_04','ac_ladis_04_q','0403000000670475','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('ac_ladis_05','ac_ladis_05_q','05030000006705A4','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('ac_ladis_06','ac_ladis_06_q','0603000000670597','1=number|2=left3_right2',1)");
        //th
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('th_ladis_01','th_ladis_01_q','000300000002C5DA','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('th_ladis_02','th_ladis_02_q','010300000002C40B','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('th_ladis_03','th_ladis_03_q','020300000002C438','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('th_ladis_04','th_ladis_04_q','030300000002C5E9','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('th_ladis_05','th_ladis_05_q','040300000002C45E','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('th_ladis_06','th_ladis_06_q','050300000002C58F','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('th_ladis_07','th_ladis_07_q','060300000002C5BC','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('th_ladis_08','th_ladis_08_q','070300000002C46D','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('th_ladis_09','th_ladis_09_q','080300000002C492','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('th_ladis_10','th_ladis_10_q','090300000002C543','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('th_ladis_11','th_ladis_11_q','0A0300000002C570','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('th_ladis_12','th_ladis_12_q','0B0300000002C4A1','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('th_ladis_13','th_ladis_13_q','0C0300000002C516','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('th_ladis_14','th_ladis_14_q','0D0300000002C4C7','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('th_ladis_15','th_ladis_15_q','0E0300000002C4F4','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('th_ladis_16','th_ladis_16_q','0F0300000002C525','1=number|2=left3_right2',1)");
        //em
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('em1_ladis_01','em1_ladis_01_q','0103000A0006E5CA','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('em1_ladis_02','em1_ladis_02_q','0203000A0006E5F9','1=number|2=left3_right2',1)");
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('em3_ladis_01','em3_ladis_01_q','0103002B000375C3','1=number|2=left3_right2',1)"); //三相电量仪特殊，长度是寄存器个数，03表示要查询三个存放float数字的寄存器
        db.execSQL("insert into instruction(protocol_code,code,str,rule,seq) values ('em3_ladis_02','em3_ladis_02_q','0203002B000375F0','1=number|2=left3_right2',1)");
        //db.execSQL("insert into instruction(protocol_code,code,str,rule,encoding,seq) values ('ac_ladis_01','ac_ladis_01_q1','0103000000670420','1=number',null,1)");

        //result
        //ups
        db.execSQL("insert into result(instruction_code,field_name,display_name,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qpi','qpi_nn','DevType',1,0,0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qmd','qmd_tt','Ups Model',1,0,0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qmd','qmd_ww','Output rated VA',1,0,0,2)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qmd','qmd_kk','Output power factor',1,0,0,3)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qmd','qmd_pp','Phase',1,0,0,4)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qmd','qmd_mm','Nominal I/P Voltage',1,0,0,5)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qmd','qmd_nn','Nominal O/P Voltage',1,0,0,6)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qmd','qmd_rr','Battery Piece Number',1,0,0,7)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qmd','qmd_bb','Battery standard voltage per unit',1,0,0,8)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,data_type,parse_flag,parse_str,warn_type,seq) values ('ups_ladis_01_qmod','qmod','Ups Mode',1,1,'P=Power on mode;S=Standby mode;Y=Bypass mode;L=Line Mode;B=Battery mode;T=Battery test mode;F=Fault mode;E=HE/ECo mode;C=Converter mode;D=Shutdown mode',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qgs','qgs_mm','Input voltage','V',1,0,0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qgs','qgs_hh','Input frequency','Hz',1,0,0,2)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qgs','qgs_ll','Output voltage','V',1,0,0,3)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qgs','qgs_nn','Output frequency','Hz',1,0,0,4)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qgs','qgs_qq','Output current','A',1,0,0,5)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qgs','qgs_dd','Output load percent','%',1,0,0,6)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qgs','qgs_kk','Positive BUS voltage','V',1,0,0,7)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qgs','qgs_vv','Negative BUS voltage','V',1,0,0,8)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qgs','qgs_ss','P Battery voltage','V',1,0,0,9)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qgs','qgs_xx','N Battery voltage','V',1,0,0,10)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qgs','qgs_tt','Max Temperature of the detecting pointers','℃',1,0,0,11)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qgs','qgs_bbb','Warning',1,0,2,12)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qbv','qbv_rr','Battery voltage','V',1,0,0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qbv','qbv_nn','Battery piece number',1,0,0,2)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qbv','qbv_mm','Battery group number',1,0,0,3)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qbv','qbv_cc','Battery capacity',1,0,0,4)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qbv','qbv_tt','Battery remain time',1,0,0,5)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qri','qri_mm','Rating Output Voltage','V',1,0,0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qri','qri_qq','Rating Output Current','A',1,0,0,2)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qri','qri_ss','Rating Voltage','V',1,0,0,3)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,data_type,parse_flag,warn_type,seq) values ('ups_ladis_01_qri','qri_rr','Rating Output Frequency','Hz',1,0,0,4)");
        //ac
        //ac1
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('ac_ladis_01_q','q_2','EvaporatorTemperature','℃',4,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('ac_ladis_01_q','q_6','IndoorTemperature','℃',12,2,10,2,'short',0,2)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('ac_ladis_01_q','q_7','IndoorHumidity','%RH',14,2,10,2,'short',0,3)");
        //ac2
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('ac_ladis_02_q','q_2','EvaporatorTemperature','℃',4,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('ac_ladis_02_q','q_6','IndoorTemperature','℃',12,2,10,2,'short',0,2)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('ac_ladis_02_q','q_7','IndoorHumidity','%RH',14,2,10,2,'short',0,3)");
        //ac3
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('ac_ladis_03_q','q_2','EvaporatorTemperature','℃',4,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('ac_ladis_03_q','q_6','IndoorTemperature','℃',12,2,10,2,'short',0,2)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('ac_ladis_03_q','q_7','IndoorHumidity','%RH',14,2,10,2,'short',0,3)");
        //ac4
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('ac_ladis_04_q','q_2','EvaporatorTemperature','℃',4,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('ac_ladis_04_q','q_6','IndoorTemperature','℃',12,2,10,2,'short',0,2)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('ac_ladis_04_q','q_7','IndoorHumidity','%RH',14,2,10,2,'short',0,3)");
        //ac5
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('ac_ladis_05_q','q_2','EvaporatorTemperature','℃',4,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('ac_ladis_05_q','q_6','IndoorTemperature','℃',12,2,10,2,'short',0,2)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('ac_ladis_05_q','q_7','IndoorHumidity','%RH',14,2,10,2,'short',0,3)");
        //ac6
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('ac_ladis_06_q','q_2','EvaporatorTemperature','℃',4,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('ac_ladis_06_q','q_6','IndoorTemperature','℃',12,2,10,2,'short',0,2)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('ac_ladis_06_q','q_7','IndoorHumidity','%RH',14,2,10,2,'short',0,3)");
        //th
        //th1
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_01_q','q_1','Temperature','℃',0,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_01_q','q_2','Humidity','%RH',2,2,10,2,'short',0,1)");
        //th2
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_02_q','q_1','Temperature','℃',0,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_02_q','q_2','Humidity','%RH',2,2,10,2,'short',0,1)");
        //th3
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_03_q','q_1','Temperature','℃',0,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_03_q','q_2','Humidity','%RH',2,2,10,2,'short',0,1)");
        //th4
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_04_q','q_1','Temperature','℃',0,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_04_q','q_2','Humidity','%RH',2,2,10,2,'short',0,1)");
        //th5
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_05_q','q_1','Temperature','℃',0,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_05_q','q_2','Humidity','%RH',2,2,10,2,'short',0,1)");
        //th6
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_06_q','q_1','Temperature','℃',0,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_06_q','q_2','Humidity','%RH',2,2,10,2,'short',0,1)");
        //th7
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_07_q','q_1','Temperature','℃',0,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_07_q','q_2','Humidity','%RH',2,2,10,2,'short',0,1)");
        //th8
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_08_q','q_1','Temperature','℃',0,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_08_q','q_2','Humidity','%RH',2,2,10,2,'short',0,1)");
        //th9
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_09_q','q_1','Temperature','℃',0,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_09_q','q_2','Humidity','%RH',2,2,10,2,'short',0,1)");
        //th10
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_10_q','q_1','Temperature','℃',0,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_10_q','q_2','Humidity','%RH',2,2,10,2,'short',0,1)");
        //th11
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_11_q','q_1','Temperature','℃',0,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_11_q','q_2','Humidity','%RH',2,2,10,2,'short',0,1)");
        //th12
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_12_q','q_1','Temperature','℃',0,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_12_q','q_2','Humidity','%RH',2,2,10,2,'short',0,1)");
        //th13
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_13_q','q_1','Temperature','℃',0,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_13_q','q_2','Humidity','%RH',2,2,10,2,'short',0,1)");
        //th14
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_14_q','q_1','Temperature','℃',0,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_14_q','q_2','Humidity','%RH',2,2,10,2,'short',0,1)");
        //th15
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_15_q','q_1','Temperature','℃',0,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_15_q','q_2','Humidity','%RH',2,2,10,2,'short',0,1)");
        //th16
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_16_q','q_1','Temperature','℃',0,2,10,2,'short',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('th_ladis_16_q','q_2','Humidity','%RH',2,2,10,2,'short',0,1)");
        //em1_1
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('em1_ladis_01_q','q_1','EffectiveCurrent','A',0,4,1,2,'float',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('em1_ladis_01_q','q_2','EffectiveVoltage','V',4,8,1,2,'float',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('em1_ladis_01_q','q_3','ActivePower','KW',8,12,1,2,'float',0,1)");
        //em1_2
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('em1_ladis_02_q','q_1','EffectiveCurrent','A',0,4,1,2,'float',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('em1_ladis_02_q','q_2','EffectiveVoltage','V',4,8,1,2,'float',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('em1_ladis_02_q','q_3','ActivePower','KW',8,12,1,2,'float',0,1)");
        //em3_1
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('em3_ladis_01_q','q_1','data1','Wh',0,4,1000,2,'int',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('em3_ladis_01_q','q_2','data2','Wh',4,8,1000,2,'int',0,1)");
        db.execSQL("insert into result(instruction_code,field_name,display_name,suffix,start_addr,len,ratio,data_type,data_type_2,warn_type,seq) values ('em3_ladis_01_q','q_3','data3','Wh',8,12,1000,2,'int',0,1)");

        //field_display
        //ups
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ups_ladis_01','qpi_nn','协议类型',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ups_ladis_01','qmd_tt','Ups型号',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ups_ladis_01','qmd_ww','输出额定VA',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ups_ladis_01','qmd_kk','输出额定 power factor',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ups_ladis_01','qmd_mm','Nominal I/P Voltage',2,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ups_ladis_01','qmd_nn','Nominal O/P Voltage',2,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ups_ladis_01','qmd_rr','电池片数量',2,1)");

        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ups_ladis_01','qmod','Ups Mode',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ups_ladis_01','qgs_mm','输入电压',3,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ups_ladis_01','qgs_hh','输入频率',3,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ups_ladis_01','qgs_ll','输出电压',3,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ups_ladis_01','qgs_nn','输出频率',3,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ups_ladis_01','qgs_qq','输出电流',3,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ups_ladis_01','qgs_dd','输出负载百分比',2,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ups_ladis_01','qgs_kk','Positive BUS voltage',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ups_ladis_01','qgs_vv','Negative BUS voltage',1,1)");


        //ac
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ac_ladis_01','q_2','蒸发器温度',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ac_ladis_01','q_6','室内温度',1,2)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('ac_ladis_01','q_7','室内湿度',1,3)");

        //em
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('em1_ladis_01','q_1','有效电流',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('em1_ladis_01','q_2','有效电压',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('em1_ladis_02','q_1','有效电流',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('em1_ladis_02','q_2','有效电压',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('em1_ladis_03','q_1','有效电流',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('em1_ladis_03','q_2','有效电压',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('em1_ladis_04','q_1','有效电流',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('em1_ladis_04','q_2','有效电压',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('em1_ladis_05','q_1','有效电流',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('em1_ladis_05','q_2','有效电压',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('em1_ladis_06','q_1','有效电流',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('em1_ladis_06','q_2','有效电压',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('em1_ladis_07','q_1','有效电流',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('em1_ladis_07','q_2','有效电压',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('em1_ladis_08','q_1','有效电流',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('em1_ladis_08','q_2','有效电压',1,1)");

        //th
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('th_ladis_01','q_1','温度',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('th_ladis_01','q_2','湿度',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('th_ladis_02','q_1','温度',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('th_ladis_02','q_2','湿度',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('th_ladis_03','q_1','温度',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('th_ladis_03','q_2','湿度',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('th_ladis_04','q_1','温度',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('th_ladis_04','q_2','湿度',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('th_ladis_05','q_1','温度',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('th_ladis_05','q_2','湿度',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('th_ladis_06','q_1','温度',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('th_ladis_06','q_2','湿度',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('th_ladis_07','q_1','温度',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('th_ladis_07','q_2','湿度',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('th_ladis_08','q_1','温度',1,1)");
        db.execSQL("insert into field_display(protocol_code,field_name,display_name,column_index,seq) values('th_ladis_08','q_2','湿度',1,1)");

        //warn_cfg
        db.execSQL("insert into warn_cfg(instruction_code,field_name,warn_enum,start_addr,len,seq) values('ups_ladis_01_qgs','qgs_bbb','00=standy;01=line-interactive;10=on-line',0,2,1)");
        db.execSQL("insert into warn_cfg(instruction_code,field_name,warn_enum,start_addr,len,seq) values('ups_ladis_01_qgs','qgs_bbb','1=Utility Fail;0=Utility Fail Recover',2,1,2)");
        db.execSQL("insert into warn_cfg(instruction_code,field_name,warn_enum,start_addr,len,seq) values('ups_ladis_01_qgs','qgs_bbb','1=Battery Low;0=Battery Low Recover',3,1,3)");
        db.execSQL("insert into warn_cfg(instruction_code,field_name,warn_enum,start_addr,len,seq) values('ups_ladis_01_qgs','qgs_bbb','1=Bypass/Boost Active;0=Bypass/Boost Active Recover',4,1,4)");
        db.execSQL("insert into warn_cfg(instruction_code,field_name,warn_enum,start_addr,len,seq) values('ups_ladis_01_qgs','qgs_bbb','1=UPS Failed;0=UPS Failed Recover',5,1,5)");
        db.execSQL("insert into warn_cfg(instruction_code,field_name,warn_enum,start_addr,len,seq) values('ups_ladis_01_qgs','qgs_bbb','1=EPO;0=EPO Recover',6,1,6)");
        db.execSQL("insert into warn_cfg(instruction_code,field_name,warn_enum,start_addr,len,seq) values('ups_ladis_01_qgs','qgs_bbb','1=Test in Progress;0=Test in Progress Recover',7,1,7)");
        db.execSQL("insert into warn_cfg(instruction_code,field_name,warn_enum,start_addr,len,seq) values('ups_ladis_01_qgs','qgs_bbb','1=Shutdown Active;0=Shutdown Active Recover',8,1,8)");
        db.execSQL("insert into warn_cfg(instruction_code,field_name,warn_enum,start_addr,len,seq) values('ups_ladis_01_qgs','qgs_bbb','1=Bat Silence;0=Bat Silence Recover',9,1,9)");
        db.execSQL("insert into warn_cfg(instruction_code,field_name,warn_enum,start_addr,len,seq) values('ups_ladis_01_qgs','qgs_bbb','1=Bat test fail;0=Bat test fail Recover',10,1,10)");
        db.execSQL("insert into warn_cfg(instruction_code,field_name,warn_enum,start_addr,len,seq) values('ups_ladis_01_qgs','qgs_bbb','1=Bat test OK;0=Bat test OK Recover',11,1,11)");

        //dev_opt
        db.execSQL("insert into dev_opt(protocol_code,opt_code,opt_name,opt_value) values('ups_ladis_01','StartUps','开启UPS','534f4e0d0a')");
        db.execSQL("insert into dev_opt(protocol_code,opt_code,opt_name,opt_value) values('ups_ladis_01','ShutdownUps','关闭UPS','534f46460d0a')");

        //sys_param
        db.execSQL("insert into sys_param(param_name,param_value) values('http_uri','http://116.62.48.175:81')");
        db.execSQL("insert into sys_param(param_name,param_value) values('websocket_uri','http://116.62.48.175:81')");

    }


}
