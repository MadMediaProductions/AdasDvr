package com.fvision.camera.manager;

import com.fvision.camera.bean.DevFileName;
import java.util.ArrayList;

public class DevFileNameManager {
    private static DevFileName currentDev = null;
    private static ArrayList<DevFileName> devList = new ArrayList<>();
    private static DevFileNameManager mDevFileNameManager;

    public static DevFileNameManager getInstance() {
        if (mDevFileNameManager == null) {
            mDevFileNameManager = new DevFileNameManager();
            devList.add(new DevFileName("VSFILE", "HWC", "com.fvision.camera"));
        }
        return mDevFileNameManager;
    }

    public ArrayList<DevFileName> getDevList() {
        return devList;
    }

    public void setCurrentDev(DevFileName dev) {
        currentDev = dev;
    }

    public DevFileName getCurrentDev() {
        return currentDev;
    }
}
