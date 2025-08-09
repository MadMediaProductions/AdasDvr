package com.alibaba.sdk.android.oss.model;

import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;

public class AppendObjectRequest extends OSSRequest {
    private String bucketName;
    private ObjectMetadata metadata;
    private String objectKey;
    private long position;
    private OSSProgressCallback<AppendObjectRequest> progressCallback;
    private byte[] uploadData;
    private String uploadFilePath;

    public AppendObjectRequest(String bucketName2, String objectKey2, String uploadFilePath2) {
        this.bucketName = bucketName2;
        this.objectKey = objectKey2;
        this.uploadFilePath = uploadFilePath2;
    }

    public AppendObjectRequest(String bucketName2, String objectKey2, String uploadFilePath2, ObjectMetadata metadata2) {
        this.bucketName = bucketName2;
        this.objectKey = objectKey2;
        this.uploadFilePath = uploadFilePath2;
        this.metadata = metadata2;
    }

    public AppendObjectRequest(String bucketName2, String objectKey2, byte[] uploadData2) {
        this.bucketName = bucketName2;
        this.objectKey = objectKey2;
        this.uploadData = uploadData2;
    }

    public AppendObjectRequest(String bucketName2, String objectKey2, byte[] uploadData2, ObjectMetadata metadata2) {
        this.bucketName = bucketName2;
        this.objectKey = objectKey2;
        this.uploadData = uploadData2;
        this.metadata = metadata2;
    }

    public long getPosition() {
        return this.position;
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

    public String getUploadFilePath() {
        return this.uploadFilePath;
    }

    public void setUploadFilePath(String uploadFilePath2) {
        this.uploadFilePath = uploadFilePath2;
    }

    public byte[] getUploadData() {
        return this.uploadData;
    }

    public void setUploadData(byte[] uploadData2) {
        this.uploadData = uploadData2;
    }

    public ObjectMetadata getMetadata() {
        return this.metadata;
    }

    public void setMetadata(ObjectMetadata metadata2) {
        this.metadata = metadata2;
    }

    public OSSProgressCallback<AppendObjectRequest> getProgressCallback() {
        return this.progressCallback;
    }

    public void setProgressCallback(OSSProgressCallback<AppendObjectRequest> progressCallback2) {
        this.progressCallback = progressCallback2;
    }

    public void setPosition(long position2) {
        this.position = position2;
    }
}
