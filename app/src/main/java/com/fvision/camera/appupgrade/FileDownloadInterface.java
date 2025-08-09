package com.fvision.camera.appupgrade;

public interface FileDownloadInterface {
    void complete(String str);

    void fail(int i, String str);

    void progress(float f);

    void update(int i, String str, String str2, String str3);
}
