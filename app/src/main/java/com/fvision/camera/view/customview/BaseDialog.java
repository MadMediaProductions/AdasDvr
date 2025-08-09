package com.fvision.camera.view.customview;

import android.app.DialogFragment;
import com.fvision.camera.service.ForegroundService;

public abstract class BaseDialog extends DialogFragment {
    protected boolean mIsInit;
    protected ForegroundService mService;

    public BaseDialog(ForegroundService paramProxyService) {
        this.mService = paramProxyService;
    }

    public BaseDialog() {
    }
}
