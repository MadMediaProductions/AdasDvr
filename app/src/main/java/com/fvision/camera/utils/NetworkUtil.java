package com.fvision.camera.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo networkinfo;
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService("connectivity");
        if (manager == null || (networkinfo = manager.getActiveNetworkInfo()) == null || !networkinfo.isAvailable()) {
            return false;
        }
        return true;
    }
}
