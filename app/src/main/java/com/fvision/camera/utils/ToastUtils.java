package com.fvision.camera.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

public class ToastUtils {
    private static Toast toast;

    public static void showToast(Context context, String text) {
        if (!TextUtils.isEmpty(text)) {
            if (toast == null) {
                toast = Toast.makeText(context, text, 0);
            } else {
                toast.setText(text);
            }
            toast.show();
        }
    }

    public static void showToast(Context context, int rId) {
        if (toast == null) {
            toast = Toast.makeText(context, rId, 0);
        } else {
            toast.setText(rId);
        }
        toast.show();
    }

    public static void showLongToast(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, 1);
        } else {
            toast.setText(text);
        }
        toast.show();
    }

    public static void showLongToast(Context context, int rId) {
        if (toast == null) {
            toast = Toast.makeText(context, rId, 1);
        } else {
            toast.setText(rId);
        }
        toast.show();
    }
}
