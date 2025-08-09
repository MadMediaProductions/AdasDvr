package com.fvision.camera.bean;

import android.util.Log;

public class CameraStateBean {
    int adasState = -1;
    boolean cam_format_state = false;
    boolean cam_lock_state = false;
    boolean cam_mode_state = false;
    boolean cam_motion_detect = false;
    boolean cam_mute_state = false;
    boolean cam_pic_state = false;
    boolean cam_play_state = false;
    boolean cam_plist_state = false;
    boolean cam_rec_state = false;
    boolean cam_sd_state = false;
    boolean cam_tf_cu_state = false;
    boolean cam_water_state = false;
    int cur_time = -1;
    byte[] devicePackageName = null;
    byte[] deviceVersion = null;
    byte[] deviceVersionCode = null;
    int duration_gsensor = -1;
    int file_index = -1;
    int playSpeed = -1;
    int save_1;
    byte[] stateFrame = null;
    int total_time = -1;

    public boolean isCam_mode_state() {
        boolean z = true;
        if (this.stateFrame == null) {
            return false;
        }
        if ((32768 & byte2Short(0, 2)) <= 1) {
            z = false;
        }
        this.cam_mode_state = z;
        return this.cam_mode_state;
    }

    public boolean isCam_rec_state() {
        boolean z = true;
        if (this.stateFrame == null) {
            return false;
        }
        if ((byte2Short(0, 2) & 16384) <= 1) {
            z = false;
        }
        this.cam_rec_state = z;
        return this.cam_rec_state;
    }

    public boolean isCam_lock_state() {
        boolean z = true;
        if (this.stateFrame == null) {
            return false;
        }
        if ((byte2Short(0, 2) & 8192) <= 1) {
            z = false;
        }
        this.cam_lock_state = z;
        return this.cam_lock_state;
    }

    public boolean isCam_play_state() {
        boolean z = true;
        if (this.stateFrame == null) {
            return false;
        }
        if ((byte2Short(0, 2) & 4096) <= 1) {
            z = false;
        }
        this.cam_play_state = z;
        return this.cam_play_state;
    }

    public boolean isCam_water_state() {
        boolean z = true;
        if (this.stateFrame == null) {
            return false;
        }
        if ((byte2Short(0, 2) & 2048) <= 1) {
            z = false;
        }
        this.cam_water_state = z;
        return this.cam_water_state;
    }

    public boolean isCam_mute_state() {
        boolean z = true;
        if (this.stateFrame == null) {
            return false;
        }
        if ((byte2Short(0, 2) & 1024) <= 1) {
            z = false;
        }
        this.cam_mute_state = z;
        return this.cam_mute_state;
    }

    public boolean isCam_sd_state() {
        boolean z = true;
        if (this.stateFrame == null) {
            return false;
        }
        if ((byte2Short(0, 2) & 512) <= 1) {
            z = false;
        }
        this.cam_sd_state = z;
        return this.cam_sd_state;
    }

    public boolean isCam_pic_state() {
        boolean z = true;
        if (this.stateFrame == null) {
            return false;
        }
        if ((byte2Short(0, 2) & 256) <= 1) {
            z = false;
        }
        this.cam_pic_state = z;
        return this.cam_pic_state;
    }

    public boolean isCam_motion_detect() {
        boolean z = true;
        if (this.stateFrame == null) {
            return false;
        }
        if ((byte2Short(0, 2) & 128) <= 1) {
            z = false;
        }
        this.cam_motion_detect = z;
        return this.cam_motion_detect;
    }

    public boolean isCam_format_state() {
        boolean z = true;
        if (this.stateFrame == null) {
            return false;
        }
        if ((byte2Short(0, 2) & 64) <= 1) {
            z = false;
        }
        this.cam_format_state = z;
        return this.cam_format_state;
    }

    public boolean isCam_plist_state() {
        boolean z = true;
        if (this.stateFrame == null) {
            return false;
        }
        if ((byte2Short(0, 2) & 32) <= 1) {
            z = false;
        }
        this.cam_plist_state = z;
        return this.cam_plist_state;
    }

    public boolean isCam_tf_cu_state() {
        boolean z = false;
        if (this.stateFrame == null) {
            return false;
        }
        if ((byte2Short(0, 2) & 16) == 0) {
            z = true;
        }
        this.cam_tf_cu_state = z;
        return this.cam_tf_cu_state;
    }

    public int getTotal_time() {
        if (this.stateFrame == null) {
            return -1;
        }
        this.total_time = byte2Short(2, 2);
        return this.total_time;
    }

    public int getCur_time() {
        if (this.stateFrame == null) {
            return -1;
        }
        this.cur_time = byte2Short(4, 2);
        return this.cur_time;
    }

    public int getDuration_gsensor() {
        if (this.stateFrame == null) {
            return -1;
        }
        this.duration_gsensor = this.stateFrame[6];
        return this.duration_gsensor;
    }

    public int getSave_1() {
        if (this.stateFrame == null) {
            return -1;
        }
        this.save_1 = this.stateFrame[7];
        return this.save_1;
    }

    public int getFile_index() {
        if (this.stateFrame == null) {
            return -1;
        }
        this.file_index = byte2Short(8, 2);
        return this.file_index;
    }

    public String getPasswd() {
        if (this.deviceVersion == null) {
            this.deviceVersion = new byte[10];
        }
        if (this.stateFrame == null) {
            return "";
        }
        System.arraycopy(this.stateFrame, 28, this.deviceVersion, 0, this.deviceVersion.length);
        StringBuffer version = new StringBuffer();
        byte[] bArr = this.deviceVersion;
        int length = bArr.length;
        for (int i = 0; i < length; i++) {
            version.append("" + ((char) bArr[i]));
        }
        return version.toString();
    }

    public int getPlaySpeed() {
        if (this.stateFrame == null) {
            return -1;
        }
        this.playSpeed = byte2Short(38, 2);
        return this.playSpeed;
    }

    public int getAdasState() {
        if (this.stateFrame == null) {
            return -1;
        }
        this.adasState = getAdasState(this.stateFrame);
        return this.adasState;
    }

    public int getAdasState(byte[] frame) {
        if (frame == null) {
            return -1;
        }
        return frame[50];
    }

    public byte[] getStateFrame() {
        return this.stateFrame;
    }

    public void setStateFrame(byte[] stateFrame2) {
        this.stateFrame = stateFrame2;
    }

    private int byte2int(int startPos, int length) {
        int i = 4;
        if (length >= 4) {
            i = length;
        }
        byte[] newByte = new byte[i];
        System.arraycopy(this.stateFrame, startPos, newByte, 0, length);
        return bytesToInt_hight2low(newByte, 0);
    }

    private short byte2Short(int startPos, int length) {
        int i = 2;
        if (length >= 2) {
            i = length;
        }
        byte[] newByte = new byte[i];
        System.arraycopy(this.stateFrame, startPos, newByte, 0, length);
        return bytesToShort(newByte, 0);
    }

    private short byte2Short(byte[] frame, int startPos, int length) {
        int i = 2;
        if (length >= 2) {
            i = length;
        }
        byte[] newByte = new byte[i];
        System.arraycopy(frame, startPos, newByte, 0, length);
        return bytesToShort(newByte, 0);
    }

    public void print() {
        Log.d("zoulequan", toString());
    }

    public String toString() {
        return " 回放:" + isCam_mode_state() + " 录像:" + isCam_rec_state() + " 加锁:" + isCam_lock_state() + " 播放:" + isCam_play_state() + " 水印:" + isCam_water_state() + " 静音:" + isCam_mute_state() + " SD:" + isCam_sd_state() + " 拍照:" + isCam_pic_state() + " 移动侦测:" + isCam_motion_detect() + " 格式化:" + isCam_format_state() + " 是否需要格式化:" + isCam_tf_cu_state() + " 更新文件列表:" + isCam_plist_state() + " 回放文件的总时长:" + getTotal_time() + " 回放文件时当前已播放的时长:" + getCur_time() + " duration_gsensor:" + getDuration_gsensor() + " save_1:" + getSave_1() + " 版本号:" + getPasswd() + " PLIST 里的文件数量:" + getFile_index() + " 播放速度:" + getPlaySpeed() + " ADAS状态:" + getAdasState();
    }

    public boolean isMainStateChange(byte[] frame) {
        if (isCam_mode_state(frame) == isCam_mode_state() && isCam_rec_state(frame) == isCam_rec_state() && isCam_lock_state(frame) == isCam_lock_state() && isCam_play_state(frame) == isCam_play_state() && isCam_water_state(frame) == isCam_water_state() && isCam_mute_state(frame) == isCam_mute_state() && isCam_sd_state(frame) == isCam_sd_state() && isCam_pic_state(frame) == isCam_pic_state() && isCam_motion_detect(frame) == isCam_motion_detect() && isCam_format_state(frame) == isCam_format_state() && getCur_time(frame) == getCur_time() && getSave_1(frame) == getSave_1() && getAdasState(frame) == getAdasState()) {
            return false;
        }
        return true;
    }

    public int getSave_1(byte[] frame) {
        return frame[7];
    }

    public boolean isCam_mode_state(byte[] frame) {
        boolean z = true;
        if ((32768 & byte2Short(frame, 0, 2)) <= 1) {
            z = false;
        }
        this.cam_mode_state = z;
        return this.cam_mode_state;
    }

    public boolean isCam_rec_state(byte[] frame) {
        boolean z = true;
        if ((byte2Short(frame, 0, 2) & 16384) <= 1) {
            z = false;
        }
        this.cam_rec_state = z;
        return this.cam_rec_state;
    }

    public boolean isCam_lock_state(byte[] frame) {
        boolean z = true;
        if ((byte2Short(frame, 0, 2) & 8192) <= 1) {
            z = false;
        }
        this.cam_lock_state = z;
        return this.cam_lock_state;
    }

    public boolean isCam_play_state(byte[] frame) {
        boolean z = true;
        if ((byte2Short(frame, 0, 2) & 4096) <= 1) {
            z = false;
        }
        this.cam_play_state = z;
        return this.cam_play_state;
    }

    public boolean isCam_water_state(byte[] frame) {
        boolean z = true;
        if ((byte2Short(frame, 0, 2) & 2048) <= 1) {
            z = false;
        }
        this.cam_water_state = z;
        return this.cam_water_state;
    }

    public boolean isCam_mute_state(byte[] frame) {
        boolean z = true;
        if ((byte2Short(frame, 0, 2) & 1024) <= 1) {
            z = false;
        }
        this.cam_mute_state = z;
        return this.cam_mute_state;
    }

    public boolean isCam_sd_state(byte[] frame) {
        boolean z = true;
        if ((byte2Short(frame, 0, 2) & 512) <= 1) {
            z = false;
        }
        this.cam_sd_state = z;
        return this.cam_sd_state;
    }

    public boolean isCam_pic_state(byte[] frame) {
        boolean z = true;
        if ((byte2Short(frame, 0, 2) & 256) <= 1) {
            z = false;
        }
        this.cam_pic_state = z;
        return this.cam_pic_state;
    }

    public boolean isCam_motion_detect(byte[] frame) {
        boolean z = true;
        if ((byte2Short(frame, 0, 2) & 128) <= 1) {
            z = false;
        }
        this.cam_motion_detect = z;
        return this.cam_motion_detect;
    }

    public boolean isCam_format_state(byte[] frame) {
        boolean z = true;
        if ((byte2Short(frame, 0, 2) & 64) <= 1) {
            z = false;
        }
        this.cam_format_state = z;
        return this.cam_format_state;
    }

    public boolean isCam_plist_state(byte[] frame) {
        boolean z = true;
        if ((byte2Short(frame, 0, 2) & 32) <= 1) {
            z = false;
        }
        this.cam_plist_state = z;
        return this.cam_plist_state;
    }

    public int getCur_time(byte[] frame) {
        this.cur_time = byte2Short(frame, 4, 2);
        return this.cur_time;
    }

    private int bytesToInt_hight2low(byte[] src, int offset) {
        return (src[offset] & 255) | ((src[offset + 1] & 255) << 8) | ((src[offset + 2] & 255) << 16) | ((src[offset + 3] & 255) << 24);
    }

    private short bytesToShort(byte[] src, int offset) {
        return (short) ((src[offset] & 255) | ((src[offset + 1] & 255) << 8));
    }

    public String getDevVersion() {
        if (this.deviceVersion == null) {
            this.deviceVersion = new byte[12];
        }
        if (this.stateFrame == null) {
            return "";
        }
        System.arraycopy(this.stateFrame, 28, this.deviceVersion, 0, this.deviceVersion.length);
        StringBuffer version = new StringBuffer();
        byte[] bArr = this.deviceVersion;
        int length = bArr.length;
        for (int i = 0; i < length; i++) {
            version.append("" + ((char) bArr[i]));
        }
        return version.toString();
    }

    public String getDevPackageName() {
        if (this.devicePackageName == null) {
            this.devicePackageName = new byte[64];
        }
        if (this.stateFrame == null) {
            return "";
        }
        System.arraycopy(this.stateFrame, 60, this.devicePackageName, 0, this.devicePackageName.length);
        return new String(this.devicePackageName).trim();
    }

    public int getDevVersionCode() {
        if (this.deviceVersionCode == null) {
            this.deviceVersionCode = new byte[4];
        }
        if (this.stateFrame == null) {
            return -1;
        }
        System.arraycopy(this.stateFrame, 56, this.deviceVersionCode, 0, this.deviceVersionCode.length);
        return bytesToInt_hight2low(this.deviceVersionCode, 0);
    }
}
