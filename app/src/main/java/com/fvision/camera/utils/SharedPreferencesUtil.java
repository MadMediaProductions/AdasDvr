package com.fvision.camera.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    public static final String ADAS_ENABLE_SWITCH = "ADAS_ENABLE_SWITCH";
    public static final String ADAS_FCW_ENABLE = "fcw_enable";
    public static final String ADAS_FCW_MIN_VELOCITY = "fcw_min_velocity";
    private static final String ADAS_IMEI = "adas_imei";
    public static final String ADAS_IS_BACK_PALY = "ADAS_IS_BACK_PALY";
    private static final String ADAS_KEY = "adas_key";
    public static final String ADAS_LDW_ENABLE = "ldw_enable";
    public static final String ADAS_LDW_MIN_VELOCITY = "ldw_min_velocity";
    public static final String ADAS_SENSOR = "adas_sensor";
    public static final String ADAS_STG_ENABLE = "stg_enable";
    public static final String ADAS_TOGGLE = "adas_toggle";
    public static final String EDOG_AUTH_SWITCH = "edog_switch";
    public static final String EDOG_ENABLE_SWITCH = "edog_show_switch";
    public static final String EDOG_FIXED_ALARM_POINT = "edog_fixed_alarm_point";
    public static final String EDOG_IS_BACK_PLAY = "EDOG_IS_BACK_PLAY";
    public static final String EDOG_RED_LIGHT = "edog_red_light";
    public static final String EDOG_SECURITY_INFO = "edog_security_info";
    public static final String EDOG_SHOW_SWITCH = "edog_show_switch";
    public static final String EDOG_SPEEDING_ALARM = "edog_speeding_alarm";
    public static final String EDOG_SWITCH = "edog_switch";
    private static final String FILE_NAME = "save_file_name";
    public static final String IS_DEV_UPGRADE = "is_dev_upgrade";
    public static final String IS_SANCTION = "is_sanction";
    public static final String IS_SNAP_SOUND = "is_snap_sound";
    public static final String LAST_SUCCESS_OPEN_DEVICE_VERSION = "last_success_open_device_version";
    public static final String MAINACTIVITY_HAS_RUN = "mainactivity_has_run";
    public static final String PREVIEW_MODEL = "preview_model";
    public static final String SNAPSHOT_VOICE = "snapshot_voice";
    public static final String SOUND_MODEL = "sound_model";
    public static final String SWITCH_SNAPSHOT = "switch_snapshot";
    public static final String TEMPORARY_USB_PATH = "temporary_usb_path";
    public static final String USB_PATH = "usb_path";

    public static boolean isSanction(Context context) {
        return ((Boolean) getData(context, IS_SANCTION, false)).booleanValue();
    }

    public static boolean getSnapShotSound(Context context) {
        return ((Boolean) getData(context, SNAPSHOT_VOICE, true)).booleanValue();
    }

    public static void setSnapShotSound(Context context, boolean is) {
        saveData(context, SNAPSHOT_VOICE, Boolean.valueOf(is));
    }

    public static boolean getFcwEnable(Context context) {
        return ((Boolean) getData(context, ADAS_FCW_ENABLE, true)).booleanValue();
    }

    public static boolean getStgEnable(Context context) {
        return ((Boolean) getData(context, ADAS_STG_ENABLE, true)).booleanValue();
    }

    public static boolean getLdwEnable(Context context) {
        return ((Boolean) getData(context, ADAS_LDW_ENABLE, true)).booleanValue();
    }

    public static int getFcwMinVelocity(Context context) {
        return ((Integer) getData(context, ADAS_FCW_MIN_VELOCITY, 30)).intValue();
    }

    public static int getLdwMinVelocity(Context context) {
        return ((Integer) getData(context, ADAS_LDW_MIN_VELOCITY, 20)).intValue();
    }

    public static boolean getAdasToggle(Context context) {
        return ((Boolean) getData(context, ADAS_TOGGLE, false)).booleanValue();
    }

    public static void setAdasToggle(Context context, boolean is) {
        saveData(context, ADAS_TOGGLE, Boolean.valueOf(is));
    }

    public static int getAdasSensor(Context context) {
        return ((Integer) getData(context, ADAS_SENSOR, 1)).intValue();
    }

    public static void setFcwEnable(Context context, boolean is) {
        saveData(context, ADAS_FCW_ENABLE, Boolean.valueOf(is));
    }

    public static void setStgEnable(Context context, boolean is) {
        saveData(context, ADAS_STG_ENABLE, Boolean.valueOf(is));
    }

    public static void setLdwEnable(Context context, boolean is) {
        saveData(context, ADAS_LDW_ENABLE, Boolean.valueOf(is));
    }

    public static void setFcwMinVelocity(Context context, int distance) {
        saveData(context, ADAS_FCW_MIN_VELOCITY, Integer.valueOf(distance));
    }

    public static void setLdwMinVelocity(Context context, int distance) {
        saveData(context, ADAS_LDW_MIN_VELOCITY, Integer.valueOf(distance));
    }

    public static void setAdasSensor(Context context, int distance) {
        saveData(context, ADAS_SENSOR, Integer.valueOf(distance));
    }

    public static void setSanction(Context context, boolean isn) {
        saveData(context, IS_SANCTION, Boolean.valueOf(isn));
    }

    public static void saveData(Context context, String key, Object data) {
        LogUtils.e("saveDatasaveDatasaveDatasaveDatasaveDatasaveDatasaveDatasaveDatasaveDatasaveDatasaveDatasaveDatasaveData");
        String type = data.getClass().getSimpleName();
        SharedPreferences.Editor editor = context.getSharedPreferences(FILE_NAME, 0).edit();
        if ("Integer".equals(type)) {
            editor.putInt(key, ((Integer) data).intValue());
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, ((Boolean) data).booleanValue());
        } else if ("String".equals(type)) {
            editor.putString(key, (String) data);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, ((Float) data).floatValue());
        } else if ("Long".equals(type)) {
            editor.putLong(key, ((Long) data).longValue());
        }
        editor.commit();
    }

    public static Object getData(Context context, String key, Object defValue) {
        String type = defValue.getClass().getSimpleName();
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, 0);
        if ("Integer".equals(type)) {
            return Integer.valueOf(sharedPreferences.getInt(key, ((Integer) defValue).intValue()));
        }
        if ("Boolean".equals(type)) {
            return Boolean.valueOf(sharedPreferences.getBoolean(key, ((Boolean) defValue).booleanValue()));
        }
        if ("String".equals(type)) {
            return sharedPreferences.getString(key, (String) defValue);
        }
        if ("Float".equals(type)) {
            return Float.valueOf(sharedPreferences.getFloat(key, ((Float) defValue).floatValue()));
        }
        if ("Long".equals(type)) {
            return Long.valueOf(sharedPreferences.getLong(key, ((Long) defValue).longValue()));
        }
        return null;
    }

    public static boolean isDevUpgrade(Context context) {
        return ((Boolean) getData(context, IS_DEV_UPGRADE, false)).booleanValue();
    }

    public static void setDevUpgrade(Context context, boolean isn) {
        saveData(context, IS_DEV_UPGRADE, Boolean.valueOf(isn));
    }

    public static boolean isSnapSound(Context context) {
        return ((Boolean) getData(context, IS_SNAP_SOUND, false)).booleanValue();
    }

    public static void setSnapSound(Context context, boolean isn) {
        saveData(context, IS_SNAP_SOUND, Boolean.valueOf(isn));
    }

    public static String getUsbPath(Context context) {
        return (String) getData(context, USB_PATH, "");
    }

    public static void setUsbPath(Context context, String path) {
        saveData(context, USB_PATH, path);
    }

    public static boolean getEdogMainBtnToggle(Context context) {
        return ((Boolean) getData(context, "edog_show_switch", false)).booleanValue();
    }

    public static void setEdogMainBtnToggle(Context context, boolean isn) {
        saveData(context, "edog_show_switch", false);
    }

    public static boolean getEdogSettingBtnToggle(Context context) {
        return ((Boolean) getData(context, "edog_switch", false)).booleanValue();
    }

    public static void setEdogSettingBtnToggle(Context context, boolean isn) {
        saveData(context, "edog_switch", Boolean.valueOf(isn));
    }

    public static String getLastUsbPath(Context context) {
        return (String) getData(context, USB_PATH, "");
    }

    public static void setLastUsbPath(Context context, String path) {
        saveData(context, USB_PATH, path);
    }

    public static String getLastDevVersion(Context context) {
        return (String) getData(context, LAST_SUCCESS_OPEN_DEVICE_VERSION, "null");
    }

    public static void setLastDevVersion(Context context, String version) {
        saveData(context, LAST_SUCCESS_OPEN_DEVICE_VERSION, version);
    }

    public static Integer getSoundModel(Context context) {
        return (Integer) getData(context, SOUND_MODEL, 1);
    }

    public static void setSoundModel(Context context, int model) {
        saveData(context, SOUND_MODEL, Integer.valueOf(model));
    }

    public static String getAdasImei(Context context) {
        return (String) getData(context, ADAS_IMEI, "");
    }

    public static void setAdasImei(Context context, String path) {
        saveData(context, ADAS_IMEI, path);
    }

    public static String getAdasKey(Context context) {
        return (String) getData(context, ADAS_KEY, "");
    }

    public static void setAdasKey(Context context, String path) {
        saveData(context, ADAS_KEY, path);
    }

    public static boolean getEdogAuthToggle(Context context) {
        return ((Boolean) getData(context, "edog_switch", false)).booleanValue();
    }

    public static void setEdogAuthToggle(Context context, boolean is) {
        saveData(context, "edog_switch", Boolean.valueOf(is));
    }

    public static boolean getEdogEnableToggle(Context context) {
        return ((Boolean) getData(context, "edog_show_switch", false)).booleanValue();
    }

    public static void setEdogEnableToggle(Context context, boolean is) {
        saveData(context, "edog_show_switch", Boolean.valueOf(is));
    }

    public static boolean getAdasEnableToggle(Context context) {
        return ((Boolean) getData(context, ADAS_ENABLE_SWITCH, false)).booleanValue();
    }

    public static void setAdasEnableToggle(Context context, boolean is) {
        saveData(context, ADAS_ENABLE_SWITCH, Boolean.valueOf(is));
    }

    public static boolean getAdasIsBackPlayToggle(Context context) {
        return ((Boolean) getData(context, ADAS_IS_BACK_PALY, false)).booleanValue();
    }

    public static void setAdasIsBackPlayToggle(Context context, boolean is) {
        saveData(context, ADAS_IS_BACK_PALY, Boolean.valueOf(is));
    }

    public static boolean getEdogIsBackPlayToggle(Context context) {
        return ((Boolean) getData(context, EDOG_IS_BACK_PLAY, false)).booleanValue();
    }

    public static void setEdogIsBackPlayToggle(Context context, boolean is) {
        saveData(context, EDOG_IS_BACK_PLAY, Boolean.valueOf(is));
    }
}
