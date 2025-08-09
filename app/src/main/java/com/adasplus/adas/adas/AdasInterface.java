package com.adasplus.adas.adas;

import android.content.Context;
import com.adasplus.adas.AdasCollisionCallback;
import com.adasplus.adas.IAdasInterface;
import com.adasplus.data.AdasConfig;
import com.adasplus.data.FcwInfo;
import com.adasplus.data.LdwInfo;

public class AdasInterface {
    private IAdasInterface mAdasInterface;

    public void setCallback(AdasCollisionCallback callback) {
        this.mAdasInterface.setCallback(callback);
    }

    public AdasInterface(Context context) {
        try {
            this.mAdasInterface = (IAdasInterface) context.getClassLoader().loadClass("com.adasplus.adas.AdasInterface").getDeclaredConstructor(new Class[]{Context.class}).newInstance(new Object[]{context});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getVerifyResult() {
        return this.mAdasInterface.getVerifyResult();
    }

    public int adasInit() {
        return this.mAdasInterface.adasInit();
    }

    public int adasProcessINC(byte[] img, int width, int height, int dim) {
        return this.mAdasInterface.adasProcessINC(img, width, height, dim);
    }

    public int adasProcess(byte[] img) {
        return this.mAdasInterface.adasProcess(img);
    }

    public LdwInfo getLdwResults() {
        return this.mAdasInterface.getLdwResults();
    }

    public float getGpsSpeed() {
        return this.mAdasInterface.getGpsSpeed();
    }

    public FcwInfo getFcwResults() {
        return this.mAdasInterface.getFcwResults();
    }

    public void adasRelease() {
        this.mAdasInterface.adasRelease();
    }

    public int getStopGoResults() {
        return this.mAdasInterface.getStopGoResults();
    }

    public void setAdasConfig(AdasConfig config) {
        this.mAdasInterface.setAdasConfig(config);
    }

    public void setUserData(String carId, String userNum, String userId, String insureId) {
        this.mAdasInterface.setUserData(carId, userNum, userId, insureId);
    }

    public AdasConfig getAdasConfig() {
        return this.mAdasInterface.getAdasConfig();
    }

    public void entryDeviceCode() {
        this.mAdasInterface.entryDeviceCode();
    }

    public void setObdSpeed(boolean useObd, float speed) {
        this.mAdasInterface.setObdSpeed(useObd, speed);
    }
}
