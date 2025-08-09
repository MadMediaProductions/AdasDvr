package com.alibaba.sdk.android.oss.model;

public class PartETag {
    private String eTag;
    private int partNumber;

    public PartETag(int partNumber2, String eTag2) {
        this.partNumber = partNumber2;
        this.eTag = eTag2;
    }

    public int getPartNumber() {
        return this.partNumber;
    }

    public void setPartNumber(int partNumber2) {
        this.partNumber = partNumber2;
    }

    public String getETag() {
        return this.eTag;
    }

    public void setETag(String eTag2) {
        this.eTag = eTag2;
    }
}
