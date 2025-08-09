package com.fvision.camera.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import java.util.HashMap;

@SuppressLint({"UseSparseArrays"})
public class SoundUtils {
    public static final int INFINITE_PLAY = -1;
    public static final int MEDIA_SOUND = 3;
    public static final int RING_SOUND = 2;
    public static final int SINGLE_PLAY = 0;
    private Context context;
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundPoolMap;
    private int soundVolType = 3;

    public SoundUtils(Context context2, int soundVolType2) {
        this.context = context2;
        this.soundVolType = soundVolType2;
        this.soundPool = new SoundPool(5, 3, 0);
        this.soundPoolMap = new HashMap<>();
    }

    public void putSound(int order, int soundRes) {
        this.soundPoolMap.put(Integer.valueOf(order), Integer.valueOf(this.soundPool.load(this.context, soundRes, 1)));
    }

    public void playSound(int order, int times) {
        Context context2 = this.context;
        Context context3 = this.context;
        AudioManager am = (AudioManager) context2.getSystemService("audio");
        float volumnRatio = ((float) am.getStreamVolume(this.soundVolType)) / ((float) am.getStreamMaxVolume(this.soundVolType));
        this.soundPool.play(this.soundPoolMap.get(Integer.valueOf(order)).intValue(), volumnRatio, volumnRatio, 1, times, 1.0f);
    }

    public void setSoundVolType(int soundVolType2) {
        this.soundVolType = soundVolType2;
    }
}
