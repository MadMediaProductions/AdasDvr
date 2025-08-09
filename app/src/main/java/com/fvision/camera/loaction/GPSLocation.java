package com.fvision.camera.loaction;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class GPSLocation implements LocationListener {
    private GPSLocationListener mGpsLocationListener;

    public GPSLocation(GPSLocationListener gpsLocationListener) {
        this.mGpsLocationListener = gpsLocationListener;
    }

    public void onLocationChanged(Location location) {
        if (location != null) {
            this.mGpsLocationListener.UpdateLocation(location);
        }
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        this.mGpsLocationListener.UpdateStatus(provider, status, extras);
        switch (status) {
            case 0:
                this.mGpsLocationListener.UpdateGPSProviderStatus(2);
                return;
            case 1:
                this.mGpsLocationListener.UpdateGPSProviderStatus(3);
                return;
            case 2:
                this.mGpsLocationListener.UpdateGPSProviderStatus(4);
                return;
            default:
                return;
        }
    }

    public void onProviderEnabled(String provider) {
        this.mGpsLocationListener.UpdateGPSProviderStatus(0);
    }

    public void onProviderDisabled(String provider) {
        this.mGpsLocationListener.UpdateGPSProviderStatus(1);
    }
}
