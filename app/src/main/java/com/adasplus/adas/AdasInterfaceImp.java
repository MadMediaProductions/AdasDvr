package com.adasplus.adas;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.adasplus.adas.adas.AdasConstants;
import com.adasplus.adas.adas.AdasInterface;
import com.adasplus.adas.adas.BuildConfig;
import com.adasplus.adas.adas.net.RequestManager;
import com.adasplus.adas.util.LogUtil;
import com.adasplus.adas.util.Util;
import com.adasplus.data.AdasConfig;
import com.adasplus.data.FcwInfo;
import com.adasplus.data.LdwInfo;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class AdasInterfaceImp {
    AdasInterface adasInterface;
    AdasConfig adas_config = new AdasConfig();

    public AdasInterfaceImp(Context mContext) {
        this.adasInterface = new AdasInterface(mContext);
        this.adasInterface.setCallback(new AdasCollisionCallback() {
            public void collision(int i) {
                if (i == 1) {
                    Log.i("Debug", "一级碰撞");
                } else if (i == 2) {
                    Log.i("Debug", "二级碰撞");
                }
            }
        });
        this.adas_config.setX(320.0f);
        this.adas_config.setY(180.0f);
        this.adas_config.setVehicleHeight(1.2f);
        this.adas_config.setVehicleWidth(1.6f);
        this.adas_config.setIsCalibCredible(0);
        AdasConfig config = this.adasInterface.getAdasConfig();
        if (config != null) {
            this.adas_config = config;
        }
    }

    public int init() {
        return this.adasInterface.adasInit();
    }

    public void release() {
        this.adasInterface.adasRelease();
    }

    public void stop() {
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
        AdasConfig adasConfig = getAdasConfig();
        if (adasConfig == null) {
            adasConfig = this.adas_config;
        }
        adasConfig.setIsLdwEnable(isOpen ? 1 : 0);
        this.adasInterface.setAdasConfig(adasConfig);
    }

    public void setFcwEnable(boolean isOpen) {
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

    public float getAdasSpeed() {
        return this.adasInterface.getGpsSpeed();
    }

    public boolean isAdasStop() {
        AdasConfig adasConfig = getAdasConfig();
        if (adasConfig == null) {
            adasConfig = this.adas_config;
        }
        return adasConfig.getIsLdwEnable() == 0 && adasConfig.getIsFcwEnable() == 0 && adasConfig.getIsStopgoEnable() == 0;
    }

    public static void entryDeviceCode(final Context context, final IAdasEntryListener callback) {
        new Thread() {
            public void run() {
                super.run();
                int count = 0;
                if (!Util.isNetworkConnected(context)) {
                    LogUtil.logE("Cannot connect work!");
                    callback.onResult(-1);
                    return;
                }
                String deviceCode = Util.getDeviceCode(context);
                while (true) {
                    if (!TextUtils.isEmpty(deviceCode)) {
                        break;
                    }
                    int count2 = count + 1;
                    if (count >= 5) {
                        count = count2;
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    deviceCode = Util.getDeviceCode(context);
                    count = count2;
                }
                if (count != 4 || !TextUtils.isEmpty(deviceCode)) {
                    Map<String, String> params = new HashMap<>();
                    String time = String.valueOf(System.currentTimeMillis());
                    params.put("IMEIS", deviceCode);
                    params.put(AdasConstants.STR_TIMESTAMP, time);
                    params.put(AdasConstants.STR_MERCHANTID, BuildConfig.ADAS_VERSION_MERCHANTID);
                    params.put(AdasConstants.STR_SIGN, Util.getStrMd5(time + Util.getStrMd5(BuildConfig.ADAS_VERSION_MERCHANTID).toLowerCase()).toLowerCase());
                    if (!Util.isNetworkConnected(context)) {
                        callback.onResult(-1);
                        return;
                    }
                    String result = RequestManager.getInstance(context).getReponseByPostMethod(AdasConstants.CREATE_KEY_URL, params);
                    LogUtil.logE(result);
                    try {
                        if (Integer.valueOf(new JSONObject(result).getInt("resultCode")).intValue() == 0) {
                            callback.onResult(0);
                        } else {
                            callback.onResult(-2);
                        }
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }
                } else {
                    LogUtil.logE("Cannot get device code!");
                    callback.onResult(-2);
                }
            }
        }.start();
    }
}
