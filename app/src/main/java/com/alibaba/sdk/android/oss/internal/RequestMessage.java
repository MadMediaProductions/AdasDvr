package com.alibaba.sdk.android.oss.internal;

import com.alibaba.sdk.android.oss.common.HttpMethod;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.HttpHeaders;
import com.alibaba.sdk.android.oss.common.utils.HttpUtil;
import com.alibaba.sdk.android.oss.common.utils.HttpdnsMini;
import com.alibaba.sdk.android.oss.common.utils.OSSUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestMessage {
    private String bucketName;
    private OSSCredentialProvider credentialProvider;
    private String downloadFilePath;
    private URI endpoint;
    private Map<String, String> headers = new HashMap();
    private boolean isAuthorizationRequired = true;
    private boolean isHttpdnsEnable = true;
    private HttpMethod method;
    private String objectKey;
    private Map<String, String> parameters = new LinkedHashMap();
    private long readStreamLength;
    private byte[] uploadData;
    private String uploadFilePath;
    private InputStream uploadInputStream;

    public HttpMethod getMethod() {
        return this.method;
    }

    public void setMethod(HttpMethod method2) {
        this.method = method2;
    }

    public URI getEndpoint() {
        return this.endpoint;
    }

    public OSSCredentialProvider getCredentialProvider() {
        return this.credentialProvider;
    }

    public void setCredentialProvider(OSSCredentialProvider credentialProvider2) {
        this.credentialProvider = credentialProvider2;
    }

    public void setEndpoint(URI endpoint2) {
        this.endpoint = endpoint2;
    }

    public boolean isHttpdnsEnable() {
        return this.isHttpdnsEnable;
    }

    public void setIsHttpdnsEnable(boolean isHttpdnsEnable2) {
        this.isHttpdnsEnable = isHttpdnsEnable2;
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

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void setHeaders(Map<String, String> headers2) {
        if (headers2 != null) {
            this.headers = headers2;
        }
    }

    public void addHeaders(Map<String, String> headers2) {
        if (headers2 != null) {
            this.headers.putAll(headers2);
        }
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public void setParameters(Map<String, String> parameters2) {
        this.parameters = parameters2;
    }

    public byte[] getUploadData() {
        return this.uploadData;
    }

    public void setUploadData(byte[] uploadData2) {
        this.uploadData = uploadData2;
    }

    public String getUploadFilePath() {
        return this.uploadFilePath;
    }

    public void setUploadFilePath(String uploadFilePath2) {
        this.uploadFilePath = uploadFilePath2;
    }

    public String getDownloadFilePath() {
        return this.downloadFilePath;
    }

    public void setDownloadFilePath(String downloadFilePath2) {
        this.downloadFilePath = downloadFilePath2;
    }

    public boolean isAuthorizationRequired() {
        return this.isAuthorizationRequired;
    }

    public void setIsAuthorizationRequired(boolean isAuthorizationRequired2) {
        this.isAuthorizationRequired = isAuthorizationRequired2;
    }

    public void setUploadInputStream(InputStream uploadInputStream2, long inputLength) {
        if (uploadInputStream2 != null) {
            this.uploadInputStream = uploadInputStream2;
            this.readStreamLength = inputLength;
        }
    }

    public InputStream getUploadInputStream() {
        return this.uploadInputStream;
    }

    public void createBucketRequestBodyMarshall(String locationConstraint) throws UnsupportedEncodingException {
        StringBuffer xmlBody = new StringBuffer();
        if (locationConstraint != null) {
            xmlBody.append("<CreateBucketConfiguration>");
            xmlBody.append("<LocationConstraint>" + locationConstraint + "</LocationConstraint>");
            xmlBody.append("</CreateBucketConfiguration>");
            byte[] binaryData = xmlBody.toString().getBytes("utf-8");
            setUploadInputStream(new ByteArrayInputStream(binaryData), (long) binaryData.length);
        }
    }

    public long getReadStreamLength() {
        return this.readStreamLength;
    }

    public String buildCanonicalURL() {
        OSSUtils.assertTrue(this.endpoint != null, "Endpoint haven't been set!");
        String scheme = this.endpoint.getScheme();
        String originHost = this.endpoint.getHost();
        if (!OSSUtils.isCname(originHost) && this.bucketName != null) {
            originHost = this.bucketName + "." + originHost;
        }
        String useHost = null;
        if (this.isHttpdnsEnable) {
            useHost = HttpdnsMini.getInstance().getIpByHostAsync(originHost);
        } else {
            OSSLog.logD("[buildCannonicalURL] - proxy exist, disable httpdns");
        }
        if (useHost == null) {
            useHost = originHost;
        }
        this.headers.put(HttpHeaders.HOST, originHost);
        String baseURL = scheme + "://" + useHost;
        if (this.objectKey != null) {
            baseURL = baseURL + "/" + HttpUtil.urlEncode(this.objectKey, "utf-8");
        }
        String queryString = OSSUtils.paramToQueryString(this.parameters, "utf-8");
        return OSSUtils.isEmptyString(queryString) ? baseURL : baseURL + "?" + queryString;
    }
}
