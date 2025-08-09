package com.fvision.camera.iface;

public interface ISmallVideoBack {
    void downloadProgress(float f);

    void fail(int i, String str);

    void success(String str);
}
