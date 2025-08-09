package com.fvision.camera.http.observer;

import android.text.TextUtils;
import com.fvision.camera.http.exception.ApiException;
import com.fvision.camera.http.retrofit.HttpRequestListener;
import com.fvision.camera.http.retrofit.RxActionManagerImpl;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public abstract class HttpRxObserver<T> implements Observer<T>, HttpRequestListener {
    private String mTag;

    /* access modifiers changed from: protected */
    public abstract void onError(ApiException apiException);

    /* access modifiers changed from: protected */
    public abstract void onStart(Disposable disposable);

    /* access modifiers changed from: protected */
    public abstract void onSuccess(T t);

    public HttpRxObserver() {
    }

    public HttpRxObserver(String tag) {
        this.mTag = tag;
    }

    public void onError(Throwable e) {
        RxActionManagerImpl.getInstance().remove(this.mTag);
        if (e instanceof ApiException) {
            onError((ApiException) e);
        } else {
            onError(new ApiException(e, 1000));
        }
    }

    public void onComplete() {
    }

    public void onNext(@NonNull T t) {
        if (!TextUtils.isEmpty(this.mTag)) {
            RxActionManagerImpl.getInstance().remove(this.mTag);
        }
        onSuccess(t);
    }

    public void onSubscribe(@NonNull Disposable d) {
        if (!TextUtils.isEmpty(this.mTag)) {
            RxActionManagerImpl.getInstance().add(this.mTag, d);
        }
        onStart(d);
    }

    public void cancel() {
        if (!TextUtils.isEmpty(this.mTag)) {
            RxActionManagerImpl.getInstance().cancel(this.mTag);
        }
    }

    public boolean isDisposed() {
        if (TextUtils.isEmpty(this.mTag)) {
            return true;
        }
        return RxActionManagerImpl.getInstance().isDisposed(this.mTag);
    }
}
