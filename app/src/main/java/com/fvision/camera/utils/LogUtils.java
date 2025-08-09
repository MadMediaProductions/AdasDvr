package com.fvision.camera.utils;

import android.util.Log;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class LogUtils {
    public static final int DEBUG = 4;
    private static final String DIRPATH = "/log";
    public static final int ERROR = 1;
    public static final int INFO = 3;
    private static final String INFORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String LOGNAME = "log.txt";
    private static String TAG = "zoulequan";
    public static final int VERBOSE = 5;
    public static final int WARN = 2;
    private static final boolean isDeBug = true;
    private static final boolean isWrite = false;

    public static void log(String tag, Throwable throwable, int type) {
        log(tag, exToString(throwable), type);
    }

    public static void log(String tag, String msg, int type) {
        switch (type) {
            case 1:
                e(tag, msg);
                return;
            case 2:
                w(tag, msg);
                return;
            case 3:
                i(tag, msg);
                return;
            case 4:
                d(tag, msg);
                return;
            case 5:
                v(tag, msg);
                return;
            default:
                return;
        }
    }

    public static void v(String tag, String msg) {
        Log.v(tag, msg);
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void w(String tag, String msg) {
        Log.w(tag, msg);
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static void e(String tag, String msg) {
        Log.w(tag, msg);
    }

    public static void e(String msg) {
        Log.w(TAG, msg);
    }

    public static void write(String tag, String msg) {
    }

    public static void write(String msg) {
    }

    public static void write(Throwable ex) {
        write("", exToString(ex));
    }

    private static String exToString(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        printWriter.close();
        return writer.toString();
    }
}
