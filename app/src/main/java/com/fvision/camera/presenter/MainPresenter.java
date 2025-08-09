package com.fvision.camera.presenter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.fvision.camera.App;
import com.fvision.camera.R;
import com.fvision.camera.appupgrade.UpgradeManager;
import com.fvision.camera.base.BasePresenter;
import com.fvision.camera.bean.CameraStateBean;
import com.fvision.camera.http.HttpRequest;
import com.fvision.camera.iface.ICameraStateChange;
import com.fvision.camera.loaction.GPSLocationListener;
import com.fvision.camera.loaction.GPSLocationManager;
import com.fvision.camera.manager.CameraStateIml;
import com.fvision.camera.manager.CmdManager;
import com.fvision.camera.manager.DevFileNameManager;
import com.fvision.camera.manager.PermissionsChecker;
import com.fvision.camera.manager.SoundManager;
import com.fvision.camera.receiver.UsbStatesReceiver;
import com.fvision.camera.service.ForegroundService;
import com.fvision.camera.ui.MainActivity;
import com.fvision.camera.utils.CameraStateUtil;
import com.fvision.camera.utils.DoCmdUtil;
import com.fvision.camera.utils.LogUtils;
import com.fvision.camera.utils.NetworkUtil;
import com.fvision.camera.utils.SharedPreferencesUtil;
import com.fvision.camera.utils.ToastUtils;
import com.fvision.camera.view.activity.PermissionsActivity;
import com.fvision.camera.view.customview.DialogAbout;
import com.fvision.camera.view.customview.DialogSetting;
import com.fvision.camera.view.customview.InstallPackagesPermissionDialog;
import com.fvision.camera.view.iface.IMainView;
import com.huiying.cameramjpeg.SearchUSB;
import com.huiying.cameramjpeg.UvcCamera;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class MainPresenter extends BasePresenter<IMainView, MainActivity> {
    public static final String[] FLOAT_WINDOWS_PERMISSIONS = {"android.permission.SYSTEM_ALERT_WINDOW"};
    public static final String[] PERMISSIONS = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_PHONE_STATE", "android.permission.READ_EXTERNAL_STORAGE"};
    private static MediaPlayer mediaPlayer;
    boolean adasIsAuth = false;
    boolean adasIsOpen = false;
    private Bitmap bmp = null;
    private int camara_sound_id = -1;
    int count = 0;
    float currentSpeed = 0.0f;
    byte[] demoImg = null;
    private GPSLocationListener gpsListener = new GPSLocationListener() {
        public void UpdateLocation(Location location) {
            MainPresenter.this.currentSpeed = location.getSpeed() * 3.6f;
        }

        public void UpdateStatus(String provider, int status, Bundle extras) {
            LogUtils.d("gps UpdateStatus = " + status);
        }

        public void UpdateGPSProviderStatus(int gpsStatus) {
            switch (gpsStatus) {
                case 0:
                    ToastUtils.showToast((Context) MainPresenter.this.mActivity, "GPS开启");
                    return;
                case 1:
                    ToastUtils.showToast((Context) MainPresenter.this.mActivity, "GPS关闭");
                    return;
                case 2:
                    ToastUtils.showToast((Context) MainPresenter.this.mActivity, "GPS不可用");
                    return;
                case 3:
                    ToastUtils.showToast((Context) MainPresenter.this.mActivity, "GPS暂时不可用");
                    return;
                default:
                    return;
            }
        }
    };
    private GPSLocationManager gpsLocationManager;
    /* access modifiers changed from: private */
    public IMainView iView = null;
    boolean is_show_adas_icon = false;
    boolean is_show_edog_icon = false;
    private long lastSyncTime = 0;
    /* access modifiers changed from: private */
    public MainActivity mActivity;
    private DialogAbout mDialogAbout = null;
    /* access modifiers changed from: private */
    public DialogSetting mDialogSetting;
    /* access modifiers changed from: private */
    public InstallPackagesPermissionDialog mInstallPackagesPermissionDialog;
    private PermissionsChecker mPermissionsChecker;
    private HashMap<Integer, Integer> mSoundMap = new HashMap<>();
    private SoundPool mSoundPool;
    private boolean onlyOnce = false;
    private UsbStatesReceiver receiver = null;
    private SearchUSB searchUSB;
    long startTime = 0;

    public MainPresenter(IMainView view, MainActivity activity) {
        super(view, activity);
        LogUtils.d("MainPersenter 构造");
        this.mActivity = activity;
        this.iView = view;
        this.receiver = new UsbStatesReceiver(activity, this.iView, this);
        initHttp();
    }

    private void printHz() {
        if (this.count < 1) {
            this.startTime = System.currentTimeMillis();
        }
        this.count++;
        if (System.currentTimeMillis() - this.startTime > 1000) {
            ((IMainView) this.mView).showToast("ADAS侦率 " + this.count);
            LogUtils.d("侦率 " + this.count);
            this.count = 0;
            this.startTime = System.currentTimeMillis();
        }
    }

    public void createFile(String path, byte[] content) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(content);
        fos.close();
    }

    public void playVoice() {
        if (SharedPreferencesUtil.isSnapSound(this.mActivity)) {
            if ("".equals("fyt") || Build.MODEL.equals("SP9832A")) {
                SoundManager.getInstance().mediaPlay(R.raw.camera_click);
            } else {
                this.mSoundPool.play(1, 1.0f, 1.0f, 0, 0, 1.0f);
            }
        }
    }

    public void initVoice() {
        if (this.mSoundPool == null) {
            this.mSoundPool = new SoundPool(10, 1, 5);
            this.mSoundPool.load(this.mActivity, R.raw.camera_click, 1);
        }
    }

    public void insertUsb(String path) {
    }

    public void pullUsb() {
        if ("".equals("fyt")) {
            Process.killProcess(Process.myPid());
            System.exit(0);
            return;
        }
        this.mActivity.finish();
    }

    public void showAppVersion() {
        if (this.mDialogAbout == null) {
            this.mDialogAbout = new DialogAbout(this.mActivity);
            this.mDialogAbout.setAppUpdateClick(new View.OnClickListener() {
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT < 26) {
                        Log.e("showAppVersion1", "222");
                        MainPresenter.this.installApp();
                    } else if (MainPresenter.this.mActivity.getPackageManager().canRequestPackageInstalls()) {
                        Log.e("showAppVersion1", "111");
                        MainPresenter.this.installApp();
                    } else {
                        if (MainPresenter.this.mInstallPackagesPermissionDialog == null) {
                            InstallPackagesPermissionDialog unused = MainPresenter.this.mInstallPackagesPermissionDialog = new InstallPackagesPermissionDialog(MainPresenter.this.mActivity);
                        }
                        MainPresenter.this.mInstallPackagesPermissionDialog.show();
                        MainPresenter.this.mInstallPackagesPermissionDialog.setOnFloatWindonsClickLinteners(new InstallPackagesPermissionDialog.OnFloatWindonsClickLinteners() {
                            public void onOk(View view) {
                                MainPresenter.this.startAppSettings();
                            }

                            public void onCancel(View view) {
                                MainPresenter.this.installApp();
                            }
                        });
                    }
                }
            });
            this.mDialogAbout.setDevUpdateClick(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.e("showAppVersion1", "333");
                    MainPresenter.this.aboutDevice();
                }
            });
        }
        String deviceVersion = CmdManager.getInstance().getCurrentState().getPasswd();
        LogUtils.d("1 deviceVersion " + deviceVersion);
        this.mDialogAbout.setAppVersion(this.mActivity.getString(R.string.app_version_format, new Object[]{getAppVersionName(this.mActivity)}));
        this.mDialogAbout.setDeviceVersion(this.mActivity.getString(R.string.device_version_format, new Object[]{deviceVersion}));
        this.mDialogAbout.show();
    }

    /* access modifiers changed from: private */
    public void startAppSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + this.mActivity.getPackageName()));
        this.mActivity.startActivity(intent);
    }

    /* access modifiers changed from: private */
    public void installApp() {
        if (!NetworkUtil.isNetworkAvailable(this.mActivity)) {
            ((IMainView) getView()).showToast(((MainActivity) getActivity()).getString(R.string.network_error));
        } else {
            new Thread(new Runnable() {
                public void run() {
                    ((IMainView) MainPresenter.this.getView()).showLoading();
                    UpgradeManager.getInstance().checkAppUpdate();
                }
            }).start();
        }
    }

    public void showSettingDialog() {
        if (this.mDialogSetting == null) {
            this.mDialogSetting = new DialogSetting(this.mActivity);
            this.mDialogSetting.setFormatClick(new View.OnClickListener() {
                public void onClick(View v) {
                    MainPresenter.this.showAppVersion();
                    MainPresenter.this.mDialogSetting.dismiss();
                }
            });
        }
        this.mDialogSetting.show();
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    public String checkResult() {
        StringBuffer result = new StringBuffer();
        String ver = SharedPreferencesUtil.getLastDevVersion(this.mActivity);
        String path = UvcCamera.getInstance().getDevpath();
        if (TextUtils.isEmpty(path)) {
            result.append(this.mActivity.getString(R.string.undetected_device) + DoCmdUtil.COMMAND_LINE_END);
        } else {
            result.append("记录仪路径:" + path + DoCmdUtil.COMMAND_LINE_END);
        }
        result.append(((MainActivity) getActivity()).getString(R.string.last_info_cord) + DoCmdUtil.COMMAND_LINE_END);
        result.append(((MainActivity) getActivity()).getString(R.string.device_version) + ver + DoCmdUtil.COMMAND_LINE_END);
        result.append(((MainActivity) getActivity()).getString(R.string.app_version) + CameraStateUtil.getVersionName(this.mActivity) + DoCmdUtil.COMMAND_LINE_END);
        result.append(((MainActivity) getActivity()).getString(R.string.android_version) + Build.VERSION.SDK_INT + "(" + Build.VERSION.RELEASE + ")\n");
        result.append(((MainActivity) getActivity()).getString(R.string.device_model) + Build.MODEL + DoCmdUtil.COMMAND_LINE_END);
        result.append("fd error:" + UvcCamera.getInstance().fd_error + DoCmdUtil.COMMAND_LINE_END);
        result.append("cmd fd error:" + UvcCamera.getInstance().cmd_fd_error + DoCmdUtil.COMMAND_LINE_END);
        return result.toString();
    }

    public void checkPermission(int request_code) {
        if (Build.VERSION.SDK_INT < 23) {
            App.getInstance().createDir();
        } else if (this.mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity(request_code);
        } else {
            App.getInstance().createDir();
        }
    }

    public void startPermissionsActivity(int request_code) {
        PermissionsActivity.startActivityForResult(this.mActivity, request_code, PERMISSIONS);
    }

    private void initHttp() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        UpgradeManager.init(this.mActivity, "jianrong_test.apk");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d("MainPersenter onCreate");
        initHttp();
        this.mPermissionsChecker = new PermissionsChecker(this.mActivity);
        initVoice();
        SoundManager.getInstance().setSoundModel(SharedPreferencesUtil.getSoundModel(this.mActivity).intValue());
        this.gpsLocationManager = GPSLocationManager.getInstances(this.mActivity);
        this.gpsLocationManager.start(this.gpsListener);
        boolean edogMainBtnToggle = SharedPreferencesUtil.getEdogMainBtnToggle((Context) getActivity());
        UvcCamera.getInstance().setStateFrameCallback(CameraStateIml.getInstance().mStateFrameCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mSoundPool != null) {
            this.mSoundPool.release();
        }
        if (this.mSoundPool != null) {
            this.mSoundPool.release();
        }
    }

    public void onResume() {
        super.onResume();
        LogUtils.d("MainPersenter onResume");
        CameraStateIml.getInstance().setOnCameraStateListner(new ICameraStateChange() {
            public void stateChange() {
                MainPresenter.this.iView.resrefhView();
            }
        });
        UvcCamera.getInstance().setStateFrameCallback(CameraStateIml.getInstance().mStateFrameCallback);
        if (CmdManager.getInstance().getCurrentState() != null && !CmdManager.getInstance().getCurrentState().isCam_rec_state()) {
            Log.e("issueecss1", "" + CmdManager.getInstance().recToggle());
        }
    }

    public void showFormatSdDialog() {
        CameraStateBean state = CmdManager.getInstance().getCurrentState();
        if (!this.onlyOnce && state.isCam_sd_state() && state.isCam_tf_cu_state()) {
            this.onlyOnce = true;
            new AlertDialog.Builder(this.mActivity).setTitle(R.string.notifycation).setMessage(R.string.confirm_format_tfcard).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    CmdManager.getInstance().formatTf();
                }
            }).setNegativeButton(R.string.no, (DialogInterface.OnClickListener) null).create().show();
        }
    }

    public void adasToggle(ForegroundService mAdasService) {
        SharedPreferencesUtil.setAdasToggle(this.mActivity, !getIsOpenAads());
        mAdasService.setAdasEnable(getIsOpenAads());
    }

    /* access modifiers changed from: protected */
    public boolean isZh() {
        return this.mActivity.getResources().getConfiguration().locale.getLanguage().endsWith("zh");
    }

    public boolean getIsOpenAads() {
        return SharedPreferencesUtil.getAdasToggle(this.mActivity) && SharedPreferencesUtil.getAdasEnableToggle(this.mActivity);
    }

    public void onStart() {
        super.onStart();
        this.receiver.registerReceiver();
    }

    public void onStop() {
        super.onStop();
        this.receiver.unregisterReceiver();
    }

    public void aboutDevice() {
        Log.e("dev_vision.checkUpdate", " code " + CmdManager.getInstance().getCurrentState().getDevVersionCode() + " |name " + CmdManager.getInstance().getCurrentState().getDevVersion() + " |devPackage " + CmdManager.getInstance().getCurrentState().getDevPackageName() + "|cmd_fd_error" + UvcCamera.getInstance().cmd_fd_error);
        if (!UvcCamera.getInstance().fd_error.equals("Success")) {
            ((IMainView) getView()).showToast(this.mActivity.getString(R.string.undetected_device));
        } else if (!UvcCamera.getInstance().cmd_fd_error.equals("Success")) {
            ((IMainView) getView()).showToast(UvcCamera.getInstance().cmd_fd_error + " " + DevFileNameManager.getInstance().getCurrentDev().getPackageName());
        } else if (!CameraStateUtil.isNetworkAvalible((Context) getActivity())) {
            ((IMainView) getView()).showToast(((MainActivity) getActivity()).getString(R.string.no_network));
        } else {
            new Thread(new Runnable() {
                public void run() {
                    ((IMainView) MainPresenter.this.getView()).showLoading();
                    Log.e("dev_vision.presenter", "4");
                    UpgradeManager.getInstance().checkDevUpdate();
                }
            }).start();
        }
    }

    public void edogSwitchClick() {
        boolean isShowEdog = SharedPreferencesUtil.getEdogEnableToggle(this.mActivity);
        this.is_show_edog_icon = !isShowEdog;
        Log.e("isShowEdog ", "isShowEdog " + isShowEdog);
        SharedPreferencesUtil.setEdogEnableToggle(this.mActivity, this.is_show_edog_icon);
        if (this.is_show_edog_icon) {
            App.getInstance().startTuzhiService();
        } else {
            App.getInstance().stopTuzhiService();
        }
    }

    public void getAdasCode(final HttpRequest.RequestIFace mFileDownloadInterface) {
        new Thread(new Runnable() {
            public void run() {
                String sn = CmdManager.getInstance().getDVRUid();
                if (!TextUtils.isEmpty(sn) && !sn.equals("0000000000000000") && !sn.equals("FFFFFFFFFFFFFFFF")) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("sn", sn);
                    map.put("customid", "1");
                    map.put("version", "1");
                    HttpRequest.getADASCode(map, mFileDownloadInterface);
                } else if (mFileDownloadInterface != null) {
                    mFileDownloadInterface.onFail(51, "没有获取到设备码，请偿试重新插拔记录仪" + UvcCamera.getInstance().cmd_fd_error);
                }
            }
        }).start();
    }
}
