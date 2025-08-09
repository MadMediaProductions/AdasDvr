package com.adasplus.adas.adas.net;

public interface IReponseListener<T> {
    void afterRequest();

    void beforeRequest();

    void connectNetworkFail(String str);

    void onFail(String str);

    void onSuccess(Result<T> result);
}
