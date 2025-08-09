package com.fvision.camera.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class RLoadingDialog extends Dialog {
    public RLoadingDialog(Context context, boolean cancelable) {
        super(context);
        setCancelable(cancelable);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        LinearLayout linearLayout = new LinearLayout(getContext());
        ProgressBar progressBar = new ProgressBar(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(125, 125);
        params.setMargins(100, 20, 100, 20);
        progressBar.setLayoutParams(params);
        linearLayout.addView(progressBar);
        setContentView(linearLayout);
    }
}
