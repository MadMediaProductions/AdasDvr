package com.hdsc.edog.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreUtils {
    public static final String KEY_ISSHOW_DIALOG = "dialogshow";
    private static SharedPreUtils spUtils;
    private final String SPNAME = "tuzhi_edog";
    SharedPreferences sp;

    private SharedPreUtils(Context context) {
        this.sp = context.getSharedPreferences("tuzhi_edog", 0);
    }

    public static SharedPreUtils getInstance(Context context) {
        if (spUtils == null) {
            spUtils = new SharedPreUtils(context);
        }
        return spUtils;
    }

    public void commitIntValue(String key, int value) {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getIntValue(String key) {
        return this.sp.getInt(key, 0);
    }

    public void commitStringValue(String key, String value) {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getStringValue(String key) {
        return this.sp.getString(key, (String) null);
    }

    public boolean getBooleanValue(String key, boolean defaultValue) {
        return this.sp.getBoolean(key, defaultValue);
    }

    public void commitBooleanValue(String key, boolean value) {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
}
