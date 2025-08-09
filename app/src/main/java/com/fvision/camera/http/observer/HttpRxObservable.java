package com.fvision.camera.http.observer;

import com.fvision.camera.http.function.HttpResultFunction;
import com.fvision.camera.http.function.ServerResultFunction;
import com.fvision.camera.http.retrofit.HttpResponse;
import com.fvision.camera.util.LogUtils;
import com.google.gson.Gson;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.Map;

public class HttpRxObservable {
    public static Observable getObservable(Observable<HttpResponse> apiObservable) {
        return apiObservable.map(new ServerResultFunction()).onErrorResumeNext(new HttpResultFunction()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable getObservable(Observable<HttpResponse> apiObservable, LifecycleProvider lifecycle) {
        if (lifecycle != null) {
            return apiObservable.map(new ServerResultFunction()).compose(lifecycle.bindToLifecycle()).onErrorResumeNext(new HttpResultFunction()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }
        return getObservable(apiObservable);
    }

    public static Observable getObservable(Observable<HttpResponse> apiObservable, LifecycleProvider<ActivityEvent> lifecycle, ActivityEvent event) {
        if (lifecycle != null) {
            return apiObservable.map(new ServerResultFunction()).compose(lifecycle.bindUntilEvent(event)).onErrorResumeNext(new HttpResultFunction()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }
        return getObservable(apiObservable);
    }

    public static Observable getObservable(Observable<HttpResponse> apiObservable, LifecycleProvider<FragmentEvent> lifecycle, FragmentEvent event) {
        if (lifecycle != null) {
            return apiObservable.map(new ServerResultFunction()).compose(lifecycle.bindUntilEvent(event)).onErrorResumeNext(new HttpResultFunction()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }
        return getObservable(apiObservable);
    }

    private static void showLog(Map<String, Object> request) {
        if (request == null || request.size() == 0) {
            LogUtils.e("[http request]:");
        }
        LogUtils.e("[http request]:" + new Gson().toJson((Object) request));
    }
}
