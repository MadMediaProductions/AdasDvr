package com.fvision.camera.bean;

public class FileItem {
    private String fileName;
    private long videoTime;
    private int zhenCount;

    public FileItem(int zhenCount2, long videoTime2, String fileName2) {
        this.zhenCount = zhenCount2;
        this.videoTime = videoTime2;
        this.fileName = fileName2;
    }

    public int getZhenCount() {
        return this.zhenCount;
    }

    public void setZhenCount(int zhenCount2) {
        this.zhenCount = zhenCount2;
    }

    public long getVideoTime() {
        return this.videoTime;
    }

    public void setVideoTime(long videoTime2) {
        this.videoTime = videoTime2;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName2) {
        this.fileName = fileName2;
    }
}
