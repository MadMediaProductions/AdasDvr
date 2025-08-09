package com.fvision.camera.utils;

import android.os.Environment;

public class Const {
    public static final String BROAD_CAST_HIDE_FLOATWINDOW = "android.action.hide.floatwindow";
    public static final String BROAD_CAST_SHOW_FLOATWINDOW = "android.action.show.floatwindow";
    public static final String BROAD_CAST_SYNC_TIME = "broadcast.sync.time";
    public static final String BROAD_PLAY_ADAS_SOUND = "android.action.jr.adas.play.sound";
    public static final String BROAD_PLAY_EDOG_SOUND = "android.action.jr.edog.play.sound";
    public static final String DEV_PACKAGE_NAME = "com.fvision.camera";
    public static final String JPG_PATH = (ROOT_PATH + "JPEG/");
    public static final String ROOT_PATH = (Environment.getExternalStorageDirectory() + "/uvccameramjpeg/");
    public static final String isPendingIntent = "isPendingIntent";
}
