package com.fvision.camera.appupgrade;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.adasplus.adas.adas.BuildConfig;
import com.fvision.camera.R;
import com.fvision.camera.http.HttpRequest;
import com.fvision.camera.iface.IProgressBack;
import com.fvision.camera.manager.CmdManager;
import com.fvision.camera.manager.DevUpgradeManager;
import com.fvision.camera.util.LogUtils;
import com.fvision.camera.utils.CameraStateUtil;
import com.fvision.camera.utils.SharedPreferencesUtil;
import java.io.File;
import java.util.HashMap;
import v4.FileProvider;

public class UpgradeManager {
    public static final int COMPLETE = 1;
    public static final int FAIL = 0;
    public static final int PROGRESS = 2;
    public static final int UPGRADE_TYPE_APP = 0;
    public static final int UPGRADE_TYPE_DEV = 1;
    /* access modifiers changed from: private */
    public static Notification.Builder builder3;
    private static String fileName = null;
    private static UpgradeManager instance = null;
    /* access modifiers changed from: private */
    public static Context mContext;
    /* access modifiers changed from: private */
    public static NotificationManager manager;
    private static Notification notif;
    private String appFileName = "jianrong_test.apk";
    /* access modifiers changed from: private */
    public int currentProgress = 0;
    private String description = null;
    private String devFileName = "DestOtaBin.bin";
    private int devVersionCode;
    /* access modifiers changed from: private */
    public String download_address = null;
    /* access modifiers changed from: private */
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(UpgradeManager.mContext, (String) msg.obj, 0).show();
                    return;
                case 2:
                    int len = ((Integer) msg.obj).intValue();
                    int unused = UpgradeManager.this.currentProgress = len;
                    UpgradeManager.builder3.setProgress(100, len, false);
                    UpgradeManager.builder3.setContentInfo(len + "%");
                    if (len == 100) {
                        UpgradeManager.builder3.setOngoing(false);
                        UpgradeManager.builder3.setContentInfo(UpgradeManager.mContext.getString(R.string.download_complete));
                    }
                    UpgradeManager.manager.notify(0, UpgradeManager.builder3.build());
                    return;
                default:
                    return;
            }
        }
    };
    /* access modifiers changed from: private */
    public FileDownloadInterface mDevUpgradeInterface = null;
    /* access modifiers changed from: private */
    public FileDownloadInterface mFileDownloadInterface = null;
    /* access modifiers changed from: private */
    public int upgradeType = -1;
    private String version = null;
    private int versionCode = 0;
    private String versionName;

    public static UpgradeManager getInstance() {
        if (instance == null) {
            instance = new UpgradeManager();
        }
        return instance;
    }

    public static void init(Context context, String fileName2) {
        mContext = context;
        fileName = fileName2;
    }

    /* access modifiers changed from: private */
    public void initNotif() {
        Context context = mContext;
        Context context2 = mContext;
        manager = (NotificationManager) context.getSystemService("notification");
        builder3 = new Notification.Builder(mContext);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel("616", BuildConfig.ADAS_VERSION_BUSINESS, 2);
            builder3.setChannelId("616");
            manager.createNotificationChannel(channel);
        }
        builder3.setSmallIcon(R.mipmap.ic_launcher).setTicker(mContext.getString(R.string.app_upgrade)).setContentInfo(mContext.getString(R.string.downloading) + this.versionCode).setOngoing(true).setContentTitle(mContext.getString(R.string.app_upgrade)).setContentText(this.description);
        notif = builder3.build();
    }

    public void setOnProgressChangeListener(FileDownloadInterface fileDownloadInterface) {
        this.mFileDownloadInterface = fileDownloadInterface;
    }

    public void setOnDevProgressChangeListener(FileDownloadInterface fileDownloadInterface) {
        this.mDevUpgradeInterface = fileDownloadInterface;
    }

    public boolean update(int versionCode2, String downloadAddress, String description2, String versionName2) {
        this.version = versionName2;
        Log.e("downloadAddress", "" + downloadAddress);
        this.download_address = downloadAddress;
        this.description = description2;
        this.versionCode = versionCode2;
        LogUtils.e("versionCode = " + versionCode2 + " " + getVersionCode());
        if (this.upgradeType == 0) {
            if (versionCode2 <= getVersionCode()) {
                Toast.makeText(mContext, mContext.getString(R.string.no_update), 0).show();
                return false;
            }
        } else if (this.upgradeType == 1 && versionCode2 <= this.devVersionCode) {
            Toast.makeText(mContext, mContext.getString(R.string.no_update), 0).show();
            return false;
        }
        showUpdateDialog();
        return true;
    }

    private void showUpdateDialog() {
        if (mContext != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setIcon(17301659);
            if (this.upgradeType == 0) {
                builder.setTitle(getVersionName() + " -> " + this.version);
            } else if (this.upgradeType == 1) {
                builder.setTitle(CmdManager.getInstance().getCurrentState().getDevVersion() + " -> " + this.version);
            }
            builder.setMessage(this.description);
            builder.setCancelable(false);
            builder.setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (Environment.getExternalStorageState().equals("mounted")) {
                        UpgradeManager.this.initNotif();
                        UpgradeManager.this.downFile(UpgradeManager.this.download_address);
                        return;
                    }
                    Toast.makeText(UpgradeManager.mContext, UpgradeManager.mContext.getString(R.string.sdcard_prompt), 0).show();
                }
            });
            builder.setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.create().show();
        }
    }

    /* access modifiers changed from: private */
    public void downFile(String url) {
        manager.notify(0, notif);
        String fileName2 = null;
        if (this.upgradeType == 0) {
            fileName2 = this.appFileName;
        } else if (this.upgradeType == 1) {
            fileName2 = this.devFileName;
        }
        FileOper.download(url, CameraStateUtil.getSDCardCachePath(), fileName2, new FileDownloadInterface() {
            public void progress(float progress) {
                Log.e("progress", "下载进度:" + progress);
                Message msg = new Message();
                msg.what = 2;
                msg.obj = Integer.valueOf((int) progress);
                UpgradeManager.this.handler.sendMessage(msg);
                if (UpgradeManager.this.upgradeType == 0) {
                    if (UpgradeManager.this.mFileDownloadInterface != null) {
                        UpgradeManager.this.mFileDownloadInterface.progress(progress);
                    }
                } else if (UpgradeManager.this.upgradeType == 1 && UpgradeManager.this.mDevUpgradeInterface != null) {
                    UpgradeManager.this.mDevUpgradeInterface.progress(progress / 2.0f);
                }
            }

            public void fail(int code, String error) {
                if (UpgradeManager.this.upgradeType == 0) {
                    if (UpgradeManager.this.mFileDownloadInterface != null) {
                        UpgradeManager.this.mFileDownloadInterface.fail(code, UpgradeManager.mContext.getResources().getString(R.string.download_fail));
                    }
                } else if (UpgradeManager.this.upgradeType == 1 && UpgradeManager.this.mDevUpgradeInterface != null) {
                    UpgradeManager.this.mDevUpgradeInterface.fail(code, UpgradeManager.mContext.getResources().getString(R.string.download_fail));
                }
            }

            public void update(int version_code, String path, String app_desc, String version) {
            }

            public void complete(String filepath) {
                Log.e("complete", "" + filepath + UpgradeManager.this.upgradeType);
                Message msg = new Message();
                msg.what = 2;
                msg.obj = 100;
                UpgradeManager.this.handler.sendMessage(msg);
                if (UpgradeManager.this.upgradeType == 0) {
                    UpgradeManager.this.install(filepath);
                } else if (UpgradeManager.this.upgradeType == 1) {
                    UpgradeManager.this.upgradeDev(filepath);
                }
            }
        });
    }

    public void install(String url) {
        String fileName2 = Environment.getExternalStorageDirectory() + ("/" + fileName);
        Log.e("install", "" + fileName2);
        Intent intent = new Intent("android.intent.action.VIEW");
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setFlags(1);
            intent.setDataAndType(FileProvider.getUriForFile(mContext, "com.fvision.camera.fileProvider", new File(fileName2)), "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(fileName2)), "application/vnd.android.package-archive");
            intent.setFlags(268435456);
        }
        mContext.startActivity(intent);
    }

    public int getVersionCode() {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getVersionName() {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void checkAppUpdate() {
        this.upgradeType = 0;
        checkUpdate((String) null);
    }

    public void checkDevUpdate() {
        this.upgradeType = 1;
        String devPackage = CmdManager.getInstance().getCurrentState().getDevPackageName();
        this.devVersionCode = CmdManager.getInstance().getCurrentState().getDevVersionCode();
        this.versionName = CmdManager.getInstance().getCurrentState().getDevVersion();
        Log.e("dev_vision.checkUpdate", " code " + this.devVersionCode + " |name " + this.versionName + " |devPackage " + devPackage);
        checkUpdate(devPackage);
    }

    public void checkUpdate(String devPackage) {
        HashMap<String, String> map = new HashMap<>();
        if (devPackage != null) {
            map.put("package", devPackage);
            LogUtils.d(" devPackage " + devPackage);
        } else if (!"".equals("") || !CmdManager.getInstance().isSupportKaiYiAdas()) {
            map.put("package", "com.fvision.camera");
        } else {
            map.put("package", "com.fvision.camera.kaiyi");
        }
        HttpRequest.requestGet(map, new HttpRequest.RequestIFace() {
            public void onSuccess(int version_code, String path, String app_desc, String version) {
                if (UpgradeManager.this.upgradeType == 0) {
                    if (UpgradeManager.this.mFileDownloadInterface != null) {
                        UpgradeManager.this.mFileDownloadInterface.update(version_code, path, app_desc, version);
                    }
                } else if (UpgradeManager.this.upgradeType == 1 && UpgradeManager.this.mDevUpgradeInterface != null) {
                    Log.e("onsuccess", "int" + UpgradeManager.this.upgradeType + "version_code" + version_code);
                    UpgradeManager.this.mDevUpgradeInterface.update(version_code, path, app_desc, version);
                }
            }

            public void onFail(int errorCode, String error) {
                if (UpgradeManager.this.upgradeType == 0) {
                    if (UpgradeManager.this.mFileDownloadInterface != null) {
                        UpgradeManager.this.mFileDownloadInterface.fail(errorCode, error);
                    }
                } else if (UpgradeManager.this.upgradeType == 1) {
                    Log.e("onFail", "out" + UpgradeManager.this.upgradeType);
                    if (UpgradeManager.this.mDevUpgradeInterface != null) {
                        Log.e("onFail", "int" + UpgradeManager.this.upgradeType);
                        UpgradeManager.this.mDevUpgradeInterface.fail(errorCode, error);
                    }
                }
            }
        }, mContext);
    }

    private boolean isSupportKaiYiAdas() {
        int startPos;
        String currentVer = SharedPreferencesUtil.getLastDevVersion(mContext);
        if (TextUtils.isEmpty(currentVer) || (startPos = currentVer.indexOf("_v")) < 0) {
            return false;
        }
        String str = currentVer.substring(startPos + 2, startPos + 5);
        if (str.compareTo("2.1.8") < 0 || str.compareTo("3.0.0") >= 0) {
            return false;
        }
        return true;
    }

    public void upgradeDev(String filepath) {
        Log.e("upgradeDev", "in");
        DevUpgradeManager.getInstance().sendFileToDevThread(filepath, new IProgressBack() {
            public void onFail(int code, String msg) {
                Log.e("upgradeDev+onfail", " 发送固件到记录仪失败:" + msg);
                if (UpgradeManager.this.mDevUpgradeInterface != null) {
                    UpgradeManager.this.mDevUpgradeInterface.fail(code, msg);
                }
            }

            public void onProgress(float progress) {
                Log.e("onProgress", progress + "");
                if (UpgradeManager.this.mDevUpgradeInterface != null) {
                    UpgradeManager.this.mDevUpgradeInterface.progress(50.0f + (progress / 2.0f));
                }
            }

            public void onSuccess(String path) {
                int ret = DevUpgradeManager.getInstance().checkUpgradeFile();
                Log.e("onSuccess+init", "" + ret);
                if (ret == 244) {
                    Log.e("", "校验成功，正在重启记录仪");
                    DevUpgradeManager.getInstance().startUpgrade();
                    if (UpgradeManager.this.mDevUpgradeInterface != null) {
                        UpgradeManager.this.mDevUpgradeInterface.complete((String) null);
                    }
                } else if (UpgradeManager.this.mDevUpgradeInterface != null) {
                    UpgradeManager.this.mDevUpgradeInterface.fail(ret, "校验失败:" + ret);
                }
            }
        });
    }
}
