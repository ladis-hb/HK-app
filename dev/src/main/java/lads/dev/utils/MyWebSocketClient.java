package lads.dev.utils;

import android.content.Context;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class MyWebSocketClient extends WebSocketClient {

    private String TAG = "MyWebSocketClient";
    //    private Context mContext;
//
//    private static MyWebSocketClient myWebSocketClient;
//
//    private MyWebSocketClient(URI serverUri) {
//        super(serverUri);
//    }
//
//    public static MyWebSocketClient getInstance(URI uri) {
//        if(myWebSocketClient == null) {
//            synchronized (MyWebSocketClient.class) {
//                if(myWebSocketClient == null) {
//                    myWebSocketClient = new MyWebSocketClient(uri);
//                }
//            }
//        }
//        return myWebSocketClient;
//    }

    public MyWebSocketClient(URI serverUri) {
        //Log.d(TAG,"in websocket");
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "~~~~~onOpen");
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "~~~~~onMessage");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "~~~~~onClose,"+code+","+reason);
    }

    @Override
    public void onError(Exception ex) {
        Log.d(TAG, "~~~~~onError");
        ex.printStackTrace();
    }
}
