package com.fvision.camera.http.Api;

import com.fvision.camera.http.retrofit.RetrofitUtils;

public class ApiUtils {
    private static Api api;

    public static Api getApi() {
        if (api == null) {
            api = (Api) RetrofitUtils.get().retrofit().create(Api.class);
        }
        return api;
    }
}
