package com.fvision.camera.bean;

import java.io.Serializable;

public class FileBeans implements Serializable {
    public int day;
    public long dayTime = 0;
    public int fileIndex;
    public String fileName;
    public int fileType = -1;
    public int hour;
    public boolean isLock;
    public int minute;
    public int month;
    public int sencond;
    public long sorttime = 0;
    public int year;

    public String toString() {
        return "FileBeans{fileName='" + this.fileName + '\'' + ", fileType=" + this.fileType + ", fileIndex=" + this.fileIndex + ", isLock=" + this.isLock + ", sorttime=" + this.sorttime + ", dayTime=" + this.dayTime + ", year=" + this.year + ", month=" + this.month + ", day=" + this.day + ", hour=" + this.hour + ", minute=" + this.minute + ", sencond=" + this.sencond + '}';
    }
}
