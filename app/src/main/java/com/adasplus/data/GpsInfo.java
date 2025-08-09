package com.adasplus.data;

import android.os.Parcel;
import android.os.Parcelable;

public class GpsInfo implements Parcelable {
    public static final Creator<GpsInfo> CREATOR = new Creator<GpsInfo>() {
        public GpsInfo createFromParcel(Parcel in) {
            return new GpsInfo(in);
        }

        public GpsInfo[] newArray(int size) {
            return new GpsInfo[size];
        }
    };
    private int flag = 0;
    private float heading;
    private float lat;
    private float lon;
    private float speed;
    private long time;

    public GpsInfo() {
    }

    protected GpsInfo(Parcel in) {
        this.flag = in.readInt();
        this.time = in.readLong();
        this.lat = in.readFloat();
        this.lon = in.readFloat();
        this.speed = in.readFloat();
        this.heading = in.readFloat();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.flag);
        dest.writeLong(this.time);
        dest.writeFloat(this.lat);
        dest.writeFloat(this.lon);
        dest.writeFloat(this.speed);
        dest.writeFloat(this.heading);
    }

    public int describeContents() {
        return 0;
    }

    public int getFlag() {
        return this.flag;
    }

    public void setFlag(int flag2) {
        this.flag = flag2;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time2) {
        this.time = time2;
    }

    public float getLat() {
        return this.lat;
    }

    public void setLat(float lat2) {
        this.lat = lat2;
    }

    public float getLon() {
        return this.lon;
    }

    public void setLon(float lon2) {
        this.lon = lon2;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed2) {
        this.speed = speed2;
    }

    public float getHeading() {
        return this.heading;
    }

    public void setHeading(float heading2) {
        this.heading = heading2;
    }

    public String toString() {
        return "GpsInfo{flag=" + this.flag + ", time=" + this.time + ", lat=" + this.lat + ", lon=" + this.lon + ", speed=" + this.speed + ", heading=" + this.heading + '}';
    }
}
