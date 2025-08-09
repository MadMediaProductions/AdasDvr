package com.fvision.camera.adas.bean;

import android.content.Context;
import android.media.SoundPool;
import android.text.TextUtils;
import android.util.Log;
import com.adasplus.adas.Adas;
import com.adasplus.adas.adas.AdasConstants;
import com.adasplus.data.AdasConfig;
import com.adasplus.data.FcwInfo;
import com.adasplus.data.LdwInfo;
import com.fvision.camera.R;
import com.fvision.camera.utils.SharedPreferencesUtil;
import java.util.HashMap;

public class AdasModel {
    private Adas mAdas;
    private IAdasFileBackListener mAdasFileBackupListener;
    /* access modifiers changed from: private */
    public AdasInterfaceImp mAdasInterfaceImp;
    private IAdasResultListener mAdasResultListener;
    private IAdasSoundListener mAdasSoundListener;
    private float mAdasSpeed = -1.0f;
    private AdasConfig mAdasconfig;
    private String mCipher;
    private Context mContext;
    private DrawInfo mDrawInfo = new DrawInfo();
    private int mFcwBeep = 0;
    private long mFcwCnt = 0;
    private FcwInfo mFcwResults;
    private String mImei;
    private int mIsAdasInit = 0;
    private boolean mIsCalibration = true;
    private long mLastTime = 0;
    private int mLdwBeep = 0;
    private long mLdwCnt = 0;
    private LdwInfo mLdwResults;
    private HashMap<Integer, Integer> mSoundMap = new HashMap<>();
    private SoundPool mSoundPool;
    private int mStgBeep = 0;

    public interface IAdasFileBackListener {
        void backUp(String str);
    }

    public interface IAdasResultListener {
        void onResult(DrawInfo drawInfo);
    }

    public interface IAdasSoundListener {
        void sound(int i);
    }

    public void setIAdasFilebackupListener(IAdasFileBackListener listener) {
        this.mAdasFileBackupListener = listener;
    }

    public void setIAdasResultListener(IAdasResultListener listener) {
        this.mAdasResultListener = listener;
    }

    public void setIAdasSoundListener(IAdasSoundListener listener) {
        this.mAdasSoundListener = listener;
    }

    public AdasModel(final Context context) {
        this.mAdas = new Adas(context);
        this.mAdas.setPrepareListener(new Adas.PrepareListener() {
            public void onPrepare(boolean b) {
                Log.e(AdasConstants.FILE_ADAS, "onPrepare b " + b);
                if (b) {
                    AdasInterfaceImp unused = AdasModel.this.mAdasInterfaceImp = new AdasInterfaceImp(context);
                }
            }
        });
        this.mContext = context;
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
        if (this.mAdasInterfaceImp != null) {
            this.mAdasInterfaceImp.setAdasSetting(config);
        }
    }

    public boolean init(String imei, String cipher) {
        this.mImei = imei;
        this.mCipher = cipher;
        this.mIsAdasInit = 0;
        if (TextUtils.isEmpty(imei)) {
            Log.e("Adas", "-------------Cannot get DVR device id-------");
            return false;
        }
        this.mSoundPool = new SoundPool(10, 4, 5);
        this.mSoundMap.put(0, Integer.valueOf(this.mSoundPool.load(this.mContext, R.raw.warning_lane, 2)));
        this.mSoundMap.put(1, Integer.valueOf(this.mSoundPool.load(this.mContext, R.raw.warning_car, 3)));
        this.mSoundMap.put(2, Integer.valueOf(this.mSoundPool.load(this.mContext, R.raw.warning_stopgo, 1)));
        this.mSoundMap.put(3, Integer.valueOf(this.mSoundPool.load(this.mContext, R.raw.adas_ldw_en, 2)));
        this.mSoundMap.put(4, Integer.valueOf(this.mSoundPool.load(this.mContext, R.raw.adas_fcw_en, 3)));
        this.mSoundMap.put(5, Integer.valueOf(this.mSoundPool.load(this.mContext, R.raw.adas_fvd_en, 1)));
        this.mAdas.setAdasInfo(imei, this.mCipher);
        this.mAdas.install();
        return true;
    }

    public void release() {
        if (this.mAdasInterfaceImp != null) {
            this.mAdasInterfaceImp.release();
            this.mAdasInterfaceImp = null;
        }
        if (this.mAdas != null) {
            this.mAdas.release();
            this.mAdas = null;
        }
        if (this.mSoundPool != null) {
            this.mSoundPool.release();
            this.mSoundPool = null;
        }
        this.mIsAdasInit = -1;
    }

    public void processData(byte[] data, int width, int height) {
        if (this.mAdasInterfaceImp != null) {
            if (this.mAdasInterfaceImp.getVerifyResult() == 1 && this.mIsAdasInit == 0) {
                this.mIsAdasInit = this.mAdasInterfaceImp.init();
                initAdasParam();
                this.mAdasInterfaceImp.setUserData("123", "13812341234", "123", "123");
                if (this.mAdasFileBackupListener != null) {
                    this.mAdasFileBackupListener.backUp(this.mAdas.getAdasInfo());
                }
            } else if (System.currentTimeMillis() - this.mLastTime > 85 && this.mIsAdasInit == 1) {
                this.mLastTime = System.currentTimeMillis();
                this.mAdasInterfaceImp.process(data, width, height, 2);
                this.mLdwResults = this.mAdasInterfaceImp.getLdwResults();
                this.mFcwResults = this.mAdasInterfaceImp.getFcwResults();
                this.mAdasconfig = this.mAdasInterfaceImp.getAdasConfig();
                this.mAdasSpeed = this.mAdasInterfaceImp.getAdasSpeed();
                if (this.mAdasconfig != null) {
                    if (this.mAdasconfig.getIsCalibCredible() != 1) {
                        this.mIsCalibration = true;
                    } else if (this.mIsCalibration) {
                        this.mIsCalibration = false;
                    }
                }
                this.mLdwCnt++;
                this.mFcwCnt++;
                if (this.mLdwResults == null || !(this.mLdwResults.getState() == 1 || this.mLdwResults.getState() == 2)) {
                    this.mLdwBeep = 0;
                } else if (this.mLdwBeep == 0 && this.mLdwCnt > 20) {
                    this.mLdwCnt = 0;
                    this.mLdwBeep = 1;
                    if (this.mAdasSpeed > 8.333334f) {
                        if (this.mAdasSoundListener != null) {
                            this.mAdasSoundListener.sound(0);
                        } else {
                            this.mSoundPool.play(this.mSoundMap.get(0).intValue(), 1.0f, 1.0f, 0, 0, 1.0f);
                        }
                    }
                }
                if (this.mFcwResults != null && this.mFcwResults.getState() == 3) {
                    float flag = 60.0f * this.mFcwResults.getCar()[0].getT() * this.mFcwResults.getCar()[0].getT();
                    if (flag < 30.0f) {
                        flag = 30.0f;
                    }
                    if (((float) this.mFcwCnt) > flag) {
                        this.mFcwCnt = 0;
                        if (this.mAdasSpeed > 5.555556f) {
                            if (this.mAdasSoundListener != null) {
                                this.mAdasSoundListener.sound(1);
                            } else {
                                this.mSoundPool.play(this.mSoundMap.get(1).intValue(), (float) 1.0d, (float) 1.0d, 0, 0, 1.0f);
                            }
                        }
                    }
                    this.mFcwBeep = 1;
                } else if (this.mFcwResults != null && (this.mFcwResults.getState() == 0 || this.mFcwResults.getState() == 1 || this.mFcwResults.getState() == 2)) {
                    this.mFcwBeep = 0;
                }
                if (this.mAdasInterfaceImp.getStopGoResults() != 1) {
                    this.mStgBeep = 0;
                } else if (this.mStgBeep == 0) {
                    this.mStgBeep = 1;
                    if (this.mAdasSoundListener != null) {
                        this.mAdasSoundListener.sound(2);
                    } else {
                        this.mSoundPool.play(this.mSoundMap.get(2).intValue(), 1.0f, 1.0f, 0, 0, 1.0f);
                    }
                }
                if (this.mAdasResultListener != null) {
                    this.mDrawInfo.setLdwResults(this.mLdwResults);
                    this.mDrawInfo.setConfig(this.mAdasconfig);
                    this.mDrawInfo.setFcwResults(this.mFcwResults);
                    this.mDrawInfo.setSpeed(this.mAdasSpeed);
                    this.mAdasResultListener.onResult(this.mDrawInfo);
                }
            }
        }
    }

    public AdasInterfaceImp getAdasInterface() {
        return this.mAdasInterfaceImp;
    }

    public boolean adasRunning() {
        if (this.mAdasInterfaceImp == null || this.mAdasInterfaceImp.getAdasConfig() == null || this.mAdasInterfaceImp.getVerifyResult() != 1) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean isZh() {
        return this.mContext.getResources().getConfiguration().locale.getLanguage().endsWith("zh");
    }
}
