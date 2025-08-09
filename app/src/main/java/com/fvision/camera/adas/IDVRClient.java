package com.fvision.camera.adas;

import android.os.ParcelFileDescriptor;
import com.huiying.cameramjpeg.UvcCamera;

public interface IDVRClient {

    public interface IDVRConnectListener {
        void onConnect();

        void onDisconnect();
    }

    public interface IDVRPrepareListener {
        void onPrepare(boolean z);
    }

    UvcCamera getDVRClient();

    String getDeviceCode();

    String getSecretKey();

    void init();

    boolean isConnect();

    void playSound(int i);

    void release();

    void saveSecretKey(String str);

    void setDVRConnectListener(IDVRConnectListener iDVRConnectListener);

    void setParcelFileDescriptor(ParcelFileDescriptor parcelFileDescriptor);

    void setPrepareListener(IDVRPrepareListener iDVRPrepareListener);
}
