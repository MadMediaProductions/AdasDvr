package com.adasplus.data;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

public class LaneInfo implements Parcelable {
    public static final Creator<LaneInfo> CREATOR = new Creator<LaneInfo>() {
        public LaneInfo createFromParcel(Parcel in) {
            return new LaneInfo(in);
        }

        public LaneInfo[] newArray(int size) {
            return new LaneInfo[size];
        }
    };
    private int isCredible;
    private Point[] points = new Point[2];

    public LaneInfo() {
    }

    protected LaneInfo(Parcel in) {
        this.isCredible = in.readInt();
        this.points = (Point[]) in.createTypedArray(Point.CREATOR);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.isCredible);
        dest.writeTypedArray(this.points, flags);
    }

    public int describeContents() {
        return 0;
    }

    public int getIsCredible() {
        return this.isCredible;
    }

    public void setIsCredible(int isCredible2) {
        this.isCredible = isCredible2;
    }

    public Point[] getPoints() {
        return this.points;
    }

    public void setPoints(Point[] points2) {
        this.points = points2;
    }

    public String toString() {
        return "LaneInfo{isCredible=" + this.isCredible + ", points=" + Arrays.toString(this.points) + '}';
    }
}
