package com.alibaba.sdk.android.oss.model;

public class CreateBucketResult extends OSSResult {
    private String bucketLocation;

    public void setBucketLocation(String location) {
        this.bucketLocation = location;
    }

    public String getBucketLocation() {
        return this.bucketLocation;
    }
}
