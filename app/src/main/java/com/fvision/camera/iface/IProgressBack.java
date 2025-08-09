package com.fvision.camera.iface;

public interface IProgressBack {
    void onFail(int i, String str);

    void onProgress(float f);

    void onSuccess(String str);
}
