package com.adasplus.adas.util;

import android.hardware.Camera;
import java.io.FileDescriptor;
import java.lang.reflect.InvocationTargetException;

public class ReflectUtils {
    private static final String METHOD_SETSHAREFD = "setShareFD";

    public static boolean isExistsetShareFD() {
        try {
            if (Class.forName("android.hardware.Camera").getDeclaredMethod(METHOD_SETSHAREFD, new Class[]{FileDescriptor.class}) != null) {
                return true;
            }
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static int setShareFD(Camera camera, FileDescriptor fileDescriptor) {
        if (!isExistsetShareFD()) {
            return -1;
        }
        try {
            return ((Integer) camera.getClass().getDeclaredMethod(METHOD_SETSHAREFD, new Class[]{FileDescriptor.class}).invoke(camera, new Object[]{fileDescriptor})).intValue();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e2) {
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
        }
        return -1;
    }
}
