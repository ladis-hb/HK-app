package lads.dev.activity;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import lads.dev.R;
import lads.dev.utils.MyDatabaseHelper;


public class AddUpsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_ups);

        final EditText txtUpsname = (EditText)findViewById(R.id.txt_addups_activity_add_ups);


        Button btnAddUps = (Button) findViewById(R.id.btn_addups_activity_add_ups);
        btnAddUps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String devname = txtUpsname.getText().toString();
                    if(devname.equals("")) {
                        Toast.makeText(AddUpsActivity.this, "设备名称不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    MyDatabaseHelper dbHelper = new MyDatabaseHelper(AddUpsActivity.this, "lads.db", null, 2);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.execSQL("insert into dev_ups(dev_name,dev_status) values(?,?)",
                            new String[]{devname,"offline"});
                    AlertDialog.Builder dialog = new AlertDialog.Builder(AddUpsActivity.this);
                    dialog.setMessage("添加UPS设备成功！");
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialog.show();
                } catch (Exception e) {
                    Toast.makeText(AddUpsActivity.this, "操作异常，"+e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("","操作异常，"+e.getMessage());
                }

            }
        });
        Button btnCancel = (Button) findViewById(R.id.btn_cancel_activity_add_ups);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
