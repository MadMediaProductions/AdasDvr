package com.hdsc.edog.entity;

public class EdogData {
    private static int mADDVersion;
    private static int mVersion;
    private int mBlockSpace;
    private int mBlockSpeed;
    private int mDisToCamera;
    private int mFindCamera;
    private int mFirstFindCamera;
    private int mPercent;
    private int mSpeedLimite;
    private int mTaCode;
    private int mUserCameraIndex;
    private int mVoiceCode;

    public EdogData(int bFindCamera, int bFirstFindCamera, int bCameraSpeedLimite, int wUserCameraIndex, int wDisToCamera, int wVoiceCode, int wTACode, int blockSpeed, int percent, int blockSpace, int version, int ADDversion) {
        this.mFindCamera = bFindCamera;
        this.mFirstFindCamera = bFirstFindCamera;
        this.mSpeedLimite = bCameraSpeedLimite;
        this.mUserCameraIndex = wUserCameraIndex;
        this.mDisToCamera = wDisToCamera;
        this.mVoiceCode = wVoiceCode;
        this.mTaCode = wTACode;
        this.mBlockSpeed = blockSpeed;
        this.mPercent = percent;
        this.mBlockSpace = blockSpace;
        mVersion = version;
        mADDVersion = ADDversion;
    }

    public int getmFindCamera() {
        return this.mFindCamera;
    }

    public void setmFindCamera(int mFindCamera2) {
        this.mFindCamera = mFindCamera2;
    }

    public int getmFirstFindCamera() {
        return this.mFirstFindCamera;
    }

    public void setmFirstFindCamera(int mFirstFindCamera2) {
        this.mFirstFindCamera = mFirstFindCamera2;
    }

    public int getmSpeedLimite() {
        return this.mSpeedLimite;
    }

    public void setmSpeedLimite(int mSpeedLimite2) {
        this.mSpeedLimite = mSpeedLimite2;
    }

    public int getmUserCameraIndex() {
        return this.mUserCameraIndex;
    }

    public void setmUserCameraIndex(int mUserCameraIndex2) {
        this.mUserCameraIndex = mUserCameraIndex2;
    }

    public int getmDisToCamera() {
        return this.mDisToCamera;
    }

    public void setmDisToCamera(int mDisToCamera2) {
        this.mDisToCamera = mDisToCamera2;
    }

    public int getmVoiceCode() {
        return this.mVoiceCode;
    }

    public void setmVoiceCode(int mVoiceCode2) {
        this.mVoiceCode = mVoiceCode2;
    }

    public int getmTaCode() {
        return this.mTaCode;
    }

    public void setmTaCode(int mTaCode2) {
        this.mTaCode = mTaCode2;
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
        return mVersion;
    }

    public int getmADDVersion() {
        return mADDVersion;
    }
}
