package com.fvision.camera.adas.ui;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class CameraWindow {
    private static Context applicationContext;
    private static int height;
    private static ImageView mQrView;
    private static View view;
    private static int width;
    private static WindowManager windowManager;

    public static void show(Context context) {
        if (applicationContext == null) {
            applicationContext = context.getApplicationContext();
            windowManager = (WindowManager) context.getSystemService("window");
        }
    }

    public static void showFullScreen() {
        if (view != null) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) view.getLayoutParams();
            params.width = width - 100;
            params.height = height - 100;
            view.setLayoutParams(params);
        }
    }

    public static void hideFullScreen() {
        if (view != null) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) view.getLayoutParams();
            params.height = 1;
            params.width = 1;
            view.setLayoutParams(params);
        }
    }

    public static void dismiss() {
        try {
            if (windowManager != null && view != null) {
                windowManager.removeView(view);
                view = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
