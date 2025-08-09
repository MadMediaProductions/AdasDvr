package com.fvision.camera.iface;

import android.content.Intent;

public interface FroegroundIface {
    void IsVst(boolean z);

    void hidePopuWindow();

    void isDetach(boolean z);

    void playAdasSound(int i);

    void playEdogSound(int i);

    void pullUsb();

    void remotecmd(Intent intent);

    void showPopuWindow();

    void syncTime();
}
