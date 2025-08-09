package com.adasplus.data;

import android.os.Parcel;
import android.os.Parcelable;

public class LdwInfo implements Parcelable {
    public static final Creator<LdwInfo> CREATOR = new Creator<LdwInfo>() {
        public LdwInfo createFromParcel(Parcel in) {
            return new LdwInfo(in);
        }

        public LdwInfo[] newArray(int size) {
            return new LdwInfo[size];
        }
    };
    private LaneInfo left;
    private LaneInfo right;
    private int state;

    public LdwInfo() {
    }

    protected LdwInfo(Parcel in) {
        this.state = in.readInt();
        this.left = (LaneInfo) in.readParcelable(LaneInfo.class.getClassLoader());
        this.right = (LaneInfo) in.readParcelable(LaneInfo.class.getClassLoader());
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.state);
        dest.writeParcelable(this.left, flags);
        dest.writeParcelable(this.right, flags);
    }

    public int describeContents() {
        return 0;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state2) {
        this.state = state2;
    }

    public LaneInfo getLeft() {
        return this.left;
    }

    public void setLeft(LaneInfo left2) {
        this.left = left2;
    }

    public LaneInfo getRight() {
        return this.right;
    }

    public String toString() {
        return "LdwInfo{state=" + this.state + ", left=" + this.left + ", right=" + this.right + '}';
    }

    public void setRight(LaneInfo right2) {
        this.right = right2;
    }
}
