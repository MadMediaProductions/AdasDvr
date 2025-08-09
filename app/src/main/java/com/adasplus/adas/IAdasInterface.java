package com.adasplus.adas;

import com.adasplus.data.AdasConfig;
import com.adasplus.data.DfwInfo;
import com.adasplus.data.FcwInfo;
import com.adasplus.data.LdwInfo;
import com.adasplus.data.PedInfo;

public interface IAdasInterface {
    int adasInit();

    int adasProcess(byte[] bArr);

    int adasProcessINC(byte[] bArr, int i, int i2, int i3);

    void adasRelease();

    void entryDeviceCode();

    AdasConfig getAdasConfig();

    DfwInfo getDfwResults();

    FcwInfo getFcwResults();

    float getGpsSpeed();

    LdwInfo getLdwResults();

    PedInfo getPedResults();

    int getStopGoResults();

    int getVerifyResult();

    void setAdasConfig(AdasConfig adasConfig);

    void setCallback(AdasCollisionCallback adasCollisionCallback);

    void setObdSpeed(boolean z, float f);

    void setStopGoSpeed(float f);

    void setUserData(String str, String str2, String str3, String str4);
}
