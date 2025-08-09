package com.fvision.camera.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CameraStateUtil {
    public static int bytesToInt_hight2low(byte[] src, int offset) {
        return (src[offset] & 255) | ((src[offset + 1] & 255) << 8) | ((src[offset + 2] & 255) << 16) | ((src[offset + 3] & 255) << 24);
    }

    public static short bytesToShort(byte[] src, int offset) {
        return (short) ((src[offset] & 255) | ((src[offset + 1] & 255) << 8));
    }

    public static int byte2int_low2hight(byte[] bytes) {
        int length = 0;
        if (bytes.length > 0) {
            length = 0 + (bytes[0] & 255);
        }
        if (bytes.length > 1) {
            length += (bytes[1] << 8) & SupportMenu.USER_MASK;
        }
        if (bytes.length > 2) {
            length += (bytes[2] << 16) & ViewCompat.MEASURED_SIZE_MASK;
        }
        if (bytes.length > 3) {
            return length + ((bytes[3] << 24) & -1);
        }
        return length;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            String hv = Integer.toHexString(b & 255);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String secondToTimeString(int second) {
        int i = (second / 60) / 60;
        int m = (second / 60) % 60;
        int s = second % 60;
        return ("" + (m < 10 ? "0" + m : Integer.valueOf(m)) + ":") + (s < 10 ? "0" + s : Integer.valueOf(s));
    }

    public static String longToString(long time, String format) {
        if (format == null) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        return new SimpleDateFormat(format).format(new Date(time));
    }

    public static String numFormat(int num) {
        String str = "0000" + num;
        return str.substring(str.length() - 5, str.length());
    }

    public static boolean isMyUsb(String usbPath) {
        boolean isn = false;
        File file = new File(usbPath);
        File[] list = file.listFiles();
        if (list == null || list.length < 1 || !file.isDirectory()) {
            return false;
        }
        for (File ff : list) {
            if (ff.getName().equals("VSFILE")) {
                isn = true;
            }
        }
        return isn;
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getSDCardCachePath() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return Environment.getExternalStorageDirectory().getPath() + "/";
        }
        return Environment.getDataDirectory().getPath() + "/";
    }

    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        List<ActivityManager.RunningTaskInfo> list = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1);
        if (list == null || list.size() <= 0 || !list.get(0).topActivity.getClassName().contains(className)) {
            return false;
        }
        return true;
    }

    public static int getVersionCode(Context mContext) {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean isNetworkAvalible(Context context) {
        NetworkInfo[] net_info;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager == null || (net_info = connectivityManager.getAllNetworkInfo()) == null) {
            return false;
        }
        for (NetworkInfo state : net_info) {
            if (state.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }
        return false;
    }
}
