package com.alibaba.sdk.android.oss.model;

import com.alibaba.sdk.android.oss.common.OSSConstants;
import com.alibaba.sdk.android.oss.common.OSSHeaders;
import com.alibaba.sdk.android.oss.common.utils.DateUtil;
import com.alibaba.sdk.android.oss.common.utils.HttpHeaders;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ObjectMetadata {
    public static final String AES_256_SERVER_SIDE_ENCRYPTION = "AES256";
    private Map<String, Object> metadata = new HashMap();
    private Map<String, String> userMetadata = new HashMap();

    public Map<String, String> getUserMetadata() {
        return this.userMetadata;
    }

    public void setUserMetadata(Map<String, String> userMetadata2) {
        this.userMetadata.clear();
        if (userMetadata2 != null && !userMetadata2.isEmpty()) {
            this.userMetadata.putAll(userMetadata2);
        }
    }

    public void setHeader(String key, Object value) {
        this.metadata.put(key, value);
    }

    public void addUserMetadata(String key, String value) {
        this.userMetadata.put(key, value);
    }

    public Date getLastModified() {
        return (Date) this.metadata.get(HttpHeaders.LAST_MODIFIED);
    }

    public void setLastModified(Date lastModified) {
        this.metadata.put(HttpHeaders.LAST_MODIFIED, lastModified);
    }

    public Date getExpirationTime() throws ParseException {
        return DateUtil.parseRfc822Date((String) this.metadata.get(HttpHeaders.EXPIRES));
    }

    public String getRawExpiresValue() {
        return (String) this.metadata.get(HttpHeaders.EXPIRES);
    }

    public void setExpirationTime(Date expirationTime) {
        this.metadata.put(HttpHeaders.EXPIRES, DateUtil.formatRfc822Date(expirationTime));
    }

    public long getContentLength() {
        Long contentLength = (Long) this.metadata.get(HttpHeaders.CONTENT_LENGTH);
        if (contentLength == null) {
            return 0;
        }
        return contentLength.longValue();
    }

    public void setContentLength(long contentLength) {
        if (contentLength > OSSConstants.DEFAULT_FILE_SIZE_LIMIT) {
            throw new IllegalArgumentException("内容长度不能超过5G字节。");
        }
        this.metadata.put(HttpHeaders.CONTENT_LENGTH, Long.valueOf(contentLength));
    }

    public String getContentType() {
        return (String) this.metadata.get("Content-Type");
    }

    public void setContentType(String contentType) {
        this.metadata.put("Content-Type", contentType);
    }

    public String getContentMD5() {
        return (String) this.metadata.get(HttpHeaders.CONTENT_MD5);
    }

    public void setContentMD5(String contentMD5) {
        this.metadata.put(HttpHeaders.CONTENT_MD5, contentMD5);
    }

    public String getContentEncoding() {
        return (String) this.metadata.get("Content-Encoding");
    }

    public void setContentEncoding(String encoding) {
        this.metadata.put("Content-Encoding", encoding);
    }

    public String getCacheControl() {
        return (String) this.metadata.get(HttpHeaders.CACHE_CONTROL);
    }

    public void setCacheControl(String cacheControl) {
        this.metadata.put(HttpHeaders.CACHE_CONTROL, cacheControl);
    }

    public String getContentDisposition() {
        return (String) this.metadata.get("Content-Disposition");
    }

    public void setContentDisposition(String disposition) {
        this.metadata.put("Content-Disposition", disposition);
    }

    public String getETag() {
        return (String) this.metadata.get(HttpHeaders.ETAG);
    }

    public String getServerSideEncryption() {
        return (String) this.metadata.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION);
    }

    public void setServerSideEncryption(String serverSideEncryption) {
        this.metadata.put(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION, serverSideEncryption);
    }

    public String getObjectType() {
        return (String) this.metadata.get(OSSHeaders.OSS_OBJECT_TYPE);
    }

    public Map<String, Object> getRawMetadata() {
        return Collections.unmodifiableMap(this.metadata);
    }
}
