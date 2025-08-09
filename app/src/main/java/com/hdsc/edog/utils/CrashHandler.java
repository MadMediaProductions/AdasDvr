package com.hdsc.edog.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String CRASH_REPORTER_EXTENSION = ".cr";
    public static final boolean DEBUG = true;
    private static CrashHandler INSTANCE = null;
    private static final String STACK_TRACE = "STACK_TRACE";
    public static final String TAG = "CrashHandler";
    private static final String VERSION_CODE = "versionCode";
    private static final String VERSION_NAME = "versionName";
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Properties mDeviceCrashInfo = new Properties();

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }

    public void init(Context ctx) {
        this.mContext = ctx;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        if (handleException(ex) || this.mDefaultHandler == null) {
            Process.killProcess(Process.myPid());
        } else {
            this.mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex != null) {
            String localizedMessage = ex.getLocalizedMessage();
            ex.printStackTrace();
            collectCrashDeviceInfo(this.mContext);
            String saveCrashInfoToFile = saveCrashInfoToFile(ex);
            sendCrashReportsToServer(this.mContext);
        }
        return true;
    }

    public void collectCrashDeviceInfo(Context ctx) {
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 1);
            if (pi != null) {
                this.mDeviceCrashInfo.put(VERSION_NAME, pi.versionName == null ? "not set" : pi.versionName);
                this.mDeviceCrashInfo.put(VERSION_CODE, Integer.valueOf(pi.versionCode));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "Error while collect package info:" + e);
        }
        for (Field field : Build.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                this.mDeviceCrashInfo.put(field.getName(), field.get((Object) null));
                Log.d(TAG, field.getName() + " : " + field.get((Object) null));
            } catch (Exception e2) {
                Log.e(TAG, "Error while collect crash info:" + e2);
            }
        }
    }

    private String saveCrashInfoToFile(Throwable ex) {
        String appname;
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);
        for (Throwable cause = ex.getCause(); cause != null; cause = cause.getCause()) {
            cause.printStackTrace(printWriter);
        }
        String result = info.toString();
        printWriter.close();
        this.mDeviceCrashInfo.put(STACK_TRACE, result);
        try {
            String appname2 = this.mContext.getPackageName();
            try {
                appname = appname2.substring(appname2.lastIndexOf(".") + 1, appname2.length());
            } catch (Exception e) {
                appname = "temp";
            }
            String path = getLocalFileSavePath(appname, ".txt", 3);
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(result.toString().getBytes());
            fos.close();
            return path;
        } catch (Exception e2) {
            Log.e(TAG, "an error occured while writing report file..." + e2);
            return null;
        }
    }

    public static String getLocalFileSavePath(String appName, String tail, int flag) {
        String path;
        String path2 = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (TextUtils.isEmpty(path2)) {
            path = Environment.getDataDirectory().getAbsolutePath() + "/";
        } else {
            path = path2 + "/data/tuzhi.edog.androidapp/";
        }
        if (tail.equals("jpb") || tail.endsWith("png")) {
            flag = 1;
        } else if (tail.equals("amr") || tail.endsWith("mp3")) {
            flag = 2;
        } else if (tail.equals("txt")) {
            flag = 0;
        }
        long name = System.currentTimeMillis();
        if (flag == 2) {
            String path3 = path + appName + "/record";
            File file = new File(path3);
            if (!file.exists()) {
                file.mkdirs();
            }
            return path3 + "/record-" + name + tail;
        } else if (flag == 1) {
            String path4 = path + appName + "/image";
            File file2 = new File(path4);
            if (!file2.exists()) {
                file2.mkdirs();
            }
            return path4 + "/img-" + name + tail;
        } else if (flag == 3) {
            String path5 = path + appName + "/crush";
            File file3 = new File(path5);
            if (!file3.exists()) {
                file3.mkdirs();
            }
            return path5 + "/crush-" + name + tail;
        } else {
            String path6 = path + appName + "/temp";
            File file4 = new File(path6);
            if (!file4.exists()) {
                file4.mkdirs();
            }
            return path6 + "/temp-" + name + tail;
        }
    }

    private void sendCrashReportsToServer(Context ctx) {
        String[] crFiles = getCrashReportFiles(ctx);
        if (crFiles != null && crFiles.length > 0) {
            TreeSet<String> sortedFiles = new TreeSet<>();
            sortedFiles.addAll(Arrays.asList(crFiles));
            Iterator<String> it = sortedFiles.iterator();
            while (it.hasNext()) {
                File cr = new File(ctx.getFilesDir(), it.next());
                postReport(cr);
                cr.delete();
            }
        }
    }

    private String[] getCrashReportFiles(Context ctx) {
        return ctx.getFilesDir().list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(CrashHandler.CRASH_REPORTER_EXTENSION);
            }
        });
    }

    private void postReport(File file) {
    }

    public void sendPreviousReportsToServer() {
        sendCrashReportsToServer(this.mContext);
    }
}
