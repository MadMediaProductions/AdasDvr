package com.fvision.camera.http.retrofit;

import android.annotation.TargetApi;
import android.support.v4.util.ArrayMap;
import io.reactivex.disposables.Disposable;

public class RxActionManagerImpl implements RxActionManager<Object> {
    private static volatile RxActionManagerImpl mInstance;
    private ArrayMap<Object, Disposable> mMaps = new ArrayMap<>();

    public static RxActionManagerImpl getInstance() {
        if (mInstance == null) {
            synchronized (RxActionManagerImpl.class) {
                if (mInstance == null) {
                    mInstance = new RxActionManagerImpl();
                }
            }
        }
        return mInstance;
    }

    @TargetApi(19)
    private RxActionManagerImpl() {
    }

    @TargetApi(19)
    public void add(Object tag, Disposable disposable) {
        this.mMaps.put(tag, disposable);
    }

    @TargetApi(19)
    public void remove(Object tag) {
        if (!this.mMaps.isEmpty()) {
            this.mMaps.remove(tag);
        }
    }

    @TargetApi(19)
    public void cancel(Object tag) {
        if (!this.mMaps.isEmpty() && this.mMaps.get(tag) != null) {
            if (!this.mMaps.get(tag).isDisposed()) {
                this.mMaps.get(tag).dispose();
            }
            this.mMaps.remove(tag);
        }
    }

    public boolean isDisposed(Object tag) {
        if (this.mMaps.isEmpty() || this.mMaps.get(tag) == null) {
            return true;
        }
        return this.mMaps.get(tag).isDisposed();
    }
}
