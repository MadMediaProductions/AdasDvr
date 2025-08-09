package com.hdsc.edog.jni;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.hdsc.edog.entity.EdogData;
import com.hdsc.edog.entity.GpsInfo;
import com.hdsc.edog.utils.Constants;
import com.hdsc.edog.utils.SharedPreUtils;
import com.hdsc.edog.utils.UpDownManager;

public class EdogDataManager implements UpDownManager.DataUpdateListener {
    private static final String TAG = "EdogDataManager";
    public static String eDogADDFilePath = null;
    public static String eDogFilePath = null;
    public static String eDogFilePath_BASE = null;
    private Context mContext;
    private SharedPreUtils sp;

    public static native EdogData getEdogData(String str, String str2, GpsInfo gpsInfo);

    static {
        System.loadLibrary("tuzhi_edog");
    }

    public EdogDataManager(Context context) {
        this.mContext = context;
        this.sp = SharedPreUtils.getInstance(context);
        UpDownManager.getInstance().setListener(this);
    }

    public boolean isRedLightOn() {
        return this.sp.getIntValue(Constants.W_RED_LIGHT) == 0;
    }

    public void setRedLightOn(boolean state) {
        int i = 1;
        SharedPreUtils sharedPreUtils = this.sp;
        if (state) {
            i = 0;
        }
        sharedPreUtils.commitIntValue(Constants.W_RED_LIGHT, i);
    }

    public boolean isSpeedLimitOn() {
        return this.sp.getIntValue(Constants.W_SPEED_LIMIT) == 0;
    }

    public void setSpeedLimitOn(boolean state) {
        int i = 1;
        SharedPreUtils sharedPreUtils = this.sp;
        if (state) {
            i = 0;
        }
        sharedPreUtils.commitIntValue(Constants.W_SPEED_LIMIT, i);
    }

    public boolean isTrafficRuleOn() {
        return this.sp.getIntValue(Constants.W_TRAFFIC_RULE) == 0;
    }

    public void setTrafficRuleOn(boolean state) {
        int i = 1;
        SharedPreUtils sharedPreUtils = this.sp;
        if (state) {
            i = 0;
        }
        sharedPreUtils.commitIntValue(Constants.W_TRAFFIC_RULE, i);
    }

    public boolean isSafeOn() {
        return this.sp.getIntValue(Constants.W_SAFE) == 0;
    }

    public void setSafeOn(boolean state) {
        int i = 1;
        SharedPreUtils sharedPreUtils = this.sp;
        if (state) {
            i = 0;
        }
        sharedPreUtils.commitIntValue(Constants.W_SAFE, i);
    }

    public void setMaxLimitSpeed(int speed) {
        this.sp.commitIntValue(Constants.W_MAX_SPEED, speed);
    }

    public int getMaxLimitSpeed() {
        int maxLimitSpeed = this.sp.getIntValue(Constants.W_MAX_SPEED);
        if (maxLimitSpeed >= 80 && maxLimitSpeed <= 160) {
            return maxLimitSpeed;
        }
        setMaxLimitSpeed(120);
        setSpeedModify(0);
        return 120;
    }

    public int getGetUSER_ID() {
        return this.sp.getIntValue("");
    }

    public boolean GetgetLogDisp() {
        return false;
    }

    public void setSpeedModify(int percent) {
        this.sp.commitIntValue(Constants.W_SPEED_MODIFY, percent);
    }

    public int getSpeedModify() {
        return this.sp.getIntValue(Constants.W_SPEED_MODIFY);
    }

    public float getModifiedSpeed(float speed) {
        return speed + ((((float) this.sp.getIntValue(Constants.W_SPEED_MODIFY)) * speed) / 100.0f);
    }

    public EdogDataInfo RgetEdogData(GpsInfo gpsInfo) {
        if (eDogFilePath == null) {
            eDogFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/uvccameramjpeg/EDOG/map03apk.bin";
        }
        if (eDogADDFilePath == null) {
            eDogADDFilePath = UpDownManager.getInstance().getAddMap();
        }
        if (eDogFilePath == null) {
            return null;
        }
        EdogData edogData = getEdogData(eDogFilePath, eDogADDFilePath, gpsInfo);
        return new EdogDataInfo(edogData.getmSpeedLimite(), edogData.getmDisToCamera(), edogData.getmFirstFindCamera(), edogData.getmFindCamera() == 1, edogData.getmTaCode(), edogData.getmVoiceCode(), edogData.getmBlockSpeed(), edogData.getmPercent(), edogData.getmBlockSpace(), edogData.getmVersion(), edogData.getmADDVersion());
    }

    public void onUpdateFinish(boolean succeed, boolean hasUpdate) {
        Log.d(TAG, "Data checking finished");
        String baseMapPath = UpDownManager.getInstance().getBaseMap();
        String addMapPath = UpDownManager.getInstance().getAddMap();
        if (baseMapPath != null) {
            eDogFilePath = baseMapPath;
        }
        if (addMapPath != null) {
            eDogADDFilePath = addMapPath;
        }
    }
}
