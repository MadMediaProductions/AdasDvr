package com.fvision.camera.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.fvision.camera.service.ForegroundService;
import com.fvision.camera.ui.MainActivity;
import com.fvision.camera.utils.LogUtils;
import com.serenegiant.usb.USBMonitor;
import java.io.File;
import v4.ContextCompat;

public class BootBroadcastReceiver extends BroadcastReceiver {
    private String path;
    private int versionState;

    public void onReceive(Context context, Intent intent) {
        LogUtils.d("111111 BootBroadcastReceiver onReceive " + intent.getAction());
        if (!intent.getAction().equals("android.intent.action.BOOT_COMPLETED") && !intent.getAction().equals("android.test.BOOT_COMPLETED") && !intent.getAction().equals(USBMonitor.ACTION_USB_DEVICE_ATTACHED) && !intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_DETACHED") && !intent.getAction().equals("android.hardware.usb.action.USB_STATE") && !intent.getAction().equals("android.hardware.usb.action.USB_ACCESSORY_ATTACHED") && !intent.getAction().equals("android.hardware.usb.action.USB_ACCESSORY_DETACHED") && !intent.getAction().equals("android.intent.action.QUICKBOOT_POWEROFF") && !intent.getAction().equals("autochips.intent.action.QB_POWERON") && intent.getAction().equals("android.intent.action.MEDIA_MOUNTED") && "".equals(".autoopen")) {
            Intent noteList = new Intent(context, MainActivity.class);
            noteList.addFlags(268435456);
            context.startActivity(noteList);
        }
        startService(context);
    }

    private void startService(Context context) {
        LogUtils.d("111111111 startService()");
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(new Intent(context, ForegroundService.class));
        } else {
            context.startService(new Intent(context, ForegroundService.class));
        }
    }

    private boolean isHavaPermission(Context context) {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE") != -1) {
            return false;
        }
        return true;
    }

    public boolean getFileName(String path2, Context context) {
        File[] subFile;
        File file = new File(path2);
        if (!file.exists() || !file.isDirectory() || (subFile = file.listFiles()) == null || subFile.length <= 0) {
            return false;
        }
        for (int fileLength = 0; fileLength < subFile.length; fileLength++) {
            if (!subFile[fileLength].isDirectory()) {
                if (!subFile[fileLength].getName().equals("VSFILE")) {
                }
            } else if (!subFile[fileLength].getName().equals("Android")) {
                return true;
            } else {
                File[] childFile = new File(subFile[fileLength].getPath()).listFiles();
                if (childFile == null || childFile.length <= 0) {
                    return false;
                }
                for (int child = 0; child < childFile.length; child++) {
                    if (childFile[child].isDirectory() && childFile[child].getName().equals("data")) {
                        File[] files = new File(childFile[child].getPath()).listFiles();
                        if (files == null || files.length <= 0) {
                            return false;
                        }
                        for (File name : files) {
                            if (!name.getName().equals("com.fvision.camera")) {
                            }
                        }
                    }
                }
                continue;
            }
        }
        return false;
    }
}
