package com.fvision.camera.http.retrofit;

import io.reactivex.disposables.Disposable;

public interface RxActionManager<T> {
    void add(T t, Disposable disposable);

    void cancel(T t);

    void remove(T t);
}
