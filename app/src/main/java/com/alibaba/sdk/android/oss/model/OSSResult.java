package com.alibaba.sdk.android.oss.model;

import java.util.Map;

public class OSSResult {
    private String requestId;
    private Map<String, String> responseHeader;
    private int statusCode;

    public int getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(int statusCode2) {
        this.statusCode = statusCode2;
    }

    public Map<String, String> getResponseHeader() {
        return this.responseHeader;
    }

    public void setResponseHeader(Map<String, String> responseHeader2) {
        this.responseHeader = responseHeader2;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId2) {
        this.requestId = requestId2;
    }
}
