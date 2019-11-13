package lads.dev.utils;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import lads.dev.activity.DevMainActivity;
import lads.dev.biz.LocalData;
import lads.dev.entity.DevEntity;
import lads.dev.entity.DevOptEntity;
import lads.dev.entity.DevOptHisEntity;

public class SocketIO {
    public static String TAG = "SocketIO";
    public static Socket mSocket = null;

    public static Socket socket(){
        Log.d(TAG,"获取socket连接");
        if(mSocket != null) return mSocket;
        if(!LocalData.Cache_sysparamlist.containsKey("webConnect") ){
            Log.e(TAG,"连接参数未定义");
            return mSocket;
        }
        if( LocalData.Cache_sysparamlist.get("webConnect").getParamValue().equals("false")){
            Log.e(TAG,"连接参数false");
            return mSocket;
        }

        try {
            mSocket = IO.socket(LocalData.Cache_sysparamlist.get("websocket_uri").getParamValue());
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return mSocket;
    }

    public static void emit(String emit,JSONObject jsonObject){
        Socket socket = socket();
        if(socket == null)return;
        //触发注册
        Log.e(TAG,"触发"+emit+"事件;json:"+jsonObject.toString());
        socket.emit(emit,jsonObject);
    }
    public static void listen(String event,Emitter.Listener listener) throws JSONException {
        Socket socket = socket();
        if(socket == null)return;
        Log.e(TAG,"监听"+event+"事件;");
        socket.on(event,listener);
    }
    public static void register() throws JSONException {
        JSONObject register = new JSONObject();
        register.put("user","mac001");
        emit("AppRegister",register);
    }
    /*public static void listener(String event, Runnable runnable, AppCompatActivity activity){
        Socket socket = socket();
        if(socket == null)return;
        Log.e(TAG,"监听"+event+"事件;");
        socket.on(event,new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                activity.runOnUiThread(runnable);
            }
        });

    }*/

}
