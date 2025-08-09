package com.fvision.camera.http.exception;

public class ServerException extends RuntimeException {
    private int code;
    private String msg;

    public ServerException(int code2, String msg2) {
        this.code = code2;
        this.msg = msg2;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
