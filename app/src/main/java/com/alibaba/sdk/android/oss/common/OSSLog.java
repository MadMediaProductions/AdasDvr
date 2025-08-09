package com.alibaba.sdk.android.oss.common;

import android.util.Log;

public class OSSLog {
    private static final String TAG = "OSS-Android-SDK";
    private static boolean enableLog;

    public static void enableLog() {
        enableLog = true;
    }

    public static void disableLog() {
        enableLog = false;
    }

    public static boolean isEnableLog() {
        return enableLog;
    }

    public static void logI(String msg) {
        if (enableLog) {
            Log.i(TAG, msg);
        }
    }

    public static void logV(String msg) {
        if (enableLog) {
            Log.v(TAG, msg);
        }
    }

    public static void logW(String msg) {
        if (enableLog) {
            Log.w(TAG, msg);
        }
    }

    public static void logD(String msg) {
        if (enableLog) {
            Log.d(TAG, msg);
        }
    }

    public static void logE(String msg) {
        if (enableLog) {
            Log.e(TAG, msg);
        }
    }
}
