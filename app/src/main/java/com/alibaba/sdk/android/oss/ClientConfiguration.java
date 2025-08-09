package com.alibaba.sdk.android.oss;

import com.alibaba.sdk.android.oss.common.utils.VersionInfoUtils;

public class ClientConfiguration {
    private static final int DEFAULT_MAX_RETRIES = 2;
    private static final String DEFAULT_USER_AGENT = VersionInfoUtils.getDefaultUserAgent();
    private int connectionTimeout = 15000;
    private int maxConcurrentRequest = 5;
    private int maxErrorRetry = 2;
    private int socketTimeout = 15000;

    public int getMaxConcurrentRequest() {
        return this.maxConcurrentRequest;
    }

    public void setMaxConcurrentRequest(int maxConcurrentRequest2) {
        this.maxConcurrentRequest = maxConcurrentRequest2;
    }

    public int getSocketTimeout() {
        return this.socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout2) {
        this.socketTimeout = socketTimeout2;
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout2) {
        this.connectionTimeout = connectionTimeout2;
    }

    public int getMaxErrorRetry() {
        return this.maxErrorRetry;
    }

    public void setMaxErrorRetry(int maxErrorRetry2) {
        this.maxErrorRetry = maxErrorRetry2;
    }
}
