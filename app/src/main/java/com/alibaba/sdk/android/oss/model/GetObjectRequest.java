package com.alibaba.sdk.android.oss.model;

public class GetObjectRequest extends OSSRequest {
    private String bucketName;
    private String objectKey;
    private Range range;

    public GetObjectRequest(String bucketName2, String objectKey2) {
        this.bucketName = bucketName2;
        this.objectKey = objectKey2;
    }

    public String getBucketName() {
        return this.bucketName;
    }

    public void setBucketName(String bucketName2) {
        this.bucketName = bucketName2;
    }

    public String getObjectKey() {
        return this.objectKey;
    }

    public void setObjectKey(String objectKey2) {
        this.objectKey = objectKey2;
    }

    public Range getRange() {
        return this.range;
    }

    public void setRange(Range range2) {
        this.range = range2;
    }
}
