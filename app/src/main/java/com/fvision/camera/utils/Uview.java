package com.fvision.camera.utils;

import android.view.MotionEvent;
import android.view.View;

public class Uview {
    public static void clickEffectByAlphaWithBg(View.OnClickListener onClickListener, View view) {
        view.setOnClickListener(onClickListener);
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                        v.getBackground().setAlpha(80);
                        return false;
                    case 1:
                        v.getBackground().setAlpha(255);
                        return false;
                    default:
                        return false;
                }
            }
        });
    }
}
