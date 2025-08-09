package com.fvision.camera.adas.bean;

import android.content.Context;
import android.util.Log;
import com.adasplus.adas.adas.AdasInterface;
import com.adasplus.data.AdasConfig;
import com.adasplus.data.FcwInfo;
import com.adasplus.data.LdwInfo;
import com.fvision.camera.util.LogUtils;
import com.fvision.camera.utils.SharedPreferencesUtil;

public class AdasInterfaceImp {
    private AdasInterface adasInterface;
    private AdasConfig adas_config = new AdasConfig();
    private Context mContext;
    private boolean mObdConnect;

    public AdasInterfaceImp(Context context) {
        Log.i("Adas", "New AdasInterfaceImpl");
        this.adasInterface = new AdasInterface(context);
        this.adas_config.setX(320.0f);
        this.adas_config.setY(180.0f);
        this.adas_config.setVehicleHeight(1.2f);
        this.adas_config.setVehicleWidth(1.6f);
        this.adas_config.setIsCalibCredible(0);
        AdasConfig config = this.adasInterface.getAdasConfig();
        if (config != null) {
            this.adas_config = config;
        }
        this.mContext = context;
    }

    public int init() {
        return this.adasInterface.adasInit();
    }

    public void release() {
        this.adasInterface.adasRelease();
    }

    public int process(byte[] img, int width, int height, int dim) {
        return this.adasInterface.adasProcessINC(img, width, height, dim);
    }

    public LdwInfo getLdwResults() {
        return this.adasInterface.getLdwResults();
    }

    public FcwInfo getFcwResults() {
        return this.adasInterface.getFcwResults();
    }

    public int getStopGoResults() {
        return this.adasInterface.getStopGoResults();
    }

    public void setUserData(String carId, String userNum, String userId, String insureId) {
        this.adasInterface.setUserData(carId, userNum, userId, insureId);
    }

    public int getVerifyResult() {
        return this.adasInterface.getVerifyResult();
    }

    public AdasConfig getAdasConfig() {
        return this.adasInterface.getAdasConfig();
    }

    public void setLdwEnable(boolean isOpen) {
        LogUtils.e(" setLdwEnable " + isOpen);
        AdasConfig adasConfig = getAdasConfig();
        if (adasConfig == null) {
            adasConfig = this.adas_config;
        }
        adasConfig.setIsLdwEnable(isOpen ? 1 : 0);
        this.adasInterface.setAdasConfig(adasConfig);
    }

    public void setFcwEnable(boolean isOpen) {
        LogUtils.e(" setLdwEnable " + isOpen);
        AdasConfig adasConfig = getAdasConfig();
        if (adasConfig == null) {
            adasConfig = this.adas_config;
        }
        adasConfig.setIsFcwEnable(isOpen ? 1 : 0);
        this.adasInterface.setAdasConfig(adasConfig);
    }

    public void setStgEnable(boolean isOpen) {
        AdasConfig adasConfig = getAdasConfig();
        if (adasConfig == null) {
            adasConfig = this.adas_config;
        }
        adasConfig.setIsStopgoEnable(isOpen ? 1 : 0);
        this.adasInterface.setAdasConfig(adasConfig);
    }

    public void setWarningSpeed(int ldw, int fcw) {
        AdasConfig adasConfig = getAdasConfig();
        if (adasConfig == null) {
            adasConfig = this.adas_config;
        }
        adasConfig.setLdwMinVelocity(ldw);
        adasConfig.setFcwMinVelocity(fcw);
        this.adasInterface.setAdasConfig(adasConfig);
    }

    public void setLdwMinVelocity(int ldwMinVelocity) {
        LogUtils.d(" ldwMinVelocity " + ldwMinVelocity);
        AdasConfig config = getAdasConfig();
        if (config == null) {
            config = this.adas_config;
        }
        config.setLdwMinVelocity(ldwMinVelocity);
        this.adasInterface.setAdasConfig(config);
    }

    public void setFcwMinVelocity(int fcwMinVelocity) {
        AdasConfig config = getAdasConfig();
        if (config == null) {
            config = this.adas_config;
        }
        config.setFcwMinVelocity(fcwMinVelocity);
        this.adasInterface.setAdasConfig(config);
    }

    public void setIsCalibCredible() {
        AdasConfig adasConfig = getAdasConfig();
        if (adasConfig == null) {
            adasConfig = this.adas_config;
        }
        adasConfig.setX(320.0f);
        adasConfig.setY(180.0f);
        adasConfig.setIsCalibCredible(0);
        this.adasInterface.setAdasConfig(adasConfig);
    }

    public void setVpoint(float x, float y) {
        AdasConfig adasConfig = getAdasConfig();
        if (adasConfig == null) {
            adasConfig = this.adas_config;
        }
        if (adasConfig.getX() - x > 1.0f || adasConfig.getX() - x < -1.0f || adasConfig.getY() - y > 1.0f || adasConfig.getY() - y < -1.0f) {
            adasConfig.setX(x);
            adasConfig.setY(y);
            adasConfig.setIsCalibCredible(0);
            this.adasInterface.setAdasConfig(adasConfig);
        }
    }

    public void setAdasEnable(boolean isOpen) {
        int i;
        int i2;
        int i3 = 1;
        AdasConfig adasConfig = getAdasConfig();
        if (adasConfig == null) {
            adasConfig = this.adas_config;
        }
        if (isOpen) {
            i = 1;
        } else {
            i = 0;
        }
        adasConfig.setIsLdwEnable(i);
        if (isOpen) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        adasConfig.setIsFcwEnable(i2);
        if (!isOpen) {
            i3 = 0;
        }
        adasConfig.setIsStopgoEnable(i3);
        this.adasInterface.setAdasConfig(adasConfig);
    }

    public boolean isAdasStop() {
        AdasConfig adasConfig = getAdasConfig();
        if (adasConfig == null) {
            adasConfig = this.adas_config;
        }
        return adasConfig.getIsLdwEnable() == 0 && adasConfig.getIsFcwEnable() == 0 && adasConfig.getIsStopgoEnable() == 0;
    }

    public float getAdasSpeed() {
        return this.adasInterface.getGpsSpeed();
    }

    public void setAdasConfig(AdasConfig config) {
        this.adasInterface.setAdasConfig(config);
    }

    public void setAdasSensor(int level) {
        LogUtils.d(" setAdasSensor " + level);
        AdasConfig adasConfig = getAdasConfig();
        if (adasConfig == null) {
            adasConfig = this.adas_config;
        }
        adasConfig.setFcwSensitivity(level);
        this.adasInterface.setAdasConfig(adasConfig);
    }

    public void setAdasSetting(AdasConfig config) {
        boolean z;
        boolean z2 = true;
        setFcwEnable(config.getIsFcwEnable() == 1);
        Log.e("setAdasSetting", "" + config.getIsFcwEnable());
        if (config.getIsStopgoEnable() == 1) {
            z = true;
        } else {
            z = false;
        }
        setStgEnable(z);
        if (config.getIsLdwEnable() != 1) {
            z2 = false;
        }
        setLdwEnable(z2);
        setFcwMinVelocity(config.getFcwMinVelocity());
        setLdwMinVelocity(config.getLdwMinVelocity());
        setAdasSensor(config.getFcwSensitivity());
    }

    public void initAdasParam() {
        int i;
        int i2 = 1;
        boolean isFcwEnable = SharedPreferencesUtil.getFcwEnable(this.mContext);
        boolean isStgEnable = SharedPreferencesUtil.getStgEnable(this.mContext);
        boolean isLdwEnable = SharedPreferencesUtil.getLdwEnable(this.mContext);
        Log.e("isFcwEnable", "" + isFcwEnable);
        int fcwMinVelocity = SharedPreferencesUtil.getFcwMinVelocity(this.mContext);
        int ldwMinVelocity = SharedPreferencesUtil.getLdwMinVelocity(this.mContext);
        int adasSensor = SharedPreferencesUtil.getAdasSensor(this.mContext);
        AdasConfig config = new AdasConfig();
        config.setIsFcwEnable(isFcwEnable ? 1 : 0);
        if (isStgEnable) {
            i = 1;
        } else {
            i = 0;
        }
        config.setIsStopgoEnable(i);
        if (!isLdwEnable) {
            i2 = 0;
        }
        config.setIsLdwEnable(i2);
        config.setFcwMinVelocity(fcwMinVelocity);
        config.setLdwMinVelocity(ldwMinVelocity);
        config.setFcwSensitivity(adasSensor);
        setAdasSetting(config);
    }
}
