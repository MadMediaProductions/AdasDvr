package com.adasplus.data;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

public class PedInfo implements Parcelable {
    public static final Creator<PedInfo> CREATOR = new Creator<PedInfo>() {
        public PedInfo createFromParcel(Parcel in) {
            return new PedInfo(in);
        }

        public PedInfo[] newArray(int size) {
            return new PedInfo[size];
        }
    };
    private int perNum;
    private Person[] persons = new Person[5];
    private int state;

    public PedInfo() {
    }

    protected PedInfo(Parcel in) {
        this.state = in.readInt();
        this.perNum = in.readInt();
        this.persons = (Person[]) in.createTypedArray(Person.CREATOR);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.state);
        dest.writeInt(this.perNum);
        dest.writeTypedArray(this.persons, flags);
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

    public int getPerNum() {
        return this.perNum;
    }

    public void setPerNum(int perNum2) {
        this.perNum = perNum2;
    }

    public Person[] getCar() {
        return this.persons;
    }

    public void setPersons(Person[] persons2) {
        this.persons = persons2;
    }

    public String toString() {
        return "PedInfo{state=" + this.state + ", perNum=" + this.perNum + ", persons=" + Arrays.toString(this.persons) + '}';
    }
}
