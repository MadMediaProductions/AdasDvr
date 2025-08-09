package com.huiying.cameramjpeg;

import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import com.fvision.camera.iface.ICoreClientCallback;
import com.fvision.camera.manager.CameraStateIml;
import com.fvision.camera.manager.DevFileNameManager;
import com.fvision.camera.util.LogUtils;
import com.serenegiant.usb.IFrameCallback;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UvcCamera {
    private static volatile UvcCamera _instance;
    private static IFrameCallback mFrameCallback;
    private static IFrameCallback mStateFrameCallback;
    private byte[] buffer;
    public String cmd_fd_error = "";
    private String cmdpath = "";
    private String devpath = "";
    public String fd_error = "";
    private String filepath = "";
    private boolean isCmdInit = false;
    private boolean isFileInit = false;
    private boolean isInit = false;
    private boolean isPreviewing = false;
    private boolean isPx3 = false;
    long lastInit = 0;
    long lastStartPreviewTime = 0;
    private List<ICoreClientCallback> listListener;
    private ICoreClientCallback mICoreClientCallback;
    private LogListener mLogListener;
    private int nativeCmdFd = 0;
    private int nativeFd = 0;
    private int nativeFileFd = 0;
    private String pkgName = "";
    private SearchUSB searchUsb = null;
    private String serachDevpath = null;

    private native String initUvcCamera(String str);

    private native String initUvcCmd(String str);

    private native int initUvcFile(String str);

    private native void nativeAdasFrameCallback(IFrameCallback iFrameCallback);

    private native boolean nativeDownloadFile(String str, String str2, ProgressCallback progressCallback);

    private native void nativeFrameCallback(IFrameCallback iFrameCallback);

    private native int nativeGetFile(int i, byte[] bArr, int i2);

    private native int nativeGetFileCount(int i, byte[] bArr);

    private native int nativeSendCmd(int i, int i2, int i3, int i4, int i5, int i6, byte[] bArr);

    private native void nativeSetFrameCallback(int i, IFrameCallback iFrameCallback);

    private native void nativeSetMainRuning(boolean z);

    private native void nativeStartAdas(byte[] bArr);

    private native void nativeStateFrameCallback(IFrameCallback iFrameCallback);

    private native boolean nativeTakesnoshot(String str);

    private native int releaseUvcCamera(int i);

    private native int setPx3(boolean z);

    private native int startPreview(int i);

    private native int stopPreview(int i);

    public native boolean GetLibDispStatus();

    public native int GetStatus();

    public native boolean GetUploadAdasDataStatus();

    public native void SetLibDispStatus(boolean z);

    public native void SetUploadAdasDataStatus(boolean z);

    public native void changeESLayout(int i, int i2);

    public native int drawESFrame();

    public native int getLanePara(int i, byte[] bArr);

    public native int getLanePos(byte[] bArr);

    public native void initGles(int i, int i2);

    public native void setDisplaySurface(Surface surface);

    public native int setLanePara(int i, int i2, int i3, float f, int i4, int i5, int i6, int i7, int i8);

    public native int setLanePos(int i, int i2);

    static {
        System.loadLibrary("uvccamera-lib");
    }

    public String getDevpath() {
        if (this.devpath == null) {
            return "";
        }
        return this.devpath;
    }

    public static UvcCamera getInstance() {
        if (_instance == null) {
            synchronized (UvcCamera.class) {
                if (_instance == null) {
                    _instance = new UvcCamera();
                    _instance.setStateFrameCallback(CameraStateIml.getInstance().mStateFrameCallback);
                }
            }
        }
        return _instance;
    }

    private UvcCamera() {
    }

    public void setOnCoreClientCallback(ICoreClientCallback callback) {
        this.mICoreClientCallback = callback;
    }

    public void setOnLog(LogListener listener) {
        this.mLogListener = listener;
    }

    public synchronized void initUvccamera() {
        long now = System.currentTimeMillis();
        if (now - this.lastInit < 1000) {
            LogUtils.d("runningLog", "1秒内，过滤");
        } else {
            this.lastInit = now;
            if (this.isInit && this.listListener != null) {
                for (ICoreClientCallback back : this.listListener) {
                    back.onInit(false, 4, "已经初始化");
                    LogUtils.e("runningLog", "已经初始化");
                }
            }
            if (this.searchUsb == null) {
                this.searchUsb = new SearchUSB();
            }
            if (!TextUtils.isEmpty(this.devpath) && !new File(this.devpath).exists()) {
                this.devpath = null;
            }
            if (TextUtils.isEmpty(this.devpath) && this.serachDevpath != null) {
                this.devpath = this.searchUsb.searchDir(this.serachDevpath);
                LogUtils.d("runningLog", "上一次正确查找的U盘所在目录 " + this.devpath);
            }
            if (TextUtils.isEmpty(this.devpath)) {
                this.devpath = this.searchUsb.searchUsbPath();
            }
            if (TextUtils.isEmpty(this.devpath)) {
                this.fd_error = "not found file";
                LogUtils.e("runningLog", "没有搜索到记录仪 not found file");
            } else {
                initUvccameraByPath(this.devpath);
            }
        }
    }

    public synchronized void initUvccameraByPath(String path) {
        if (this.isInit) {
            LogUtils.e("runningLog", "已经初始化");
        } else {
            File file = new File(path);
            if (!file.exists()) {
                LogUtils.e("runningLog", "路径不存在 " + path);
            } else {
                if (this.searchUsb == null) {
                    this.searchUsb = new SearchUSB();
                }
                if (file.isDirectory()) {
                    this.devpath = this.searchUsb.searchDir(path);
                }
                if (this.devpath == null) {
                    this.fd_error = "not found file!";
                    LogUtils.e("runningLog", "not found file!");
                } else {
                    String[] reault = initUvcCamera(this.devpath).split("_");
                    this.fd_error = reault[1];
                    this.nativeFd = Integer.valueOf(reault[0]).intValue();
                    if (this.nativeFd > 0) {
                        this.isInit = true;
                        LogUtils.d("runningLog", "预览初始化成功");
                        this.fd_error = "Success";
                        if (this.listListener != null) {
                            for (ICoreClientCallback back : this.listListener) {
                                LogUtils.e("runningLog", "回调 " + back.toString());
                                back.initCmd(true, this.fd_error);
                                back.onIsAvailable(true);
                                back.onConnect();
                            }
                        } else {
                            LogUtils.e("runningLog", "listListener == null");
                        }
                        initCmd();
                    } else {
                        LogUtils.e("runningLog", "预览初始化失败");
                        this.isInit = false;
                        if (this.listListener != null) {
                            for (ICoreClientCallback back2 : this.listListener) {
                                back2.initCmd(false, this.fd_error);
                            }
                        }
                    }
                }
            }
        }
    }

    public int sendCmd(int requesttype, int request, int value, int index, int length, byte[] data) {
        if (this.nativeCmdFd > 0) {
            return nativeSendCmd(this.nativeCmdFd, requesttype, request, value, index, length, data);
        }
        Log.e("zoulequan", "nativeCmdFd <= 0 nativeCmdFd = " + this.nativeCmdFd);
        return -1;
    }

    public int getFile(byte[] data) {
        if (this.nativeCmdFd > 0) {
            return nativeGetFile(this.nativeCmdFd, data, data.length);
        }
        return -1;
    }

    public int getFileCount(byte[] data) {
        if (this.nativeFileFd > 0) {
            return nativeGetFileCount(this.nativeFileFd, data);
        }
        return -1;
    }

    public synchronized void startPreview() {
        if (System.currentTimeMillis() - this.lastStartPreviewTime >= 1000) {
            this.lastStartPreviewTime = System.currentTimeMillis();
            Log.e("startPreview", "开始预览");
            new Thread(new Runnable() {
                public void run() {
                    UvcCamera.this.stopPreview();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    UvcCamera.this.startPreviewOp();
                }
            }).start();
        }
    }

    /* access modifiers changed from: private */
    public void startPreviewOp() {
        if (startPreview(this.nativeFd) >= 0) {
            this.isPreviewing = true;
        }
    }

    public void stopPreview() {
        if (!this.isInit) {
            this.isPreviewing = false;
        } else if (this.nativeFd > 0) {
            stopPreview(this.nativeFd);
            this.isPreviewing = false;
        }
    }

    public void releaseUvccamera() {
        if (this.isInit) {
            if (this.mICoreClientCallback != null) {
                this.mICoreClientCallback.onDisconnect();
            }
            releaseUvcCamera(this.nativeFd);
            _instance = null;
            this.isInit = false;
            this.devpath = null;
            this.nativeFd = -1;
            this.nativeCmdFd = -1;
        }
    }

    public void setFrameCallback(IFrameCallback frameCallback) {
        mFrameCallback = frameCallback;
        nativeFrameCallback(frameCallback);
    }

    public void setStateFrameCallback(IFrameCallback frameCallback) {
        mStateFrameCallback = frameCallback;
        nativeStateFrameCallback(frameCallback);
    }

    public static void onFrameData(byte[] data) {
        if (data != null && mFrameCallback != null) {
            mFrameCallback.onFrame(data);
        }
    }

    public boolean isInit() {
        return this.isInit;
    }

    public void setAdasFrameCallback(IFrameCallback frameCallback) {
        if (frameCallback != null) {
            nativeAdasFrameCallback(frameCallback);
        }
    }

    public void setMainRuning(boolean run) {
        nativeSetMainRuning(run);
    }

    public boolean isPreviewing() {
        return this.isPreviewing;
    }

    public void setPkgName(String pkgName2) {
        this.pkgName = pkgName2;
    }

    public boolean takeSnapshot(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        return nativeTakesnoshot(path);
    }

    public boolean downloadFile(String downpath, String localpath, ProgressCallback callback) {
        return nativeDownloadFile(downpath, localpath, callback);
    }

    public void setDevpath(String path) {
        this.devpath = path;
    }

    public void setSerachDevpath(String path) {
        this.serachDevpath = path;
    }

    public void initCmd() {
        String[] reault;
        LogUtils.d("runningLog", "开始初始化指令文件");
        if (this.devpath == null) {
            this.cmd_fd_error = "devpath == null";
            LogUtils.e("runningLog", "devpath == null");
        } else if (DevFileNameManager.getInstance().getCurrentDev() == null || TextUtils.isEmpty(DevFileNameManager.getInstance().getCurrentDev().getCmd())) {
            LogUtils.e("runningLog", "DevFileNameManager == null");
        } else {
            this.cmdpath = this.devpath.replace(DevFileNameManager.getInstance().getCurrentDev().getPreView(), String.format("Android/data/%s/" + DevFileNameManager.getInstance().getCurrentDev().getCmd(), new Object[]{DevFileNameManager.getInstance().getCurrentDev().getPackageName()}));
            LogUtils.d("runningLog", "指令文件路径 " + this.cmdpath);
            new File(this.cmdpath);
            if (this.isPx3) {
                reault = initUvcCmd(this.cmdpath.replace("/storage", "/mnt/media_rw")).split("_");
            } else {
                reault = initUvcCmd(this.cmdpath).split("_");
            }
            LogUtils.d("runningLog", "reault[1] " + reault[1]);
            this.cmd_fd_error = this.cmdpath + " " + reault[1];
            this.nativeCmdFd = Integer.valueOf(reault[0]).intValue();
            if (this.nativeCmdFd > 0) {
                this.isCmdInit = true;
                this.cmd_fd_error = "Success";
                LogUtils.d("runningLog", "初始化指令文件成功");
                if (this.listListener != null) {
                    for (ICoreClientCallback back : this.listListener) {
                        LogUtils.d("runningLog", "回调 " + back.getClass());
                        back.initCmd(true, this.cmd_fd_error);
                    }
                    return;
                }
                LogUtils.e("runningLog", "无法回调 listListener==null");
                return;
            }
            LogUtils.d("runningLog", "初始化指令文件失败 " + this.cmd_fd_error);
            if (this.listListener != null) {
                for (ICoreClientCallback back2 : this.listListener) {
                    back2.initCmd(false, this.cmd_fd_error);
                }
            }
        }
    }

    public void setPx3Model(boolean isn) {
        this.isPx3 = isn;
        setPx3(isn);
    }

    public void addListener(ICoreClientCallback iCoreClientCallback) {
        if (iCoreClientCallback != null) {
            if (this.listListener == null) {
                this.listListener = new ArrayList();
            }
            if (this.listListener.size() > 0) {
                Iterator var2 = this.listListener.iterator();
                while (true) {
                    if (!var2.hasNext()) {
                        break;
                    }
                    ICoreClientCallback back = var2.next();
                    if (back.getClass().equals(iCoreClientCallback.getClass())) {
                        this.listListener.remove(back);
                        break;
                    }
                }
            }
            this.listListener.add(iCoreClientCallback);
        }
    }
}
