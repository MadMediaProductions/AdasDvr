package com.fvision.camera.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import com.fvision.camera.iface.FroegroundIface;
import com.fvision.camera.manager.CmdManager;
import com.fvision.camera.service.ForegroundService;
import com.fvision.camera.utils.Const;
import com.fvision.camera.utils.ExternalUtil;
import com.fvision.camera.utils.LogUtils;
import com.fvision.camera.utils.ToastUtils;
import com.serenegiant.usb.USBMonitor;
import java.util.Iterator;

public class FroegroundReceiver extends BroadcastReceiver {
    public IntentFilter filter = new IntentFilter();
    FroegroundIface iface;
    ForegroundService service;

    public FroegroundReceiver(Context context, FroegroundIface iview) {
        this.service = (ForegroundService) context;
        this.iface = iview;
        this.filter.addAction(Const.BROAD_CAST_SHOW_FLOATWINDOW);
        this.filter.addAction(Const.BROAD_CAST_HIDE_FLOATWINDOW);
        this.filter.addAction(USBMonitor.ACTION_USB_DEVICE_ATTACHED);
        this.filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        this.filter.addAction(Const.BROAD_CAST_SYNC_TIME);
        this.filter.addAction("android.intent.action.TIME_TICK");
        this.filter.addAction("android.intent.action.MEDIA_EJECT");
        this.filter.addAction(ExternalUtil.HUIYING_BROADCAST_SEND);
        this.filter.addAction(Const.BROAD_PLAY_EDOG_SOUND);
        this.filter.addAction(Const.BROAD_PLAY_ADAS_SOUND);
        this.filter.addAction("com.stcloud.drive.EVT_ACC_STATUS");
        this.filter.addAction("com.stcloud.drive.RESP_VST");
    }

    public Intent registerReceiver() {
        return this.service.registerReceiver(this, this.filter);
    }

    public void unregisterReceiver() {
        this.service.unregisterReceiver(this);
    }

    public void onReceive(Context context, Intent intent) {
        LogUtils.d("FroegroundReceiver " + intent.getAction());
        if (intent.getAction().equals(Const.BROAD_CAST_SHOW_FLOATWINDOW)) {
            this.iface.showPopuWindow();
        } else if (intent.getAction().equals(Const.BROAD_CAST_HIDE_FLOATWINDOW)) {
            this.iface.hidePopuWindow();
        } else if (intent.getAction().equals(Const.BROAD_CAST_SYNC_TIME) || intent.getAction().equals("android.intent.action.TIME_TICK") || intent.getAction().equals("android.intent.action.TIME_SET")) {
            if (isSync()) {
                this.iface.syncTime();
            }
        } else if (intent.getAction().equals("android.intent.action.MEDIA_EJECT")) {
            this.iface.pullUsb();
        } else if (intent.getAction().equals(ExternalUtil.HUIYING_BROADCAST_SEND)) {
            ToastUtils.showLongToast(context, "收到广播");
            this.iface.remotecmd(intent);
        } else if (intent.getAction().equals("com.stcloud.drive.EVT_ACC_STATUS")) {
            this.iface.isDetach(true);
        } else if (intent.getAction().equals("com.stcloud.drive.RESP_VST")) {
            this.iface.IsVst(intent.getBooleanExtra("accessory", false));
        } else if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
            this.iface.isDetach(true);
        } else if (intent.getAction().equals(Const.BROAD_PLAY_EDOG_SOUND)) {
            Iterator<Integer> it = intent.getIntegerArrayListExtra("raws").iterator();
            while (it.hasNext()) {
                Integer raw = it.next();
                if (raw != null) {
                    this.iface.playEdogSound(raw.intValue());
                }
            }
        } else if (intent.getAction().equals(Const.BROAD_PLAY_ADAS_SOUND)) {
            Iterator<Integer> it2 = intent.getIntegerArrayListExtra("raws").iterator();
            while (it2.hasNext()) {
                this.iface.playAdasSound(it2.next().intValue());
            }
        }
    }

    private boolean isSync() {
        if (System.currentTimeMillis() - CmdManager.getInstance().getLastSyncTime() > 3600000) {
            return true;
        }
        return false;
    }

    private void startService(Context context) {
        LogUtils.d("111111111 startService()");
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(new Intent(context, ForegroundService.class));
        } else {
            context.startService(new Intent(context, ForegroundService.class));
        }
    }
}
