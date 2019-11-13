package lads.dev.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lads.dev.R;
import lads.dev.biz.DbDataService;
import lads.dev.biz.LocalData;
import lads.dev.entity.DevEntity;
import lads.dev.entity.KeyValueEntity;
import lads.dev.entity.ProtocolEntity;
import lads.dev.entity.SpEntity;
import lads.dev.entity.TypeEntity;
import lads.dev.utils.MyApplication;
import lads.dev.utils.MyDatabaseHelper;
import lads.dev.utils.MyDevUtil;
import lads.dev.utils.MyUtil;
import lads.dev.viewadapter.StrSingleAdapter;

public class SpConfigActivity extends AppCompatActivity {

    private static final String TAG="SpConfigActivity";

    Button btnFinish,btnAdd,btnDelete;
    Spinner spinnerSp,spinnerType;
    RecyclerView rvDev,rvProtocol;
    EditText txtDev;
    String selectedProtocolCode,selectedDeviceCode,strMac = "";
    List<DevEntity> devlist;
    List<ProtocolEntity> protocollist;
    MyDatabaseHelper dbHelper;
    DbDataService dbDataService;
    TextView txtDevDesc;
    String Default_SerilPort = "SerialPort1";
    String Default_Pro = "UPS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp_config);

        dbHelper = new MyDatabaseHelper(SpConfigActivity.this, 2);
        dbDataService = new DbDataService(dbHelper.getDb());


        Button btn_Serilport1 = findViewById(R.id.SerilPort1);
        Button btn_Serilport2 = findViewById(R.id.SerilPort2);
        Button btn_Serilport3 = findViewById(R.id.SerilPort3);
        Button btn_Serilport4 = findViewById(R.id.SerilPort4);
        Button btn_pro_ups = findViewById(R.id.Pro_UPS);
        Button btn_pro_ac = findViewById(R.id.Pro_AC);
        Button btn_pro_em = findViewById(R.id.Pro_EM);
        Button btn_pro_th = findViewById(R.id.Pro_TH);

        btnFinish =  findViewById(R.id.spConfigaAtivity_btn_finish);
        btnAdd = (Button) findViewById(R.id.spConfigaAtivity_btn_addDevice);
        btnDelete = (Button) findViewById(R.id.spConfigaAtivity_btn_deleteDevice);
        spinnerSp = (Spinner) findViewById(R.id.spConfigaAtivity_spinner_sp);
        spinnerType = (Spinner) findViewById(R.id.spConfigaAtivity_spinner_type);
        rvDev = (RecyclerView) findViewById(R.id.spConfigaAtivity_recycler_device);
        rvProtocol = (RecyclerView) findViewById(R.id.spConfigaAtivity_recycler_protocol);
        txtDev = (EditText) findViewById(R.id.spConfigaAtivity_txt_devName);
        txtDevDesc = (TextView) findViewById(R.id.spConfigaAtivity_txt_devDesc);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        rvDev.setLayoutManager(layoutManager);
        rvProtocol.setLayoutManager(layoutManager2);
        //获取设备mac地址
        strMac = LocalData.Cache_sysparamlist.get("MacStr").getParamValue();

        //设置默认SerialPort
        btn_Serilport1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerSp.setSelection(0);
                loadDev();
            }
        });
        btn_Serilport2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerSp.setSelection(1);
                loadDev();
            }
        });
        btn_Serilport3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerSp.setSelection(2);
                loadDev();
            }
        });
        btn_Serilport4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerSp.setSelection(3);
                loadDev();
            }
        });
         btn_pro_ups.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 spinnerType.setSelection(0);
                 loadProtocol();
             }
         });
        btn_pro_ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerType.setSelection(1);
                loadProtocol();
            }
        });
        btn_pro_em.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerType.setSelection(2);
                loadProtocol();
            }
        });
        btn_pro_th.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerType.setSelection(3);
                loadProtocol();
            }
        });

        spinnerSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadDev();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedProtocolCode="";
                loadProtocol();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbDataService.getDev();
                finish();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deviceName = txtDev.getText().toString();
                if(MyUtil.isStringEmpty(deviceName)) {
                    Toast.makeText(SpConfigActivity.this, "Device name required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(MyUtil.isStringEmpty(selectedProtocolCode)) {
                    Toast.makeText(SpConfigActivity.this, "Protocol required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int idxType = spinnerType.getSelectedItemPosition();
                String typcCode = LocalData.typelist.get(idxType).getCode();
                int idxSp = spinnerSp.getSelectedItemPosition();
                String spCode = LocalData.splist.get(idxSp).getCode();
                String spNo = String.valueOf(idxSp+1);

                int seq = 1;
                if(!MyUtil.isListEmpty(devlist)) {
                    seq=devlist.get(devlist.size()-1).getSeq()+1;
                }
                String deviceCode = strMac+String.format("%02d", idxSp+1)+String.format("%03d",seq);
                DevEntity entity = new DevEntity(typcCode,selectedProtocolCode,deviceName,deviceCode,spNo,spCode,"0",seq);
                int ret = dbDataService.addDevice(entity);
                if(ret == 0) {
                    Toast.makeText(SpConfigActivity.this, "Device name already exist!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    dbDataService.getDev();
                    loadDev();
                    Toast.makeText(SpConfigActivity.this, "Add succeed!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SpConfigActivity.this)
                        .setTitle("Confirm")
                        .setMessage("Sure to delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dbDataService.deleteDevice(selectedDeviceCode);
                                dbDataService.getDev();
                                loadDev();
                                Toast.makeText(SpConfigActivity.this, "Delete succeed!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });

        loaddata();
    }



    private void loaddata() {
        List<SpEntity> splist = LocalData.splist;
        if(splist==null) {
            return;
        }
        String[] arr = new String[splist.size()];
        for(int i=0;i<splist.size();i++) {
            arr[i] = splist.get(i).getCode();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSp.setAdapter(adapter);
        spinnerSp.setSelection(0);

        List<TypeEntity> typelist = LocalData.typelist;
        arr = new String[typelist.size()];
        for(int i=0;i<typelist.size();i++) {
            arr[i] = typelist.get(i).getCode();
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
        spinnerType.setSelection(0);
    }

    private void loadDev() {

        String spCode = spinnerSp.getSelectedItem().toString();
        Log.d(TAG, "~~~"+spCode);
        devlist = new ArrayList<>();
        List<KeyValueEntity> list = new ArrayList<>();
        for(DevEntity entity : LocalData.devlist) {
            if(entity.getSpCode().equals(spCode)) {
                devlist.add(entity);
                KeyValueEntity kv = new KeyValueEntity(entity.getCode(), entity.getName());
                list.add(kv);
            }
        }
        StrSingleAdapter strSingleAdapter = new StrSingleAdapter(SpConfigActivity.this,list);
        strSingleAdapter.setOnItemClickListener(new StrSingleAdapter.OnItemClickListener() {
            @Override
            public void setOnClickItemListener(View view, int position, String key) {
                selectedDeviceCode = key;
                DevEntity devEntity = devlist.get(position);
                int idxSp = spinnerSp.getSelectedItemPosition();
                String protocolName = "";
                for(ProtocolEntity protocolEntity : LocalData.protocollist) {
                    if(protocolEntity.getCode().equals(devEntity.getProtocolCode())) {
                        protocolName = protocolEntity.getName();
                        break;
                    }
                }
                String msg = "设备名称: "+devEntity.getName()+",   SerialPort: "+selectedDeviceCode+", 协议: "+protocolName;
                txtDevDesc.setText(msg);
            }
        });
        rvDev.setAdapter(strSingleAdapter);
    }

    private void loadProtocol() {
        String typeCode = spinnerType.getSelectedItem().toString();
        Log.d(TAG, typeCode);
        protocollist = new ArrayList<>();
        List<KeyValueEntity> list = new ArrayList<>();
        for(ProtocolEntity entity : LocalData.protocollist) {
            if(entity.getTypeCode().equals(typeCode)) {
                protocollist.add(entity);
                KeyValueEntity keyValueEntity = new KeyValueEntity(entity.getCode(), entity.getName());
                list.add(keyValueEntity);
            }
        }
        StrSingleAdapter strSingleAdapter = new StrSingleAdapter(SpConfigActivity.this, list);
        strSingleAdapter.setOnItemClickListener(new StrSingleAdapter.OnItemClickListener() {
            @Override
            public void setOnClickItemListener(View view, int position, String key) {
                selectedProtocolCode = key;

                txtDev.setText(list.get(position).getValue());
            }
        });
        rvProtocol.setAdapter(strSingleAdapter);
    }

}
