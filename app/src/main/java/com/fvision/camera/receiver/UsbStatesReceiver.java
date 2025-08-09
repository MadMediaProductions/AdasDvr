package com.fvision.camera.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.fvision.camera.presenter.MainPresenter;
import com.fvision.camera.ui.MainActivity;
import com.fvision.camera.utils.LogUtils;
import com.fvision.camera.view.iface.IMainView;
import com.serenegiant.usb.USBMonitor;

public class UsbStatesReceiver extends BroadcastReceiver {
    public static final int USB_STATE_OFF = 1;
    public static final int USB_STATE_ON = 0;
    MainActivity execactivity;
    public IntentFilter filter = new IntentFilter();
    IMainView iMainView;
    MainPresenter presenter;

    public UsbStatesReceiver(Context context, IMainView iview, MainPresenter presenter2) {
        this.execactivity = (MainActivity) context;
        this.iMainView = iview;
        this.presenter = presenter2;
        this.filter.addAction("android.intent.action.MEDIA_CHECKING");
        this.filter.addAction("android.intent.action.MEDIA_MOUNTED");
        this.filter.addAction("android.intent.action.MEDIA_EJECT");
        this.filter.addAction("android.intent.action.MEDIA_REMOVED");
        this.filter.addAction(USBMonitor.ACTION_USB_DEVICE_ATTACHED);
        this.filter.addAction("android.hardware.usb.action.USB_ACCESSORY_DETACHED");
        this.filter.addDataScheme("file");
    }

    public Intent registerReceiver() {
        return this.execactivity.registerReceiver(this, this.filter);
    }

    public void unregisterReceiver() {
        this.execactivity.unregisterReceiver(this);
    }

    public void onReceive(Context context, Intent intent) {
        String path;
        LogUtils.d("111111 UsbStatesReceiver " + intent.getAction());
        if (!intent.getAction().equals("android.intent.action.MEDIA_CHECKING")) {
            if (intent.getAction().equals("android.intent.action.MEDIA_EJECT")) {
                this.iMainView.usbStateChange((String) null, 1);
                return;
            }
            if (!intent.getAction().equals("android.intent.action.MEDIA_MOUNTED") || (path = intent.getData().getPath()) == null || !path.equals("")) {
            }
        }
    }
}
