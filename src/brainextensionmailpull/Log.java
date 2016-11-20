/*
 * simple logger
 */
package brainextensionmailpull;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author xandi
 */
public class Log {

    static boolean doInfo = false;
    static boolean doError = false;
    static boolean doTime = false;
    static boolean doOther = false;
    
    public static void set(boolean info, boolean error, boolean time, boolean other){
        doInfo = info;
    }

    public static void log(String level, String text) {
        if (doInfo) {
            System.out.println(getCurrentTimeStamp() + " :: " + level + " :: " + text);
        }

    }
    
     public static void info(String text) {
        if (doInfo) {
            System.out.println(getCurrentTimeStamp() + " :: " + "INFO :: " + text);
        }
    }

    public static void time(String text) {
        if (doTime) {
            System.out.println(getCurrentTimeStamp() + " :: " + "INFO :: " + text);
        }
    }

    public static void error(String text) {
        if (doError) {
            System.out.println(getCurrentTimeStamp() + " :: " + "ERROR :: " + text);
        }
    }

    private static String getCurrentTimeStamp() {

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

}
