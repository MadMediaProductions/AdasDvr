package com.fvision.camera.manager;

import android.util.Log;
import com.fvision.camera.iface.ICameraStateChange;
import com.serenegiant.usb.IFrameCallback;
import java.util.ArrayList;
import java.util.List;

public class CameraStateIml {
    private static CameraStateIml _instance;
    private List<ICameraStateChange> listenerList;
    private ICameraStateChange mICameraStateChangeCustomInterFace = null;
    private ICameraStateChange mICameraStateChangeIOnlyOnce = null;
    private ICameraStateChange mICameraStateChangeInterFace = null;
    public IFrameCallback mStateFrameCallback = new IFrameCallback() {
        public void onFrame(byte[] frame) {
            if (frame.length < 200) {
                Log.i("onFrame", "frame.length < 200");
            } else if (frame[frame.length - 9] != 0 && frame[frame.length - 1] != 0 && frame[frame.length - 2] != 0 && frame[frame.length - 3] != 0 && frame[frame.length - 4] != 0 && frame[frame.length - 5] != 0 && frame[frame.length - 6] != 0 && frame[frame.length - 7] != 0 && frame[frame.length - 8] != 0) {
                Log.i("onFrame", "frame[frame.length - 9]");
            } else if (frame[0] == 0 && frame[1] == 0 && frame[2] == 0 && frame[3] == 0 && frame[4] == 0 && frame[5] == 0 && frame[6] == 0 && frame[7] == 0 && frame[8] == 0) {
                Log.i("onFrame", "frame[0] == 0");
            } else {
                if (CmdManager.getInstance().getCurrentState().getStateFrame() == null) {
                    CameraStateIml.this.change(frame);
                }
                if (CmdManager.getInstance().getCurrentState().isMainStateChange(frame)) {
                    CameraStateIml.this.change(frame);
                }
            }
        }
    };

    public static CameraStateIml getInstance() {
        if (_instance == null) {
            _instance = new CameraStateIml();
        }
        return _instance;
    }

    /* access modifiers changed from: private */
    public void change(byte[] frame) {
        CmdManager.getInstance().getCurrentState().setStateFrame(frame);
        if (this.mICameraStateChangeInterFace != null) {
            this.mICameraStateChangeInterFace.stateChange();
        }
        if (this.mICameraStateChangeIOnlyOnce != null) {
            this.mICameraStateChangeIOnlyOnce.stateChange();
            this.mICameraStateChangeIOnlyOnce = null;
        }
        if (this.mICameraStateChangeCustomInterFace != null) {
            this.mICameraStateChangeCustomInterFace.stateChange();
        }
        if (this.listenerList != null) {
            for (ICameraStateChange listener : this.listenerList) {
                listener.stateChange();
            }
        }
    }

    public void setOnCameraStateListner(ICameraStateChange icameraState) {
        this.mICameraStateChangeInterFace = icameraState;
    }

    public void setOnCameraStateOnlyOnceListner(ICameraStateChange icameraState) {
        this.mICameraStateChangeIOnlyOnce = icameraState;
    }

    public void setOnCameraStateCustomListner(ICameraStateChange icameraState) {
        this.mICameraStateChangeCustomInterFace = icameraState;
    }

    public void delListener(ICameraStateChange icameraState) {
        if (this.listenerList != null) {
            this.listenerList.remove(icameraState);
        }
    }

    public void addListener(ICameraStateChange icameraState) {
        if (icameraState != null) {
            if (this.listenerList == null) {
                this.listenerList = new ArrayList();
            }
            if (this.listenerList.size() > 0) {
                for (ICameraStateChange back : this.listenerList) {
                    if (back.getClass().equals(icameraState.getClass())) {
                        return;
                    }
                }
            }
            this.listenerList.add(icameraState);
        }
    }
}
