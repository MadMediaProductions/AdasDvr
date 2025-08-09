package com.fvision.camera.manager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.fvision.camera.iface.ICameraStateChange;
import com.fvision.camera.iface.IProgressBack;
import com.fvision.camera.util.LogUtils;
import com.huiying.cameramjpeg.UvcCamera;

public class DevUpgradeManager implements ICameraStateChange {
    public static final int WHAT_LOCK_TIME_OUT = 56;
    public static final int WHAT_SEND_FILE_TO_DEV_IMP = 55;
    private static DevUpgradeManager _instance;
    private String fileName;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 55:
                    new Thread(new Runnable() {
                        public void run() {
                            DevUpgradeManager.this.sendFileToDevImp();
                        }
                    }).start();
                    return;
                case 56:
                    boolean unused = DevUpgradeManager.this.sendLock = false;
                    return;
                default:
                    return;
            }
        }
    };
    private IProgressBack mIProgressBack;
    /* access modifiers changed from: private */
    public boolean sendLock = false;

    public static DevUpgradeManager getInstance() {
        if (_instance == null) {
            _instance = new DevUpgradeManager();
        }
        return _instance;
    }

    public int checkUpgradeFile() {
        return CmdManager.getInstance().checkUpgradeFile();
    }

    public int startUpgrade() {
        int ret = CmdManager.getInstance().startUpgrade();
        if (ret == 255) {
            UvcCamera.getInstance().stopPreview();
            UvcCamera.getInstance().releaseUvccamera();
        }
        return ret;
    }

    public void sendFileToDevThread(String upgradeFile, IProgressBack back) {
        if (!CmdManager.getInstance().isSupperDevUpgrade()) {
            if (back != null) {
                back.onFail(6, "记录仪版本不支持，版本必须大于等于 1.2.3 小于 2.0.0，或 大于等于 2.2.3");
            }
        } else if (!CmdManager.getInstance().getCurrentState().isCam_sd_state()) {
            if (back != null) {
                back.onFail(7, "未检测到TF卡");
            }
        } else if (!this.sendLock) {
            this.sendLock = true;
            this.mHandler.removeMessages(56);
            this.mHandler.sendEmptyMessageDelayed(56, 300000);
            this.mIProgressBack = back;
            this.fileName = upgradeFile;
            if (CmdManager.getInstance().getCurrentState().isCam_rec_state()) {
                CmdManager.getInstance().recToggle();
                CameraStateIml.getInstance().addListener(this);
                LogUtils.d("正在关闭录像");
                return;
            }
            new Thread(new Runnable() {
                public void run() {
                    DevUpgradeManager.this.sendFileToDevImp();
                }
            }).start();
        } else if (back != null) {
            back.onFail(3, "正在发送固件到记录仪,请稍后再试...");
        }
    }

    /* access modifiers changed from: private */
    public void sendFileToDevImp() {
        int ret;
        LogUtils.d("devUpgrade 开始发送固件到记录仪");
        CmdManager.getInstance().openWriteFile();
        byte[] fileData = readFile(this.fileName, this.mIProgressBack);
        if (fileData == null) {
            this.sendLock = false;
            return;
        }
        int totalDataSize = 0;
        byte[] endData = null;
        if (fileData.length % 8192 > 0) {
            endData = new byte[(fileData.length % 8192)];
            totalDataSize = (fileData.length / 8192) + 1;
            System.arraycopy(fileData, (totalDataSize - 1) * 8192, endData, 0, endData.length);
        }
        for (int i = 0; i < fileData.length / 8192; i++) {
            if (this.mIProgressBack != null) {
                float progress = (float) ((i * 100) / (fileData.length / 8192));
                LogUtils.d("total = " + (fileData.length / 8192) + " i = " + i + " progress " + progress + "%");
                this.mIProgressBack.onProgress(progress);
            }
            byte[] singleData = new byte[8192];
            System.arraycopy(fileData, i * 8192, singleData, 0, singleData.length);
            int ret2 = CmdManager.getInstance().writeFileData(singleData, i);
            if (ret2 == 170) {
                if (this.mIProgressBack != null) {
                    this.mIProgressBack.onFail(4, "写数据错误!0x66 打开失败 0x77 写入失败 0x55 未检测到TF卡 错误码:" + ret2);
                }
                this.sendLock = false;
                return;
            }
        }
        if (endData == null || totalDataSize <= 0 || (ret = CmdManager.getInstance().writeFileData(endData, totalDataSize)) != 170) {
            CmdManager.getInstance().closeReadFile();
            if (this.mIProgressBack != null) {
                this.mIProgressBack.onSuccess("Success");
            }
            this.sendLock = false;
            return;
        }
        if (this.mIProgressBack != null) {
            this.mIProgressBack.onFail(4, "写数据错误!0x66 打开失败 0x77 写入失败 0x55 未检测到TF卡 错误码:" + ret);
        }
        this.sendLock = false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0053  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public byte[] readFile(String r11, IProgressBack r12) {
        /*
            r10 = this;
            r0 = 0
            r9 = 2
            r8 = 0
            boolean r6 = android.text.TextUtils.isEmpty(r11)
            if (r6 == 0) goto L_0x0014
            if (r12 == 0) goto L_0x0011
            r6 = 5
            java.lang.String r7 = "参数1:为空"
            r12.onFail(r6, r7)
        L_0x0011:
            r10.sendLock = r8
        L_0x0013:
            return r0
        L_0x0014:
            java.io.File r2 = new java.io.File
            r2.<init>(r11)
            boolean r6 = r2.exists()
            if (r6 != 0) goto L_0x002a
            if (r12 == 0) goto L_0x0027
            r6 = 1
            java.lang.String r7 = "指定路径的固件文件不存在"
            r12.onFail(r6, r7)
        L_0x0027:
            r10.sendLock = r8
            goto L_0x0013
        L_0x002a:
            r3 = 0
            r0 = 0
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x003f, IOException -> 0x004d }
            r4.<init>(r2)     // Catch:{ FileNotFoundException -> 0x003f, IOException -> 0x004d }
            int r5 = r4.available()     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0070 }
            byte[] r0 = new byte[r5]     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0070 }
            r4.read(r0)     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0070 }
            r4.close()     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0070 }
            r3 = r4
            goto L_0x0013
        L_0x003f:
            r1 = move-exception
        L_0x0040:
            r1.printStackTrace()
            if (r12 == 0) goto L_0x004a
            java.lang.String r6 = "找不到文件 FileNotFoundException"
            r12.onFail(r9, r6)
        L_0x004a:
            r10.sendLock = r8
            goto L_0x0013
        L_0x004d:
            r1 = move-exception
        L_0x004e:
            r1.printStackTrace()
            if (r12 == 0) goto L_0x006d
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "IO异常 "
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r7 = r1.getMessage()
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r6 = r6.toString()
            r12.onFail(r9, r6)
        L_0x006d:
            r10.sendLock = r8
            goto L_0x0013
        L_0x0070:
            r1 = move-exception
            r3 = r4
            goto L_0x004e
        L_0x0073:
            r1 = move-exception
            r3 = r4
            goto L_0x0040
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fvision.camera.manager.DevUpgradeManager.readFile(java.lang.String, com.fvision.camera.iface.IProgressBack):byte[]");
    }

    public void stateChange() {
        if (!CmdManager.getInstance().getCurrentState().isCam_rec_state()) {
            LogUtils.d("关闭录像成功，开始发送固件");
            this.mHandler.sendEmptyMessage(55);
            CameraStateIml.getInstance().delListener(this);
        }
    }
}
