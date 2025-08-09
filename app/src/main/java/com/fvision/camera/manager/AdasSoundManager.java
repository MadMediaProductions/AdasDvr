package com.fvision.camera.manager;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import com.fvision.camera.App;
import com.fvision.camera.util.LogUtils;
import java.util.HashMap;
import java.util.LinkedList;

public class AdasSoundManager {
    private static AdasSoundManager instance = null;
    /* access modifiers changed from: private */
    public static MediaPlayer mediaPlayer;
    private static SoundPool soundPool;
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer1) {
            try {
                if (AdasSoundManager.mediaPlayer != null) {
                    AdasSoundManager.mediaPlayer.stop();
                    AdasSoundManager.mediaPlayer.release();
                    MediaPlayer unused = AdasSoundManager.mediaPlayer = null;
                }
            } catch (Exception e) {
            } finally {
                AdasSoundManager.this.isProcess = false;
            }
            AdasSoundManager.this.process();
        }
    };
    private int currentCmd = -1;
    public boolean isProcess = false;
    private boolean mAudioFocus = false;
    private HashMap<Integer, Integer> mSoundMap = new HashMap<>();
    private LinkedList<Integer> soundList = new LinkedList<>();

    public static AdasSoundManager getInstance() {
        if (instance == null) {
            instance = new AdasSoundManager();
            initSoundPool();
        }
        return instance;
    }

    public boolean ismAudioFocus() {
        return this.mAudioFocus;
    }

    public void setmAudioFocus(boolean mAudioFocus2) {
        this.mAudioFocus = mAudioFocus2;
    }

    public void playSound(int rawId) {
        this.soundList.add(Integer.valueOf(rawId));
        Log.e("msg_ADAS ", "" + this.soundList.size());
        if (this.mSoundMap.get(Integer.valueOf(rawId)) == null && SoundManager.getInstance().getSoundModel() == 1) {
            this.mSoundMap.put(Integer.valueOf(rawId), Integer.valueOf(soundPool.load(App.getInstance(), rawId, 1)));
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    AdasSoundManager.this.process();
                }
            });
            return;
        }
        process();
    }

    public void process() {
        LogUtils.d("soundList " + this.soundList.size());
        if (!this.isProcess) {
            if (this.soundList.size() == 0) {
                SoundManager.getInstance().setAdasIsPlaying(false);
                if (SoundManager.getInstance().getSoundModel() == 0 && !SoundManager.getInstance().isEdogIsPlaying()) {
                    SoundManager.getInstance().abandonAudioFocus();
                    return;
                }
                return;
            }
            SoundManager.getInstance().setAdasIsPlaying(true);
            this.isProcess = true;
            this.currentCmd = this.soundList.removeFirst().intValue();
            play(this.currentCmd);
            int soundModel = SoundManager.getInstance().getSoundModel();
            SoundManager.getInstance();
            if (soundModel == 1) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(3500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        AdasSoundManager.this.isProcess = false;
                        AdasSoundManager.this.process();
                    }
                }).start();
            }
        }
    }

    private void play(int cmd) {
        if (SoundManager.getInstance().getSoundModel() == 1) {
            soundPool.play(this.mSoundMap.get(Integer.valueOf(cmd)).intValue(), 1.0f, 1.0f, 0, 0, 1.0f);
        } else if (SoundManager.getInstance().getSoundModel() == 0) {
            SoundManager.getInstance().requestAudioFocus();
            mediaPlay(cmd);
        }
    }

    private void mediaPlay(int raw) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = MediaPlayer.create(App.getInstance(), raw);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    if (mp != null) {
                        mp.start();
                    }
                }
            });
            mediaPlayer.setOnCompletionListener(this.beepListener);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
        }
    }

    private static void initSoundPool() {
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(1);
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(3);
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
            return;
        }
        soundPool = new SoundPool(1, 1, 5);
    }
}
