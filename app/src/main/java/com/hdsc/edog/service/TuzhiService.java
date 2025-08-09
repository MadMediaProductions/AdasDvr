package com.hdsc.edog.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.sdk.android.oss.common.RequestParameters;
import com.fvision.camera.App;
import com.fvision.camera.R;
import com.fvision.camera.ui.MainActivity;
import com.fvision.camera.utils.LogUtils;
import com.hdsc.edog.entity.GpsInfo;
import com.hdsc.edog.jni.DataPlay;
import com.hdsc.edog.jni.DataShow;
import com.hdsc.edog.jni.EdogDataInfo;
import com.hdsc.edog.jni.EdogDataManager;
import com.hdsc.edog.net.HttpRequestManager;
import com.hdsc.edog.utils.SharedPreUtils;
import com.hdsc.edog.utils.ToolUtils;
import com.hdsc.edog.utils.UpDownManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TuzhiService extends Service {
    public static final String ACTION_CRREATE_ALL_FLOATVIEW = "tuzhi.edog.androidapp.createallfloatview";
    public static final String ACTION_CRREATE_RADAR_FLOATVIEW = "tuzhi.edog.androidapp.createradarfloatview";
    public static final String ACTION_CRREATE_SPEED_FLOATVIEW = "tuzhi.edog.androidapp.createspeedfloatview";
    public static final String ACTION_REMOVE_ALL_FLOATVIEW = "tuzhi.edog.androidapp.removeallfloatview";
    public static final String ACTION_REMOVE_RADAR_FLOATVIEW = "tuzhi.edog.androidapp.removeradarfloatview";
    public static final String ACTION_REMOVE_SPEED_FLOATVIEW = "tuzhi.edog.androidapp.removespeedfloatview";
    public static String BROADCAST_TEST_EDOG = "broadcast_test_edog";
    public static int Dir_save0 = 0;
    public static int Dir_save1 = 0;
    public static long GPRSTOTAL = 0;
    public static int KEY_ISSHOW_DIALOG = 0;
    public static int Lat_save0 = 0;
    public static int Lat_save1 = 0;
    public static int Lng_save0 = 0;
    public static int Lng_save1 = 0;
    public static int Use_Addver;
    public static int Use_Mapver;
    public static TextView fDistance;
    public static ImageView fImage;
    public static TextView fSpeed;
    public static GpsInfo gpsInfo;
    public static LinearLayout llSpeed;
    public static LinearLayout llWarn;
    public static RelativeLayout mRadarFloatLayout;
    public static ImageView mRadarFloatView;
    public static RelativeLayout mSpeedFloatLayout;
    public static boolean radarfvIsVisible = false;
    public static boolean sBackgroundRunning = false;
    public static boolean speedfvIsVisible = false;
    /* access modifiers changed from: private */
    public int BlockSpeedLimit = 0;
    /* access modifiers changed from: private */
    public int BlockSpeedTime = 0;
    private int LAST_gpsFixTime = -1;
    /* access modifiers changed from: private */
    public EdogDataInfo L_edogDataInfo;
    /* access modifiers changed from: private */
    public int Last_radarType = -1;
    /* access modifiers changed from: private */
    public int MAINLoopCont = 0;
    /* access modifiers changed from: private */
    public long MainLoop = 0;
    /* access modifiers changed from: private */
    public int Rd_city_cont = 0;
    /* access modifiers changed from: private */
    public int Rx_Rdsingle = 0;
    private int SAD_RD_ERR = 0;
    /* access modifiers changed from: private */
    public int SAD_RD_Time = 5000;
    private final String TAG = "TuzhiService";
    /* access modifiers changed from: private */
    public int TIME_ERR = -1;
    /* access modifiers changed from: private */
    public int TIME_RD = -1;
    private int TestChkN0 = 0;
    private Calendar calendar;
    /* access modifiers changed from: private */
    public DataPlay dataPlay;
    /* access modifiers changed from: private */
    public DataShow dataShow;
    /* access modifiers changed from: private */
    public EdogDataInfo edogDataInfo;
    /* access modifiers changed from: private */
    public EdogDataManager edogDataManager;
    Runnable edogRunnable = new Runnable() {
        public void run() {
            while (TuzhiService.this.edog_run) {
                long unused = TuzhiService.this.MainLoop = System.currentTimeMillis();
                TuzhiService.access$408(TuzhiService.this);
                if (MainActivity.startMuteTime > 0) {
                    MainActivity.startMuteTime--;
                    if (MainActivity.startMuteTime == 0) {
                        TuzhiService.this.mHandler.sendEmptyMessage(2);
                    }
                }
                int TgpsFixTime = TuzhiService.gpsInfo.getgpsFixTime();
                if (TgpsFixTime > 0 && TgpsFixTime <= 8) {
                    TuzhiService.gpsInfo.setgpsFixTime(TgpsFixTime - 1);
                }
                if (TuzhiService.gpsInfo != null) {
                    synchronized (App.mlock) {
                        EdogDataInfo unused2 = TuzhiService.this.edogDataInfo = TuzhiService.this.edogDataManager.RgetEdogData(TuzhiService.gpsInfo);
                        if (TuzhiService.this.edogDataInfo.ismIsAlarm()) {
                        }
                    }
                }
                if (TuzhiService.this.edogDataInfo == null) {
                    EdogDataInfo unused3 = TuzhiService.this.edogDataInfo = TuzhiService.this.L_edogDataInfo;
                } else {
                    TuzhiService.Use_Mapver = TuzhiService.this.edogDataInfo.getmVersion();
                    TuzhiService.Use_Addver = TuzhiService.this.edogDataInfo.getmADDVersion();
                    EdogDataInfo unused4 = TuzhiService.this.L_edogDataInfo = TuzhiService.this.edogDataInfo;
                }
                if (TuzhiService.gpsInfo.getmapupdata() == 2) {
                    TuzhiService.gpsInfo.setmapupdata(0);
                    TuzhiService.this.mHandler.sendEmptyMessage(98);
                } else if (TuzhiService.gpsInfo.getmapupdata() == 1) {
                    TuzhiService.gpsInfo.setmapupdata(0);
                }
                if (TuzhiService.this.edogDataInfo.getmFirstFindCamera() == 1) {
                    if (TuzhiService.this.edogDataInfo.getmAlarmType() == 12) {
                        int unused5 = TuzhiService.this.BlockSpeedLimit = TuzhiService.this.edogDataInfo.getmSpeedLimit();
                        int unused6 = TuzhiService.this.BlockSpeedTime = 1;
                    } else if (TuzhiService.this.edogDataInfo.getmAlarmType() == 6) {
                        int unused7 = TuzhiService.this.BlockSpeedTime = 0;
                        int unused8 = TuzhiService.this.BlockSpeedLimit = 0;
                    }
                }
                if (TuzhiService.this.BlockSpeedTime > 0) {
                    TuzhiService.access$908(TuzhiService.this);
                    if (TuzhiService.this.BlockSpeedTime >= 2400) {
                        int unused9 = TuzhiService.this.BlockSpeedLimit = 0;
                        int unused10 = TuzhiService.this.BlockSpeedTime = 0;
                    }
                }
                TuzhiService.this.mHandler.sendEmptyMessage(0);
                if (TuzhiService.this.edogDataInfo.getmFirstFindCamera() != 0 || TuzhiService.this.edogDataInfo.ismIsAlarm()) {
                    TuzhiService.this.dataPlay.edogDataPlayer(TuzhiService.this.edogDataInfo);
                }
                if (TuzhiService.this.SAD_RD_Time > 0 && TuzhiService.this.SAD_RD_Time < 5000) {
                    TuzhiService.access$1010(TuzhiService.this);
                }
                if (TuzhiService.this.Rd_city_cont > 0 && TuzhiService.this.Rd_city_cont < 7) {
                    TuzhiService.access$1108(TuzhiService.this);
                }
                if (TuzhiService.this.Rx_Rdsingle > 0) {
                    TuzhiService.access$1210(TuzhiService.this);
                    if (TuzhiService.this.Rx_Rdsingle == 0) {
                        int unused11 = TuzhiService.this.Rd_city_cont = 0;
                    }
                }
                if (TuzhiService.this.TIME_RD > 0) {
                    TuzhiService.access$1310(TuzhiService.this);
                }
                if (TuzhiService.this.TIME_RD == 0 && TuzhiService.this.radarDisp < 0) {
                    int unused12 = TuzhiService.this.TIME_RD = -1;
                    int unused13 = TuzhiService.this.Last_radarType = -1;
                    int unused14 = TuzhiService.this.radarDisp = 6;
                    TuzhiService.this.mHandler.sendEmptyMessage(1);
                }
                if (TuzhiService.this.TIME_ERR > 0) {
                    TuzhiService.access$1610(TuzhiService.this);
                }
                if (TuzhiService.this.TIME_ERR == 0 && TuzhiService.this.radarCFOK && TuzhiService.this.radarDisp < 0) {
                    boolean unused15 = TuzhiService.this.radarCFOK = false;
                    int unused16 = TuzhiService.this.radarDisp = 8;
                    TuzhiService.this.mHandler.sendEmptyMessage(1);
                }
                if (TuzhiService.this.Last_radarType >= 16 && TuzhiService.this.radarCFOK) {
                    if (TuzhiService.this.SAD_RD_Time == 0) {
                        boolean unused17 = TuzhiService.this.radarCFOK = false;
                        int unused18 = TuzhiService.this.radarDisp = 11;
                        TuzhiService.this.mHandler.sendEmptyMessage(1);
                    }
                    if ((TuzhiService.this.Last_radarType & 15) < 2) {
                        if (TuzhiService.this.MAINLoopCont % 3 == 0 && TuzhiService.this.SAD_RD_Time != 0) {
                            TuzhiService.this.dataPlay.radarDataPlayer(8);
                        }
                    } else if (TuzhiService.this.MAINLoopCont % 6 == 0 && TuzhiService.this.SAD_RD_Time != 0) {
                        TuzhiService.this.dataPlay.radarDataPlayer(8);
                    }
                }
                int SpeedOverFlg = 0;
                if (TuzhiService.this.edogDataManager.isSpeedLimitOn()) {
                    if (TuzhiService.gpsInfo.getSpeed() > TuzhiService.this.edogDataInfo.getmSpeedLimit() && TuzhiService.this.edogDataInfo.getmSpeedLimit() >= 20) {
                        SpeedOverFlg = 1;
                    } else if (TuzhiService.this.edogDataInfo.getmBlockSpace() > 0 && TuzhiService.this.edogDataInfo.getmBlockSpeed() > TuzhiService.this.BlockSpeedLimit && TuzhiService.this.BlockSpeedLimit > 20) {
                        SpeedOverFlg = 2;
                    } else if (TuzhiService.gpsInfo.getSpeed() > TuzhiService.this.BlockSpeedLimit && TuzhiService.this.BlockSpeedLimit > 20) {
                        SpeedOverFlg = 2;
                    }
                }
                if (TuzhiService.gpsInfo.getSpeed() > TuzhiService.this.edogDataManager.getMaxLimitSpeed()) {
                    SpeedOverFlg = 3;
                }
                TuzhiService.this.dataPlay.SpeedOverPlayer(SpeedOverFlg, TuzhiService.this.MAINLoopCont);
                while (System.currentTimeMillis() - TuzhiService.this.MainLoop < 500 && System.currentTimeMillis() - TuzhiService.this.MainLoop >= 0) {
                    ToolUtils.sleep(20);
                }
            }
        }
    };
    private Thread edogThread;
    /* access modifiers changed from: private */
    public boolean edog_run = true;
    private SimpleDateFormat format;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            EdogDataInfo mEdogDataInfo = TuzhiService.this.L_edogDataInfo;
            mEdogDataInfo.setmFirstFindCamera(3);
            TuzhiService.this.dataPlay.edogDataPlayer(mEdogDataInfo);
        }
    };
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (!TuzhiService.sBackgroundRunning) {
                        TuzhiService.this.dataShow.edogDataShow(0, TuzhiService.this.edogDataInfo, TuzhiService.this.BlockSpeedLimit);
                        return;
                    }
                    return;
                case 1:
                    if (!TuzhiService.sBackgroundRunning) {
                        TuzhiService.this.dataShow.radarDataShow(TuzhiService.this.radarDisp);
                        int unused = TuzhiService.this.radarDisp = -1;
                        return;
                    }
                    return;
                case 98:
                    synchronized (App.mlock) {
                        UpDownManager.getInstance().BaseDataCheck(false);
                        Log.e("copyFile", "copyBaseFile   yyyy  ");
                    }
                    return;
                default:
                    return;
            }
        }
    };
    private GpsLocationListener mLocationListener;
    private LocationManager mLoctionManager;
    float mTouchStartX;
    float mTouchStartY;
    WindowManager mWindowManager;
    private final float minDistance = 0.0f;
    private final long minTime = 500;
    /* access modifiers changed from: private */
    public boolean radarCFOK = false;
    /* access modifiers changed from: private */
    public int radarDisp = -1;
    private boolean radar_run = true;
    private int speed_chk = 0;
    private int testspeed = 0;
    WindowManager.LayoutParams wmRadarParams;
    WindowManager.LayoutParams wmSpeedParams;
    float x1;
    float x2;
    float y1;
    float y2;
    private int zxcv = 0;

    static /* synthetic */ int access$1010(TuzhiService x0) {
        int i = x0.SAD_RD_Time;
        x0.SAD_RD_Time = i - 1;
        return i;
    }

    static /* synthetic */ int access$1108(TuzhiService x0) {
        int i = x0.Rd_city_cont;
        x0.Rd_city_cont = i + 1;
        return i;
    }

    static /* synthetic */ int access$1210(TuzhiService x0) {
        int i = x0.Rx_Rdsingle;
        x0.Rx_Rdsingle = i - 1;
        return i;
    }

    static /* synthetic */ int access$1310(TuzhiService x0) {
        int i = x0.TIME_RD;
        x0.TIME_RD = i - 1;
        return i;
    }

    static /* synthetic */ int access$1610(TuzhiService x0) {
        int i = x0.TIME_ERR;
        x0.TIME_ERR = i - 1;
        return i;
    }

    static /* synthetic */ int access$408(TuzhiService x0) {
        int i = x0.MAINLoopCont;
        x0.MAINLoopCont = i + 1;
        return i;
    }

    static /* synthetic */ int access$908(TuzhiService x0) {
        int i = x0.BlockSpeedTime;
        x0.BlockSpeedTime = i + 1;
        return i;
    }

    @SuppressLint({"NewApi"})
    public void onCreate() {
        super.onCreate();
        Log.e("msg", "TuzhiService_onCreate~ ");
        SharedPreUtils instance = SharedPreUtils.getInstance(this);
        KEY_ISSHOW_DIALOG = 1;
        startForeground(0, new Notification(R.mipmap.ic_launcher, "wf update service is running", System.currentTimeMillis()));
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_TEST_EDOG);
        registerReceiver(this.mBroadcastReceiver, filter);
        this.dataShow = new DataShow(this);
        this.dataPlay = new DataPlay(this);
        this.edogDataManager = new EdogDataManager(this);
        this.calendar = Calendar.getInstance();
        this.format = new SimpleDateFormat("yyyyMMdd");
        this.mLoctionManager = (LocationManager) getSystemService(RequestParameters.SUBRESOURCE_LOCATION);
        gpsInfo = new GpsInfo();
        this.L_edogDataInfo = new EdogDataInfo(0, 0, 0, false, 0, 0, 0, 0, 0, -1, 0);
        location(this.mLoctionManager);
        try {
            this.edogThread = new Thread(this.edogRunnable);
            this.edogThread.start();
        } catch (Exception e) {
            e.printStackTrace();
            this.edog_run = false;
            this.edogThread.interrupt();
            this.edogThread.stop();
            this.edogRunnable = null;
        }
        MainActivity.GetUSER_ID = this.edogDataManager.getGetUSER_ID();
        gpsInfo.setmapupdata(2);
        UpDownManager.mLastUpdateTime = 0;
        UpDownManager.mLastUploadTime = 0;
        UpDownManager.Enupdata = false;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e("TuzhiService", "TuzhiService onStartCommand 0 ");
        if (App.isCurrent(this)) {
            ToolUtils.getInstance().exitNotify(this);
        }
        if (MainActivity.isSystemBoot == 1) {
            MainActivity.isSystemBoot = 0;
            MainActivity.isMinMode = true;
            if (this.edogDataManager.GetgetLogDisp()) {
                App.operateFloatView(this, ACTION_CRREATE_ALL_FLOATVIEW);
            }
            ToolUtils.getInstance().showRunBgNotify(this);
            Log.e("TuzhiService", "onStartCommand::exit :  ");
            App.exit();
        } else if (MainActivity.isMinMode) {
            MainActivity.isSystemBoot = 0;
            if (this.edogDataManager.GetgetLogDisp()) {
                App.operateFloatView(this, ACTION_CRREATE_ALL_FLOATVIEW);
            }
            ToolUtils.getInstance().showRunBgNotify(this);
            App.exit();
        }
        Log.e("TuzhiService", "isMinMode 0 ");
        return 2;
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void location(LocationManager gpsManager) {
        LogUtils.d("开始定位");
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            LogUtils.d("有定位权限1");
            Location lastKnownLocation = gpsManager.getLastKnownLocation("gps");
            this.mLocationListener = new GpsLocationListener();
            gpsManager.requestLocationUpdates("gps", 500, 0.0f, this.mLocationListener);
            return;
        }
        LogUtils.d("没有定位权限1");
    }

    public void printGpsLocation(Location location) {
        LogUtils.d(" printGpsLocation ");
        if (location != null) {
            gpsInfo.setAltitude((int) location.getAltitude());
            gpsInfo.setLat((int) (location.getLatitude() * 100000.0d));
            gpsInfo.setLng((int) (location.getLongitude() * 100000.0d));
            gpsInfo.setGpsTimeS((int) (location.getTime() / 1000));
            this.LAST_gpsFixTime = gpsInfo.getgpsFixTime();
            int Tspeed = (int) this.edogDataManager.getModifiedSpeed(location.getSpeed() * 3.6f);
            if (gpsInfo.getgpsFixTime() <= 0 || Tspeed <= 10) {
                if (gpsInfo.getgpsFixTime() > 0 && Tspeed <= 10) {
                    gpsInfo.setSpeed(Tspeed);
                    this.speed_chk = 0;
                } else if (gpsInfo.getgpsFixTime() == 0) {
                    gpsInfo.setSpeed(0);
                    this.speed_chk = 0;
                }
            } else if (Tspeed - gpsInfo.getSpeed() < 8 || this.speed_chk > 4 || this.LAST_gpsFixTime < 1) {
                gpsInfo.setSpeed(Tspeed);
                this.speed_chk = 0;
            }
            gpsInfo.setgpsFixTime(8);
            this.speed_chk++;
            gpsInfo.setGpsDate(Integer.parseInt(this.format.format(this.calendar.getTime())));
            if (gpsInfo.getSpeed() >= 5) {
                gpsInfo.setBearing((int) location.getBearing());
            }
            if (Math.abs(Lat_save0 - gpsInfo.getLat()) > 150 || Math.abs(Lng_save0 - gpsInfo.getLng()) > 150) {
                Lat_save1 = Lat_save0;
                Lng_save1 = Lng_save0;
                Dir_save1 = Dir_save0;
                Lat_save0 = gpsInfo.getLat();
                Lng_save0 = gpsInfo.getLng();
                Dir_save0 = gpsInfo.getBearing();
            }
        }
    }

    public class GpsLocationListener implements LocationListener {
        public GpsLocationListener() {
        }

        public void onLocationChanged(Location location) {
            LogUtils.d("定到位置");
            TuzhiService.this.printGpsLocation(location);
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

    public void onDestroy() {
        unregisterReceiver(this.mBroadcastReceiver);
        Log.v("TuzhiService", "onDestroy");
        KEY_ISSHOW_DIALOG = 0;
        GPRSTOTAL = 0;
        stopForeground(true);
        ToolUtils.getInstance().exitNotify(this);
        HttpRequestManager.getInstance().shutDownThreadPool();
        if (!(this.mLoctionManager == null || this.mLocationListener == null)) {
            this.mLoctionManager.removeUpdates(this.mLocationListener);
        }
        this.radar_run = false;
        this.edog_run = false;
        this.edogThread.interrupt();
        this.edogRunnable = null;
        this.dataPlay.close_play();
        mRadarFloatView = null;
        super.onDestroy();
    }
}
