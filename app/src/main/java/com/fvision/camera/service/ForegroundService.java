package com.fvision.camera.service;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.adasplus.adas.adas.AdasConstants;
import com.adasplus.adas.adas.BuildConfig;
import com.fvision.camera.R;
import com.fvision.camera.adas.IAdasService;
import com.fvision.camera.adas.IDVRClient;
import com.fvision.camera.adas.bean.AdasInterfaceImp;
import com.fvision.camera.adas.bean.AdasModel;
import com.fvision.camera.adas.bean.DVRClient;
import com.fvision.camera.adas.bean.DrawInfo;
import com.fvision.camera.bean.CameraStateBean;
import com.fvision.camera.iface.FroegroundIface;
import com.fvision.camera.iface.ICameraStateChange;
import com.fvision.camera.manager.ActivityStackManager;
import com.fvision.camera.manager.CameraStateIml;
import com.fvision.camera.manager.CmdManager;
import com.fvision.camera.manager.SoundManager;
import com.fvision.camera.receiver.FroegroundReceiver;
import com.fvision.camera.ui.MainActivity;
import com.fvision.camera.utils.CameraStateUtil;
import com.fvision.camera.utils.CmdUtil;
import com.fvision.camera.utils.Const;
import com.fvision.camera.utils.ExternalUtil;
import com.fvision.camera.utils.GetPicUtil;
import com.fvision.camera.utils.LogUtils;
import com.fvision.camera.utils.SharedPreferencesUtil;
import com.fvision.camera.view.iface.AdasKeyResult;
import com.huiying.cameramjpeg.UvcCamera;
import com.serenegiant.usb.IFrameCallback;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimerTask;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import org.opencv.features2d.FeatureDetector;
import pub.devrel.easypermissions.EasyPermissions;

public class ForegroundService extends Service implements ICameraStateChange, IAdasService {
    private static final int NOTIFICATION_ID = 1;
    public static final String[] PERMISSIONS = {"android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final String TAG = "ForegroundService";
    public static boolean isVst = false;
    private static final Class<?>[] mSetForegroundSignature = {Boolean.TYPE};
    private static final Class<?>[] mStartForegroundSignature = {Integer.TYPE, Notification.class};
    private static final Class<?>[] mStopForegroundSignature = {Boolean.TYPE};
    private final int WHAT_UPDATE_NO_SIGNAL = 1;
    IFrameCallback adasFrame = new IFrameCallback() {
        public void onFrame(final byte[] frame) {
            ForegroundService.this.mAdasHandler.post(new Runnable() {
                public void run() {
                    if (ForegroundService.this.isBindService && frame.length == 1382400) {
                        ForegroundService.this.mAdasModel.processData(frame, 1280, 720);
                    }
                }
            });
        }
    };
    /* access modifiers changed from: private */
    public String adasImei = null;
    /* access modifiers changed from: private */
    public String adasKey = null;
    /* access modifiers changed from: private */
    public AdasKeyResult adasKeyResult;
    private String channelId = "616";
    private String channelName = BuildConfig.ADAS_VERSION_BUSINESS;
    /* access modifiers changed from: private */
    public Runnable clearDoubleThread = new Runnable() {
        public void run() {
            int unused = ForegroundService.this.doubleConut = 0;
        }
    };
    private String cmd;
    private int count = 0;
    /* access modifiers changed from: private */
    public String deviceCode;
    /* access modifiers changed from: private */
    public int doubleConut = 0;
    byte[] errorData = null;
    RelativeLayout float_root;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1116 && CmdManager.getInstance().getCurrentState() != null && !CmdManager.getInstance().getCurrentState().isCam_rec_state() && System.currentTimeMillis() - ForegroundService.this.lastTime > 120000) {
                Log.e("timer+是否录像", "" + CmdManager.getInstance().recToggle());
            }
            super.handleMessage(msg);
        }
    };
    FroegroundIface iface = new FroegroundIface() {
        public void showPopuWindow() {
            ForegroundService.this.createFloatView();
        }

        public void hidePopuWindow() {
            ForegroundService.this.hideFloatWindow();
        }

        public void syncTime() {
            ForegroundService.this.serviceSyncTime();
        }

        public void pullUsb() {
            ForegroundService.this.pull_usb();
        }

        public void playEdogSound(int rid) {
            if (ForegroundService.this.isBindService) {
                SoundManager.getInstance().edogPlay(rid);
            }
        }

        public void playAdasSound(int rid) {
            SoundManager.getInstance().adasPlay(rid);
        }

        public void remotecmd(Intent intent) {
            switch (intent.getIntExtra(ExternalUtil.KEY_TYPE, -1)) {
                case 1001:
                    ExternalUtil.sendRemoteStateBroadcast(ForegroundService.this.getApplicationContext());
                    return;
                case 1003:
                    Log.e("msg+remotecmd", "remotecmd");
                    if ("".equals(".vst")) {
                        ForegroundService.this.remoteOp(intent);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void IsVst(boolean ii) {
            ForegroundService.isVst = ii;
        }

        public void isDetach(boolean isdetach) {
            ForegroundService.this.send2Vst(ForegroundService.this.getApplicationContext());
        }
    };
    /* access modifiers changed from: private */
    public boolean initViewPlace = false;
    /* access modifiers changed from: private */
    public boolean isBindService = false;
    private boolean isShowFloatView = false;
    boolean isWrite = false;
    private boolean isgetOp = false;
    private long lastSyncTime = 0;
    /* access modifiers changed from: private */
    public long lastTime = 0;
    private LinkedList<String> linkedList = new LinkedList<>();
    /* access modifiers changed from: private */
    public Handler mAdasHandler;
    /* access modifiers changed from: private */
    public boolean mAdasInit = false;
    /* access modifiers changed from: private */
    public AdasModel mAdasModel;
    private HandlerThread mAdasThread;
    private final IBinder mBinder = new ForegroundBinder();
    /* access modifiers changed from: private */
    public IDVRClient mDVRClient;
    /* access modifiers changed from: private */
    public IDVRConnectListener mDVRConnectListener;
    /* access modifiers changed from: private */
    public IDrawInfoListener mDrawInfoListener;
    LinearLayout mFloatLayout;
    GLSurfaceView mFloatView;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    ForegroundService.this.showNoSignal(((Boolean) msg.obj).booleanValue());
                    return;
                default:
                    return;
            }
        }
    };
    /* access modifiers changed from: private */
    public Handler mMainHanlder;
    private NotificationManager mNM;
    private boolean mReflectFlg = false;
    private Method mSetForeground;
    private Object[] mSetForegroundArgs = new Object[1];
    private Method mStartForeground;
    private Object[] mStartForegroundArgs = new Object[2];
    private Method mStopForeground;
    private Object[] mStopForegroundArgs = new Object[1];
    /* access modifiers changed from: private */
    public float mTouchStartX;
    /* access modifiers changed from: private */
    public float mTouchStartY;
    WindowManager mWindowManager;
    RelativeLayout no_signal_layout;
    private Notification notification;
    FroegroundReceiver receiver;
    private String source;
    int stateHight = -1;
    TimerTask timerTask = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1116;
            ForegroundService.this.handler.sendMessage(message);
        }
    };
    private String tm;
    /* access modifiers changed from: private */
    public int versionState;
    WindowManager.LayoutParams wmParams;
    /* access modifiers changed from: private */
    public float x;
    /* access modifiers changed from: private */
    public float y;

    static /* synthetic */ int access$2208(ForegroundService x0) {
        int i = x0.doubleConut;
        x0.doubleConut = i + 1;
        return i;
    }

    public void playSound(int raw) {
        SoundManager.getInstance().edogPlay(raw);
    }

    public void send2Vst(Context context) {
        if ("".equals(".vst")) {
            boolean isCamCOnnect = CmdManager.getInstance().getCamIsConnectState();
            if (CmdManager.getInstance().getCurrentState().getDevVersion().contains("W")) {
                this.versionState = 0;
                ExternalUtil.sendOpReturnBroadcast(context, isCamCOnnect, this.versionState, CmdManager.getInstance().getCurrentState().isCam_rec_state());
                return;
            }
            this.versionState = 1;
            ExternalUtil.sendOpReturnBroadcast(context, isCamCOnnect, this.versionState, CmdManager.getInstance().getCurrentState().isCam_rec_state());
        }
    }

    public void setIsBindService(boolean isn) {
        this.isBindService = isn;
    }

    /* access modifiers changed from: private */
    public void remoteOp(Intent intent) {
        int op = intent.getIntExtra(ExternalUtil.EXTRA_OP, -1);
        String time = intent.getStringExtra(ExternalUtil.EXTRA_GET_PIC_FROM_BACKPLAY);
        String mGCmdSeq = intent.getStringExtra("MGCmdSeq");
        String platformSource = intent.getStringExtra("MGPlatformSource");
        switch (op) {
            case 7:
                if (time == null) {
                    ExternalUtil.sendOpReturnBroadcast(getApplicationContext(), false, "1999");
                }
                Log.e("remoteOp2", "remoteOp");
                if (!UvcCamera.getInstance().isInit()) {
                    ExternalUtil.sendOpReturnBroadcast(getApplicationContext(), false, "1991");
                    Log.e("remoteOp3", "remoteOp");
                    return;
                }
                this.isgetOp = true;
                this.lastTime = System.currentTimeMillis();
                GetPicUtil.getInstance(getApplicationContext()).goPlayBackMode(getApplicationContext(), time, mGCmdSeq, platformSource);
                return;
            default:
                return;
        }
    }

    public void onCreate() {
        super.onCreate();
        UvcCamera.getInstance().setStateFrameCallback(CameraStateIml.getInstance().mStateFrameCallback);
        CameraStateIml.getInstance().addListener(this);
        Log.d(TAG, "onCreate");
        this.receiver = new FroegroundReceiver(this, this.iface);
        this.receiver.registerReceiver();
        this.stateHight = getStateHight();
        initNot();
        initAdas();
    }

    private void sendCMDToVst() {
        Intent intent = new Intent();
        intent.setAction("com.stcloud.drive.REQ_VST");
        intent.putExtra("mfrs", "HUIYING");
        intent.setFlags(32);
        sendBroadcast(intent);
    }

    public void setAdasKeyResult(AdasKeyResult adasKeyResult2) {
        this.adasKeyResult = adasKeyResult2;
    }

    private void initAdas() {
        this.receiver.registerReceiver();
        this.mMainHanlder = new Handler(Looper.getMainLooper());
        this.mAdasThread = new HandlerThread("AdasThread");
        this.mAdasThread.start();
        this.mAdasHandler = new Handler(this.mAdasThread.getLooper());
        Log.e("adasService", "onCreate()");
        this.mDVRClient = new DVRClient(getApplicationContext());
        this.mDVRClient.setDVRConnectListener(new IDVRClient.IDVRConnectListener() {
            public void onConnect() {
                if (ForegroundService.this.mDVRConnectListener != null) {
                    ForegroundService.this.mDVRConnectListener.onConnect();
                }
            }

            public void onDisconnect() {
                ForegroundService.this.mDVRClient.setParcelFileDescriptor((ParcelFileDescriptor) null);
                if (ForegroundService.this.mDVRConnectListener != null) {
                    ForegroundService.this.mDVRConnectListener.onDisConnect();
                }
            }
        });
        this.mDVRClient.setPrepareListener(new IDVRClient.IDVRPrepareListener() {
            public void onPrepare(boolean flag) {
                LogUtils.d("runningLog", "onPrepare()");
                try {
                    if (ForegroundService.this.deviceCode == null) {
                        String unused = ForegroundService.this.deviceCode = CmdManager.getInstance().getUUIDCode();
                        LogUtils.d("runningLog", "获取UUIDCODE " + ForegroundService.this.deviceCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (SharedPreferencesUtil.getAdasEnableToggle(ForegroundService.this.getApplicationContext()) && SharedPreferencesUtil.getAdasToggle(ForegroundService.this.getApplicationContext())) {
                    LogUtils.d("runningLog", "启动ADAS " + ForegroundService.this.deviceCode);
                    ForegroundService.this.startADAS(ForegroundService.this.deviceCode);
                }
            }
        });
        this.mAdasModel = new AdasModel(getApplicationContext());
        this.mAdasModel.setIAdasResultListener(new AdasModel.IAdasResultListener() {
            public void onResult(DrawInfo drawInfo) {
                if (ForegroundService.this.mDrawInfoListener != null) {
                    ForegroundService.this.mDrawInfoListener.onDraw(drawInfo);
                }
            }
        });
        this.mAdasModel.setIAdasFilebackupListener(new AdasModel.IAdasFileBackListener() {
            public void backUp(String content) {
                if (ForegroundService.this.mDVRClient != null) {
                    Log.e("成功启动ADAS", "向记录仪里保存ADAS KEY " + content);
                    if (content != null) {
                        SharedPreferencesUtil.setAdasKey(ForegroundService.this.getApplicationContext(), content);
                    }
                    SharedPreferencesUtil.setAdasImei(ForegroundService.this.getApplicationContext(), ForegroundService.this.adasImei);
                    Log.e(AdasConstants.FILE_BACKUP, "" + content);
                    ForegroundService.this.mDVRClient.saveSecretKey(content);
                    if (content != null) {
                        if (ForegroundService.this.adasKeyResult != null) {
                            ForegroundService.this.adasKeyResult.onSuccess();
                        }
                    } else if (ForegroundService.this.adasKeyResult != null) {
                        ForegroundService.this.adasKeyResult.onFail(0, "");
                    }
                }
            }
        });
        this.mAdasModel.setIAdasSoundListener(new AdasModel.IAdasSoundListener() {
            public void sound(final int sound) {
                if (ForegroundService.this.mDVRClient != null) {
                    ForegroundService.this.mMainHanlder.post(new Runnable() {
                        public void run() {
                            switch (sound) {
                                case 0:
                                    if (CmdUtil.isZh(ForegroundService.this.getApplicationContext())) {
                                        SoundManager.getInstance().adasPlay(R.raw.warning_lane);
                                        return;
                                    } else if (CmdUtil.isVN(ForegroundService.this.getApplicationContext())) {
                                        SoundManager.getInstance().adasPlay(R.raw.adas_ldw_vi);
                                        return;
                                    } else if (CmdUtil.isRu(ForegroundService.this.getApplicationContext())) {
                                        SoundManager.getInstance().adasPlay(R.raw.adas_ldw_ru);
                                        return;
                                    } else {
                                        SoundManager.getInstance().adasPlay(R.raw.adas_ldw_en);
                                        return;
                                    }
                                case 1:
                                    if (CmdUtil.isZh(ForegroundService.this.getApplicationContext())) {
                                        SoundManager.getInstance().adasPlay(R.raw.warning_car);
                                        return;
                                    } else if (CmdUtil.isVN(ForegroundService.this.getApplicationContext())) {
                                        SoundManager.getInstance().adasPlay(R.raw.adas_fcw_vi);
                                        return;
                                    } else if (CmdUtil.isRu(ForegroundService.this.getApplicationContext())) {
                                        SoundManager.getInstance().adasPlay(R.raw.adas_fcw_ru);
                                        return;
                                    } else {
                                        SoundManager.getInstance().adasPlay(R.raw.adas_fcw_en);
                                        return;
                                    }
                                case 2:
                                    if (CmdUtil.isZh(ForegroundService.this.getApplicationContext())) {
                                        SoundManager.getInstance().adasPlay(R.raw.warning_stopgo);
                                        return;
                                    } else if (CmdUtil.isRu(ForegroundService.this.getApplicationContext())) {
                                        SoundManager.getInstance().adasPlay(R.raw.adas_fvd_ru);
                                        return;
                                    } else if (CmdUtil.isVN(ForegroundService.this.getApplicationContext())) {
                                        SoundManager.getInstance().adasPlay(R.raw.adas_fvd_vi);
                                        return;
                                    } else {
                                        SoundManager.getInstance().adasPlay(R.raw.adas_fvd_en);
                                        return;
                                    }
                                default:
                                    return;
                            }
                        }
                    });
                }
            }
        });
        this.mAdasInit = false;
    }

    private void initNot() {
        if (this.mNM == null || this.notification == null) {
            this.mNM = (NotificationManager) getSystemService("notification");
            try {
                this.mStartForeground = ForegroundService.class.getMethod("startForeground", mStartForegroundSignature);
                this.mStopForeground = ForegroundService.class.getMethod("stopForeground", mStopForegroundSignature);
            } catch (NoSuchMethodException e) {
                this.mStopForeground = null;
                this.mStartForeground = null;
            }
            try {
                this.mSetForeground = getClass().getMethod("setForeground", mSetForegroundSignature);
                Notification.Builder builder = new Notification.Builder(this);
                if (Build.VERSION.SDK_INT >= 26) {
                    MainActivity.hasRun = 2;
                    NotificationChannel channel = new NotificationChannel(this.channelId, this.channelName, 2);
                    builder.setChannelId(this.channelId);
                    this.mNM.createNotificationChannel(channel);
                }
                Intent intent = new Intent(this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean(Const.isPendingIntent, true);
                intent.putExtras(bundle);
                builder.setContentIntent(PendingIntent.getActivity(this, 0, intent, 134217728));
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setTicker(getString(R.string.app_name));
                builder.setContentTitle(getString(R.string.app_name));
                this.notification = builder.build();
                startForegroundCompat(1, this.notification);
                this.mMainHanlder = new Handler(Looper.getMainLooper());
            } catch (NoSuchMethodException e2) {
                throw new IllegalStateException("OS doesn't have Service.startForeground OR Service.setForeground!");
            }
        }
    }

    public void sendVst(Context context) {
        if ("".equals(".vst")) {
            boolean isCamCOnnect = CmdManager.getInstance().getCamIsConnectState();
            if (CmdManager.getInstance().getCurrentState().getDevVersion().contains("W")) {
                this.versionState = 0;
                ExternalUtil.sendOpReturnBroadcast(context, isCamCOnnect, this.versionState, CmdManager.getInstance().getCurrentState().isCam_rec_state());
                return;
            }
            this.versionState = 1;
            ExternalUtil.sendOpReturnBroadcast(context, isCamCOnnect, this.versionState, CmdManager.getInstance().getCurrentState().isCam_rec_state());
        }
    }

    public void sendVst() {
        if ("".equals(".vst")) {
            final boolean isCamCOnnect = CmdManager.getInstance().getCamIsConnectState();
            CameraStateIml.getInstance().setOnCameraStateOnlyOnceListner(new ICameraStateChange() {
                public void stateChange() {
                    String devVersion = CmdManager.getInstance().getCurrentState().getDevVersion();
                    CameraStateBean stateBean = CmdManager.getInstance().getCurrentState();
                    if (devVersion != null && devVersion.length() > 0) {
                        if (devVersion.contains("W")) {
                            int unused = ForegroundService.this.versionState = 0;
                            ExternalUtil.sendOpReturnBroadcast(ForegroundService.this.getApplicationContext(), isCamCOnnect, ForegroundService.this.versionState, stateBean.isCam_rec_state());
                            return;
                        }
                        int unused2 = ForegroundService.this.versionState = 1;
                        ExternalUtil.sendOpReturnBroadcast(ForegroundService.this.getApplicationContext(), isCamCOnnect, ForegroundService.this.versionState, stateBean.isCam_rec_state());
                    }
                }
            });
        }
    }

    public void pull_usb() {
        LogUtils.d("pull_usb()");
        UvcCamera.getInstance().stopPreview();
        ActivityStackManager.getManager().exitApp(this);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        serviceSyncTime();
        if ("".equals(".vst")) {
            sendCMDToVst();
        }
        Log.e("onStartCommand", "" + this.deviceCode);
        setAdasFrame();
        init();
        this.mAdasModel.initAdasParam();
        if (Build.VERSION.SDK_INT < 26) {
            return 2;
        }
        startForegroundCompat(1, this.notification);
        return 2;
    }

    private void init() {
        LogUtils.e("init()");
        if (!EasyPermissions.hasPermissions(getApplicationContext(), PERMISSIONS)) {
            LogUtils.e("runningLog", "没有权限，无法初始化");
        } else {
            this.mMainHanlder.post(new Runnable() {
                public void run() {
                    ForegroundService.this.mDVRClient.init();
                }
            });
        }
    }

    public void setAdasFrame() {
        if (!CmdManager.getInstance().getCurrentState().isCam_mode_state()) {
            Log.e("setAdasFrame", "setAdasFrame");
            UvcCamera.getInstance().setAdasFrameCallback(this.adasFrame);
        }
    }

    public boolean getAdasInit() {
        return this.mAdasInit;
    }

    public void test() {
        this.isWrite = true;
    }

    /* access modifiers changed from: private */
    public void serviceSyncTime() {
        if (UvcCamera.getInstance().isInit()) {
            CmdManager.getInstance().syncTime();
        }
    }

    private boolean currentTimeIsRight() {
        boolean is = true;
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 0, 1);
        if (now <= calendar.getTimeInMillis()) {
            is = false;
        }
        if (!is) {
            LogUtils.e("时间错误,不同步 " + CameraStateUtil.longToString(now, (String) null));
        }
        return is;
    }

    public IBinder onBind(Intent intent) {
        Log.d("zoulequan", "service onBind");
        this.isBindService = true;
        return this.mBinder;
    }

    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        LogUtils.d("service unbindService");
        this.isBindService = false;
    }

    public boolean onUnbind(Intent intent) {
        Log.d("zoulequan", "service onUnbind");
        this.isBindService = false;
        return super.onUnbind(intent);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        hideFloatWindow();
        stopForegroundCompat(1);
        this.receiver.unregisterReceiver();
    }

    public IDVRClient getDVRClient() {
        return this.mDVRClient;
    }

    public void setDrawInfoListener(IDrawInfoListener listener) {
        this.mDrawInfoListener = listener;
    }

    public void setDVRConnectListener(IDVRConnectListener listener) {
        this.mDVRConnectListener = listener;
    }

    public AdasInterfaceImp getAdasInterfaceImp() {
        return this.mAdasModel.getAdasInterface();
    }

    public boolean getAdasState() {
        return this.mAdasModel.adasRunning();
    }

    public void setAdasEnable(boolean enable) {
        if (this.mAdasModel.getAdasInterface() != null) {
            this.mAdasModel.getAdasInterface().setAdasEnable(enable);
        }
    }

    public void startADAS(String deviceCode2) {
        if (this.mAdasInit) {
            Log.e(AdasConstants.FILE_ADAS, "startADAS " + this.mAdasInit);
        } else if (TextUtils.isEmpty(deviceCode2) || deviceCode2 == null) {
            Log.e(AdasConstants.FILE_ADAS, "startADAS deviceCode == null");
        } else {
            activiteAdas(deviceCode2);
        }
    }

    public void activiteAdas(final String deviceCode2) {
        this.mMainHanlder.post(new Runnable() {
            public void run() {
                if (ForegroundService.this.mAdasModel != null) {
                    String unused = ForegroundService.this.adasImei = deviceCode2;
                    String unused2 = ForegroundService.this.adasKey = CmdManager.getInstance().readKeyStore();
                    String lastSuccessStartAdasImei = SharedPreferencesUtil.getAdasImei(ForegroundService.this.getApplicationContext());
                    String lastSuccessStartAdasKey = SharedPreferencesUtil.getAdasKey(ForegroundService.this.getApplicationContext());
                    if (ForegroundService.this.adasImei.equals(lastSuccessStartAdasImei)) {
                        String unused3 = ForegroundService.this.adasKey = lastSuccessStartAdasKey;
                    }
                    Log.e(AdasConstants.FILE_ADAS, "onFail deviceCode " + deviceCode2 + " mAdasInit " + ForegroundService.this.mAdasInit + " mDVRClient.getSecretKey()" + ForegroundService.this.adasKey);
                    boolean unused4 = ForegroundService.this.mAdasInit = ForegroundService.this.mAdasModel.init(deviceCode2, ForegroundService.this.adasKey);
                }
            }
        });
    }

    private void initFloatView() {
        this.wmParams = new WindowManager.LayoutParams();
        Application application = getApplication();
        getApplication();
        this.mWindowManager = (WindowManager) application.getSystemService("window");
        Log.i(TAG, "mWindowManager--->" + this.mWindowManager);
        LogUtils.d(" Build.VERSION.SDK_INT " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= 26) {
            this.wmParams.type = 2038;
        } else if (Build.VERSION.SDK_INT <= 19 || "".equals(".px3")) {
            this.wmParams.type = FeatureDetector.PYRAMID_GFTT;
        } else {
            this.wmParams.type = FeatureDetector.PYRAMID_ORB;
        }
        this.wmParams.format = 1;
        this.wmParams.flags = 8;
        this.wmParams.gravity = 51;
        this.wmParams.x = 0;
        this.wmParams.y = 0;
        this.wmParams.width = -2;
        this.wmParams.height = -2;
        this.mFloatLayout = (LinearLayout) LayoutInflater.from(getApplication()).inflate(R.layout.float_layout, (ViewGroup) null);
        this.mFloatLayout.setVisibility(8);
        this.mFloatLayout.findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ForegroundService.this.takePicture();
            }
        });
        this.no_signal_layout = (RelativeLayout) this.mFloatLayout.findViewById(R.id.layout_no_signal);
        this.mFloatView = (GLSurfaceView) this.mFloatLayout.findViewById(R.id.float_id);
        this.mFloatView.setEGLContextClientVersion(2);
        this.mFloatView.setRenderer(new GLSurfaceView.Renderer() {
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                UvcCamera.getInstance().initGles(1280, 720);
            }

            public void onSurfaceChanged(GL10 gl, int width, int height) {
                UvcCamera.getInstance().changeESLayout(width, height);
            }

            public void onDrawFrame(GL10 gl) {
                if (UvcCamera.getInstance().drawESFrame() == 0) {
                    ForegroundService.this.sendNoSignalMessage(false);
                }
            }
        });
        this.mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        Log.i(TAG, "Width/2--->" + (this.mFloatView.getMeasuredWidth() / 2));
        Log.i(TAG, "Height/2--->" + (this.mFloatView.getMeasuredHeight() / 2));
        this.mFloatLayout.setOnTouchListener(new View.OnTouchListener() {
            float downX = 0.0f;
            float downY = 0.0f;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                        this.downX = event.getX();
                        this.downY = event.getY();
                        if (ForegroundService.this.initViewPlace) {
                            float unused = ForegroundService.this.mTouchStartX = ForegroundService.this.mTouchStartX + (event.getRawX() - ForegroundService.this.x);
                            float unused2 = ForegroundService.this.mTouchStartY = ForegroundService.this.mTouchStartY + (event.getRawY() - ForegroundService.this.y);
                            break;
                        } else {
                            boolean unused3 = ForegroundService.this.initViewPlace = true;
                            float unused4 = ForegroundService.this.mTouchStartX = ForegroundService.this.mTouchStartX + (event.getRawX() - ((float) ForegroundService.this.wmParams.x));
                            float unused5 = ForegroundService.this.mTouchStartY = ForegroundService.this.mTouchStartY + (event.getRawY() - ((float) ForegroundService.this.wmParams.y));
                            break;
                        }
                    case 1:
                        ForegroundService.access$2208(ForegroundService.this);
                        ForegroundService.this.startDoubleTimeoutThread();
                        if (ForegroundService.this.doubleConut == 2) {
                            ForegroundService.this.mHandler.removeCallbacks(ForegroundService.this.clearDoubleThread);
                            Intent main = new Intent(ForegroundService.this, MainActivity.class);
                            main.setFlags(268435456);
                            ForegroundService.this.startActivity(main);
                            ForegroundService.this.hideFloatWindow();
                            break;
                        }
                        break;
                    case 2:
                        float unused6 = ForegroundService.this.x = event.getRawX();
                        float unused7 = ForegroundService.this.y = event.getRawY();
                        ForegroundService.this.wmParams.x = (int) (ForegroundService.this.x - ForegroundService.this.mTouchStartX);
                        ForegroundService.this.wmParams.y = (int) (ForegroundService.this.y - ForegroundService.this.mTouchStartY);
                        ForegroundService.this.mWindowManager.updateViewLayout(ForegroundService.this.mFloatLayout, ForegroundService.this.wmParams);
                        break;
                }
                return true;
            }
        });
        this.mFloatLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e("msg", "回到主界面");
                Intent main = new Intent(ForegroundService.this, MainActivity.class);
                main.setFlags(268435456);
                ForegroundService.this.startActivity(main);
                ForegroundService.this.stopSelf();
            }
        });
    }

    /* access modifiers changed from: private */
    public void sendNoSignalMessage(boolean isShow) {
        Message msg = new Message();
        msg.what = 1;
        msg.obj = Boolean.valueOf(isShow);
        this.mHandler.sendMessage(msg);
    }

    public void createFloatView() {
        if (this.mWindowManager == null || this.mFloatLayout == null) {
            initFloatView();
        }
        hideFloatWindow();
        this.mFloatLayout.setVisibility(0);
        this.mWindowManager.addView(this.mFloatLayout, this.wmParams);
        this.isShowFloatView = true;
    }

    public void hideFloatWindow() {
        if (this.mWindowManager != null && this.mFloatLayout.getVisibility() == 0) {
            this.mWindowManager.removeView(this.mFloatLayout);
            this.mFloatLayout.setVisibility(8);
            this.isShowFloatView = false;
        }
    }

    /* access modifiers changed from: private */
    public void startDoubleTimeoutThread() {
        this.mHandler.postDelayed(this.clearDoubleThread, 1000);
    }

    private int getStateHight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return -1;
    }

    /* access modifiers changed from: private */
    public void showNoSignal(boolean isShow) {
        this.no_signal_layout.setVisibility(isShow ? 0 : 8);
    }

    /* access modifiers changed from: package-private */
    public void invokeMethod(Method method, Object[] args) {
        try {
            method.invoke(this, args);
        } catch (InvocationTargetException e) {
            Log.w("ApiDemos", "Unable to invoke method", e);
        } catch (IllegalAccessException e2) {
            Log.w("ApiDemos", "Unable to invoke method", e2);
        }
    }

    /* access modifiers changed from: package-private */
    public void startForegroundCompat(int id, Notification notification2) {
        if (this.mReflectFlg) {
            if (this.mStartForeground != null) {
                this.mStartForegroundArgs[0] = Integer.valueOf(id);
                this.mStartForegroundArgs[1] = notification2;
                invokeMethod(this.mStartForeground, this.mStartForegroundArgs);
                return;
            }
            this.mSetForegroundArgs[0] = Boolean.TRUE;
            invokeMethod(this.mSetForeground, this.mSetForegroundArgs);
            this.mNM.notify(id, notification2);
        } else if (Build.VERSION.SDK_INT >= 5) {
            startForeground(id, notification2);
        } else {
            this.mSetForegroundArgs[0] = Boolean.TRUE;
            invokeMethod(this.mSetForeground, this.mSetForegroundArgs);
            this.mNM.notify(id, notification2);
        }
    }

    /* access modifiers changed from: private */
    public void takePicture() {
        new Thread(new Runnable() {
            public void run() {
                final boolean sucess = UvcCamera.getInstance().takeSnapshot(Const.JPG_PATH + (new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss-SSS").format(new Date()) + ".jpg"));
                ForegroundService.this.mHandler.post(new Runnable() {
                    public void run() {
                        if (sucess) {
                            Toast.makeText(ForegroundService.this, R.string.take_snapshot_sucess, 0).show();
                        } else {
                            Toast.makeText(ForegroundService.this, R.string.take_snapshot_fail, 0).show();
                        }
                    }
                });
            }
        }).start();
    }

    /* access modifiers changed from: package-private */
    public void stopForegroundCompat(int id) {
        if (this.mReflectFlg) {
            if (this.mStopForeground != null) {
                this.mStopForegroundArgs[0] = Boolean.TRUE;
                invokeMethod(this.mStopForeground, this.mStopForegroundArgs);
                return;
            }
            this.mNM.cancel(id);
            this.mSetForegroundArgs[0] = Boolean.FALSE;
            invokeMethod(this.mSetForeground, this.mSetForegroundArgs);
        } else if (Build.VERSION.SDK_INT >= 5) {
            stopForeground(true);
        } else {
            this.mNM.cancel(id);
            this.mSetForegroundArgs[0] = Boolean.FALSE;
            invokeMethod(this.mSetForeground, this.mSetForegroundArgs);
        }
    }

    public void stateChange() {
        LogUtils.d("zoulequan " + CmdManager.getInstance().getCurrentState().toString());
    }

    public class ForegroundBinder extends Binder {
        public ForegroundBinder() {
        }

        public ForegroundService getService() {
            return ForegroundService.this;
        }
    }

    public String getAdasImei() {
        return this.adasImei;
    }

    public String getAdasKey() {
        return this.adasKey;
    }
}
