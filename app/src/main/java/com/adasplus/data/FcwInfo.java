package com.adasplus.data;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

public class FcwInfo implements Parcelable {
    public static final Creator<FcwInfo> CREATOR = new Creator<FcwInfo>() {
        public FcwInfo createFromParcel(Parcel in) {
            return new FcwInfo(in);
        }

        public FcwInfo[] newArray(int size) {
            return new FcwInfo[size];
        }
    };
    private CarInfo[] car = new CarInfo[5];
    private int carNum;
    private int state;

    public FcwInfo() {
    }

    protected FcwInfo(Parcel in) {
        this.state = in.readInt();
        this.carNum = in.readInt();
        this.car = (CarInfo[]) in.createTypedArray(CarInfo.CREATOR);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.state);
        dest.writeInt(this.carNum);
        dest.writeTypedArray(this.car, flags);
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

    public int getCarNum() {
        return this.carNum;
    }

    public void setCarNum(int carNum2) {
        this.carNum = carNum2;
    }

    public CarInfo[] getCar() {
        return this.car;
    }

    public void setCar(CarInfo[] car2) {
        this.car = car2;
    }

    public String toString() {
        return "FcwInfo{state=" + this.state + ", carNum=" + this.carNum + ", car=" + Arrays.toString(this.car) + '}';
    }
}
