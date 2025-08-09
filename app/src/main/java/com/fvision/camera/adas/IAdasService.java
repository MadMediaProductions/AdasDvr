package com.fvision.camera.adas;

import com.fvision.camera.adas.bean.AdasInterfaceImp;
import com.fvision.camera.adas.bean.DrawInfo;

public interface IAdasService {

    public interface IDVRConnectListener {
        void onConnect();

        void onDisConnect();
    }

    public interface IDrawInfoListener {
        void onDraw(DrawInfo drawInfo);
    }

    AdasInterfaceImp getAdasInterfaceImp();

    boolean getAdasState();

    IDVRClient getDVRClient();

    void setAdasEnable(boolean z);

    void setDVRConnectListener(IDVRConnectListener iDVRConnectListener);

    void setDrawInfoListener(IDrawInfoListener iDrawInfoListener);
}
