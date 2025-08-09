package com.alibaba.sdk.android.oss.network;

import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import okhttp3.OkHttpClient;

public class ExecutionContext<T extends OSSRequest> {
    private CancellationHandler cancellationHandler = new CancellationHandler();
    private OkHttpClient client;
    private OSSCompletedCallback completedCallback;
    private OSSProgressCallback progressCallback;
    private T request;

    public ExecutionContext(OkHttpClient client2, T request2) {
        this.client = client2;
        this.request = request2;
    }

    public T getRequest() {
        return this.request;
    }

    public void setRequest(T request2) {
        this.request = request2;
    }

    public OkHttpClient getClient() {
        return this.client;
    }

    public void setClient(OkHttpClient client2) {
        this.client = client2;
    }

    public CancellationHandler getCancellationHandler() {
        return this.cancellationHandler;
    }

    public void setCancellationHandler(CancellationHandler cancellationHandler2) {
        this.cancellationHandler = cancellationHandler2;
    }

    public OSSCompletedCallback getCompletedCallback() {
        return this.completedCallback;
    }

    public void setCompletedCallback(OSSCompletedCallback completedCallback2) {
        this.completedCallback = completedCallback2;
    }

    public OSSProgressCallback getProgressCallback() {
        return this.progressCallback;
    }

    public void setProgressCallback(OSSProgressCallback progressCallback2) {
        this.progressCallback = progressCallback2;
    }
}
