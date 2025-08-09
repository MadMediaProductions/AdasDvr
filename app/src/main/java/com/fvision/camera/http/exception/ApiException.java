package com.fvision.camera.http.exception;

public class ApiException extends Exception {
    private int code;
    private String msg;

    public ApiException(Throwable throwable, int code2) {
        super(throwable);
        this.code = code2;
    }

    public ApiException(int code2, String msg2) {
        this.code = code2;
        this.msg = msg2;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code2) {
        this.code = code2;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg2) {
        this.msg = msg2;
    }
}
