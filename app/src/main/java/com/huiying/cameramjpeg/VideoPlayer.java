package com.huiying.cameramjpeg;

import android.text.TextUtils;
import android.view.Surface;

public class VideoPlayer {
    private long mNativePtr;

    private native void nativeChangeESLayout(long j, int i, int i2);

    private native void nativeClose(long j);

    private native long nativeCreate();

    private native void nativeDrawESFrame(long j);

    private native int nativeGetCurrentPos(long j);

    private native int nativeGetDuration(long j);

    private native void nativeInitGles(long j, int i, int i2);

    private native int nativePrepare(long j);

    private native void nativeRelease(long j);

    private native void nativeSetDisplaySurface(long j, Surface surface);

    private native void nativeSetSpeed(long j, int i);

    private native void nativeSetStateCallback(long j, StatePlayCallback statePlayCallback);

    private native void nativeSetVideoPath(long j, String str);

    private native void nativeStart(long j);

    private native void nativeStop(long j);

    public VideoPlayer() {
        this.mNativePtr = 0;
        this.mNativePtr = nativeCreate();
    }

    public void setVideoPath(String path) {
        if (this.mNativePtr != 0 && !TextUtils.isEmpty(path)) {
            nativeSetVideoPath(this.mNativePtr, path);
        }
    }

    public void setDisplaySurface(Surface surface) {
        if (this.mNativePtr != 0) {
            nativeSetDisplaySurface(this.mNativePtr, surface);
        }
    }

    public int prepair() {
        if (this.mNativePtr != 0) {
            return nativePrepare(this.mNativePtr);
        }
        return -1;
    }

    public void start() {
        if (this.mNativePtr != 0) {
            nativeStart(this.mNativePtr);
        }
    }

    public void stop() {
        if (this.mNativePtr != 0) {
            nativeStop(this.mNativePtr);
        }
    }

    public void close() {
        if (this.mNativePtr != 0) {
            nativeClose(this.mNativePtr);
        }
    }

    public void release() {
        if (this.mNativePtr != 0) {
            nativeRelease(this.mNativePtr);
            this.mNativePtr = 0;
        }
    }

    public int getDuration() {
        if (this.mNativePtr != 0) {
            return nativeGetDuration(this.mNativePtr);
        }
        return 0;
    }

    public int getCurrentPos() {
        if (this.mNativePtr != 0) {
            return nativeGetCurrentPos(this.mNativePtr);
        }
        return 0;
    }

    public void setSpeed(int speed) {
        if (this.mNativePtr != 0) {
            nativeSetSpeed(this.mNativePtr, speed);
        }
    }

    public void setStateCallback(StatePlayCallback callback) {
        if (this.mNativePtr != 0) {
            nativeSetStateCallback(this.mNativePtr, callback);
        }
    }

    public void initGles(int width, int height) {
        nativeInitGles(this.mNativePtr, width, height);
    }

    public void changeESLayout(int width, int height) {
        nativeChangeESLayout(this.mNativePtr, width, height);
    }

    public void drawESFrame() {
        nativeDrawESFrame(this.mNativePtr);
    }
}
