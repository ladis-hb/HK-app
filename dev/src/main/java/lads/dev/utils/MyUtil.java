package lads.dev.utils;

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
}
