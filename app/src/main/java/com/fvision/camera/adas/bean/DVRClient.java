package com.fvision.camera.adas.bean;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.fvision.camera.adas.IDVRClient;
import com.fvision.camera.iface.ICoreClientCallback;
import com.fvision.camera.manager.CmdManager;
import com.fvision.camera.utils.Const;
import com.fvision.camera.utils.LogUtils;
import com.huiying.cameramjpeg.UvcCamera;
import java.util.ArrayList;

public class DVRClient implements IDVRClient {
    /* access modifiers changed from: private */
    public IDVRConnectListener mConnectListener;
    /* access modifiers changed from: private */
    public boolean mConnected;
    private Context mContext;
    private ICoreClientCallback mCoreClientCallback = new ICoreClientCallback() {
        public void onConnect() {
        }

        public void onDisconnect() {
        }

        public void initCmd(boolean b, String s) {
            boolean unused = DVRClient.this.mIsPrepare = b;
            LogUtils.d("runningLog", "初始化指令文件" + (b ? "成功" : "失败") + "原因:" + s + " initCmd()");
            if (DVRClient.this.mPrepareListener != null) {
                DVRClient.this.mPrepareListener.onPrepare(b);
            }
            boolean unused2 = DVRClient.this.mConnected = b;
            if (DVRClient.this.mConnectListener == null) {
                return;
            }
            if (b) {
                DVRClient.this.mConnectListener.onConnect();
            } else {
                DVRClient.this.mConnectListener.onDisconnect();
            }
        }

        public void onIsAvailable(boolean b) {
        }

        public void onInit(boolean b, int i, String s) {
        }
    };
    /* access modifiers changed from: private */
    public boolean mIsPrepare;
    /* access modifiers changed from: private */
    public IDVRPrepareListener mPrepareListener;
    private String path;

    public DVRClient(Context context) {
        this.mContext = context;
    }

    public DVRClient(Context context, String path2) {
        this.path = path2;
    }

    public void setDVRConnectListener(IDVRConnectListener listener) {
        this.mConnectListener = listener;
    }

    public void setPrepareListener(IDVRPrepareListener listener) {
        this.mPrepareListener = listener;
    }

    public String getDeviceCode() {
        return CmdManager.getInstance().getUUIDCode();
    }

    public void saveSecretKey(String key) {
        Log.e("saveSecretKey", "" + key + " " + this.mIsPrepare);
        CmdManager.getInstance().writeKeyStore(key);
    }

    public String getSecretKey() {
        return CmdManager.getInstance().readKeyStore();
    }

    public void playSound(int sound) {
        ArrayList<Integer> raws = new ArrayList<>();
        raws.add(Integer.valueOf(sound));
        Intent playSoundIntent = new Intent(Const.BROAD_PLAY_ADAS_SOUND);
        Bundle b = new Bundle();
        b.putIntegerArrayList("raws", raws);
        playSoundIntent.putExtras(b);
        playSoundIntent.addFlags(32);
        this.mContext.sendBroadcast(playSoundIntent);
    }

    public void init() {
        Log.e("AdasService", "init()");
        UvcCamera.getInstance().addListener(this.mCoreClientCallback);
        if (!UvcCamera.getInstance().isInit()) {
            if (!"".equals(".px3")) {
                LogUtils.d("runningLog", "开始初始化...initUvccamera()");
                UvcCamera.getInstance().initUvccamera();
            } else {
                UvcCamera.getInstance().initUvccameraByPath("/mnt/media_rw");
            }
        }
        if (UvcCamera.getInstance().isInit()) {
            if (!UvcCamera.getInstance().isPreviewing()) {
                UvcCamera.getInstance().startPreview();
                LogUtils.d("runningLog", "开始预览...startPreview()");
            }
            CmdManager.getInstance().syncTime();
        }
    }

    public void release() {
    }

    public UvcCamera getDVRClient() {
        return UvcCamera.getInstance();
    }

    public void setParcelFileDescriptor(ParcelFileDescriptor parcelFileDescriptor) {
    }

    public boolean isConnect() {
        return this.mConnected;
    }
}
