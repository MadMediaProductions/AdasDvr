package com.fvision.camera;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import com.fvision.camera.service.ForegroundService;
import com.fvision.camera.util.LogUtils;
import com.fvision.camera.utils.CameraStateUtil;
import com.fvision.camera.utils.DoCmdUtil;
import com.fvision.camera.utils.SharedPreferencesUtil;
import com.hdsc.edog.jni.StorageDevice;
import com.hdsc.edog.service.TuzhiService;
import com.hdsc.edog.utils.UpDownManager;
import com.huiying.cameramjpeg.UvcCamera;
import com.tencent.bugly.crashreport.CrashReport;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class App extends Application {
    public static final String EDOG_PATH = (ROOT_PATH + "EDOG/");
    public static final String JPG_PATH = (ROOT_PATH + "JPEG/");
    public static final String LOG_PATH = (ROOT_PATH + "LOG/");
    public static final String[] PERMISSIONS = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_PHONE_STATE"};
    public static final String POWER_OFF_RADAR_ACTION = "landsem.intent.action.RADAR_POWER_OFF";
    private static final String ROOT_PATH = (Environment.getExternalStorageDirectory() + "/uvccameramjpeg/");
    private static final String TAG = App.class.getSimpleName();
    public static final String VIDEO_PATH = (ROOT_PATH + "VIDEO/");
    private static App _instance;
    public static final boolean isTestVersion = false;
    static List<Activity> mList = new ArrayList();
    public static App mTuzhiApplication;
    private static WindowManager mWindowManager;
    public static Object mlock = new Object();
    public static final String path = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
    private Intent serviceIntent;

    public void onCreate() {
        super.onCreate();
        _instance = this;
        LogUtils.isDeBug = true;
        Intent intent = new Intent(getApplicationContext(), ForegroundService.class);
        UvcCamera.getInstance().SetUploadAdasDataStatus(true);
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        edogonCreateInit();
        reportCrashInfo();
    }

    public static App getInstance() {
        return _instance;
    }

    public void createDir() {
        File jpeg = new File((Environment.getExternalStorageDirectory() + "/uvccameramjpeg/") + "JPEG/");
        if (!jpeg.exists()) {
            jpeg.mkdirs();
        }
        File log = new File(LOG_PATH);
        if (!log.exists()) {
            log.mkdirs();
        }
        File edog = new File(EDOG_PATH);
        if (!edog.exists()) {
            edog.mkdirs();
        }
    }

    private void recordMessage() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread thread, Throwable ex) {
                StringWriter out = new StringWriter();
                File root = new File(App.LOG_PATH);
                if (!root.exists()) {
                    root.mkdirs();
                }
                try {
                    FileOutputStream fos = new FileOutputStream(new File(App.LOG_PATH, "error" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date(System.currentTimeMillis())) + ".txt"));
                    PrintWriter err = new PrintWriter(out);
                    for (Field field : Build.class.getDeclaredFields()) {
                        try {
                            out.append((field.getName() + ":" + field.get((Object) null)) + DoCmdUtil.COMMAND_LINE_END);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ex.printStackTrace(err);
                    fos.write(out.toString().getBytes());
                    fos.close();
                    err.close();
                    out.close();
                } catch (IllegalArgumentException e2) {
                    e2.printStackTrace();
                } catch (FileNotFoundException e3) {
                    e3.printStackTrace();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
                Process.killProcess(Process.myPid());
                System.exit(0);
                try {
                    throw ex;
                } catch (Throwable e5) {
                    e5.printStackTrace();
                }
            }
        });
    }

    private void edogonCreateInit() {
        Log.d(TAG, "TuzhiApplication  come into oncreate");
        StorageDevice.context = this;
        mTuzhiApplication = this;
        super.onCreate();
        new IntentFilter().addAction("com.dx.intent.colse_edog");
        Log.d(TAG, "register jilingou");
        new UpDownManager().AddUpinit(this, Environment.getExternalStorageDirectory().getAbsolutePath() + "/JLG");
    }

    public static final App getIntance() {
        return mTuzhiApplication;
    }

    public boolean isServiceRunning(String serviceName) {
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList) ((ActivityManager) getSystemService("activity")).getRunningServices(100);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    public void startTuzhiService() {
        this.serviceIntent = new Intent(getApplicationContext(), TuzhiService.class);
        getApplicationContext().startService(this.serviceIntent);
    }

    public void stopTuzhiService() {
        if (this.serviceIntent != null) {
            getApplicationContext().stopService(this.serviceIntent);
        }
    }

    public void onTerminate() {
        super.onTerminate();
    }

    public static void addActivity(Activity activity) {
        mList.add(activity);
    }

    public static void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null) {
                    activity.finish();
                }
            }
            mList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeSpeedWindow(Context context, View smallWindow) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService("window");
        }
        if (smallWindow != null && TuzhiService.speedfvIsVisible) {
            mWindowManager.removeView(smallWindow);
        }
        TuzhiService.speedfvIsVisible = false;
    }

    public static void removeRadarWindow(Context context, View smallWindow) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService("window");
        }
        if (smallWindow != null && TuzhiService.radarfvIsVisible) {
            mWindowManager.removeView(smallWindow);
        }
        TuzhiService.radarfvIsVisible = false;
    }

    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    public static boolean isCurrent(Context context) {
        String currentPackageName = context.getPackageName();
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1);
        if (runningTaskInfos == null) {
            return false;
        }
        if (currentPackageName.trim().equalsIgnoreCase(runningTaskInfos.get(0).topActivity.getPackageName().trim())) {
            return true;
        }
        return false;
    }

    public static void operateFloatView(Context context, String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        context.sendBroadcast(intent);
    }

    public void Edogexit() {
        Log.e("dd", "send power off ");
        TuzhiService.sBackgroundRunning = false;
        Intent intent = new Intent();
        intent.setAction("landsem.intent.action.RADAR_POWER_OFF");
        sendBroadcast(intent);
        exit();
        if (this.serviceIntent != null) {
            stopService(this.serviceIntent);
        }
        if (TuzhiService.speedfvIsVisible) {
            removeSpeedWindow(mTuzhiApplication, TuzhiService.mSpeedFloatLayout);
        }
        if (TuzhiService.radarfvIsVisible) {
            removeRadarWindow(mTuzhiApplication, TuzhiService.mRadarFloatLayout);
        }
    }

    private void reportCrashInfo() {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setAppChannel("");
        strategy.setAppPackageName(getPackageName());
        strategy.setAppVersion("VersionCode:" + CameraStateUtil.getVersionCode(getApplicationContext()) + " VersionName:" + CameraStateUtil.getVersionName(getApplicationContext()));
        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
            public Map<String, String> onCrashHandleStart(int crashType, String errorType, String errorMessage, String errorStack) {
                LinkedHashMap<String, String> map = new LinkedHashMap<>();
                map.put("CRASH_TYPE_JAVA_CRASH", "" + crashType);
                map.put("CRASH_TYPE_JAVA_CATCH", "" + crashType);
                map.put("CRASH_TYPE_NATIVE", "" + crashType);
                map.put("CRASH_TYPE_ANR", "" + crashType);
                map.put("ERROR_TYPE", errorType);
                map.put("ERROR_MESSAGE", errorMessage);
                map.put("DEV_VERSION", SharedPreferencesUtil.getLastDevVersion(App.this.getApplicationContext()));
                return map;
            }

            public byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType, String errorMessage, String errorStack) {
                try {
                    return "Extra data.".getBytes("UTF-8");
                } catch (Exception e) {
                    return null;
                }
            }
        });
        CrashReport.initCrashReport(getApplicationContext(), "37a507a16d", true, strategy);
    }
}
