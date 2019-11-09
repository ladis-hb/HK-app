package lads.dev.entity;

public class BroadcastArguments {
    private static String Update= "lads.dev.update";
    private static String Ups= "lads.dev.update.Ups";
    private static String Ac = "lads.dev.update.Ac";
    private static String Em ="lads.dev.update.Em";
    private static String Th ="lads.dev.update.Th";

    public static String getUpdate(){
        return Update;
    }
    public static String getUps(){
        return Ups;
    }
    public static String getAc(){
        return Ac;
    }
    public static String getEm(){
        return Em;
    }
    public static String getTh(){
        return Th;
    }
}
