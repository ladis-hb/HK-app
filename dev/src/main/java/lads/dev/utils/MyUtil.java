package lads.dev.utils;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Date;
import java.util.List;

public class MyUtil {
    public static boolean isStringEmpty(String str) {
        if(str == null || str.trim().equals("")) {
            return true;
        }
        return false;
    }

    public static boolean isListEmpty(List list) {
        if(list == null || list.size()==0) {
            return true;
        }
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String ForMatDate(Date date){
        SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(date.toString());
    }
}
