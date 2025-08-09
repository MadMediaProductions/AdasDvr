package com.fvision.camera.listener;

import android.os.Bundle;

public interface LifeCycleListener {
    void onCreate(Bundle bundle);

    void onDestroy();

    void onPause();

    void onRestart();

    void onResume();

    void onStart();

    void onStop();
}
