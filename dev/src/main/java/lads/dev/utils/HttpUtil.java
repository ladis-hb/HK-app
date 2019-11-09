package lads.dev.utils;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {

    private static String TAG="HttpUtil";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static OkHttpClient client = new OkHttpClient();


    public static void httpGet(String urlStr) {

        Request request = new Request.Builder()
                .url(urlStr)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    Log.d(TAG,response.body().string());
                }
            }
        });
    }

    //异步post,call会重开线程
    public static void AsyncHttpPost(String urlStr, String jsonStr) {
        //Log.d(TAG,"上传数据到云"+urlStr+ jsonStr);
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder()
                .url(urlStr)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG , response.body().string());
            }
        });
    }
    //同步post
    public static String httpPost(String urlStr, String jsonStr) throws IOException {
       // Log.d(TAG,"上传数据到云"+urlStr+ jsonStr);
        OkHttpClient client = new OkHttpClient();

        String result = "";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        //jsonStr = "{\"username\":\"lisi\",\"nickname\":\"李四\"}";
        RequestBody body = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder()
                .url(urlStr)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            result = response.body().string();
        }else {
            result = "false";
        }
        return result;
    }
    //检测ip是否畅通
    public static Boolean checkUrl(String address,int waitMilliSecond) {
        try {
            URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(waitMilliSecond);
            conn.setReadTimeout(waitMilliSecond);

            //HTTP connect
            try {
                conn.connect();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            int code = conn.getResponseCode();
            if ((code >= 100) && (code < 400)) {
                return true;
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

