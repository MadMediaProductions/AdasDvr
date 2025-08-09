package com.hdsc.edog.jni;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import com.fvision.camera.BuildConfig;
import com.fvision.camera.R;
import com.fvision.camera.manager.SoundManager;
import com.fvision.camera.util.Cmd_Const;
import com.fvision.camera.utils.Const;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.opencv.highgui.Highgui;

public class DataPlay {
    private static final String TAG = "DataPlay";
    static boolean flgSPEEDOVER = false;
    public static boolean isPlaying = false;
    private static Map<Integer, Integer> kmMap = null;
    private static Map<Integer, Integer> mMap = null;
    private static MediaPlayer mMediaPlayer = null;
    private static Map<Integer, Integer> positionMap = null;
    private static Map<Integer, Integer> warnTypeMap = null;
    private boolean Playing_One = false;
    private List<String> audioList;
    private EdogDataManager edogDataManager;
    boolean isPlay = false;
    AudioManager mAudioManager;
    private Context mContext;
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;
    boolean mPlayEnabled = true;
    private boolean player_run = true;
    private RadarDataManager radarDataManager;
    private HashMap soundMap;
    Runnable testThread = new Runnable() {
        public void run() {
            while (true) {
                DataPlay.this.testElaySound();
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public DataPlay(Context context) {
        this.mContext = context;
        init();
        this.mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                Log.d(DataPlay.TAG, "onAudioFocusChange:focusChange = " + focusChange);
                switch (focusChange) {
                }
            }
        };
        this.audioList = new ArrayList();
        this.soundMap = new HashMap();
        if (this.edogDataManager == null) {
            this.edogDataManager = new EdogDataManager(context);
        }
        if (this.radarDataManager == null) {
            this.radarDataManager = new RadarDataManager(context);
        }
        this.mPlayEnabled = true;
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
    }

    public void setPlayEnabled(boolean enabled) {
        this.mPlayEnabled = enabled;
    }

    public void SpeedOverPlayer(int SpeedOverFlg, int MAINLoopCont) {
        if (SpeedOverFlg != 0) {
            if (!flgSPEEDOVER) {
                flgSPEEDOVER = true;
                SoundManager.getInstance().edogPlay(R.raw.speedover);
            } else if (MAINLoopCont % 6 == 0) {
                SoundManager.getInstance().edogPlay(R.raw.didi);
            }
        } else if (SpeedOverFlg == 0) {
            flgSPEEDOVER = false;
        }
    }

    public void radarDataPlayer(int radarType) {
        if (this.mPlayEnabled) {
            if (radarType == 1) {
                this.audioList.add("LASER.mp3");
            } else if (radarType == 2) {
                this.audioList.add("K.mp3");
            } else if (radarType == 3) {
                this.audioList.add("KA.mp3");
            } else if (radarType == 4) {
                this.audioList.add("KU.mp3");
            } else if (radarType == 5) {
                this.audioList.add("X.mp3");
            } else if (radarType == 8) {
                this.audioList.add("RFDIDI.mp3");
            } else if (radarType == 19) {
                this.audioList.add("RDERR.mp3");
            }
        }
    }

    /* access modifiers changed from: private */
    public void testElaySound() {
        ArrayList<Integer> raws = new ArrayList<>();
        raws.add(Integer.valueOf(R.raw.ta8));
        raws.add(Integer.valueOf(R.raw.m500));
        raws.add(Integer.valueOf(R.raw.td00));
        raws.add(Integer.valueOf(R.raw.km80));
        Intent playSoundIntent = new Intent(Const.BROAD_PLAY_EDOG_SOUND);
        Bundle b = new Bundle();
        b.putIntegerArrayList("raws", raws);
        playSoundIntent.putExtras(b);
        playSoundIntent.addFlags(32);
        this.mContext.sendBroadcast(playSoundIntent);
    }

    public void edogDataPlayer(EdogDataInfo EdogPlayInfo) {
        this.isPlay = this.mAudioManager.isMusicActive();
        int TAType = EdogPlayInfo.getmPosition();
        int distance = ((EdogPlayInfo.getmDistance() + 40) / 100) * 100;
        int warnType = EdogPlayInfo.getmAlarmType();
        int speedLimit = EdogPlayInfo.getmSpeedLimit();
        boolean isAlarm = EdogPlayInfo.ismIsAlarm();
        int FirstFindCamera = EdogPlayInfo.getmFirstFindCamera();
        ArrayList<Integer> raws = new ArrayList<>();
        if (isAlarm) {
            if (FirstFindCamera == 1 || FirstFindCamera == 5) {
                raws.add(positionMap.get(Integer.valueOf(TAType)));
                if (distance > 100 && distance < 1000 && FirstFindCamera == 1 && warnType != 12) {
                    raws.add(mMap.get(Integer.valueOf(distance)));
                }
                raws.add(warnTypeMap.get(Integer.valueOf(warnType)));
                if (speedLimit < 30 || speedLimit > 120) {
                    raws.add(Integer.valueOf(R.raw.beware_driver));
                } else {
                    raws.add(kmMap.get(Integer.valueOf(speedLimit)));
                }
                playSound(raws);
            } else if (FirstFindCamera == 2) {
                playSound((int) R.raw.second);
            }
        }
        if (FirstFindCamera == 3) {
            playSound((int) R.raw.pass);
        }
    }

    private static void init() {
        if (positionMap == null) {
            positionMap = new HashMap();
            positionMap.put(0, Integer.valueOf(R.raw.ta0));
            positionMap.put(1, Integer.valueOf(R.raw.ta1));
            positionMap.put(2, Integer.valueOf(R.raw.ta2));
            positionMap.put(3, Integer.valueOf(R.raw.ta3));
            positionMap.put(4, Integer.valueOf(R.raw.ta4));
            positionMap.put(5, Integer.valueOf(R.raw.ta5));
            positionMap.put(6, Integer.valueOf(R.raw.ta6));
            positionMap.put(7, Integer.valueOf(R.raw.ta7));
            positionMap.put(8, Integer.valueOf(R.raw.ta8));
            positionMap.put(9, Integer.valueOf(R.raw.ta9));
        }
        if (mMap == null) {
            mMap = new HashMap();
            mMap.put(0, Integer.valueOf(R.raw.m0));
            mMap.put(100, Integer.valueOf(R.raw.m100));
            mMap.put(200, Integer.valueOf(R.raw.m200));
            mMap.put(300, Integer.valueOf(R.raw.m300));
            mMap.put(Integer.valueOf(Highgui.CV_CAP_PROP_XI_DOWNSAMPLING), Integer.valueOf(R.raw.m400));
            mMap.put(500, Integer.valueOf(R.raw.m500));
            mMap.put(600, Integer.valueOf(R.raw.m600));
            mMap.put(700, Integer.valueOf(R.raw.m700));
            mMap.put(800, Integer.valueOf(R.raw.m800));
            mMap.put(900, Integer.valueOf(R.raw.m900));
        }
        if (kmMap == null) {
            kmMap = new HashMap();
            kmMap.put(0, Integer.valueOf(R.raw.km0));
            kmMap.put(10, Integer.valueOf(R.raw.km10));
            kmMap.put(20, Integer.valueOf(R.raw.km20));
            kmMap.put(30, Integer.valueOf(R.raw.km30));
            kmMap.put(40, Integer.valueOf(R.raw.km40));
            kmMap.put(50, Integer.valueOf(R.raw.km50));
            kmMap.put(60, Integer.valueOf(R.raw.km60));
            kmMap.put(70, Integer.valueOf(R.raw.km70));
            kmMap.put(80, Integer.valueOf(R.raw.km80));
            kmMap.put(90, Integer.valueOf(R.raw.km90));
            kmMap.put(100, Integer.valueOf(R.raw.km100));
            kmMap.put(110, Integer.valueOf(R.raw.km110));
            kmMap.put(120, Integer.valueOf(R.raw.km120));
            kmMap.put(130, Integer.valueOf(R.raw.km130));
            kmMap.put(150, Integer.valueOf(R.raw.km150));
        }
        if (warnTypeMap == null) {
            warnTypeMap = new HashMap();
            warnTypeMap.put(0, Integer.valueOf(R.raw.td00));
            warnTypeMap.put(1, Integer.valueOf(R.raw.td01));
            warnTypeMap.put(2, Integer.valueOf(R.raw.td02));
            warnTypeMap.put(3, Integer.valueOf(R.raw.td03));
            warnTypeMap.put(4, Integer.valueOf(R.raw.td04));
            warnTypeMap.put(5, Integer.valueOf(R.raw.td05));
            warnTypeMap.put(6, Integer.valueOf(R.raw.td06));
            warnTypeMap.put(7, Integer.valueOf(R.raw.td07));
            warnTypeMap.put(8, Integer.valueOf(R.raw.td08));
            warnTypeMap.put(9, Integer.valueOf(R.raw.td09));
            warnTypeMap.put(10, Integer.valueOf(R.raw.td0a));
            warnTypeMap.put(11, Integer.valueOf(R.raw.td0b));
            warnTypeMap.put(12, Integer.valueOf(R.raw.td0c));
            warnTypeMap.put(208, Integer.valueOf(R.raw.tdd0));
            warnTypeMap.put(Integer.valueOf(BuildConfig.VERSION_CODE), Integer.valueOf(R.raw.tdd1));
            warnTypeMap.put(210, Integer.valueOf(R.raw.tdd2));
            warnTypeMap.put(211, Integer.valueOf(R.raw.tdd3));
            warnTypeMap.put(212, Integer.valueOf(R.raw.tdd4));
            warnTypeMap.put(213, Integer.valueOf(R.raw.tdd5));
            warnTypeMap.put(214, Integer.valueOf(R.raw.tdd6));
            warnTypeMap.put(215, Integer.valueOf(R.raw.tdd7));
            warnTypeMap.put(216, Integer.valueOf(R.raw.tdd8));
            warnTypeMap.put(217, Integer.valueOf(R.raw.tdd9));
            warnTypeMap.put(218, Integer.valueOf(R.raw.tdda));
            warnTypeMap.put(219, Integer.valueOf(R.raw.tddb));
            warnTypeMap.put(220, Integer.valueOf(R.raw.tddc));
            warnTypeMap.put(221, Integer.valueOf(R.raw.tddd));
            warnTypeMap.put(Integer.valueOf(Cmd_Const.DEV_FILE_CHECK_NOT_DETECTED_TF), Integer.valueOf(R.raw.tdde));
            warnTypeMap.put(223, Integer.valueOf(R.raw.tddf));
            warnTypeMap.put(224, Integer.valueOf(R.raw.tde0));
            warnTypeMap.put(225, Integer.valueOf(R.raw.tde1));
            warnTypeMap.put(226, Integer.valueOf(R.raw.tde2));
            warnTypeMap.put(227, Integer.valueOf(R.raw.tde3));
            warnTypeMap.put(228, Integer.valueOf(R.raw.tde4));
            warnTypeMap.put(229, Integer.valueOf(R.raw.tde5));
            warnTypeMap.put(230, Integer.valueOf(R.raw.tde6));
            warnTypeMap.put(231, Integer.valueOf(R.raw.tde7));
            warnTypeMap.put(232, Integer.valueOf(R.raw.tde8));
            warnTypeMap.put(Integer.valueOf(Cmd_Const.DEV_FILE_CHECK_FAIL), Integer.valueOf(R.raw.tde9));
            warnTypeMap.put(234, Integer.valueOf(R.raw.tdea));
            warnTypeMap.put(235, Integer.valueOf(R.raw.tdeb));
            warnTypeMap.put(236, Integer.valueOf(R.raw.tded));
            warnTypeMap.put(237, Integer.valueOf(R.raw.tded));
            warnTypeMap.put(238, Integer.valueOf(R.raw.tdee));
            warnTypeMap.put(239, Integer.valueOf(R.raw.tdef));
            warnTypeMap.put(247, Integer.valueOf(R.raw.tdf7));
            warnTypeMap.put(248, Integer.valueOf(R.raw.tdf8));
            warnTypeMap.put(249, Integer.valueOf(R.raw.tdf9));
            warnTypeMap.put(251, Integer.valueOf(R.raw.tdfb));
            warnTypeMap.put(252, Integer.valueOf(R.raw.tdfc));
            warnTypeMap.put(253, Integer.valueOf(R.raw.tdfd));
            warnTypeMap.put(254, Integer.valueOf(R.raw.tdfe));
        }
    }

    public void close_play() {
        this.audioList = new ArrayList();
        this.Playing_One = false;
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        Intent it = new Intent("com.hdsc.edog.stop");
        it.addFlags(32);
        this.mContext.sendBroadcast(it);
    }

    private void continuMusic() {
        Intent freshIntent = new Intent();
        freshIntent.setAction("com.android.music.musicservicecommand.togglepause");
        freshIntent.putExtra("command", "togglepause");
        this.mContext.sendBroadcast(freshIntent);
    }

    private void playSound(ArrayList<Integer> raws) {
        Intent playSoundIntent = new Intent(Const.BROAD_PLAY_EDOG_SOUND);
        Bundle b = new Bundle();
        b.putIntegerArrayList("raws", raws);
        playSoundIntent.putExtras(b);
        playSoundIntent.addFlags(32);
        this.mContext.sendBroadcast(playSoundIntent);
    }

    private void playSound(int raw) {
        ArrayList<Integer> raws = new ArrayList<>();
        raws.add(Integer.valueOf(raw));
        Intent playSoundIntent = new Intent(Const.BROAD_PLAY_EDOG_SOUND);
        Bundle b = new Bundle();
        b.putIntegerArrayList("raws", raws);
        playSoundIntent.putExtras(b);
        playSoundIntent.addFlags(32);
        this.mContext.sendBroadcast(playSoundIntent);
    }

    public void sendMediaButton(Context context, int keyCode) {
        if (!this.mAudioManager.isMusicActive()) {
            KeyEvent keyEvent = new KeyEvent(0, keyCode);
            Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
            intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
            context.sendOrderedBroadcast(intent, (String) null);
            KeyEvent keyEvent2 = new KeyEvent(1, keyCode);
            Intent intent2 = new Intent("android.intent.action.MEDIA_BUTTON");
            intent2.putExtra("android.intent.extra.KEY_EVENT", keyEvent2);
            context.sendOrderedBroadcast(intent2, (String) null);
        }
    }
}
