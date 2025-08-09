package com.alibaba.sdk.android.oss.internal;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.OSSLog;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;

public class OSSRetryHandler {
    private int maxRetryCount = 2;

    public int getMaxRetryCount() {
        return this.maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount2) {
        this.maxRetryCount = maxRetryCount2;
    }

    public OSSRetryHandler(int maxRetryCount2) {
        this.maxRetryCount = maxRetryCount2;
    }

    public OSSRetryType shouldRetry(Exception e, int currentRetryCount) {
        if (currentRetryCount >= this.maxRetryCount) {
            return OSSRetryType.OSSRetryTypeShouldNotRetry;
        }
        if (e instanceof ClientException) {
            Exception localException = (Exception) e.getCause();
            if ((localException instanceof InterruptedIOException) && !(localException instanceof SocketTimeoutException)) {
                OSSLog.logE("[shouldRetry] - is interrupted!");
                return OSSRetryType.OSSRetryTypeShouldNotRetry;
            } else if ((localException instanceof IOException) && localException.getMessage() != null && localException.getMessage().indexOf("Canceled") != -1) {
                return OSSRetryType.OSSRetryTypeShouldNotRetry;
            } else {
                if (localException instanceof IllegalArgumentException) {
                    return OSSRetryType.OSSRetryTypeShouldNotRetry;
                }
                OSSLog.logD("shouldRetry - " + e.toString());
                e.getCause().printStackTrace();
                return OSSRetryType.OSSRetryTypeShouldRetry;
            }
        } else if (!(e instanceof ServiceException)) {
            return OSSRetryType.OSSRetryTypeShouldNotRetry;
        } else {
            ServiceException serviceException = (ServiceException) e;
            if (serviceException.getErrorCode() != null && serviceException.getErrorCode().equalsIgnoreCase("RequestTimeTooSkewed")) {
                return OSSRetryType.OSSRetryTypeShouldFixedTimeSkewedAndRetry;
            }
            if (serviceException.getStatusCode() >= 500) {
                return OSSRetryType.OSSRetryTypeShouldRetry;
            }
            return OSSRetryType.OSSRetryTypeShouldNotRetry;
        }
    }
}
