package lads.dev.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.SimpleTimeZone;

import lads.dev.R;
import lads.dev.biz.DbDataService;
import lads.dev.entity.DataHisEntity;
import lads.dev.entity.KeyValueEntity;
import lads.dev.utils.MyDatabaseHelper;
import lads.dev.viewadapter.DevAdapter;
import lads.dev.viewadapter.HisDataAdapter;

public class HisDataActivity extends AppCompatActivity {

    Button btnBack;
    RecyclerView recyclerView;
    MyDatabaseHelper dbHelper;
    DbDataService dbDataService;

    private String TAG="HisDataActivity";
    private String devCode;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his_data);

        Bundle bundle = this.getIntent().getExtras();
        devCode = bundle.getString("deviceCode");

        dbHelper = new MyDatabaseHelper(HisDataActivity.this, 2);
        dbDataService = new DbDataService(dbHelper.getDb());
        btnBack = findViewById(R.id.hisactivity_btn_return);
        this.recyclerView = findViewById(R.id.hisactivity_recyclerview_hisdata);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loadHisData();
    }



    private void loadHisData() {
        List<KeyValueEntity> list = new ArrayList<>();
        List<DataHisEntity> li = dbDataService.getHisDataByDevcode(devCode);
        for(DataHisEntity dataHisEntity:li){
            String createTime = sdf.format(dataHisEntity.getCreateTime());
            String msg = dataHisEntity.getMsg();
            String s = "";
            if(msg.length()>50) s = msg.substring(0, 50);
            KeyValueEntity kv = new KeyValueEntity(createTime, s, msg);
            list.add(kv);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        HisDataAdapter hisDataAdapter = new HisDataAdapter(this, list);
        hisDataAdapter.setHisDataAdapterCallback(new HisDataAdapter.HisDataAdapterCallback() {
            @Override
            public void itemClick(String msg) {
                showInfo(msg);
            }
        });
        recyclerView.setAdapter(hisDataAdapter);
    }

    private void showInfo(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}
