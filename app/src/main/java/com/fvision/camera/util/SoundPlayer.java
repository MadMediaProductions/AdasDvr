package com.fvision.camera.util;

import android.content.Context;
import android.media.SoundPool;

public class SoundPlayer {
    private Context context;
    /* access modifiers changed from: private */
    public int soundID;
    private SoundPool soundPool = new SoundPool(10, 1, 5);

    public SoundPlayer(Context context2) {
        this.context = context2;
    }

    public int loadRes(int resId) {
        return this.soundPool.load(this.context, resId, 1);
    }

    public void play(int soundID2, int priority, boolean isLoop) {
        this.soundPool.play(soundID2, 1.0f, 1.0f, priority, isLoop ? -1 : 0, 1.0f);
    }

    public void stop(int soundID2) {
        this.soundPool.stop(soundID2);
        this.soundPool.release();
    }

    public void play(int resId, final boolean isLoop) {
        this.soundID = loadRes(resId);
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                SoundPlayer.this.play(SoundPlayer.this.soundID, 0, isLoop);
            }
        });
    }

    public void stop() {
        stop(this.soundID);
    }
}
