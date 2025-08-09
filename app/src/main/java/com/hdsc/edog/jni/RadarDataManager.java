package com.hdsc.edog.jni;

import android.content.Context;
import com.hdsc.edog.utils.Constants;
import com.hdsc.edog.utils.SharedPreUtils;
import com.hdsc.edog.utils.ToolUtils;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class RadarDataManager {
    private static final String TAG = "RadarDataManager";
    private static FileDescriptor mFd;
    private static FileInputStream mFileInputStream;
    private static FileOutputStream mFileOutputStream;
    public static String radarData = "";
    private String CC55Single = "AA55CC55";
    private String SADSingle = "CC55F7";
    private int hasRead_all = 0;
    private String[] kABan = {"CC5B", "CC5A", "CC59", "CC58"};
    private String[] kBan = {"CC53", "CC52", "CC51", "CC50"};
    private String[] kUBan = {"CC4B", "CC4A", "CC49", "CC48"};
    private String laser = "CC80";
    private Context mContext;
    private String noSingle = "AA55";
    private SharedPreUtils sp;
    private String[] xBan = {"CC43", "CC42", "CC41", "CC40"};

    private static native int checkRadar(String str);

    private static native FileDescriptor openRadar(String str, int i, int i2);

    public RadarDataManager(Context context) {
        this.mContext = context;
        this.sp = SharedPreUtils.getInstance(context);
    }

    public void setRadarMode(int mode) {
        this.sp.commitIntValue(Constants.W_RADAR_MODE, mode);
    }

    public int getRadarMode() {
        int RadarMode = this.sp.getIntValue(Constants.W_RADAR_MODE);
        if (RadarMode >= 1 && RadarMode <= 4) {
            return RadarMode;
        }
        setRadarMode(1);
        setRadarMuteSpeed(40);
        return 1;
    }

    public void setRadarMuteSpeed(int speed) {
        this.sp.commitIntValue(Constants.W_RADAR_MUTE_SPEED, speed);
    }

    public int getRadarMuteSpeed() {
        return this.sp.getIntValue(Constants.W_RADAR_MUTE_SPEED);
    }

    static {
        System.loadLibrary("tuzhi_edog");
    }

    public int parseRadarData(byte[] data, int hasRead) {
        if (this.hasRead_all == 0) {
            radarData = "";
        }
        String TradarData = ToolUtils.bytesToHexString(data, hasRead);
        this.hasRead_all += hasRead;
        if (this.hasRead_all > 64) {
            this.hasRead_all = 0;
        } else {
            radarData += TradarData;
        }
        if (!radarData.contains(this.noSingle)) {
            if (radarData.contains(this.laser)) {
                this.hasRead_all = 0;
                return 16;
            }
            for (int i = 0; i < this.kBan.length; i++) {
                if (radarData.contains(this.kBan[i])) {
                    this.hasRead_all = 0;
                    return i + 32;
                }
            }
            for (int i2 = 0; i2 < this.kABan.length; i2++) {
                if (radarData.contains(this.kABan[i2])) {
                    this.hasRead_all = 0;
                    return i2 + 48;
                }
            }
            for (int i3 = 0; i3 < this.kUBan.length; i3++) {
                if (radarData.contains(this.kUBan[i3])) {
                    this.hasRead_all = 0;
                    return i3 + 64;
                }
            }
            for (int i4 = 0; i4 < this.xBan.length; i4++) {
                if (radarData.contains(this.xBan[i4])) {
                    this.hasRead_all = 0;
                    return i4 + 80;
                }
            }
        }
        if (!radarData.contains(this.noSingle) || this.hasRead_all < 22) {
            return -1;
        }
        String checkData = radarData.substring(radarData.lastIndexOf(this.CC55Single) + 4);
        this.hasRead_all = 0;
        if (checkData.length() >= 60) {
            return 0;
        }
        if (checkData.contains(this.SADSingle)) {
            return checkRadar(checkData);
        }
        return 0;
    }

    public FileDescriptor initRadar() {
        String devName = this.sp.getStringValue(Constants.RADAR_COM);
        if (devName == null) {
            mFd = null;
        } else if (devName == "关闭com口") {
            mFd = null;
        } else if (devName == "ttyS0" || devName == "ttys0") {
            mFd = null;
            return mFd;
        } else {
            File device = new File("/dev/" + devName);
            device.getAbsolutePath();
            if (!device.canRead() || !device.canWrite()) {
                try {
                    Process su = Runtime.getRuntime().exec("/system/bin/su");
                    su.getOutputStream().write(("chmod 666 " + device.getAbsolutePath() + "\nexit\n").getBytes());
                    if (su.waitFor() != 0 || !device.canRead() || !device.canWrite()) {
                    }
                } catch (Exception e) {
                }
            }
            mFd = openRadar(device.getAbsolutePath(), 9600, 0);
        }
        return mFd;
    }
}
