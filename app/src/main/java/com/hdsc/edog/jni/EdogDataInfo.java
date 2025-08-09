package com.hdsc.edog.jni;

public class EdogDataInfo {
    private int mADDVersion = 0;
    private int mAlarmType = 0;
    private int mBlockSpace = 0;
    private int mBlockSpeed = 0;
    private int mDistance = 0;
    private int mFirstFindCamera = 0;
    private boolean mIsAlarm = false;
    private int mPercent = 0;
    private int mPosition = 0;
    private int mSpeedLimit = 0;
    private int mVersion = -1;

    public EdogDataInfo(int speedLimit, int distance, int firstFindCamera, boolean isAlarm, int position, int alarmType, int blockSpeed, int percent, int blockSpace, int version, int ADDversion) {
        this.mSpeedLimit = speedLimit;
        this.mDistance = distance;
        this.mFirstFindCamera = firstFindCamera;
        this.mIsAlarm = isAlarm;
        this.mPosition = position;
        this.mAlarmType = alarmType;
        this.mBlockSpeed = blockSpeed;
        this.mPercent = percent;
        this.mBlockSpace = blockSpace;
        this.mVersion = version;
        this.mADDVersion = ADDversion;
    }

    public int getmSpeedLimit() {
        return this.mSpeedLimit;
    }

    public void setmSpeedLimit(int mSpeedLimit2) {
        this.mSpeedLimit = mSpeedLimit2;
    }

    public int getmDistance() {
        return this.mDistance;
    }

    public void setmDistance(int mDistance2) {
        this.mDistance = mDistance2;
    }

    public int getmFirstFindCamera() {
        return this.mFirstFindCamera;
    }

    public void setmFirstFindCamera(int mFirstFindCamera2) {
        this.mFirstFindCamera = mFirstFindCamera2;
    }

    public boolean ismIsAlarm() {
        return this.mIsAlarm;
    }

    public void setmIsAlarm(boolean mIsAlarm2) {
        this.mIsAlarm = mIsAlarm2;
    }

    public int getmPosition() {
        return this.mPosition;
    }

    public void setmPosition(int mPosition2) {
        this.mPosition = mPosition2;
    }

    public int getmAlarmType() {
        return this.mAlarmType;
    }

    public void setmAlarmType(int mAlarmType2) {
        this.mAlarmType = mAlarmType2;
    }

    public int getmBlockSpeed() {
        return this.mBlockSpeed;
    }

    public void setmBlockSpeed(int mBlockSpeed2) {
        this.mBlockSpeed = mBlockSpeed2;
    }

    public int getmPercent() {
        return this.mPercent;
    }

    public void setmPercent(int mPercent2) {
        this.mPercent = mPercent2;
    }

    public int getmBlockSpace() {
        return this.mBlockSpace;
    }

    public void setmBlockSpace(int mBlockSpace2) {
        this.mBlockSpace = mBlockSpace2;
    }

    public int getmVersion() {
        return this.mVersion;
    }

    public void setmVersion(int mVersion2) {
        this.mVersion = mVersion2;
    }

    public int getmADDVersion() {
        return this.mADDVersion;
    }

    public void setmADDVersion(int mADDVersion2) {
        this.mADDVersion = mADDVersion2;
    }

    public String toString() {
        return " 当前点限速，速度:" + this.mSpeedLimit + " 离当前限速点的距离:" + this.mDistance + " 是否有报警:" + this.mIsAlarm + " 报警  位置:" + this.mPosition + " mFirstFindCamera:" + this.mFirstFindCamera + " 报警类型:" + this.mAlarmType + " mBlockSpeed:" + this.mBlockSpeed + " mPercent:" + this.mPercent + "mBlockSpace:" + this.mBlockSpace + "mVersion:" + this.mVersion + "mADDVersion:" + this.mADDVersion;
    }
}
