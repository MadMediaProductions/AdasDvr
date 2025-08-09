package com.fvision.camera.bean;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AdasResult implements Serializable {
    @SerializedName("err")
    private int err = -1;

    public int getErr() {
        return this.err;
    }

    public void setErr(int err2) {
        this.err = err2;
    }

    public String toString() {
        switch (this.err) {
            case 0:
                return "注册成功";
            case 101:
                return "序列列号已注册";
            case 102:
                return "输⼊入参数错误";
            case 103:
                return "IP未授权（发起register请求的服务器器IP地址需要事先在中科⻰龙智登记）";
            case 200:
                return "⼚厂商未授权";
            case 201:
                return "服务器异常";
            default:
                return "未知:" + this.err;
        }
    }
}
