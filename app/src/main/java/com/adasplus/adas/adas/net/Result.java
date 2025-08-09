package com.adasplus.adas.adas.net;

public class Result<T> {
    private String code;
    private T data;
    private String msg;

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public T getData() {
        return this.data;
    }

    public void setCode(String code2) {
        this.code = code2;
    }

    public void setMsg(String msg2) {
        this.msg = msg2;
    }

    public void setData(T data2) {
        this.data = data2;
    }

    public String toString() {
        return "Result{code='" + this.code + '\'' + ", msg='" + this.msg + '\'' + ", data=" + this.data + '}';
    }
}
