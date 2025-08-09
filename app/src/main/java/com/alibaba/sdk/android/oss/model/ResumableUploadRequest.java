package com.alibaba.sdk.android.oss.model;

import android.support.v4.media.session.PlaybackStateCompat;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSConstants;
import java.io.File;
import java.util.Map;

public class ResumableUploadRequest extends OSSRequest {
    private String bucketName;
    private Map<String, String> callbackParam;
    private Map<String, String> callbackVars;
    private ObjectMetadata metadata;
    private String objectKey;
    private long partSize = PlaybackStateCompat.ACTION_SET_REPEAT_MODE;
    private OSSProgressCallback<ResumableUploadRequest> progressCallback;
    private String recordDirectory;
    private String uploadFilePath;

    public ResumableUploadRequest(String bucketName2, String objectKey2, String uploadFilePath2) {
        this.bucketName = bucketName2;
        this.objectKey = objectKey2;
        this.uploadFilePath = uploadFilePath2;
    }

    public ResumableUploadRequest(String bucketName2, String objectKey2, String uploadFilePath2, ObjectMetadata metadata2) {
        this.bucketName = bucketName2;
        this.objectKey = objectKey2;
        this.uploadFilePath = uploadFilePath2;
        this.metadata = metadata2;
    }

    public ResumableUploadRequest(String bucketName2, String objectKey2, String uploadFilePath2, String recordDirectory2) {
        this.bucketName = bucketName2;
        this.objectKey = objectKey2;
        this.uploadFilePath = uploadFilePath2;
        setRecordDirectory(recordDirectory2);
    }

    public ResumableUploadRequest(String bucketName2, String objectKey2, String uploadFilePath2, ObjectMetadata metadata2, String recordDirectory2) {
        this.bucketName = bucketName2;
        this.objectKey = objectKey2;
        this.uploadFilePath = uploadFilePath2;
        this.metadata = metadata2;
        setRecordDirectory(recordDirectory2);
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

    public String getRecordDirectory() {
        return this.recordDirectory;
    }

    public void setRecordDirectory(String recordDirectory2) {
        File file = new File(recordDirectory2);
        if (!file.exists() || !file.isDirectory()) {
            throw new IllegalArgumentException("Record directory must exist, and it should be a directory!");
        }
        this.recordDirectory = recordDirectory2;
    }

    public ObjectMetadata getMetadata() {
        return this.metadata;
    }

    public void setMetadata(ObjectMetadata metadata2) {
        this.metadata = metadata2;
    }

    public OSSProgressCallback<ResumableUploadRequest> getProgressCallback() {
        return this.progressCallback;
    }

    public void setProgressCallback(OSSProgressCallback<ResumableUploadRequest> progressCallback2) {
        this.progressCallback = progressCallback2;
    }

    public long getPartSize() {
        return this.partSize;
    }

    public void setPartSize(long partSize2) {
        if (partSize2 < OSSConstants.MIN_PART_SIZE_LIMIT) {
            throw new IllegalArgumentException("Part size must be greater than or equal to 100KB!");
        }
        this.partSize = partSize2;
    }

    public Map<String, String> getCallbackParam() {
        return this.callbackParam;
    }

    public void setCallbackParam(Map<String, String> callbackParam2) {
        this.callbackParam = callbackParam2;
    }

    public Map<String, String> getCallbackVars() {
        return this.callbackVars;
    }

    public void setCallbackVars(Map<String, String> callbackVars2) {
        this.callbackVars = callbackVars2;
    }
}
