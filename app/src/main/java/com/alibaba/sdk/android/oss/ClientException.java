package com.alibaba.sdk.android.oss;

import com.fvision.camera.utils.DoCmdUtil;

public class ClientException extends Exception {
    public ClientException() {
    }

    public ClientException(String message) {
        super("[ErrorMessage]: " + message);
    }

    public ClientException(Throwable cause) {
        super(cause);
    }

    public ClientException(String message, Throwable cause) {
        super("[ErrorMessage]: " + message, cause);
    }

    public String getMessage() {
        String base = super.getMessage();
        return getCause() == null ? base : getCause().getMessage() + DoCmdUtil.COMMAND_LINE_END + base;
    }
}
