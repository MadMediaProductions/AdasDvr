package com.fvision.camera.loaction;

import android.location.Location;
import android.os.Bundle;

public interface GPSLocationListener {
    void UpdateGPSProviderStatus(int i);

    void UpdateLocation(Location location);

    void UpdateStatus(String str, int i, Bundle bundle);
}
