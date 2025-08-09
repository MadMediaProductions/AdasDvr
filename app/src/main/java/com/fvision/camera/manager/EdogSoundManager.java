package com.fvision.camera.manager;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import com.fvision.camera.App;
import com.fvision.camera.R;
import com.fvision.camera.utils.LogUtils;
import java.util.HashMap;
import java.util.LinkedList;

public class EdogSoundManager {
    private static EdogSoundManager instance = null;
    private static Context mContext;
    /* access modifiers changed from: private */
    public static MediaPlayer mediaPlayer;
    private static SoundPool soundPool;
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer1) {
            try {
                if (EdogSoundManager.mediaPlayer != null) {
                    EdogSoundManager.mediaPlayer.stop();
                    EdogSoundManager.mediaPlayer.release();
                    MediaPlayer unused = EdogSoundManager.mediaPlayer = null;
                }
            } catch (Exception e) {
            } finally {
                EdogSoundManager.this.isProcess = false;
            }
            EdogSoundManager.this.process();
        }
    };
    /* access modifiers changed from: private */
    public int currentCmd = -1;
    public boolean isProcess = false;
    private boolean mAudioFocus = false;
    private HashMap<Integer, Integer> mSoundMap = new HashMap<>();
    private LinkedList<Integer> soundList = new LinkedList<>();

    public static EdogSoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new EdogSoundManager();
            mContext = context;
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
        if (this.mSoundMap.get(Integer.valueOf(rawId)) != null || SoundManager.getInstance().getSoundModel() != 1) {
            process();
        } else if (soundPool != null && mContext != null) {
            this.mSoundMap.put(Integer.valueOf(rawId), Integer.valueOf(soundPool.load(mContext, rawId, 1)));
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    EdogSoundManager.this.process();
                }
            });
        } else if (this.soundList.size() > 0) {
            try {
                this.soundList.remove(rawId);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("playSound异常", Log.getStackTraceString(e));
            }
        }
    }

    public void process() {
        LogUtils.d("soundList " + this.soundList.size());
        if (!this.isProcess) {
            if (this.soundList.size() == 0) {
                SoundManager.getInstance().setEdogIsPlaying(false);
                if (SoundManager.getInstance().getSoundModel() == 0 && !SoundManager.getInstance().isAdasIsPlaying()) {
                    SoundManager.getInstance().abandonAudioFocus();
                    return;
                }
                return;
            }
            SoundManager.getInstance().setEdogIsPlaying(true);
            this.isProcess = true;
            try {
                this.currentCmd = this.soundList.removeFirst().intValue();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("process", "" + e.toString());
            }
            play(this.currentCmd);
            int soundModel = SoundManager.getInstance().getSoundModel();
            SoundManager.getInstance();
            if (soundModel == 1) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep((long) EdogSoundManager.this.getAudioDuation(EdogSoundManager.this.currentCmd));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        EdogSoundManager.this.isProcess = false;
                        EdogSoundManager.this.process();
                    }
                }).start();
            }
        }
    }

    private void play(int cmd) {
        if (SoundManager.getInstance().getSoundModel() == 1) {
            if (soundPool != null && this.mSoundMap.get(Integer.valueOf(cmd)) != null) {
                soundPool.play(this.mSoundMap.get(Integer.valueOf(cmd)).intValue(), 1.0f, 1.0f, 0, 0, 1.0f);
            }
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

    /* access modifiers changed from: private */
    public int getAudioDuation(int id) {
        switch (id) {
            case R.raw.adas_fcw:
            case R.raw.adas_fcw_en:
            case R.raw.speedover:
            case R.raw.tdfe:
                return 4500;
            case R.raw.adas_fcw2:
            case R.raw.adas_ldw2:
            case R.raw.km100:
            case R.raw.km20:
            case R.raw.km30:
            case R.raw.km40:
            case R.raw.km50:
            case R.raw.km60:
            case R.raw.km70:
            case R.raw.km80:
            case R.raw.km90:
            case R.raw.m0:
            case R.raw.second:
            case R.raw.ta0:
            case R.raw.ta8:
            case R.raw.ta9:
            case R.raw.td00:
            case R.raw.td01:
            case R.raw.td02:
            case R.raw.td03:
            case R.raw.td04:
            case R.raw.td05:
            case R.raw.td09:
            case R.raw.td0a:
            case R.raw.td0b:
            case R.raw.tdd2:
            case R.raw.tdd5:
            case R.raw.tddb:
            case R.raw.tde6:
            case R.raw.tde8:
            case R.raw.tdf9:
            case R.raw.tdfb:
                return 2500;
            case R.raw.adas_fvd:
            case R.raw.adas_fvd_en:
            case R.raw.adas_ldw_en:
            case R.raw.km110:
            case R.raw.km120:
            case R.raw.km130:
            case R.raw.ta2:
            case R.raw.ta3:
            case R.raw.ta4:
            case R.raw.ta5:
            case R.raw.ta6:
            case R.raw.ta7:
            case R.raw.td06:
            case R.raw.td07:
            case R.raw.td08:
            case R.raw.tdd9:
            case R.raw.tdda:
                return 3500;
            case R.raw.beware_driver:
            case R.raw.didi:
            case R.raw.km0:
            case R.raw.km10:
            case R.raw.km150:
            case R.raw.m100:
            case R.raw.m200:
            case R.raw.m300:
            case R.raw.m400:
            case R.raw.m500:
            case R.raw.m600:
            case R.raw.m700:
            case R.raw.m800:
            case R.raw.m900:
            case R.raw.pass:
            case R.raw.td0c:
            case R.raw.tdd0:
            case R.raw.tdd1:
            case R.raw.tdd3:
            case R.raw.tdd4:
            case R.raw.tdd6:
            case R.raw.tdd7:
            case R.raw.tdd8:
            case R.raw.tddd:
            case R.raw.tdde:
            case R.raw.tddf:
            case R.raw.tde0:
            case R.raw.tde1:
            case R.raw.tde2:
            case R.raw.tde3:
            case R.raw.tde4:
            case R.raw.tde5:
            case R.raw.tde7:
            case R.raw.tde9:
            case R.raw.tdeb:
            case R.raw.tdec:
            case R.raw.tded:
            case R.raw.tdee:
            case R.raw.tdef:
            case R.raw.tdf8:
            case R.raw.tdfc:
                return 1500;
            default:
                return 0;
        }
    }
}
