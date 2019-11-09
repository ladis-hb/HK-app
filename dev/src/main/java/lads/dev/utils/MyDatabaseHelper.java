package lads.dev.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2019-07-26
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    String TAG="MyDatabaseHelper";



    private Context mContext;
    private SQLiteDatabase db;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    public MyDatabaseHelper(Context context, int version) {
        super(context, "lads.db", null, version);

        if(mContext == null) {
            mContext = context;
        }
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TYPE);
        db.execSQL(CREATE_TABLE_PROTOCOL);
        db.execSQL(CREATE_TABLE_DEV);
        db.execSQL(CREATE_TABLE_INSTRUCTION);
        db.execSQL(CREATE_TABLE_RESULT);
        db.execSQL(CREATE_TABLE_FIELD_DISPLAY);
        db.execSQL(CREATE_TABLE_WARN_CFG);
        db.execSQL(CREATE_TABLE_WARN_HIS);
        db.execSQL(CREATE_TABLE_SP);
        db.execSQL(CREATE_TABLE_DATA_HIS);
        db.execSQL(CREATE_TABLE_DEV_OPT);
        db.execSQL(CREATE_TABLE_DEV_OPT_HIS);
        db.execSQL(CREATE_TABLE_SYS_PARAM);
        Log.d(TAG, "onCreate, create tables");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "MyDatabaseHelper.onUpgrade,oldVersion:"+oldVersion+",newVersion:"+newVersion);
        if(newVersion==2){
            db.execSQL("drop table if exists dev_ups");
            db.execSQL("drop table if exists serialport_dev_config");
            db.execSQL("drop table if exists sys_param");
            db.execSQL("drop table if exists warning");
            db.execSQL("drop table if exists type");
            db.execSQL("drop table if exists protocol");
            db.execSQL("drop table if exists dev");
            db.execSQL("drop table if exists instruction");
            db.execSQL("drop table if exists result");
            db.execSQL("drop table if exists field_display");
            db.execSQL("drop table if exists warn_cfg");
            db.execSQL("drop table if exists warn_his");
            db.execSQL("drop table if exists sp");
            db.execSQL("drop table if exists data_his");
            db.execSQL("drop table if exists dev_opt");
            db.execSQL("drop table if exists dev_opt_his");
            db.execSQL("drop table if exists sys_param");
            db.execSQL(CREATE_TABLE_TYPE);
            db.execSQL(CREATE_TABLE_PROTOCOL);
            db.execSQL(CREATE_TABLE_DEV);
            db.execSQL(CREATE_TABLE_INSTRUCTION);
            db.execSQL(CREATE_TABLE_RESULT);
            db.execSQL(CREATE_TABLE_FIELD_DISPLAY);
            db.execSQL(CREATE_TABLE_WARN_CFG);
            db.execSQL(CREATE_TABLE_WARN_HIS);
            db.execSQL(CREATE_TABLE_SP);
            db.execSQL(CREATE_TABLE_DATA_HIS);
            db.execSQL(CREATE_TABLE_DEV_OPT);
            db.execSQL(CREATE_TABLE_DEV_OPT_HIS);
            db.execSQL(CREATE_TABLE_SYS_PARAM);
            Log.d(TAG, "onUpgrade, create tables");
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "oldVersion:"+oldVersion+",newVersion:"+newVersion);
        if(newVersion==1) {
            db.execSQL("drop table if exists type");
            db.execSQL("drop table if exists protocol");
            db.execSQL("drop table if exists dev");
            db.execSQL("drop table if exists instruction");
            db.execSQL("drop table if exists result");
            db.execSQL("drop table if exists field_display");
            db.execSQL("drop table if exists warn_cfg");
            db.execSQL("drop table if exists warn_his");
            db.execSQL("drop table if exists sp");
            db.execSQL("drop table if exists data_his");
            db.execSQL("drop table if exists dev_opt");
            db.execSQL("drop table if exists dev_opt_his");
            db.execSQL("drop table if exists sys_param");
            Log.d(TAG, "onDowngrade, drop tables");
        }
    }

    /**
     * type：UPS=UPS；AC=空调；EM=电量仪；TH=温湿度
     */
    public static final String CREATE_TABLE_TYPE="create table type("
            +"id integer primary key autoincrement,"
            +"name text,"
            +"code text"
            +")";

    /**
     * ups_ladis_01 代表当前使用的ups协议
     * read_type:读串口的方式，1=标准modbus协议，2=ladis电源
     */
    public static final String CREATE_TABLE_PROTOCOL="create table protocol("
            +"id integer primary key autoincrement,"
            +"type_code text,"
            +"name text,"
            +"code text,"
            +"read_type text"
            +")";

    /**
     * auto generate device name
     * device code: mac+sp_no+dev_seq
     * online_flag: 1=on;0=off
     */
    public static final String CREATE_TABLE_DEV="create table dev("
            +"id integer primary key autoincrement,"
            +"type_code text,"
            +"protocol_code text,"
            +"name text,"
            +"code text,"
            +"sp_no text,"
            +"sp_code text,"
            +"online_flag text,"
            +"seq integer"
            +")";

    /**
     * serial port config
     *
     */
    public static final String CREATE_TABLE_SP="create table sp(" +
            "id integer primary key autoincrement," +
            "code text," +
            "baud_rate integer," +
            "seq integer," +
            "state integer" +
            ")";

    public static final String CREATE_TABLE_DATA_HIS="create table data_his(" +
            "id integer primary key autoincrement," +
            "sp_code text," +
            "dev_code text," +
            "dev_name text," +
            "msg text," +
            "create_time datetime" +
            ")";

    public static final String CREATE_TABLE_DEV_OPT="create table dev_opt(" +
            "id integer primary key autoincrement," +
            "protocol_code text," +
            "opt_code text," +
            "opt_name text," +
            "opt_value text" +
            ")";

    public static final String CREATE_TABLE_DEV_OPT_HIS="create table dev_opt_his(" +
            "id integer primary key autoincrement," +
            "dev_code text," +
            "dev_name text," +
            "opt_name text," +
            "opt_value text," +
            "create_time datetime" +
            ")";

    /**
     * rule:distinguish different parse way. 1=ascii|2=left1_right0|3=len or split_whitespace(or other split symbol) / 1=number / 1=mix
     */
    public static final String CREATE_TABLE_INSTRUCTION="create table instruction("
            +"id integer primary key autoincrement,"
            +"protocol_code text,"
            +"code text,"
            +"str text,"
            +"rule text,"
            +"encoding text,"
            +"seq integer"
            +")";

    /**
     * data_type:1=string;2=number;
     * data_type_2:int/short/ushort/float
     * parse_flag:0=normal;1=parse according to 'parse_str'
     * warn_type:0=not warn;1=according to field lower and upper limit;2=according to table warn_cfg
     * display_name: send to web via http
     */
    public static final String CREATE_TABLE_RESULT="create table result("
            +"id integer primary key autoincrement,"
            +"instruction_code text,"
            +"field_name text,"
            +"display_name text,"
            +"prefix text,"
            +"suffix text,"
            +"start_addr integer,"
            +"len integer,"
            +"ratio integer,"
            +"data_type text,"
            +"data_type_2 text,"
            +"parse_flag text,"
            +"parse_str text,"
            +"warn_type text,"
            +"lower_limit text,"
            +"upper_limit text,"
            +"seq integer"
            +")";

    /**
     * display_name:display in app
     */
    public static final String CREATE_TABLE_FIELD_DISPLAY="create table field_display("
            +"id integer primary key autoincrement,"
            +"protocol_code text,"
            +"field_name text,"
            +"display_name text,"
            +"column_index integer,"
            +"seq integer"
            +")";

    public static final String CREATE_TABLE_WARN_CFG="create table warn_cfg("
            +"id integer primary key autoincrement,"
            +"instruction_code text,"
            +"field_name text,"
            +"warn_enum text,"
            +"start_addr integer,"
            +"len integer,"
            +"seq integer"
            +")";

    public static final String CREATE_TABLE_WARN_HIS="create table warn_his("
            +"id integer primary key autoincrement,"
            +"dev_code text,"
            +"dev_name text,"
            +"dev_type text,"
            +"warn_title text,"
            +"warn_content text,"
            +"create_time datetime"
            +")";

    public static final String CREATE_TABLE_SYS_PARAM="create table sys_param("
            +"id integer primary key autoincrement,"
            +"param_name text,"
            +"param_value text"
            +")";

    public SQLiteDatabase getDb() {
        return db;
    }
}
