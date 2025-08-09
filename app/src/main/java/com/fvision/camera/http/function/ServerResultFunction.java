package com.fvision.camera.http.function;

import com.fvision.camera.http.exception.ServerException;
import com.fvision.camera.http.retrofit.HttpResponse;
import com.fvision.camera.util.LogUtils;
import com.google.gson.Gson;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class ServerResultFunction implements Function<HttpResponse, Object> {
    public Object apply(@NonNull HttpResponse response) throws Exception {
        LogUtils.e(response.toString());
        if (response.isSuccess()) {
            return new Gson().toJson(response.getResult());
        }
        throw new ServerException(response.getStatus() ? 200 : -1, response.getMsg());
    }
}
