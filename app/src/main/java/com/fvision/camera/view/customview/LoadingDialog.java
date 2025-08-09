package com.fvision.camera.view.customview;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;
import com.fvision.camera.R;

public class LoadingDialog extends Dialog {
    private TextView tv_text;

    public LoadingDialog(Context context) {
        this(context, R.style.dialog);
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_loading);
        getWindow().setGravity(17);
        this.tv_text = (TextView) findViewById(R.id.tv_message);
        if ("".equals(".fyt")) {
            setCanceledOnTouchOutside(true);
            getWindow().setFlags(8, 8);
        } else {
            setCanceledOnTouchOutside(false);
        }
        setCancelable(true);
    }

    public LoadingDialog setMessage(String message) {
        this.tv_text.setText(message);
        return this;
    }
}
