package com.hdsc.edog.jni;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StorageDevice {
    public static Context context;
    public boolean mMoveable;
    public String mPath;
    public String mState;

    public static StorageDevice[] getDevices() {
        try {
            try {
                Object[] storageVolume = (Object[]) StorageManager.class.getDeclaredMethod("getVolumeList", new Class[0]).invoke((StorageManager) context.getSystemService("storage"), new Object[0]);
                if (storageVolume != null) {
                    StorageDevice[] devices = new StorageDevice[storageVolume.length];
                    try {
                        Method isRemovable = storageVolume[0].getClass().getDeclaredMethod("isRemovable", new Class[0]);
                        Method getPath = storageVolume[0].getClass().getDeclaredMethod("getPath", new Class[0]);
                        Method getState = storageVolume[0].getClass().getDeclaredMethod("getState", new Class[0]);
                        for (int i = 0; i < devices.length; i++) {
                            devices[i] = new StorageDevice();
                            devices[i].mMoveable = ((Boolean) isRemovable.invoke(storageVolume[i], new Object[0])).booleanValue();
                            devices[i].mPath = (String) getPath.invoke(storageVolume[i], new Object[0]);
                            devices[i].mState = (String) getState.invoke(storageVolume[i], new Object[0]);
                        }
                        return devices;
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            } catch (IllegalArgumentException e3) {
                e3.printStackTrace();
            } catch (InvocationTargetException e4) {
                e4.printStackTrace();
            }
            return null;
        } catch (NoSuchMethodException e5) {
            e5.printStackTrace();
            return null;
        }
    }

    private static String getSDPath() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    public static String getExternaSDPath() {
        StorageDevice[] devices = getDevices();
        if (devices != null) {
            int i = 0;
            while (true) {
                if (i >= devices.length) {
                    break;
                } else if (!devices[i].mMoveable) {
                    i++;
                } else if (devices[i].mState.contains("mounted")) {
                    Log.i("liyichang ", "find sd");
                    StorageDevice Candidate = devices[i];
                }
            }
        }
        if (0 != 0) {
            return null.mPath;
        }
        Log.i("liyichang ", "find def sd");
        return getSDPath();
    }
}
