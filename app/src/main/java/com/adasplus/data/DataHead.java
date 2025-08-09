package com.adasplus.data;

import android.os.Parcel;
import android.os.Parcelable;

public class DataHead implements Parcelable {
    public static final Creator<DataHead> CREATOR = new Creator<DataHead>() {
        public DataHead createFromParcel(Parcel in) {
            return new DataHead(in);
        }

        public DataHead[] newArray(int size) {
            return new DataHead[size];
        }
    };
    private long boottime;
    private String carid;
    private String imei;
    private String insureid;
    private String prodid;
    private String userid;
    private String usernum;
    private String version;

    public DataHead() {
    }

    protected DataHead(Parcel in) {
        this.version = in.readString();
        this.imei = in.readString();
        this.prodid = in.readString();
        this.carid = in.readString();
        this.userid = in.readString();
        this.usernum = in.readString();
        this.insureid = in.readString();
        this.boottime = in.readLong();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.version);
        dest.writeString(this.imei);
        dest.writeString(this.prodid);
        dest.writeString(this.carid);
        dest.writeString(this.userid);
        dest.writeString(this.usernum);
        dest.writeString(this.insureid);
        dest.writeLong(this.boottime);
    }

    public int describeContents() {
        return 0;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version2) {
        this.version = version2;
    }

    public String getImei() {
        return this.imei;
    }

    public void setImei(String imei2) {
        this.imei = imei2;
    }

    public String getProdid() {
        return this.prodid;
    }

    public void setProdid(String prodid2) {
        this.prodid = prodid2;
    }

    public String getCarid() {
        return this.carid;
    }

    public void setCarid(String carid2) {
        this.carid = carid2;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid2) {
        this.userid = userid2;
    }

    public String getUsernum() {
        return this.usernum;
    }

    public void setUsernum(String usernum2) {
        this.usernum = usernum2;
    }

    public String getInsureid() {
        return this.insureid;
    }

    public void setInsureid(String insureid2) {
        this.insureid = insureid2;
    }

    public long getBoottime() {
        return this.boottime;
    }

    public void setBoottime(long boottime2) {
        this.boottime = boottime2;
    }

    public String toString() {
        return "DataHead{version='" + this.version + '\'' + ", imei='" + this.imei + '\'' + ", prodid='" + this.prodid + '\'' + ", carid='" + this.carid + '\'' + ", userid='" + this.userid + '\'' + ", usernum='" + this.usernum + '\'' + ", insureid='" + this.insureid + '\'' + ", boottime=" + this.boottime + '}';
    }
}
