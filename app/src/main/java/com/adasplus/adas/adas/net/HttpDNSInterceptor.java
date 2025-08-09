package com.adasplus.adas.adas.net;

import java.io.IOException;
import java.util.Random;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HttpDNSInterceptor implements Interceptor {
    public Response intercept(Chain chain) throws IOException {
        String hostIP;
        Request originRequest = chain.request();
        HttpUrl httpUrl = originRequest.url();
        String url = httpUrl.toString();
        String host = httpUrl.host();
        if (!host.equals("androidsdk.adasplus.com")) {
            return chain.proceed(originRequest);
        }
        if (new Random().nextInt(2) == 0) {
            hostIP = "101.201.38.70";
        } else {
            hostIP = "101.201.31.10";
        }
        Request.Builder builder = originRequest.newBuilder();
        builder.url(url.replaceFirst(host, hostIP));
        builder.header("host", hostIP);
        return chain.proceed(builder.build());
    }
}
