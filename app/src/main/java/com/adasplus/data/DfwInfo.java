package com.adasplus.data;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

public class DfwInfo implements Parcelable {
    public static final Creator<DfwInfo> CREATOR = new Creator<DfwInfo>() {
        public DfwInfo createFromParcel(Parcel in) {
            return new DfwInfo(in);
        }

        public DfwInfo[] newArray(int size) {
            return new DfwInfo[size];
        }
    };
    private RectInt face;
    private Point[] keyPoint = new Point[30];
    private float pitch;
    private float roll;
    private int state;
    private int state1;
    private int state2;
    private int state3;
    private float yaw;

    public DfwInfo() {
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state4) {
        this.state = state4;
    }

    public int getState1() {
        return this.state1;
    }

    public void setState1(int state12) {
        this.state1 = state12;
    }

    public int getState2() {
        return this.state2;
    }

    public void setState2(int state22) {
        this.state2 = state22;
    }

    public int getState3() {
        return this.state3;
    }

    public void setState3(int state32) {
        this.state3 = state32;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw2) {
        this.yaw = yaw2;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch2) {
        this.pitch = pitch2;
    }

    public float getRoll() {
        return this.roll;
    }

    public void setRoll(float roll2) {
        this.roll = roll2;
    }

    public RectInt getFace() {
        return this.face;
    }

    public void setFace(RectInt face2) {
        this.face = face2;
    }

    public Point[] getKeyPoint() {
        return this.keyPoint;
    }

    public void setKeyPoint(Point[] keyPoint2) {
        this.keyPoint = keyPoint2;
    }

    protected DfwInfo(Parcel in) {
        this.state = in.readInt();
        this.state1 = in.readInt();
        this.state2 = in.readInt();
        this.state3 = in.readInt();
        this.yaw = in.readFloat();
        this.pitch = in.readFloat();
        this.roll = in.readFloat();
        this.face = (RectInt) in.readParcelable(RectInt.class.getClassLoader());
        this.keyPoint = (Point[]) in.createTypedArray(Point.CREATOR);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.state);
        dest.writeInt(this.state1);
        dest.writeInt(this.state2);
        dest.writeInt(this.state3);
        dest.writeFloat(this.yaw);
        dest.writeFloat(this.pitch);
        dest.writeFloat(this.roll);
        dest.writeParcelable(this.face, flags);
        dest.writeTypedArray(this.keyPoint, flags);
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "DfwInfo{state=" + this.state + ", state1=" + this.state1 + ", state2=" + this.state2 + ", state3=" + this.state3 + ", yaw=" + this.yaw + ", pitch=" + this.pitch + ", roll=" + this.roll + ", face=" + this.face + ", keyPoint=" + Arrays.toString(this.keyPoint) + '}';
    }
}
