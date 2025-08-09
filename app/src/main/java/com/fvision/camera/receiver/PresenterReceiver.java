package com.fvision.camera.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PresenterReceiver extends BroadcastReceiver {
    private VstResposeIface vstIface;

    public interface VstResposeIface {
        void setIsRespose(boolean z);
    }

    public void setVstResposeIfaceListener(VstResposeIface vstIface2) {
        this.vstIface = vstIface2;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.stcloud.drive.RESP_VST")) {
            this.vstIface.setIsRespose(intent.getBooleanExtra("accessory", false));
        }
    }
}
