package lads.dev.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

import lads.dev.R;
import lads.dev.biz.DbDataService;
import lads.dev.biz.LocalData;
import lads.dev.entity.DataHisEntity;
import lads.dev.entity.DevEntity;
import lads.dev.entity.ViewEntity;
import lads.dev.fragment.DevAcFragment;
import lads.dev.fragment.DevEmFragment;
import lads.dev.fragment.DevHomeFragment;
import lads.dev.fragment.DevIoFragment;
import lads.dev.fragment.DevSettingFragment;
import lads.dev.fragment.DevTHFragment;
import lads.dev.fragment.DevUpsFragment;
import lads.dev.utils.HttpUtil;
import lads.dev.utils.MyDatabaseHelper;
import lads.dev.utils.MyUtil;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;


public class DevMainActivity extends AppCompatActivity implements DevHomeFragment.OnFragmentInteractionListener,
        DevUpsFragment.OnFragmentInteractionListener,DevAcFragment.OnFragmentInteractionListener,DevEmFragment.OnFragmentInteractionListener,
        DevTHFragment.OnFragmentInteractionListener,DevSettingFragment.OnFragmentInteractionListener, DevIoFragment.OnFragmentInteractionListener {

    String TAG="DevMainActivity";

    MyDatabaseHelper dbHelper;
    DbDataService dbDataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_main);

        try {

            dbHelper = new MyDatabaseHelper(DevMainActivity.this, 2);
            dbDataService = new DbDataService(dbHelper.getDb());
            dbDataService.initContextData();
        } catch (Exception e) {
            Toast.makeText(this, "数据异常，刷新数据", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            new MyDatabaseHelper(DevMainActivity.this, 1);
            dbHelper = new MyDatabaseHelper(DevMainActivity.this, 2);
            dbDataService = new DbDataService(dbHelper.getDb());
            dbDataService.initContextData();
        }

        handlerData.postDelayed(runnableData, 2*60*60*1000); //两个小时刷新一次在线设备
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d(TAG, "onFragmentInteraction");
    }

    Handler handlerData = new Handler();
    Runnable runnableData = new Runnable() {
        @Override
        public void run() {
            dbDataService.getDev();
            handlerData.postDelayed(this, 2*60*60*1000);
        }
    };
}