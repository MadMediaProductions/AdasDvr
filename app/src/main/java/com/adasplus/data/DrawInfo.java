package com.adasplus.data;

import android.os.Parcel;
import android.os.Parcelable;

public class DrawInfo implements Parcelable {
    public static final Creator<DrawInfo> CREATOR = new Creator<DrawInfo>() {
        public DrawInfo createFromParcel(Parcel in) {
            return new DrawInfo(in);
        }

        public DrawInfo[] newArray(int size) {
            return new DrawInfo[size];
        }
    };
    private AdasConfig config;
    private FcwInfo fcwResults;
    private LdwInfo ldwResults;

    public DrawInfo() {
    }

    protected DrawInfo(Parcel in) {
        this.ldwResults = (LdwInfo) in.readParcelable(LdwInfo.class.getClassLoader());
        this.fcwResults = (FcwInfo) in.readParcelable(FcwInfo.class.getClassLoader());
        this.config = (AdasConfig) in.readParcelable(AdasConfig.class.getClassLoader());
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.ldwResults, flags);
        dest.writeParcelable(this.fcwResults, flags);
        dest.writeParcelable(this.config, flags);
    }

    public int describeContents() {
        return 0;
    }

    public LdwInfo getLdwResults() {
        return this.ldwResults;
    }

    public void setLdwResults(LdwInfo ldwResults2) {
        this.ldwResults = ldwResults2;
    }

    public FcwInfo getFcwResults() {
        return this.fcwResults;
    }

    public void setFcwResults(FcwInfo fcwResults2) {
        this.fcwResults = fcwResults2;
    }

    public AdasConfig getConfig() {
        return this.config;
    }

    public void setConfig(AdasConfig config2) {
        this.config = config2;
    }

    public String toString() {
        return "DrawInfo{ldwResults=" + this.ldwResults + ", fcwResults=" + this.fcwResults + ", config=" + this.config + '}';
    }
}
