package com.fvision.camera.http.Api;

import com.fvision.camera.http.retrofit.HttpResponse;
import io.reactivex.Observable;
import java.util.Map;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;

public interface Api {
    @GET("index.php?r=api/watch/auth")
    Observable<HttpResponse> adasAuth(@QueryMap Map<String, Object> map);

    @GET("index.php?r=api/package/check-update")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Observable<HttpResponse> updateApp(@QueryMap Map<String, Object> map);
}
