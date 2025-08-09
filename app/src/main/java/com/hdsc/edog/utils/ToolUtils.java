package com.hdsc.edog.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class ToolUtils {
    private static ToolUtils instance;

    private ToolUtils() {
    }

    public static ToolUtils getInstance() {
        if (instance == null) {
            instance = new ToolUtils();
        }
        return instance;
    }

    public boolean getGpsStatus(LocationManager locationManager) {
        return locationManager.isProviderEnabled("gps");
    }

    public void openGPS(Context context) {
        context.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
    }

    public void showRunBgNotify(Context context) {
    }

    public void exitNotify(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService("notification");
        if (manager != null) {
            manager.cancel(1);
        }
    }

    public String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String bytesToHexString(byte[] src, int srclength) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < srclength; i++) {
            String hv = Integer.toHexString(src[i] & 255).toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static void copyBaseFile(Context context, String mMyDir) throws Throwable {
        try {
            File file = new File(mMyDir + "/map03apk.bin");
            if (file.exists()) {
                file.delete();
            }
            File rootFile = new File(mMyDir);
            if (!rootFile.exists()) {
                rootFile.mkdirs();
            }
            file.createNewFile();
            InputStream inStream = context.getAssets().open("map03apk.bin");
            OutputStream outStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            while (true) {
                int length = inStream.read(buffer);
                if (length > 0) {
                    outStream.write(buffer, 0, length);
                } else {
                    outStream.flush();
                    outStream.close();
                    Log.e("copyFile", "ToolUtils  copyBaseFile  ");
                    inStream.close();
                    return;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public static void sleep(int time) {
        try {
            Thread.currentThread();
            Thread.sleep((long) time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean isBootStart(Context context) {
        return SharedPreUtils.getInstance(context).getIntValue(Constants.BOOT_START) == 1;
    }

    public static String[] getFiles(String path) {
        File[] allFiles = new File(path).listFiles();
        List<String> fileNameList = new ArrayList<>();
        for (File file : allFiles) {
            if (file.getName().contains("tty")) {
                fileNameList.add(file.getName());
            }
        }
        if (fileNameList.size() == 0) {
            return null;
        }
        String[] filesName = new String[fileNameList.size()];
        for (int i = 0; i < fileNameList.size(); i++) {
            filesName[i] = fileNameList.get(i);
        }
        return filesName;
    }

    public static boolean checkActivityExist(Context ctx, Intent intent) {
        List<ResolveInfo> list = ctx.getPackageManager().queryIntentActivities(intent, 0);
        Log.i("BootReceiver", "activity list is: " + list.size());
        if (list.size() > 0) {
            return true;
        }
        return false;
    }
}
