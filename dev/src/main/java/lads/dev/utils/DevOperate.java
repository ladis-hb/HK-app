package lads.dev.utils;

import java.util.TimerTask;

import lads.dev.biz.LocalData;
import lads.dev.entity.DevOptHisEntity;

public class DevOperate {
    //下发socket操作指令
    public static Boolean addDevOpt(DevOptHisEntity devOptHisEntity) {
        String spNo = LocalData.Cache_all_devlist.get(devOptHisEntity.getDevCode()).getSpNo();
        if(LocalData.Cache_Open_SpNo.contains(spNo)) return false;
        int baudrate = LocalData.Cache_splist.get(spNo).getBaudrate();
        LocalData.Cache_Open_SpNo.add(spNo);
        SerialPortUtils serialPortUtils= new SerialPortUtils();
        serialPortUtils.openSerialPort(Integer.parseInt(spNo),baudrate);
        serialPortUtils.sendSerialPort(devOptHisEntity.getOptValue());
        serialPortUtils.closeSerialPort();
        LocalData.Cache_Open_SpNo.remove(spNo);
        return true;
    }
}
