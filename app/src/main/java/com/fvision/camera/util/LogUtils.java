package com.fvision.camera.util;

import android.text.TextUtils;
import android.text.format.DateFormat;
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
    public static boolean isDeBug = false;
    public static boolean isWrite = false;

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
        if (isDeBug) {
            Log.v(tag, msg);
        }
        if (isWrite) {
            write(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDeBug) {
            Log.d(tag, msg);
        }
        if (isWrite) {
            write(tag, msg);
        }
    }

    public static void d(String msg) {
        if (isDeBug) {
            Log.d(TAG, msg);
        }
        if (isWrite) {
            write(TAG, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isDeBug) {
            Log.i(tag, msg);
        }
        if (isWrite) {
            write(tag, msg);
        }
    }

    public static void i(String msg) {
        if (isDeBug) {
            Log.i(TAG, msg);
        }
        if (isWrite) {
            write(TAG, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isDeBug) {
            Log.w(tag, msg);
        }
        if (isWrite) {
            write(tag, msg);
        }
    }

    public static void w(String msg) {
        if (isDeBug) {
            Log.w(TAG, msg);
        }
        if (isWrite) {
            write(TAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDeBug) {
            Log.w(tag, msg);
        }
        if (isWrite) {
            write(tag, msg);
        }
    }

    public static void e(String msg) {
        if (isDeBug) {
            Log.w(TAG, msg);
        }
        if (isWrite) {
            write(TAG, msg);
        }
    }

    public static void write(String tag, String msg) {
        if (isWrite) {
            String path = FileUtils.createMkdirsAndFiles("/uvccameramjpeg/LOG", LOGNAME);
            if (!TextUtils.isEmpty(path)) {
                FileUtils.write2File(path, DateFormat.format(INFORMAT, System.currentTimeMillis()) + tag + "========>>" + msg + "\n=================================分割线=================================", true);
            }
        }
    }

    public static void write(String msg) {
        if (isWrite) {
            write("zoulequan", msg);
        }
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
