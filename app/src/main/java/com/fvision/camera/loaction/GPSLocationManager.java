package com.fvision.camera.loaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;
import com.alibaba.sdk.android.oss.common.RequestParameters;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;

public class GPSLocationManager {
    private static final String GPS_LOCATION_NAME = "gps";
    private static GPSLocationManager gpsLocationManager;
    private static String mLocateType;
    private static Object objLock = new Object();
    private ExecutorService fixedThreadPool;
    private boolean isGpsEnabled;
    private boolean isOPenGps;
    private LocationManager locationManager;
    private WeakReference<Activity> mContext;
    private GPSLocation mGPSLocation;
    private float mMinDistance;
    private long mMinTime;
    private Handler mainThreadHandler;

    private GPSLocationManager(Activity context) {
        initData(context);
    }

    private void initData(Activity context) {
        this.mContext = new WeakReference<>(context);
        if (this.mContext.get() != null) {
            this.locationManager = (LocationManager) ((Activity) this.mContext.get()).getSystemService(RequestParameters.SUBRESOURCE_LOCATION);
        }
        LocationManager locationManager2 = this.locationManager;
        mLocateType = GPS_LOCATION_NAME;
        this.isOPenGps = false;
        this.mMinTime = 1000;
        this.mMinDistance = 0.0f;
        this.mainThreadHandler = new Handler() {
        };
    }

    public static GPSLocationManager getInstances(Activity context) {
        if (gpsLocationManager == null) {
            synchronized (objLock) {
                if (gpsLocationManager == null) {
                    gpsLocationManager = new GPSLocationManager(context);
                }
            }
        }
        return gpsLocationManager;
    }

    public void setScanSpan(long minTime) {
        this.mMinTime = minTime;
    }

    public void setMinDistance(float minDistance) {
        this.mMinDistance = minDistance;
    }

    public void start(GPSLocationListener gpsLocationListener) {
        start(gpsLocationListener, this.isOPenGps);
    }

    public void start(GPSLocationListener gpsLocationListener, boolean isOpenGps) {
        this.isOPenGps = isOpenGps;
        if (this.mContext.get() != null) {
            this.mGPSLocation = new GPSLocation(gpsLocationListener);
            this.isGpsEnabled = this.locationManager.isProviderEnabled(GPS_LOCATION_NAME);
            if (!this.isGpsEnabled && this.isOPenGps) {
                openGPS();
            } else if (ActivityCompat.checkSelfPermission((Context) this.mContext.get(), "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission((Context) this.mContext.get(), "android.permission.ACCESS_COARSE_LOCATION") == 0) {
                this.mGPSLocation.onLocationChanged(this.locationManager.getLastKnownLocation(mLocateType));
                this.locationManager.requestLocationUpdates(mLocateType, this.mMinTime, this.mMinDistance, this.mGPSLocation);
            }
        }
    }

    public void openGPS() {
        Toast.makeText((Context) this.mContext.get(), "请打开GPS设置", 0).show();
        if (Build.VERSION.SDK_INT > 15) {
            ((Activity) this.mContext.get()).startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"), 0);
        }
    }

    public void stop() {
        if (this.mContext.get() == null) {
            return;
        }
        if (ActivityCompat.checkSelfPermission((Context) this.mContext.get(), "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission((Context) this.mContext.get(), "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            this.locationManager.removeUpdates(this.mGPSLocation);
        }
    }
}
