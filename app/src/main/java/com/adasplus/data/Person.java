package com.adasplus.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {
    public static final Creator<Person> CREATOR = new Creator<Person>() {
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
    private RectInt carRect;
    private float dis;
    private float s;
    private int state;
    private float t;

    public Person() {
    }

    protected Person(Parcel in) {
        this.dis = in.readFloat();
        this.t = in.readFloat();
        this.s = in.readFloat();
        this.state = in.readInt();
        this.carRect = (RectInt) in.readParcelable(RectInt.class.getClassLoader());
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.dis);
        dest.writeFloat(this.t);
        dest.writeFloat(this.s);
        dest.writeInt(this.state);
        dest.writeParcelable(this.carRect, flags);
    }

    public int describeContents() {
        return 0;
    }

    public float getDis() {
        return this.dis;
    }

    public void setDis(float dis2) {
        this.dis = dis2;
    }

    public float getT() {
        return this.t;
    }

    public void setT(float t2) {
        this.t = t2;
    }

    public float getS() {
        return this.s;
    }

    public void setS(float s2) {
        this.s = s2;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state2) {
        this.state = state2;
    }

    public RectInt getCarRect() {
        return this.carRect;
    }

    public void setCarRect(RectInt carRect2) {
        this.carRect = carRect2;
    }

    public String toString() {
        return "Person{dis=" + this.dis + ", t=" + this.t + ", s=" + this.s + ", state=" + this.state + ", carRect=" + this.carRect + '}';
    }
}
