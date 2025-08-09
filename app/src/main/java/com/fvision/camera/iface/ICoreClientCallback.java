package com.fvision.camera.iface;

public interface ICoreClientCallback {
    void initCmd(boolean z, String str);

    void onConnect();

    void onDisconnect();

    void onInit(boolean z, int i, String str);

    void onIsAvailable(boolean z);
}
