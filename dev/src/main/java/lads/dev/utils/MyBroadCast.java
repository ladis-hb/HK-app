package lads.dev.utils;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class MyBroadCast {
    //定义广播接受器
    private static LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(MyApplication.getContext());
    private static IntentFilter intentFilter = new IntentFilter();

    public static void Recv(String th,BroadcastReceiver broadcastReceiver){
        //挂载广播器实例
        intentFilter.addAction(th);
        //注册监听到实例
        localBroadcastManager.registerReceiver(broadcastReceiver,intentFilter);
    }
    public static void Send(Intent intent){
        localBroadcastManager.sendBroadcast(intent);
    }

}
