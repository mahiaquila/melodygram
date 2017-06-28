package com.melodygram.utils;

import android.util.Log;

/**
 * Created by LALIT on 15-06-2016.
 */

public class LogD {

    public static void v(String tag,String msg,boolean logPrint) {
        if (msg != null && msg.length() > 0) {
            if (logPrint)
                Log.v(tag, msg);
        }
    }

    public static void i(String tag,String msg,boolean logPrint) {
        if (msg != null && msg.length() > 0) {
            if (logPrint)
                Log.i(tag, msg);
        }
    }

    public static void e(String tag,String msg,boolean logPrint) {
        if (msg != null && msg.length() > 0) {
            if (logPrint)
                Log.e(tag, msg);
        }
    }
}
