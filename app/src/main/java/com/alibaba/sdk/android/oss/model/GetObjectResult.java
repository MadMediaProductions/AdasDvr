package com.alibaba.sdk.android.oss.model;

import java.io.InputStream;

public class GetObjectResult extends OSSResult {
    private long contentLength;
    private ObjectMetadata metadata = new ObjectMetadata();
    private InputStream objectContent;

    public ObjectMetadata getMetadata() {
        return this.metadata;
    }

    public void setMetadata(ObjectMetadata metadata2) {
        this.metadata = metadata2;
    }

    public InputStream getObjectContent() {
        return this.objectContent;
    }

    public void setObjectContent(InputStream objectContent2) {
        this.objectContent = objectContent2;
    }

    public long getContentLength() {
        return this.contentLength;
    }

    public void setContentLength(long contentLength2) {
        this.contentLength = contentLength2;
    }
}
