package com.adasplus.data;

import android.os.Parcel;
import android.os.Parcelable;

public class RectInt implements Parcelable {
    public static final Creator<RectInt> CREATOR = new Creator<RectInt>() {
        public RectInt createFromParcel(Parcel in) {
            return new RectInt(in);
        }

        public RectInt[] newArray(int size) {
            return new RectInt[size];
        }
    };
    private int h;
    private int w;
    private int x;
    private int y;

    public RectInt() {
    }

    protected RectInt(Parcel in) {
        this.x = in.readInt();
        this.y = in.readInt();
        this.w = in.readInt();
        this.h = in.readInt();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.x);
        dest.writeInt(this.y);
        dest.writeInt(this.w);
        dest.writeInt(this.h);
    }

    public int describeContents() {
        return 0;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x2) {
        this.x = x2;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y2) {
        this.y = y2;
    }

    public int getW() {
        return this.w;
    }

    public void setW(int w2) {
        this.w = w2;
    }

    public int getH() {
        return this.h;
    }

    public void setH(int h2) {
        this.h = h2;
    }

    public String toString() {
        return "RectInt{x=" + this.x + ", y=" + this.y + ", w=" + this.w + ", h=" + this.h + '}';
    }
}
