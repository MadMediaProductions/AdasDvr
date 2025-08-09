package com.fvision.camera.http.exception;

import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import org.json.JSONException;
import retrofit2.HttpException;

public class ExceptionEngine {
    public static final int ACTIVATE_LIMIT = 1012;
    public static final int ANALYTIC_CLIENT_DATA_ERROR = 1002;
    public static final int ANALYTIC_SERVER_DATA_ERROR = 1001;
    public static final int CONNECT_ERROR = 1003;
    public static final int TIME_OUT_ERROR = 1004;
    public static final int UN_KNOWN_ERROR = 1000;

    public static ApiException handleException(Throwable e) {
        if (e instanceof HttpException) {
            ApiException ex = new ApiException(e, ((HttpException) e).code());
            ex.setMsg("网络错误");
            return ex;
        } else if (e instanceof ServerException) {
            ServerException serverExc = (ServerException) e;
            ApiException ex2 = new ApiException((Throwable) serverExc, serverExc.getCode());
            ex2.setMsg(serverExc.getMsg());
            return ex2;
        } else if ((e instanceof JsonParseException) || (e instanceof JSONException) || (e instanceof ParseException) || (e instanceof MalformedJsonException)) {
            ApiException ex3 = new ApiException(e, 1001);
            ex3.setMsg("解析错误");
            return ex3;
        } else if (e instanceof ConnectException) {
            ApiException ex4 = new ApiException(e, 1003);
            ex4.setMsg("连接失败");
            return ex4;
        } else if (e instanceof SocketTimeoutException) {
            ApiException ex5 = new ApiException(e, 1004);
            ex5.setMsg("网络超时");
            return ex5;
        } else {
            ApiException ex6 = new ApiException(e, 1000);
            ex6.setMsg("未知错误");
            return ex6;
        }
    }
}
