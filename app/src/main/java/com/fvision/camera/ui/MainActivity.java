package com.fvision.camera.ui;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;
import com.adasplus.adas.adas.AdasConstants;
import com.alibaba.sdk.android.oss.common.RequestParameters;
import com.fvision.camera.App;
import com.fvision.camera.R;
import com.fvision.camera.adas.IAdasService;
import com.fvision.camera.adas.bean.DrawInfo;
import com.fvision.camera.adas.ui.SVDrawRectangle;
import com.fvision.camera.appupgrade.FileDownloadInterface;
import com.fvision.camera.appupgrade.UpgradeManager;
import com.fvision.camera.base.BaseActivity;
import com.fvision.camera.bean.CameraStateBean;
import com.fvision.camera.http.HttpRequest;
import com.fvision.camera.iface.ICameraStateChange;
import com.fvision.camera.iface.ICoreClientCallback;
import com.fvision.camera.manager.CameraStateIml;
import com.fvision.camera.manager.CmdManager;
import com.fvision.camera.manager.DevFileNameManager;
import com.fvision.camera.manager.SoundManager;
import com.fvision.camera.presenter.MainPresenter;
import com.fvision.camera.service.ForegroundService;
import com.fvision.camera.utils.CameraStateUtil;
import com.fvision.camera.utils.CmdUtil;
import com.fvision.camera.utils.Const;
import com.fvision.camera.utils.DoCmdUtil;
import com.fvision.camera.utils.LogUtils;
import com.fvision.camera.utils.SharedPreferencesUtil;
import com.fvision.camera.utils.ToastUtils;
import com.fvision.camera.view.activity.PlaybackActivity;
import com.fvision.camera.view.activity.SettingActivity;
import com.fvision.camera.view.customview.DevUpgradeDialog;
import com.fvision.camera.view.customview.DialogAdasSetting;
import com.fvision.camera.view.customview.FloatWindowDialog;
import com.fvision.camera.view.customview.FormatTfDialog;
import com.fvision.camera.view.customview.IconView;
import com.fvision.camera.view.iface.AdasKeyResult;
import com.fvision.camera.view.iface.IMainView;
import com.hdsc.edog.view.RingView;
import com.huiying.cameramjpeg.UvcCamera;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import org.opencv.imgproc.Imgproc;
import v4.ContextCompat;

public class MainActivity extends BaseActivity implements IMainView, View.OnClickListener, ICoreClientCallback, SurfaceHolder.Callback {
    public static boolean GetLogDisp = true;
    public static int GetUSER_ID = 0;
    public static final String POWER_OFF_RADAR_ACTION = "landsem.intent.action.RADAR_POWER_OFF";
    public static final String POWER_ON_RADAR_ACTION = "landsem.intent.action.RADAR_POWER_ON";
    public static final int REQUEST_CODE = 0;
    public static final int REQUEST_CODE_APP_UPGRESS = 3;
    public static final int REQUEST_CODE_INSERT_USB = 1;
    public static final int REQUEST_CODE_PHOTO = 2;
    public static final String SHOW_DIALOG_FLAG = "isShowDialog";
    public static ImageView blockLimitSpeedIv;
    public static TextView blockSpaceLbTv;
    public static TextView blockSpaceTv;
    public static TextView blockSpeedLbTv;
    public static TextView blockSpeedTv;
    public static TextView car_speed;
    public static TextView dataVersionTv;
    public static ImageView directionIv;
    public static TextView directionTv;
    public static int hasRun = -1;
    public static int height;
    public static boolean isMinMode = false;
    public static int isSystemBoot = 0;
    public static ImageView ivGps;
    public static TextView muteTv;
    public static ProgressBar pbG;
    public static ProgressBar pbR;
    public static ImageView radarImg;
    public static ImageView radarTypeImg;
    public static RingView ringView;
    public static TextView speed1;
    public static TextView speed2;
    public static TextView speed3;
    public static Button spkBtn;
    public static int startMuteTime = 0;
    public static TextView tvDis;
    public static int width;
    public static ImageView wsImg;
    public static ImageView wtImg;
    public static float xdpi;
    public static float ydpi;
    private final int DELAY_HIDE_TIME = 10000;
    private final String TAG = "MainActivity";
    private final int WHAT_APP_UPGRADE_PROGRESS = 3;
    private final int WHAT_DRAW = 30;
    private final int WHAT_FORMAT_TF_SDCARD = Imgproc.COLOR_YUV2RGB_YVYU;
    private final int WHAT_HIDE_CONTROL_VIEW = 2;
    private final int WHAT_OPEN_FAIL_ERROR = 5;
    private final int WHAT_RESET_SERVICE = 40;
    private final int WHAT_RESREFH_VIEW = 0;
    private final int WHAT_SHOW_DEVICE_INFO = 10;
    private final int WHAT_UPDATE_NO_SIGNAL = 1;
    private long adaStartime = 0;
    private DialogAdasSetting adasDialog;
    private AdasKeyResult adasKeyResult = new AdasKeyResult() {
        public void onSuccess() {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    MainActivity.this.mAdasService.setAdasKeyResult((AdasKeyResult) null);
                    MainActivity.this.dialog.setContent(MainActivity.this.getString(R.string.adas_activate_success));
                    MainActivity.this.dialog.showBtnOk();
                    MainActivity.this.dialog.hideProgress();
                    MainActivity.this.dialog.show();
                }
            });
        }

        public void onFail(int code, String msg) {
            MainActivity.this.dialog.setContent(MainActivity.this.getString(R.string.adas_activate_fail) + " " + msg);
            MainActivity.this.dialog.showBtnOk();
            MainActivity.this.dialog.show();
        }
    };
    private IconView adas_icon_down;
    private IconView adas_icon_up;
    /* access modifiers changed from: private */
    public ProgressBar appUpgradeProgress = null;
    /* access modifiers changed from: private */
    public TextView appUpgradeProgressValue = null;
    private IconView back;
    public float bear = 0.0f;
    private RelativeLayout blueRl;
    public float currentSpeed = 0.0f;
    private TextView device_model;
    /* access modifiers changed from: private */
    public DevUpgradeDialog dialog;
    private ImageView direction;
    private LinearLayout directionLayout;
    private IconView edog_icon_down;
    private IconView edog_icon_up;
    private LinearLayout edog_layout;
    /* access modifiers changed from: private */
    public boolean flag = false;
    private LinearLayout gps_backgroup;
    private RelativeLayout guideRl;
    /* access modifiers changed from: private */
    public boolean isFormatTf = false;
    private boolean isServiceConnection = false;
    private boolean isSetCheck = false;
    private boolean isShowClick = false;
    private int isSupportAdasForDev = -1;
    /* access modifiers changed from: private */
    public boolean isUpgradeing = false;
    private boolean is_snapshot_voice;
    private Animation l2ranimation;
    /* access modifiers changed from: private */
    public RelativeLayout layout;
    /* access modifiers changed from: private */
    public RelativeLayout layout_no_signal = null;
    private View leftmenu;
    private LinearLayout ll_format_ft;
    private LinearLayout ll_hotspot_1;
    private LinearLayout ll_playback_1;
    private LinearLayout ll_setting_1;
    private IconView lock_switch;
    /* access modifiers changed from: private */
    public ForegroundService mAdasService;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (MainActivity.this.mAdasService == null) {
                ForegroundService unused = MainActivity.this.mAdasService = ((ForegroundService.ForegroundBinder) service).getService();
                MainActivity.this.mAdasService.setIsBindService(true);
                MainActivity.this.mAdasService.setAdasEnable(MainActivity.this.presenter.getIsOpenAads());
                MainActivity.this.mAdasService.setDrawInfoListener(new IAdasService.IDrawInfoListener() {
                    public void onDraw(final DrawInfo drawInfo) {
                        if (MainActivity.this.presenter.getIsOpenAads()) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    String str;
                                    boolean z = true;
                                    TextView access$3600 = MainActivity.this.mTvSpeed;
                                    if (drawInfo.getSpeed() < 0.0f) {
                                        str = "0 Km/h";
                                    } else {
                                        str = ((int) (((double) drawInfo.getSpeed()) * 3.6d)) + " Km/h";
                                    }
                                    access$3600.setText(str);
                                    if (MainActivity.this.mSVDraw.isDraw()) {
                                        MainActivity.this.mSVDraw.processResult(drawInfo);
                                    } else {
                                        MainActivity.this.mSVDraw.drawCheckLine();
                                    }
                                    if (drawInfo.getConfig() != null) {
                                        if (MainActivity.this.mAdasService.getAdasInit()) {
                                            MainActivity.this.mSVDraw.setVisibility(0);
                                        }
                                        if (drawInfo.getConfig().getIsCalibCredible() == 1) {
                                            if (MainActivity.this.mIsShowCalibrate) {
                                                MainActivity.this.mTvCalibrate.setVisibility(8);
                                                MainActivity.this.mTvSpeed.setVisibility(8);
                                                boolean unused = MainActivity.this.mIsShowCalibrate = !MainActivity.this.mIsShowCalibrate;
                                            }
                                        } else if (!MainActivity.this.mIsShowCalibrate && SharedPreferencesUtil.getAdasToggle(MainActivity.this)) {
                                            MainActivity.this.mTvCalibrate.setVisibility(0);
                                            MainActivity.this.mTvSpeed.setVisibility(0);
                                            MainActivity.this.mSVDraw.setVisibility(0);
                                            MainActivity mainActivity = MainActivity.this;
                                            if (MainActivity.this.mIsShowCalibrate) {
                                                z = false;
                                            }
                                            boolean unused2 = mainActivity.mIsShowCalibrate = z;
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
                MainActivity.this.setAdasEnable(MainActivity.this.presenter.getIsOpenAads());
                MainActivity.this.mAdasService.setDVRConnectListener(MainActivity.this.mIDVRConnectListener);
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            ForegroundService unused = MainActivity.this.mAdasService = null;
        }
    };
    private Dialog mDialog;
    private FloatWindowDialog mFloatWindowDialog;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    MainActivity.this.presenter.showFormatSdDialog();
                    MainActivity.this.resrefhStateView();
                    return;
                case 1:
                    MainActivity.this.showNoSignal(((Boolean) msg.obj).booleanValue());
                    return;
                case 2:
                    MainActivity.this.showControlView(false);
                    return;
                case 3:
                    MainActivity.this.showAppUpgradeProgress((float) ((Integer) msg.obj).intValue());
                    return;
                case 5:
                    if (!"".equals(".fyt")) {
                        Log.e("fd_error", "" + UvcCamera.getInstance().fd_error + " cmd_fd_error:" + UvcCamera.getInstance().cmd_fd_error);
                        ImageView id_question_mark = (ImageView) MainActivity.this.findViewById(R.id.id_question_mark);
                        ((RelativeLayout) MainActivity.this.findViewById(R.id.layout_undetected_device)).setVisibility(0);
                        if (UvcCamera.getInstance().fd_error.equals("Success") && UvcCamera.getInstance().cmd_fd_error.equals("Success")) {
                            id_question_mark.setBackgroundResource(R.mipmap.usb2);
                            return;
                        } else if (UvcCamera.getInstance().fd_error.equals("not found file")) {
                            id_question_mark.setBackgroundResource(R.mipmap.usb1);
                            return;
                        } else if (MainActivity.this.layout_no_signal.getVisibility() == 0) {
                            MainActivity.this.dialog.setContent("fd " + UvcCamera.getInstance().fd_error + "   cmd " + UvcCamera.getInstance().cmd_fd_error);
                            MainActivity.this.dialog.showBtnOk();
                            MainActivity.this.dialog.hideProgress();
                            MainActivity.this.dialog.show();
                            return;
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                case 30:
                    MainActivity.this.draw();
                    return;
                case 40:
                    if ((MainActivity.this.layout_no_signal == null || MainActivity.this.layout_no_signal.getVisibility() != 8) && UvcCamera.getInstance().fd_error != null && UvcCamera.getInstance().fd_error.equals("Success")) {
                        MainActivity.this.stopService(new Intent(MainActivity.this, ForegroundService.class));
                        MainActivity.this.startService(new Intent(MainActivity.this, ForegroundService.class));
                        return;
                    }
                    return;
                case Imgproc.COLOR_YUV2RGB_YVYU /*117*/:
                    if (MainActivity.this.isFormatTf) {
                        ToastUtils.showLongToast((Context) MainActivity.this, MainActivity.this.getString(R.string.format_tf_sucess));
                        return;
                    } else {
                        ToastUtils.showLongToast((Context) MainActivity.this, MainActivity.this.getString(R.string.format_tf_fail));
                        return;
                    }
                default:
                    return;
            }
        }
    };
    IAdasService.IDVRConnectListener mIDVRConnectListener = new IAdasService.IDVRConnectListener() {
        public void onConnect() {
            Log.e("Adas", "DVRClient connect!");
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    MainActivity.this.setAdasEnable(MainActivity.this.presenter.getIsOpenAads());
                }
            });
        }

        public void onDisConnect() {
            Log.e("Adas", "DVRClient disconnect!");
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    MainActivity.this.surfaceview.setVisibility(8);
                    MainActivity.this.mSVDraw.setVisibility(8);
                    MainActivity.this.mTvCalibrate.setVisibility(8);
                    boolean unused = MainActivity.this.mIsShowCalibrate = false;
                }
            });
        }
    };
    private boolean mIsBindSurface = false;
    /* access modifiers changed from: private */
    public boolean mIsShowCalibrate;
    private GpsLocationListener mLocationListener;
    private LocationManager mLoctionManager;
    /* access modifiers changed from: private */
    public SVDrawRectangle mSVDraw;
    /* access modifiers changed from: private */
    public RelativeLayout mTopLayout;
    /* access modifiers changed from: private */
    public TextView mTvCalibrate;
    private TextView mTvCancel;
    private TextView mTvCheck;
    /* access modifiers changed from: private */
    public TextView mTvSpeed;
    private final float minDistance = 2.0f;
    private final long minTime = 1000;
    private IconView mute_switch;
    /* access modifiers changed from: private */
    public boolean onlyOnce = false;
    Runnable openFailCheckThread = new Runnable() {
        public void run() {
            if (MainActivity.this.layout_no_signal == null || MainActivity.this.layout_no_signal.getVisibility() != 8) {
                try {
                    Thread.sleep(6000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (CameraStateUtil.isForeground(MainActivity.this.getApplicationContext(), "MainActivity")) {
                    MainActivity.this.printTestPrompt();
                }
            }
        }
    };
    private ImageView photo_preview = null;
    private IconView popupWindow;
    /* access modifiers changed from: private */
    public MainPresenter presenter = new MainPresenter(this, this);
    private Animation r2lanimation;
    private IconView record_switch;
    private RelativeLayout rootview;
    private TextView sanction_tips = null;
    private AnimationSet scale0to100;
    private AnimationSet scale100to0;
    private IconView setting;
    private IconView snapshot;
    /* access modifiers changed from: private */
    public List<String> snapshots = new ArrayList();
    private LinearLayout speedLayout;
    private ImageView speed_01;
    private ImageView speed_02;
    private ImageView speed_03;
    /* access modifiers changed from: private */
    public GLSurfaceView surfaceview;
    private TextView test_version;
    private Space upSpace;

    static {
        System.loadLibrary("crypto");
    }

    /* access modifiers changed from: private */
    public void draw() {
        if (this.surfaceview == null) {
            this.mHandler.sendEmptyMessageDelayed(30, 40);
            return;
        }
        this.surfaceview.requestRender();
        this.mHandler.sendEmptyMessageDelayed(30, 40);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d("mainActivity onCreate()");
        hasRun = 1;
        UpgradeManager.init(this, "jianrong_test.apk");
        resetPreview(this.rootview);
        this.presenter.checkPermission(0);
        showCamera();
        showControlView(false);
        resrefhStateView();
        ShowOrHideLocationInfo();
        this.presenter.initVoice();
        this.dialog = new DevUpgradeDialog(this);
        if ("".equals(".fyt")) {
            if (!UvcCamera.getInstance().isPreviewing() && !UvcCamera.getInstance().isInit() && !this.progressDialog.isShowing()) {
                this.progressDialog.show();
            }
            this.layout = (RelativeLayout) findViewById(R.id.layout_insert_device2);
            this.layout.setVisibility(0);
        }
        this.mLoctionManager = (LocationManager) getSystemService(RequestParameters.SUBRESOURCE_LOCATION);
        location(this.mLoctionManager);
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(new Intent(this, ForegroundService.class));
        } else {
            startService(new Intent(this, ForegroundService.class));
        }
        UvcCamera.getInstance().addListener(this);
        sendBroadcast(new Intent(Const.BROAD_CAST_HIDE_FLOATWINDOW));
        new Thread(this.openFailCheckThread).start();
        this.mHandler.sendEmptyMessageDelayed(40, 1000);
        this.mHandler.sendEmptyMessage(30);
        SoundManager.getInstance().init(this);
        sanction();
        Log.e("isCamConnect", "" + CmdManager.getInstance().getCamIsConnectState());
    }

    public void printTestPrompt() {
        this.mHandler.sendEmptyMessage(5);
    }

    public void showAppUpgradeProgress(float progress) {
        this.appUpgradeProgress.setVisibility(0);
        this.appUpgradeProgressValue.setVisibility(0);
        this.appUpgradeProgress.setProgress((int) progress);
        this.appUpgradeProgressValue.setText("" + ((int) progress) + "%");
    }

    private void sanction() {
        LogUtils.d(" sanction() " + SharedPreferencesUtil.isSanction(this));
        this.sanction_tips.setVisibility(SharedPreferencesUtil.isSanction(this) ? 0 : 8);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        Log.d("zoulequan", "mainActivity onResume()");
        bindAdasService();
        if (this.mAdasService != null) {
            this.mAdasService.setAdasFrame();
            this.mAdasService.setIsBindService(SharedPreferencesUtil.getAdasEnableToggle(this));
        }
        this.mSVDraw.setZOrderOnTop(true);
        this.mSVDraw.setZOrderMediaOverlay(true);
        this.mSVDraw.getHolder().setFormat(-2);
        resrefhStateView();
        adasIconState();
        showEdogLayout();
        showAdasIcon();
        showSpace();
        if (SharedPreferencesUtil.getEdogEnableToggle(this)) {
            App.getInstance().startTuzhiService();
        } else if (!SharedPreferencesUtil.getEdogIsBackPlayToggle(this)) {
            App.getInstance().stopTuzhiService();
        }
    }

    private void showAdasIcon() {
        if (SharedPreferencesUtil.getAdasEnableToggle(this)) {
            this.adas_icon_down.setVisibility(0);
        } else {
            this.adas_icon_down.setVisibility(8);
        }
    }

    private void showSpace() {
        boolean isShowSpace;
        int i = 0;
        boolean isShowAdas = SharedPreferencesUtil.getAdasEnableToggle(this);
        boolean edog_auth = SharedPreferencesUtil.getEdogAuthToggle(this);
        if ((!isShowAdas || !edog_auth) && (isShowAdas || edog_auth)) {
            isShowSpace = false;
        } else {
            isShowSpace = true;
        }
        Space space = this.upSpace;
        if (isShowSpace) {
            i = 8;
        }
        space.setVisibility(i);
    }

    private void showCamera() {
        this.surfaceview.setEGLContextClientVersion(2);
        this.surfaceview.setRenderer(new GLSurfaceView.Renderer() {
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                UvcCamera.getInstance().initGles(1280, 720);
            }

            public void onSurfaceChanged(GL10 gl, int width, int height) {
                UvcCamera.getInstance().changeESLayout(width, height);
            }

            public void onDrawFrame(GL10 gl) {
                if (UvcCamera.getInstance().drawESFrame() == 0) {
                    if (!MainActivity.this.onlyOnce) {
                        String deviceVersion = CmdManager.getInstance().getCurrentState().getPasswd();
                        if (!TextUtils.isEmpty(deviceVersion)) {
                            Log.d("sad", "sdasdada");
                            SharedPreferencesUtil.setLastDevVersion(MainActivity.this.getApplicationContext(), deviceVersion);
                            SharedPreferencesUtil.setUsbPath(MainActivity.this.getApplicationContext(), UvcCamera.getInstance().getDevpath());
                            if ("".equals(".vst")) {
                                Log.e("ForegroundService.isVst", "" + ForegroundService.isVst);
                                if (!deviceVersion.contains("W") || !ForegroundService.isVst) {
                                    MainActivity.this.showToast(MainActivity.this.getString(R.string.no_stardar_recoder));
                                } else {
                                    MainActivity.this.sendNoSignalMessage(false);
                                }
                            } else if ("".equals(".fyt")) {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (MainActivity.this.progressDialog.isShowing()) {
                                            if (MainActivity.this.layout.getVisibility() == 0) {
                                                MainActivity.this.layout.setVisibility(8);
                                            }
                                            MainActivity.this.progressDialog.dismiss();
                                        }
                                    }
                                });
                            } else {
                                MainActivity.this.sendNoSignalMessage(false);
                            }
                            boolean unused = MainActivity.this.onlyOnce = true;
                        } else {
                            return;
                        }
                    }
                    if (!"".equals(".vst")) {
                        MainActivity.this.sendNoSignalMessage(false);
                    }
                }
            }
        });
        this.surfaceview.setRenderMode(0);
    }

    /* access modifiers changed from: private */
    public void hideLeftMenu() {
        this.leftmenu.setVisibility(8);
    }

    private void showLeftMenu() {
        this.leftmenu.setVisibility(0);
    }

    /* access modifiers changed from: private */
    public void resrefhStateView() {
        boolean z;
        boolean z2 = true;
        int i = 0;
        if (!isFinishing()) {
            CameraStateBean state = CmdManager.getInstance().getCurrentState();
            if (state.getStateFrame() != null) {
                state.print();
                findViewById(R.id.tf_state).setSelected(state.isCam_sd_state());
                findViewById(R.id.lock_state).setVisibility(state.isCam_lock_state() ? 0 : 8);
                View findViewById = findViewById(R.id.camera_mic_state);
                if (!state.isCam_mute_state()) {
                    z = true;
                } else {
                    z = false;
                }
                findViewById.setSelected(z);
                View findViewById2 = findViewById(R.id.iv_camera_audio);
                if (state.isCam_sd_state()) {
                    z2 = false;
                }
                findViewById2.setSelected(z2);
                findViewById(R.id.iv_camera_lock).setSelected(state.isCam_lock_state());
                findViewById(R.id.iv_record).setSelected(state.isCam_sd_state());
                View findViewById3 = findViewById(R.id.camera_recording_state);
                if (!state.isCam_rec_state()) {
                    i = 8;
                }
                findViewById3.setVisibility(i);
                if (state.isCam_rec_state()) {
                    updateSuccess();
                }
                changeEnable();
                sanction();
                setAdasEnable(SharedPreferencesUtil.getAdasEnableToggle(this));
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateSuccess() {
        if (!SharedPreferencesUtil.isDevUpgrade(getApplicationContext()) || this.dialog == null) {
            Log.e("DevUpgrade", "不在升级，退出");
            return;
        }
        SharedPreferencesUtil.setDevUpgrade(getApplicationContext(), false);
        this.dialog.setContent(getString(R.string.dev_upgrade_success));
        this.dialog.showBtnOk();
        this.dialog.hideProgress();
        this.dialog.show();
    }

    private void changeEnable() {
        boolean isEnable = CmdManager.getInstance().getCurrentState().isCam_sd_state();
        findViewById(R.id.ll_camera_audio).setEnabled(isEnable);
        findViewById(R.id.iv_camera_audio).setEnabled(isEnable);
        findViewById(R.id.ll_camera_lock).setEnabled(isEnable);
        findViewById(R.id.iv_camera_lock).setEnabled(isEnable);
        findViewById(R.id.ll_record).setEnabled(isEnable);
        findViewById(R.id.iv_record).setEnabled(isEnable);
        findViewById(R.id.ll_snapshot).setEnabled(isEnable);
        findViewById(R.id.iv_snapshot).setEnabled(isEnable);
    }

    /* access modifiers changed from: private */
    public void showNoSignal(boolean isShow) {
        this.layout_no_signal.setVisibility(isShow ? 0 : 8);
    }

    /* access modifiers changed from: private */
    public void sendNoSignalMessage(boolean isShow) {
        Message msg = new Message();
        msg.what = 1;
        msg.obj = Boolean.valueOf(isShow);
        this.mHandler.sendMessage(msg);
    }

    /* access modifiers changed from: protected */
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    /* access modifiers changed from: protected */
    public void init() {
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.rootview = (RelativeLayout) findViewById(R.id.rootview);
        this.surfaceview = (GLSurfaceView) findViewById(R.id.glsurfaceview);
        this.layout_no_signal = (RelativeLayout) findViewById(R.id.layout_no_signal);
        this.leftmenu = findViewById(R.id.left_menu);
        this.mute_switch = (IconView) findViewById(R.id.iv_camera_audio);
        this.record_switch = (IconView) findViewById(R.id.iv_record);
        this.snapshot = (IconView) findViewById(R.id.iv_snapshot);
        this.lock_switch = (IconView) findViewById(R.id.iv_camera_lock);
        this.setting = (IconView) findViewById(R.id.iv_setting);
        this.photo_preview = (ImageView) findViewById(R.id.photo_preview);
        this.popupWindow = (IconView) findViewById(R.id.home);
        this.popupWindow.setVisibility(0);
        this.gps_backgroup = (LinearLayout) findViewById(R.id.gps_backgroup);
        this.direction = (ImageView) findViewById(R.id.iv_direction);
        this.speed_01 = (ImageView) findViewById(R.id.iv_speed_01);
        this.speed_02 = (ImageView) findViewById(R.id.iv_speed_02);
        this.speed_03 = (ImageView) findViewById(R.id.iv_speed_03);
        this.back = (IconView) findViewById(R.id.iv_back);
        this.ll_playback_1 = (LinearLayout) findViewById(R.id.ll_playback_1);
        this.ll_hotspot_1 = (LinearLayout) findViewById(R.id.ll_hotspot_1);
        this.ll_format_ft = (LinearLayout) findViewById(R.id.ll_format_ft);
        this.ll_setting_1 = (LinearLayout) findViewById(R.id.ll_setting_1);
        this.test_version = (TextView) findViewById(R.id.test_version);
        this.test_version.setVisibility(8);
        this.device_model = (TextView) findViewById(R.id.device_model);
        this.device_model.setText(Build.MODEL);
        this.appUpgradeProgress = (ProgressBar) findViewById(R.id.app_upgrade_progress);
        this.appUpgradeProgressValue = (TextView) findViewById(R.id.app_upgrade_progress_value);
        this.sanction_tips = (TextView) findViewById(R.id.sanction_tips);
        this.adas_icon_down = (IconView) findViewById(R.id.adas_switch_down);
        this.edog_icon_up = (IconView) findViewById(R.id.edog_switch_up);
        this.upSpace = (Space) findViewById(R.id.upSpace);
        ((AnimationDrawable) findViewById(R.id.camera_recording_state).getBackground()).start();
        initADAS();
        initEdogView();
    }

    /* access modifiers changed from: protected */
    public void initData() {
    }

    /* access modifiers changed from: protected */
    public void initListener() {
        this.surfaceview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.surfaceview_onClick();
            }
        });
        this.mute_switch.setOnClickListener(this);
        this.record_switch.setOnClickListener(this);
        this.lock_switch.setOnClickListener(this);
        this.setting.setOnClickListener(this);
        this.snapshot.setOnClickListener(this);
        this.popupWindow.setOnClickListener(this);
        this.back.setOnClickListener(this);
        this.mute_switch.setOnIconClick(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.switchMute();
            }
        });
        this.record_switch.setOnIconClick(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.switchRecord();
            }
        });
        this.lock_switch.setOnIconClick(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.switchLock();
            }
        });
        this.setting.setOnIconClick(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.goSetting();
            }
        });
        this.snapshot.setOnIconClick(new View.OnClickListener() {
            public void onClick(View v) {
                if (CmdUtil.versionCompareTo("4.0") > 0) {
                    Log.e("msg", "takePicture");
                    MainActivity.this.takePicture();
                    return;
                }
                MainActivity.this.takePicture_screenshot();
            }
        });
        this.popupWindow.setOnIconClick(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.showPopupWindow();
            }
        });
        this.back.setOnIconClick(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.presenter.pullUsb();
            }
        });
        this.photo_preview.setOnClickListener(this);
        this.ll_playback_1.setOnClickListener(this);
        this.ll_hotspot_1.setOnClickListener(this);
        this.ll_format_ft.setOnClickListener(this);
        this.ll_setting_1.setOnClickListener(this);
        this.adas_icon_down.setOnIconClick(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e(AdasConstants.FILE_ADAS, "点击中.....");
                if (MainActivity.this.layout_no_signal.getVisibility() == 0) {
                    MainActivity.this.dialog.setContent(MainActivity.this.getString(R.string.undetected_device));
                    MainActivity.this.dialog.showBtnOk();
                    MainActivity.this.dialog.hideProgress();
                    MainActivity.this.dialog.show();
                    return;
                }
                MainActivity.this.loadAndShowAdas();
            }
        });
        this.edog_icon_up.setOnIconClick(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e("edog_icon_up", "");
                MainActivity.this.presenter.edogSwitchClick();
                MainActivity.this.showEdogLayout();
            }
        });
        UpgradeManager.getInstance().setOnProgressChangeListener(new FileDownloadInterface() {
            public void progress(float progress) {
                Message msg = new Message();
                msg.what = 3;
                msg.obj = Integer.valueOf((int) progress);
                MainActivity.this.mHandler.sendMessage(msg);
            }

            public void complete(String filepath) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        MainActivity.this.showToast(MainActivity.this.getString(R.string.download_complete));
                        MainActivity.this.appUpgradeProgress.setVisibility(8);
                        MainActivity.this.appUpgradeProgressValue.setVisibility(8);
                    }
                });
            }

            public void fail(int code, String error) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        MainActivity.this.dismisLoadingDialog();
                        MainActivity.this.showToast(MainActivity.this.getString(R.string.download_fail));
                        MainActivity.this.appUpgradeProgress.setVisibility(8);
                        MainActivity.this.appUpgradeProgressValue.setVisibility(8);
                    }
                });
            }

            public void update(final int version_code, final String path, String app_desc, final String version) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        String upgradePint;
                        MainActivity.this.dismisLoadingDialog();
                        if ("".equals(".px3")) {
                            upgradePint = "Add ADAS";
                        } else {
                            upgradePint = MainActivity.this.getString(R.string.upgrade_pint);
                        }
                        UpgradeManager.getInstance().update(version_code, path, upgradePint, version);
                    }
                });
            }
        });
        UpgradeManager.getInstance().setOnDevProgressChangeListener(new FileDownloadInterface() {
            public void progress(float progress) {
                final int upgradeProgress = (int) progress;
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        MainActivity.this.dialog.hideBtnOk();
                        MainActivity.this.dialog.showProgress();
                        MainActivity.this.dialog.setContent(MainActivity.this.getString(R.string.dev_file_download_progress) + " " + upgradeProgress + "%");
                        MainActivity.this.dialog.show();
                        MainActivity.this.dialog.timeOutHide(61000, new View.OnClickListener() {
                            public void onClick(View v) {
                                MainActivity.this.dialog.showBtnOk();
                                MainActivity.this.dialog.hideProgress();
                                MainActivity.this.dialog.setContent(MainActivity.this.getString(R.string.dev_download_time_out));
                                MainActivity.this.dialog.show();
                            }
                        });
                    }
                });
            }

            public void complete(String filepath) {
                boolean unused = MainActivity.this.isUpgradeing = true;
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.e("showAppVersion1", "444");
                        SharedPreferencesUtil.setDevUpgrade(MainActivity.this.getApplicationContext(), true);
                        MainActivity.this.dialog.setContent(MainActivity.this.getString(R.string.dev_reset_content));
                        MainActivity.this.dialog.show();
                        MainActivity.this.dialog.timeOutHide(30000, new View.OnClickListener() {
                            public void onClick(View v) {
                                if (UvcCamera.getInstance().isInit()) {
                                    MainActivity.this.showDevUpgradeSucess();
                                } else {
                                    MainActivity.this.dialog.showBtnOk();
                                }
                            }
                        });
                    }
                });
            }

            public void fail(int code, final String error) {
                boolean unused = MainActivity.this.isUpgradeing = false;
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        SharedPreferencesUtil.setDevUpgrade(MainActivity.this.getApplicationContext(), false);
                        MainActivity.this.dismisLoadingDialog();
                        MainActivity.this.dialog.setContent(error);
                        MainActivity.this.dialog.hideProgress();
                        MainActivity.this.dialog.showBtnOk();
                        MainActivity.this.dialog.show();
                    }
                });
            }

            public void update(int version_code, String path, String app_desc, String version) {
                final int i = version_code;
                final String str = path;
                final String str2 = app_desc;
                final String str3 = version;
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        MainActivity.this.dismisLoadingDialog();
                        UpgradeManager.getInstance().update(i, str, str2, str3);
                    }
                });
            }
        });
    }

    private boolean checkActiviteAdas() {
        String uuidCode = CmdManager.getInstance().getUUIDCode();
        Log.e("uuidCode", "" + uuidCode);
        if (uuidCode == null || TextUtils.isEmpty(uuidCode)) {
            ToastUtils.showLongToast((Context) this, "error:" + UvcCamera.getInstance().cmd_fd_error);
            return true;
        } else if (uuidCode.length() < 14 || uuidCode.equals("0000000000000000") || uuidCode.equals("FFFFFFFFFFFFFFFF")) {
            return true;
        } else {
            if (this.mAdasService == null || this.mAdasService.getAdasInterfaceImp() == null || this.mAdasService.getAdasInterfaceImp().getVerifyResult() > 0) {
                return false;
            }
            return true;
        }
    }

    private void activiteAdas() {
        this.dialog.setContent(getString(R.string.adas_activate_ing));
        this.dialog.hideBtnOk();
        this.dialog.showProgress();
        this.dialog.show();
        this.dialog.timeOutHide(30000, new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.dialog.setContent(MainActivity.this.getString(R.string.network_connection_timeout));
                MainActivity.this.dialog.showBtnOk();
                MainActivity.this.dialog.hideProgress();
                MainActivity.this.dialog.show();
            }
        });
        Log.e("", "开始激活");
        this.presenter.getAdasCode(new HttpRequest.RequestIFace() {
            public void onSuccess(int version_code, String path, String app_desc, String version) {
                Log.e("", "获取激活码成功 " + path);
                CmdManager.getInstance().setUUIDCode(path);
                MainActivity.this.mAdasService.activiteAdas(path);
            }

            public void onFail(final int errorCode, final String error) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        MainActivity.this.dialog.setContent(MainActivity.this.getString(R.string.adas_activate_fail) + errorCode + "  " + error);
                        MainActivity.this.dialog.showBtnOk();
                        MainActivity.this.dialog.hideProgress();
                        MainActivity.this.dialog.show();
                    }
                });
            }
        });
    }

    /* access modifiers changed from: private */
    public void loadAndShowAdas() {
        Log.e("loadAndShowAdas", "adasUuid " + CmdManager.getInstance().getUUIDCode() + (this.mAdasService == null));
        if (this.mAdasService != null) {
            if (!this.mAdasService.getAdasState()) {
                StringBuffer tips = new StringBuffer();
                Log.e("loadAndShowAdas", "mAdasService.getAdasState()" + this.mAdasService.getAdasState());
                if (checkActiviteAdas()) {
                    if (!CameraStateUtil.isNetworkAvalible(getApplicationContext())) {
                        this.dialog.setContent(getString(R.string.adas_first_activate));
                        this.dialog.showBtnOk();
                        this.dialog.hideProgress();
                        this.dialog.show();
                        return;
                    } else if (this.mAdasService != null) {
                        this.mAdasService.setAdasKeyResult(this.adasKeyResult);
                        activiteAdas();
                    } else {
                        this.dialog.setContent(getString(R.string.server_is_not_start));
                        this.dialog.showBtnOk();
                        this.dialog.hideProgress();
                        this.dialog.show();
                    }
                } else if (System.currentTimeMillis() - this.adaStartime > 5000) {
                    this.adaStartime = 0;
                } else {
                    ToastUtils.showLongToast(getApplicationContext(), getString(R.string.adas_starting) + ".....");
                    this.adaStartime = System.currentTimeMillis();
                    return;
                }
                if (this.mAdasService.getAdasInterfaceImp() == null) {
                    tips.append("error code:105");
                    this.mAdasService.startADAS(CmdManager.getInstance().getUUIDCode());
                    this.adaStartime = System.currentTimeMillis();
                    ToastUtils.showLongToast(getApplicationContext(), getString(R.string.adas_starting) + ".....");
                    return;
                }
                int verify = this.mAdasService.getAdasInterfaceImp().getVerifyResult();
                if (verify <= 0) {
                    tips.append("没有激活,verify = " + verify);
                    this.mAdasService.startADAS(CmdManager.getInstance().getUUIDCode());
                    this.adaStartime = System.currentTimeMillis();
                    ToastUtils.showLongToast(getApplicationContext(), getString(R.string.adas_starting) + ".....");
                    return;
                }
                Log.e("loadAndShowAdas", "verify" + verify);
                if (this.mAdasService.getAdasInterfaceImp().getAdasConfig() == null) {
                    showToast(getString(R.string.adas_starting) + "！");
                    return;
                }
                return;
            }
            this.adasDialog = new DialogAdasSetting(this.mAdasService);
            this.adasDialog.show(getFragmentManager(), "adasDialog");
            this.adasDialog.setOnAdasSetListener(new DialogAdasSetting.OnAdasSetListener() {
                public void adasAdjust(boolean toggle) {
                    if (!MainActivity.this.mAdasService.getAdasState()) {
                        Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.recorder_not_ready), 0).show();
                        return;
                    }
                    MainActivity.this.mSVDraw.setIsDraw(false);
                    MainActivity.this.barDisplay();
                    boolean unused = MainActivity.this.flag = true;
                }

                public void adasToggle(boolean toggle) {
                    MainActivity.this.mainAdasToggle();
                }
            });
        }
    }

    private void ShowOrHideLocationInfo() {
        if ("".equals(".overseas")) {
            this.gps_backgroup.setVisibility(0);
            this.direction.setVisibility(0);
            this.speed_01.setVisibility(0);
            this.speed_02.setVisibility(0);
            this.speed_03.setVisibility(0);
            return;
        }
        this.gps_backgroup.setVisibility(8);
        this.direction.setVisibility(8);
        this.speed_01.setVisibility(8);
        this.speed_02.setVisibility(8);
        this.speed_03.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void showDevUpgradeSucess() {
        this.isUpgradeing = false;
        SharedPreferencesUtil.setDevUpgrade(getApplicationContext(), false);
        this.dialog.setContent(getString(R.string.dev_upgrade_success));
        this.dialog.showBtnOk();
        this.dialog.hideProgress();
        this.dialog.show();
    }

    /* access modifiers changed from: private */
    public void surfaceview_onClick() {
        boolean isShow;
        boolean z = true;
        View left_bar = findViewById(R.id.left_bar);
        View setting_layout = findViewById(R.id.left_menu);
        if (left_bar.getVisibility() == 0) {
            isShow = true;
        } else {
            isShow = false;
        }
        if (isShow) {
            z = false;
        }
        showControlView(z);
        if (!isShow) {
            timerHideControlView();
        }
        setting_layout.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void showControlView(boolean isShow) {
        View left_bar = findViewById(R.id.left_bar);
        View right_bar = findViewById(R.id.right_bar);
        View findViewById = findViewById(R.id.photo_preview);
        if (isShow) {
            showScale0to100(left_bar);
            showScale0to100(right_bar);
        } else if (left_bar.getVisibility() == 0) {
            hideScale100to0(left_bar);
            hideScale100to0(right_bar);
        }
    }

    private void timerHideControlView() {
        this.mHandler.removeMessages(2);
        this.mHandler.sendEmptyMessageDelayed(2, 10000);
    }

    private void showScale0to100(View v) {
        if (this.scale0to100 == null) {
            this.scale0to100 = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.scale_0_to_100);
        }
        v.startAnimation(this.scale0to100);
        v.setVisibility(0);
    }

    private void hideScale100to0(View v) {
        if (this.scale100to0 == null) {
            this.scale100to0 = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.scale_100_to_0);
        }
        v.startAnimation(this.scale100to0);
        v.setVisibility(8);
    }

    private void showRight2LeftView(View v) {
        if (this.r2lanimation == null) {
            this.r2lanimation = AnimationUtils.loadAnimation(this, R.anim.translate);
        }
        v.startAnimation(this.r2lanimation);
        v.setVisibility(0);
    }

    private void hideLeft2RightView(View v) {
        if (this.l2ranimation == null) {
            this.l2ranimation = AnimationUtils.loadAnimation(this, R.anim.translate_left_to_right);
        }
        v.startAnimation(this.l2ranimation);
        v.setVisibility(8);
    }

    public void showLoading() {
        runOnUiThread(new Runnable() {
            public void run() {
                MainActivity.this.showLoadingDialog();
            }
        });
    }

    public void closeLoading() {
        dismisLoadingDialog();
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                ToastUtils.showLongToast(MainActivity.this.getApplicationContext(), msg);
            }
        });
    }

    public void resrefhView() {
        this.mHandler.sendEmptyMessage(0);
    }

    public void usbStateChange(String path, int state) {
        if (state == 0) {
            if ("".equals(".px3")) {
                this.presenter.insertUsb(path.replace("/storage", "/mnt/media_rw"));
            } else {
                this.presenter.insertUsb(path);
            }
        } else if (!"".equals(".fyt")) {
            this.presenter.pullUsb();
            if (!SharedPreferencesUtil.isDevUpgrade(getApplicationContext())) {
                Process.killProcess(Process.myPid());
                System.exit(0);
            }
        }
    }

    public void adasSoundPlay(final int raw) {
        runOnUiThread(new Runnable() {
            public void run() {
                SoundManager.getInstance().adasPlay(raw);
            }
        });
    }

    public void edogSoundPlay(ArrayList<Integer> arrayList) {
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home:
                showPopupWindow();
                return;
            case R.id.ll_camera_audio:
                switchMute();
                return;
            case R.id.ll_record:
                switchRecord();
                return;
            case R.id.ll_camera_lock:
                switchLock();
                return;
            case R.id.ll_snapshot:
                if (CmdUtil.versionCompareTo("4.0") > 0) {
                    Log.e("msg", "takePicture");
                    takePicture();
                    return;
                }
                takePicture_screenshot();
                return;
            case R.id.ll_setting:
                Log.e("msms", "设置界面");
                goSetting();
                return;
            case R.id.iv_back:
                this.presenter.pullUsb();
                return;
            case R.id.ll_playback_1:
                Log.e("", "点击进入回放模式");
                goPlayBackActivity();
                return;
            case R.id.ll_setting_1:
                startActivity(new Intent(this, SettingActivity.class));
                return;
            case R.id.ll_format_ft:
                formatTf();
                return;
            case R.id.ll_hotspot_1:
                this.presenter.showAppVersion();
                return;
            default:
                return;
        }
    }

    private void adasIconState() {
        boolean iconState = SharedPreferencesUtil.getAdasToggle(this.mContext);
        Log.e("iconState", "" + iconState);
        this.adas_icon_down.setBackgroundResource((!iconState || !SharedPreferencesUtil.getAdasEnableToggle(this.mContext)) ? R.mipmap.adas_icon_nor : R.mipmap.adas_icon_enable);
    }

    /* access modifiers changed from: private */
    public void showEdogLayout() {
        int i;
        int i2 = 0;
        boolean edog_enable = SharedPreferencesUtil.getEdogEnableToggle(this);
        boolean edog_auth = SharedPreferencesUtil.getEdogAuthToggle(this);
        Log.e("showEdogLayout", "" + edog_enable + edog_auth + (this.edog_layout.getVisibility() == 0));
        LinearLayout linearLayout = this.edog_layout;
        if (!edog_enable || !edog_auth) {
            i = 8;
        } else {
            i = 0;
        }
        linearLayout.setVisibility(i);
        IconView iconView = this.edog_icon_up;
        if (!edog_auth) {
            i2 = 8;
        }
        iconView.setVisibility(i2);
        this.edog_icon_up.setBackgroundResource((!edog_enable || !edog_auth) ? R.mipmap.edog_icon_nor : R.mipmap.edog_icon_enable);
    }

    private void syncTime() {
        new Thread(new Runnable() {
            public void run() {
                CmdManager.getInstance().syncTime();
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public void sendCmd(Runnable runnable, final String toastContent) {
        if (!CmdManager.getInstance().getCurrentState().isCam_sd_state()) {
            ToastUtils.showLongToast((Context) this, (int) R.string.has_no_tfcard);
            return;
        }
        showLoadingDialog();
        timeOutCloseLoadingDialog(1500);
        new Thread(runnable).start();
        CameraStateIml.getInstance().setOnCameraStateOnlyOnceListner(new ICameraStateChange() {
            public void stateChange() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        MainActivity.this.dismisLoadingDialog();
                        if (toastContent != null) {
                            try {
                                if (toastContent.contains("only") || toastContent.contains("Read-only") || toastContent.contains("read") || toastContent.contains("Only")) {
                                    ToastUtils.showToast(MainActivity.this.getApplicationContext(), DoCmdUtil.execCommand(DoCmdUtil.cmd, false).toString());
                                    return;
                                }
                                ToastUtils.showToast(MainActivity.this.getApplicationContext(), toastContent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    /* access modifiers changed from: private */
    public void showPopupWindow() {
        if (Build.VERSION.SDK_INT < 23) {
            finish();
            sendBroadcast(new Intent(Const.BROAD_CAST_SHOW_FLOATWINDOW));
        } else if (!Settings.canDrawOverlays(getApplicationContext())) {
            if (this.mFloatWindowDialog == null) {
                this.mFloatWindowDialog = new FloatWindowDialog(this);
            }
            this.mFloatWindowDialog.show();
            this.mFloatWindowDialog.setOnFloatWindonsClickLinteners(new FloatWindowDialog.OnFloatWindonsClickLinteners() {
                public void onOk(View view) {
                    MainActivity.this.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + MainActivity.this.getPackageName())));
                }

                public void onCancel(View view) {
                }
            });
        } else {
            finish();
            sendBroadcast(new Intent(Const.BROAD_CAST_SHOW_FLOATWINDOW));
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.d("onNewIntent close service");
        sendBroadcast(new Intent(Const.BROAD_CAST_HIDE_FLOATWINDOW));
    }

    public void photoPreview() {
        gotoSnapshotPreview();
    }

    public void goSetting() {
        showLeftMenu();
        showControlView(false);
    }

    private String getDevVersion() {
        String currentVersion = CmdManager.getInstance().getCurrentState().getDevVersion();
        int startPos = currentVersion.indexOf("_v");
        return currentVersion.substring(startPos + 2, startPos + 5);
    }

    private void formatTf() {
        FormatTfDialog tfDialog = new FormatTfDialog(this);
        tfDialog.setOnFloatWindonsClickLinteners(new FormatTfDialog.OnFloatWindonsClickLinteners() {
            public void onOk(View view) {
                MainActivity.this.sendCmd(new Runnable() {
                    public void run() {
                        boolean unused = MainActivity.this.isFormatTf = CmdManager.getInstance().formatTf();
                        MainActivity.this.mHandler.sendEmptyMessageDelayed(Imgproc.COLOR_YUV2RGB_YVYU, 1000);
                    }
                }, "");
            }

            public void onCancel(View view) {
            }
        });
        tfDialog.show();
    }

    /* access modifiers changed from: private */
    public void switchRecord() {
        sendCmd(new Runnable() {
            public void run() {
                CmdManager.getInstance().recToggle();
            }
        }, (String) null);
    }

    /* access modifiers changed from: private */
    public void switchMute() {
        sendCmd(new Runnable() {
            public void run() {
                CmdManager.getInstance().recSoundToggle();
            }
        }, (String) null);
    }

    /* access modifiers changed from: private */
    public void switchLock() {
        sendCmd(new Runnable() {
            public void run() {
                CmdManager.getInstance().lockToggle();
            }
        }, (String) null);
    }

    /* access modifiers changed from: private */
    public void takePicture() {
        if (!CmdManager.getInstance().getCurrentState().isCam_sd_state()) {
            ToastUtils.showLongToast((Context) this, (int) R.string.has_no_tfcard);
            return;
        }
        showLoadingDialog();
        timeOutCloseLoadingDialog(1200);
        this.presenter.playVoice();
        new Thread(new Runnable() {
            public void run() {
                final boolean ret = CmdManager.getInstance().takePictures();
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (ret) {
                            ToastUtils.showToast(MainActivity.this.getApplicationContext(), (int) R.string.take_snapshot_sucess);
                        }
                        MainActivity.this.dismisLoadingDialog();
                    }
                });
            }
        }).start();
        dismisLoadingDialog();
        ToastUtils.showToast(getApplicationContext(), (int) R.string.take_snapshot_sucess);
        CameraStateIml.getInstance().setOnCameraStateOnlyOnceListner(new ICameraStateChange() {
            public void stateChange() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                    }
                });
            }
        });
    }

    /* access modifiers changed from: private */
    public void takePicture_screenshot() {
        if (!CmdManager.getInstance().getCurrentState().isCam_sd_state()) {
            ToastUtils.showToast((Context) this, (int) R.string.has_no_tfcard);
            return;
        }
        showLoadingDialog();
        new Thread(new Runnable() {
            public void run() {
                final String path = Const.JPG_PATH + (new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss-SSS").format(new Date()) + ".jpg");
                final boolean sucess = UvcCamera.getInstance().takeSnapshot(path);
                MainActivity.this.presenter.playVoice();
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (sucess) {
                            ToastUtils.showLongToast(MainActivity.this.getApplicationContext(), (int) R.string.take_snapshot_sucess);
                            MainActivity.this.snapshots.add(0, path);
                            MainActivity.this.showSnapshot();
                        } else {
                            MainActivity.this.presenter.checkPermission(2);
                        }
                        MainActivity.this.dismisLoadingDialog();
                    }
                });
            }
        }).start();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            ToastUtils.showToast((Context) this, getString(R.string.request_refused));
            return;
        }
        ToastUtils.showToast((Context) this, getString(R.string.request_granted));
        if (requestCode == 0) {
            App.getInstance().createDir();
        } else if (requestCode == 1) {
            String temPath = (String) SharedPreferencesUtil.getData(this, SharedPreferencesUtil.TEMPORARY_USB_PATH, "");
            if (!temPath.equals("") && CameraStateUtil.isMyUsb(temPath)) {
                String devpath = temPath + "/" + DevFileNameManager.getInstance().getCurrentDev().getPreView();
                SharedPreferencesUtil.saveData(this, SharedPreferencesUtil.USB_PATH, devpath);
                UvcCamera.getInstance().setDevpath(devpath);
                this.presenter.insertUsb(devpath);
            }
        }
        ToastUtils.showToast((Context) this, getString(R.string.request_granted));
    }

    public void printTestPrompt(String prompt) {
        Message msg = new Message();
        msg.what = 5;
        msg.obj = prompt;
        this.mHandler.sendMessage(msg);
    }

    private void goPlayBackActivity() {
        if (!CmdManager.getInstance().getCurrentState().isCam_sd_state()) {
            ToastUtils.showLongToast((Context) this, (int) R.string.has_no_tfcard);
        } else if (CmdManager.getInstance().getCurrentState().isCam_rec_state() || !CmdManager.getInstance().getCurrentState().isCam_mode_state()) {
            showLoadingDialog();
            timeOutCloseLoadingDialog(2000);
            Log.e("", "点击进入回放模式3");
            CameraStateIml.getInstance().setOnCameraStateCustomListner(new ICameraStateChange() {
                public void stateChange() {
                    CameraStateBean state = CmdManager.getInstance().getCurrentState();
                    if (state.isCam_mode_state() && !state.isCam_rec_state() && state.getFile_index() > -1 && state.isCam_plist_state()) {
                        CameraStateIml.getInstance().setOnCameraStateCustomListner((ICameraStateChange) null);
                        Log.e("", "满足进入回放模式条件");
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                MainActivity.this.hideLeftMenu();
                                MainActivity.this.dismisLoadingDialog();
                                UvcCamera.getInstance().setMainRuning(false);
                                MainActivity.this.startActivity(new Intent(MainActivity.this, PlaybackActivity.class));
                            }
                        });
                    }
                    Log.e("", "点击进入回放模式2");
                }
            });
            if (CmdManager.getInstance().getCurrentState().isCam_rec_state()) {
                CmdManager.getInstance().recToggle();
            }
            if (!CmdManager.getInstance().getCurrentState().isCam_mode_state()) {
                CmdManager.getInstance().modelToggle();
            }
        } else {
            hideLeftMenu();
            UvcCamera.getInstance().setMainRuning(false);
            Intent intent = new Intent(this, PlaybackActivity.class);
            Log.e("", "点击进入回放模式1");
            startActivity(intent);
        }
    }

    /* access modifiers changed from: private */
    public void showSnapshot() {
        if (this.snapshots.size() > 0) {
            this.photo_preview.setImageURI(Uri.fromFile(new File(this.snapshots.get(0))));
            this.photo_preview.setVisibility(8);
            return;
        }
        this.photo_preview.setVisibility(8);
    }

    private void gotoSnapshotPreview() {
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            this.presenter.pullUsb();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initADAS() {
        this.mSVDraw = (SVDrawRectangle) findViewById(R.id.svdraw);
        this.mTvSpeed = (TextView) findViewById(R.id.tv_main_speed);
        this.mTvCalibrate = (TextView) findViewById(R.id.tv_main_calibrate);
        this.mTvCheck = (TextView) findViewById(R.id.tv_adas_check);
        this.mTvCancel = (TextView) findViewById(R.id.tv_adas_setting);
        this.mTopLayout = (RelativeLayout) findViewById(R.id.top_layout);
        this.mTopLayout.setVisibility(8);
        initADASListener();
    }

    private void bindAdasService() {
        this.isServiceConnection = bindService(new Intent(getApplicationContext(), ForegroundService.class), this.mConnection, 1);
    }

    private void initADASListener() {
        this.mSVDraw.setListener(new SVDrawRectangle.IAdasConfigListener() {
            public void setAdasConfigXY(float x, float y) {
                if (MainActivity.this.mAdasService != null) {
                    MainActivity.this.mAdasService.getAdasInterfaceImp().setVpoint(x, y);
                }
            }
        });
        this.mSVDraw.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != 1) {
                    return false;
                }
                MainActivity.this.surfaceview_onClick();
                Log.e("msg", MainActivity.this.mSVDraw.getHeight() + "." + MainActivity.this.mSVDraw.getWidth());
                return false;
            }
        });
        this.mTvCheck.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.mSVDraw.setIsDraw(true);
                MainActivity.this.barDisplay();
                boolean unused = MainActivity.this.flag = false;
                Handler handler = MainActivity.this.mSVDraw.getHandler();
                if (handler != null) {
                    handler.sendEmptyMessage(153);
                }
            }
        });
        this.mTvCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.mSVDraw.setIsDraw(true);
                MainActivity.this.barDisplay();
                boolean unused = MainActivity.this.flag = false;
            }
        });
    }

    /* access modifiers changed from: private */
    public void barDisplay() {
        this.isShowClick = !this.isShowClick;
        if (this.isShowClick) {
            runOnUiThread(new Runnable() {
                public void run() {
                    MainActivity.this.mTopLayout.setVisibility(0);
                }
            });
            AlphaAnimation am = new AlphaAnimation(0.0f, 1.0f);
            am.setDuration(200);
            this.mTopLayout.startAnimation(am);
            return;
        }
        AlphaAnimation am2 = new AlphaAnimation(1.0f, 0.0f);
        am2.setDuration(200);
        this.mTopLayout.startAnimation(am2);
        this.mTopLayout.setVisibility(8);
    }

    public void onConnect() {
    }

    public void onDisconnect() {
    }

    public void initCmd(boolean isSuccess, String s) {
        if (!isSuccess) {
        }
    }

    public void onIsAvailable(boolean b) {
    }

    public void onInit(boolean isSuccess, int i, String s) {
        if (isSuccess && !isFinishing()) {
            runOnUiThread(new Runnable() {
                public void run() {
                    MainActivity.this.updateSuccess();
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        Log.d("zoulequan", "mainActivity onPause()");
        if (this.mAdasService != null && !SharedPreferencesUtil.getAdasIsBackPlayToggle(this)) {
            this.mAdasService.setIsBindService(false);
        }
        if (this.isServiceConnection) {
            unbindService(this.mConnection);
            this.isServiceConnection = false;
        }
        if (!SharedPreferencesUtil.getEdogIsBackPlayToggle(this)) {
            App.getInstance().stopTuzhiService();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d("mainactivity onDestroy()");
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.mIsBindSurface = true;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width2, int height2) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.mIsBindSurface = false;
        this.mTvCheck.setText(getString(R.string.check));
    }

    private void location(LocationManager gpsManager) {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            if (gpsManager.getLastKnownLocation("gps") == null) {
                Location location = gpsManager.getLastKnownLocation("network");
            }
            this.mLocationListener = new GpsLocationListener();
            gpsManager.requestLocationUpdates("gps", 1000, 2.0f, this.mLocationListener);
        }
    }

    public class GpsLocationListener implements LocationListener {
        public GpsLocationListener() {
        }

        public void onLocationChanged(Location location) {
            MainActivity.this.currentSpeed = location.getSpeed() * 3.6f;
            MainActivity.this.bear = location.getBearing();
            if ("".equals(".overseas")) {
                MainActivity.this.showDirection(MainActivity.this.bear);
                MainActivity.this.showSpeed(MainActivity.this.currentSpeed);
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void showDirection(float direction_num) {
        if (direction_num >= 0.0f && direction_num <= 360.0f) {
            int eum = (int) (direction_num / 45.0f);
            if (eum == 0 || eum == 8) {
                this.direction.setBackgroundDrawable(getResources().getDrawable(R.mipmap.n));
            } else if (eum > 0 && eum < 2) {
                this.direction.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ne));
            } else if (eum == 2) {
                this.direction.setBackgroundDrawable(getResources().getDrawable(R.mipmap.e));
            } else if (eum > 2 && eum < 4) {
                this.direction.setBackgroundDrawable(getResources().getDrawable(R.mipmap.f0se));
            } else if (eum == 4) {
                this.direction.setBackgroundDrawable(getResources().getDrawable(R.mipmap.s));
            } else if (eum > 4 && eum < 6) {
                this.direction.setBackgroundDrawable(getResources().getDrawable(R.mipmap.sw));
            } else if (eum == 6) {
                this.direction.setBackgroundDrawable(getResources().getDrawable(R.mipmap.w));
            } else if (eum > 6 && eum < 8) {
                this.direction.setBackgroundDrawable(getResources().getDrawable(R.mipmap.nw));
            }
        }
    }

    /* access modifiers changed from: private */
    public void showSpeed(float speed) {
        int g = ((int) (speed % 100.0f)) % 10;
        int s = (int) ((speed % 100.0f) / 10.0f);
        int b = (int) (speed / 100.0f);
        if (speed > 100.0f) {
            this.speed_01.setVisibility(0);
            this.speed_01.setBackgroundDrawable(getSpeedNum(b));
            this.speed_02.setBackgroundDrawable(getSpeedNum(s));
            this.speed_03.setBackgroundDrawable(getSpeedNum(g));
        } else if (speed == 0.0f) {
            this.speed_01.setVisibility(8);
            this.speed_02.setBackgroundDrawable(getSpeedNum(0));
            this.speed_03.setBackgroundDrawable(getSpeedNum(0));
        } else {
            this.speed_01.setVisibility(8);
            this.speed_02.setBackgroundDrawable(getSpeedNum(s));
            this.speed_03.setBackgroundDrawable(getSpeedNum(g));
        }
    }

    private Drawable getSpeedNum(int num) {
        if (num == 0) {
            return getResources().getDrawable(R.mipmap.d0);
        }
        if (num == 1) {
            return getResources().getDrawable(R.mipmap.d1);
        }
        if (num == 2) {
            return getResources().getDrawable(R.mipmap.d2);
        }
        if (num == 3) {
            return getResources().getDrawable(R.mipmap.d3);
        }
        if (num == 4) {
            return getResources().getDrawable(R.mipmap.d4);
        }
        if (num == 5) {
            return getResources().getDrawable(R.mipmap.d5);
        }
        if (num == 6) {
            return getResources().getDrawable(R.mipmap.d6);
        }
        if (num == 7) {
            return getResources().getDrawable(R.mipmap.d7);
        }
        if (num == 8) {
            return getResources().getDrawable(R.mipmap.d8);
        }
        if (num == 9) {
            return getResources().getDrawable(R.mipmap.d9);
        }
        return null;
    }

    private void uploadAdasError(String error) {
        if (error == null) {
            String error2 = " last Imei" + SharedPreferencesUtil.getAdasImei(getApplicationContext()) + " last Key " + SharedPreferencesUtil.getAdasImei(getApplicationContext()) + "  now Imei " + this.mAdasService.getAdasImei() + " now Key " + this.mAdasService.getAdasKey() + " verifyResult " + this.mAdasService.getAdasInterfaceImp().getVerifyResult();
        }
    }

    /* access modifiers changed from: private */
    public void mainAdasToggle() {
        this.presenter.adasToggle(this.mAdasService);
        Log.e("mainAdasToggle()", "111");
        setAdasEnable(this.presenter.getIsOpenAads());
        adasIconState();
    }

    /* access modifiers changed from: private */
    public void setAdasEnable(boolean isn) {
        Log.e("setAdasEnable", "" + isn);
        if (!isn) {
            this.mSVDraw.setVisibility(8);
            this.mTvCalibrate.setVisibility(8);
            this.mTvCheck.setVisibility(8);
            this.mTvSpeed.setVisibility(8);
            this.mIsShowCalibrate = false;
            return;
        }
        if (this.isSupportAdasForDev != -1) {
            this.mSVDraw.setVisibility(0);
        }
        this.mTvCheck.setVisibility(0);
    }

    private void initEdogView() {
        Log.e("dd", "initViews  1 ");
        this.edog_layout = (LinearLayout) findViewById(R.id.edog_layout);
        wtImg = (ImageView) findViewById(R.id.main_iv_warntype);
        wsImg = (ImageView) findViewById(R.id.main_iv_warnspeed);
        tvDis = (TextView) findViewById(R.id.main_tv_distance);
        ivGps = (ImageView) findViewById(R.id.iv_gps_bg);
        new RelativeLayout.LayoutParams(-1, -2).setMargins(0, (int) RingView.dip2px(this, 100.0f), 0, 0);
        car_speed = (TextView) findViewById(R.id.car_speed);
        this.gps_backgroup = (LinearLayout) findViewById(R.id.gps_backgroup);
        this.direction = (ImageView) findViewById(R.id.iv_direction);
        this.speed_01 = (ImageView) findViewById(R.id.iv_speed_01);
        this.speed_02 = (ImageView) findViewById(R.id.iv_speed_02);
        this.speed_03 = (ImageView) findViewById(R.id.iv_speed_03);
        dataVersionTv = (TextView) findViewById(R.id.data_version);
        directionTv = (TextView) findViewById(R.id.main_tv_direction);
        directionIv = (ImageView) findViewById(R.id.main_iv_direction);
        blockSpeedTv = (TextView) findViewById(R.id.block_tv_speed);
        blockSpeedLbTv = (TextView) findViewById(R.id.block_tv_speed_lb);
        blockSpaceTv = (TextView) findViewById(R.id.block_tv_space);
        blockSpaceLbTv = (TextView) findViewById(R.id.block_tv_space_lb);
        blockLimitSpeedIv = (ImageView) findViewById(R.id.block_iv_limitspeed);
        if (blockSpeedLbTv != null) {
            blockSpeedLbTv.setVisibility(4);
        }
        if (blockSpaceLbTv != null) {
            blockSpaceLbTv.setVisibility(4);
        }
        if (pbG != null) {
            pbG.setVisibility(4);
        }
        if (pbR != null) {
            pbR.setVisibility(4);
        }
        if (blockLimitSpeedIv != null) {
            blockLimitSpeedIv.setImageResource(0);
        }
        if (blockSpeedTv != null) {
            blockSpeedTv.setText("");
        }
        if (blockSpaceTv != null) {
            blockSpaceTv.setText("");
        }
    }
}
