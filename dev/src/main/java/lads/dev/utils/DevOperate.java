package lads.dev.utils;

import lads.dev.biz.LocalData;
import lads.dev.entity.DevOptHisEntity;

public class DevOperate {
    //下发socket操作指令
    public static void addDevOpt(DevOptHisEntity devOptHisEntity) {
        String spNo = LocalData.Cache_all_devlist.get(devOptHisEntity.getDevCode()).getSpNo();
        int baudrate = LocalData.Cache_splist.get(spNo).getBaudrate();
        SerialPortUtils serialPortUtils= new SerialPortUtils();
        serialPortUtils.openSerialPort(Integer.parseInt(spNo),baudrate);
        serialPortUtils.sendSerialPort(devOptHisEntity.getOptValue());
    }
}
