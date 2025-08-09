package com.fvision.camera.manager;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import com.fvision.camera.App;
import com.fvision.camera.utils.LogUtils;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TimerTask;

public class SoundManager {
    public static final int FOCUS_ADAS = 2;
    public static final int FOCUS_EDOG = 1;
    public static final int MEDIA_PLAYER = 0;
    public static final int SOUND_POOL = 1;
    private static final int WHAT_ADAS_PLAY = 10;
    private static final int WHAT_EDOG_PLAY = 20;
    private static final int WHAT_MEDIA_PLAY = 30;
    public static final int WHAT_TIMEOUT_AUDIO_FOCUS = 40;
    private static SoundManager instance = null;
    /* access modifiers changed from: private */
    public static MediaPlayer mediaPlayer;
    private static SoundPool soundPool;
    private boolean adasIsPlaying = false;
    AudioManager.OnAudioFocusChangeListener afChangeListener2 = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
        }
    };
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer1) {
            try {
                if (SoundManager.mediaPlayer != null) {
                    SoundManager.mediaPlayer.stop();
                    SoundManager.mediaPlayer.release();
                    MediaPlayer unused = SoundManager.mediaPlayer = null;
                }
            } catch (Exception e) {
            }
        }
    };
    private int currentCmd = -1;
    private boolean edogIsPlaying = false;
    public int focus = -1;
    private boolean isPlaying = true;
    public boolean isProcess = false;
    /* access modifiers changed from: private */
    public Activity mActivity;
    private boolean mAudioFocus = false;
    public AudioManager mAudioManager;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 10:
                    AdasSoundManager.getInstance().playSound(msg.arg1);
                    return;
                case 20:
                    if (SoundManager.this.mActivity != null) {
                        EdogSoundManager.getInstance(SoundManager.this.mActivity).playSound(msg.arg1);
                        return;
                    }
                    return;
                case 30:
                    SoundManager.this.sendMediaButton(126);
                    return;
                case 40:
                    SoundManager.this.abandonAudioFocus();
                    return;
                default:
                    return;
            }
        }
    };
    private HashMap<Integer, Integer> mSoundMap = new HashMap<>();
    private int model = 0;
    private LinkedList<Integer> soundList = new LinkedList<>();
    TimerTask task = new TimerTask() {
        public void run() {
            SoundManager.this.isProcess = false;
            SoundManager.this.process();
        }
    };

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
            initSoundPool();
        }
        return instance;
    }

    public void init(Activity activity) {
        this.mActivity = activity;
        this.mAudioManager = (AudioManager) this.mActivity.getApplicationContext().getSystemService("audio");
    }

    public boolean isAdasIsPlaying() {
        return this.adasIsPlaying;
    }

    public void setAdasIsPlaying(boolean adasIsPlaying2) {
        this.adasIsPlaying = adasIsPlaying2;
    }

    public boolean isEdogIsPlaying() {
        return this.edogIsPlaying;
    }

    public void setEdogIsPlaying(boolean edogIsPlaying2) {
        this.edogIsPlaying = edogIsPlaying2;
    }

    public void setSoundModel(int model2) {
        this.model = model2;
    }

    public int getSoundModel() {
        return this.model;
    }

    public void playSound(int rawId) {
        this.soundList.add(Integer.valueOf(rawId));
        if (this.mSoundMap.get(Integer.valueOf(rawId)) == null) {
            this.mSoundMap.put(Integer.valueOf(rawId), Integer.valueOf(soundPool.load(App.getInstance(), rawId, 1)));
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    SoundManager.this.process();
                }
            });
            return;
        }
        process();
    }

    public void playBackMusic() {
        this.mHandler.sendEmptyMessageDelayed(30, 2000);
    }

    public void process() {
        LogUtils.d("soundList " + this.soundList.size());
        if (this.soundList.size() != 0 && !this.isProcess) {
            LogUtils.d("soundList process...");
            this.isProcess = true;
            this.currentCmd = this.soundList.removeFirst().intValue();
            play(this.currentCmd);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SoundManager.this.isProcess = false;
                    SoundManager.this.process();
                }
            }).start();
        }
    }

    private void play(int cmd) {
        soundPool.play(this.mSoundMap.get(Integer.valueOf(cmd)).intValue(), 1.0f, 1.0f, 0, 0, 1.0f);
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

    public void mediaPlay(int raw) {
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

    public void sendMediaButton(int keyCode) {
        boolean backMusicIsPlay = this.mAudioManager.isMusicActive();
        if (keyCode == 127) {
            if (!isAdasIsPlaying() && !isEdogIsPlaying()) {
                this.isPlaying = backMusicIsPlay;
            }
            if (!backMusicIsPlay) {
                return;
            }
        }
        if (keyCode != 126 || (this.isPlaying && !isAdasIsPlaying() && !isEdogIsPlaying())) {
            KeyEvent keyEvent = new KeyEvent(0, keyCode);
            Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
            intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
            this.mActivity.sendOrderedBroadcast(intent, (String) null);
        }
    }

    public void adasPlay(int rawid) {
        this.adasIsPlaying = true;
        Log.e("msg", "121");
        AdasSoundManager.getInstance().playSound(rawid);
    }

    public void edogPlay(int rawid) {
        this.edogIsPlaying = true;
        EdogSoundManager.getInstance(this.mActivity).playSound(rawid);
    }

    public void requestAudioFocus() {
        if (!this.mAudioFocus && getInstance().mAudioManager.requestAudioFocus(this.afChangeListener2, 3, 2) == 1) {
            this.mAudioFocus = true;
            getInstance().focus = 2;
        }
    }

    public void abandonAudioFocus() {
        if (this.mAudioFocus) {
            this.mAudioManager.abandonAudioFocus(this.afChangeListener2);
            this.mAudioFocus = false;
        }
    }

    public void timeOutAbandonAudioFocus(long duration) {
        this.mHandler.sendEmptyMessageDelayed(40, duration);
    }

    public void clearTimeOutAbandonAudioFocus() {
        this.mHandler.removeMessages(40);
    }
}
