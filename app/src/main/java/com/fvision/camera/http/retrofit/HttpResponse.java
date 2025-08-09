package com.fvision.camera.http.retrofit;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class HttpResponse {
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private Object result;
    @SerializedName("status")
    private boolean status;

    public boolean isSuccess() {
        return this.status;
    }

    public String toString() {
        return "[http response]{\"status\": " + this.status + ",\"msg\":" + this.msg + ",\"result\":" + new Gson().toJson(this.result) + "}";
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg2) {
        this.msg = msg2;
    }

    public boolean getStatus() {
        return this.status;
    }

    public void setStatus(boolean status2) {
        this.status = status2;
    }

    public Object getResult() {
        return this.result;
    }

    public void setResult(Object result2) {
        this.result = result2;
    }
}
