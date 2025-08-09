package com.fvision.camera.http.function;

import com.fvision.camera.http.exception.ExceptionEngine;
import com.fvision.camera.util.LogUtils;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class HttpResultFunction<T> implements Function<Throwable, Observable<T>> {
    public Observable<T> apply(@NonNull Throwable throwable) throws Exception {
        LogUtils.e("HttpResultFunction:" + throwable);
        return Observable.error((Throwable) ExceptionEngine.handleException(throwable));
    }
}
