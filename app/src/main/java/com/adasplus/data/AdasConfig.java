package com.adasplus.data;

import android.os.Parcel;
import android.os.Parcelable;

public class AdasConfig implements Parcelable {
    public static final Creator<AdasConfig> CREATOR = new Creator<AdasConfig>() {
        public AdasConfig createFromParcel(Parcel in) {
            return new AdasConfig(in);
        }

        public AdasConfig[] newArray(int size) {
            return new AdasConfig[size];
        }
    };
    private int dfwMinVelocity;
    private int dfwSensitivity;
    private int fcwMinVelocity;
    private int fcwSensitivity;
    private int isCalibCredible;
    private int isDfwEnable;
    private int isFcwEnable;
    private int isLdwEnable;
    private int isPedEnable;
    private int isStopgoEnable;
    private int ldwMinVelocity;
    private int ldwSensitivity;
    private int pedMinVelocity;
    private int pedSensitivity;
    private float vehicleHeight;
    private float vehicleWidth;
    private float x;
    private float y;

    public AdasConfig() {
    }

    protected AdasConfig(Parcel in) {
        this.isCalibCredible = in.readInt();
        this.x = in.readFloat();
        this.y = in.readFloat();
        this.vehicleHeight = in.readFloat();
        this.vehicleWidth = in.readFloat();
        this.ldwSensitivity = in.readInt();
        this.fcwSensitivity = in.readInt();
        this.pedSensitivity = in.readInt();
        this.dfwSensitivity = in.readInt();
        this.ldwMinVelocity = in.readInt();
        this.fcwMinVelocity = in.readInt();
        this.pedMinVelocity = in.readInt();
        this.dfwMinVelocity = in.readInt();
        this.isLdwEnable = in.readInt();
        this.isFcwEnable = in.readInt();
        this.isStopgoEnable = in.readInt();
        this.isPedEnable = in.readInt();
        this.isDfwEnable = in.readInt();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.isCalibCredible);
        dest.writeFloat(this.x);
        dest.writeFloat(this.y);
        dest.writeFloat(this.vehicleHeight);
        dest.writeFloat(this.vehicleWidth);
        dest.writeInt(this.ldwSensitivity);
        dest.writeInt(this.fcwSensitivity);
        dest.writeInt(this.pedSensitivity);
        dest.writeInt(this.dfwSensitivity);
        dest.writeInt(this.ldwMinVelocity);
        dest.writeInt(this.fcwMinVelocity);
        dest.writeInt(this.pedMinVelocity);
        dest.writeInt(this.dfwMinVelocity);
        dest.writeInt(this.isLdwEnable);
        dest.writeInt(this.isFcwEnable);
        dest.writeInt(this.isStopgoEnable);
        dest.writeInt(this.isPedEnable);
        dest.writeInt(this.isDfwEnable);
    }

    public int describeContents() {
        return 0;
    }

    public int getIsCalibCredible() {
        return this.isCalibCredible;
    }

    public void setIsCalibCredible(int isCalibCredible2) {
        this.isCalibCredible = isCalibCredible2;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x2) {
        this.x = x2;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y2) {
        this.y = y2;
    }

    public float getVehicleHeight() {
        return this.vehicleHeight;
    }

    public void setVehicleHeight(float vehicleHeight2) {
        this.vehicleHeight = vehicleHeight2;
    }

    public float getVehicleWidth() {
        return this.vehicleWidth;
    }

    public void setVehicleWidth(float vehicleWidth2) {
        this.vehicleWidth = vehicleWidth2;
    }

    public int getLdwSensitivity() {
        return this.ldwSensitivity;
    }

    public void setLdwSensitivity(int ldwSensitivity2) {
        this.ldwSensitivity = ldwSensitivity2;
    }

    public int getFcwSensitivity() {
        return this.fcwSensitivity;
    }

    public void setFcwSensitivity(int fcwSensitivity2) {
        this.fcwSensitivity = fcwSensitivity2;
    }

    public int getPedSensitivity() {
        return this.pedSensitivity;
    }

    public void setPedSensitivity(int pedSensitivity2) {
        this.pedSensitivity = pedSensitivity2;
    }

    public int getDfwSensitivity() {
        return this.dfwSensitivity;
    }

    public void setDfwSensitivity(int dfwSensitivity2) {
        this.dfwSensitivity = dfwSensitivity2;
    }

    public int getLdwMinVelocity() {
        return this.ldwMinVelocity;
    }

    public void setLdwMinVelocity(int ldwMinVelocity2) {
        this.ldwMinVelocity = ldwMinVelocity2;
    }

    public int getFcwMinVelocity() {
        return this.fcwMinVelocity;
    }

    public void setFcwMinVelocity(int fcwMinVelocity2) {
        this.fcwMinVelocity = fcwMinVelocity2;
    }

    public int getPedMinVelocity() {
        return this.pedMinVelocity;
    }

    public void setPedMinVelocity(int pedMinVelocity2) {
        this.pedMinVelocity = pedMinVelocity2;
    }

    public int getDfwMinVelocity() {
        return this.dfwMinVelocity;
    }

    public void setDfwMinVelocity(int dfwMinVelocity2) {
        this.dfwMinVelocity = dfwMinVelocity2;
    }

    public int getIsLdwEnable() {
        return this.isLdwEnable;
    }

    public void setIsLdwEnable(int isLdwEnable2) {
        this.isLdwEnable = isLdwEnable2;
    }

    public int getIsFcwEnable() {
        return this.isFcwEnable;
    }

    public void setIsFcwEnable(int isFcwEnable2) {
        this.isFcwEnable = isFcwEnable2;
    }

    public int getIsStopgoEnable() {
        return this.isStopgoEnable;
    }

    public void setIsStopgoEnable(int isStopgoEnable2) {
        this.isStopgoEnable = isStopgoEnable2;
    }

    public int getIsPedEnable() {
        return this.isPedEnable;
    }

    public void setIsPedEnable(int isPedEnable2) {
        this.isPedEnable = isPedEnable2;
    }

    public int getIsDfwEnable() {
        return this.isDfwEnable;
    }

    public void setIsDfwEnable(int isDfwEnable2) {
        this.isDfwEnable = isDfwEnable2;
    }

    public String toString() {
        return "AdasConfig{isCalibCredible=" + this.isCalibCredible + ", x=" + this.x + ", y=" + this.y + ", vehicleHeight=" + this.vehicleHeight + ", vehicleWidth=" + this.vehicleWidth + ", ldwSensitivity=" + this.ldwSensitivity + ", fcwSensitivity=" + this.fcwSensitivity + ", pedSensitivity=" + this.pedSensitivity + ", dfwSensitivity=" + this.dfwSensitivity + ", ldwMinVelocity=" + this.ldwMinVelocity + ", fcwMinVelocity=" + this.fcwMinVelocity + ", pedMinVelocity=" + this.pedMinVelocity + ", dfwMinVelocity=" + this.dfwMinVelocity + ", isLdwEnable=" + this.isLdwEnable + ", isFcwEnable=" + this.isFcwEnable + ", isStopgoEnable=" + this.isStopgoEnable + ", isPedEnable=" + this.isPedEnable + ", isDfwEnable=" + this.isDfwEnable + '}';
    }
}
