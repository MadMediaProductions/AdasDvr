package com.fvision.camera.view.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioTrack;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.fvision.camera.R;
import com.fvision.camera.base.BaseActivity;
import com.fvision.camera.bean.CameraStateBean;
import com.fvision.camera.bean.FileBeans;
import com.fvision.camera.iface.ICameraStateChange;
import com.fvision.camera.manager.CameraStateIml;
import com.fvision.camera.manager.CmdManager;
import com.fvision.camera.utils.CameraStateUtil;
import com.fvision.camera.utils.LogUtils;
import com.fvision.camera.utils.ToastUtils;
import com.huiying.cameramjpeg.UvcCamera;
import com.serenegiant.usb.IFrameCallback;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class VideoPlayActivity extends BaseActivity implements View.OnClickListener {
    private static final int HIDE_BG_FRAME = 17;
    public static final String KEY_FILEBEAN = "KEYFILEBEAN";
    public static final String KEY_FILE_BEAN = "key_filebean";
    public static final String KEY_IS_LOCK = "key_is_lock";
    private static final int PAUSE = 0;
    private static final int PLAY = 1;
    private static final int SHOW_BG_FRAME = 18;
    private static final int SHOW_PROGRESS = 16;
    private RelativeLayout activityvideoplay;
    /* access modifiers changed from: private */
    public View bg_frame;
    private int buf_size;
    ImageView ffwd = null;
    /* access modifiers changed from: private */
    public boolean isDownFastButton = false;
    private boolean isLock;
    private ImageView iv_play;
    /* access modifiers changed from: private */
    public int lastBtnType = -1;
    private ImageView lock;
    /* access modifiers changed from: private */
    public AudioTrack mAudioTrack;
    /* access modifiers changed from: private */
    public FileBeans mFileBeans;
    private IFrameCallback mFrameCallback = new IFrameCallback() {
        public void onFrame(byte[] frame) {
            Log.e("mainactivity", "audioData " + frame.length);
            synchronized (this) {
                if (!(VideoPlayActivity.this.mAudioTrack == null || frame == null)) {
                    try {
                        VideoPlayActivity.this.mAudioTrack.write(frame, 0, frame.length);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
    private GLSurfaceView mGLSurfaceView;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 17:
                    if (VideoPlayActivity.this.bg_frame != null) {
                        VideoPlayActivity.this.bg_frame.setVisibility(8);
                        return;
                    }
                    return;
                case 18:
                    if (VideoPlayActivity.this.bg_frame != null) {
                        VideoPlayActivity.this.bg_frame.setVisibility(0);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };
    /* access modifiers changed from: private */
    public UvcCamera mUvcCamera;
    private TextView play_speed;
    ImageView rew = null;
    /* access modifiers changed from: private */
    public SeekBar seekbar;
    private TextView video_time;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetPreview(this.activityvideoplay);
        sendPlayCmd();
        initAudio();
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.activityvideoplay = (RelativeLayout) findViewById(R.id.activity_video_play);
        this.video_time = (TextView) findViewById(R.id.video_time);
        this.seekbar = (SeekBar) findViewById(R.id.seekbar);
        this.iv_play = (ImageView) findViewById(R.id.iv_play);
        this.mGLSurfaceView = (GLSurfaceView) findViewById(R.id.glsurfaceview);
        this.bg_frame = findViewById(R.id.bg_frame);
        this.play_speed = (TextView) findViewById(R.id.play_speed);
        this.rew = (ImageView) findViewById(R.id.rew);
        this.ffwd = (ImageView) findViewById(R.id.ffwd);
        this.lock = (ImageView) findViewById(R.id.lock);
        String deviceVersion = CmdManager.getInstance().getCurrentState().getPasswd();
        int startpos = deviceVersion.lastIndexOf("v");
        if (startpos >= 0) {
            String str = deviceVersion.substring(startpos, startpos + 4);
            LogUtils.d("version " + str + " deviceVersion " + deviceVersion);
            if (str.compareTo("v1.1") <= 0) {
                this.rew.setVisibility(8);
                this.ffwd.setVisibility(8);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void initData() {
        this.mUvcCamera = UvcCamera.getInstance();
        this.mFileBeans = (FileBeans) getIntent().getSerializableExtra(KEY_FILEBEAN);
        if (this.mFileBeans.fileType == 2 || !CmdManager.getInstance().isSupportBackPlayLock()) {
            this.lock.setVisibility(8);
        }
    }

    /* access modifiers changed from: protected */
    public void initListener() {
        this.mGLSurfaceView.setEGLContextClientVersion(2);
        this.mGLSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                VideoPlayActivity.this.mUvcCamera.initGles(1280, 720);
            }

            public void onSurfaceChanged(GL10 gl, int width, int height) {
                UvcCamera.getInstance().changeESLayout(width, height);
            }

            public void onDrawFrame(GL10 gl) {
                if (UvcCamera.getInstance().drawESFrame() == 0 && VideoPlayActivity.this.bg_frame.getVisibility() == 0) {
                    VideoPlayActivity.this.mHandler.sendEmptyMessage(17);
                }
            }
        });
        this.iv_play.setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        this.rew.setOnClickListener(this);
        this.ffwd.setOnClickListener(this);
        this.lock.setOnClickListener(this);
        CameraStateIml.getInstance().setOnCameraStateListner(new ICameraStateChange() {
            public void stateChange() {
                VideoPlayActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        VideoPlayActivity.this.resrefhView();
                    }
                });
            }
        });
    }

    /* access modifiers changed from: private */
    public void resrefhView() {
        CameraStateBean state = CmdManager.getInstance().getCurrentState();
        state.print();
        if (!state.isCam_sd_state()) {
            ToastUtils.showLongToast((Context) this, getString(R.string.tf_pushed_out));
            finish();
        }
        String totalTime = CameraStateUtil.secondToTimeString(state.getTotal_time());
        String curTime = CameraStateUtil.secondToTimeString(state.getCur_time());
        this.seekbar.setMax(state.getTotal_time());
        this.seekbar.setProgress(state.getCur_time());
        this.video_time.setText(curTime + "/" + totalTime);
        this.iv_play.setImageResource(state.isCam_play_state() ? R.mipmap.preview_stop_nomal : R.mipmap.preview_play_nomal);
        if (state.getPlaySpeed() == 4 || !this.isDownFastButton) {
            this.play_speed.setVisibility(8);
            return;
        }
        this.play_speed.setVisibility(0);
        this.play_speed.setText(formatPlaySpeed(state.getPlaySpeed()));
    }

    public void initAudio() {
        UvcCamera.getInstance().setFrameCallback(this.mFrameCallback);
        try {
            this.buf_size = AudioTrack.getMinBufferSize(16000, 2, 2);
            this.mAudioTrack = new AudioTrack(3, 16000, 2, 2, this.buf_size * 4, 1);
            this.mAudioTrack.play();
        } catch (Exception excetion) {
            excetion.printStackTrace();
        }
    }

    private String formatPlaySpeed(int playSpeed) {
        StringBuffer str = new StringBuffer();
        if (playSpeed < 4) {
            str.append(getString(R.string.fast_back));
        } else {
            str.append(getString(R.string.fast_forward));
        }
        str.append("×");
        if (playSpeed < 4) {
            str.append("" + (4 - playSpeed));
        } else {
            str.append("" + (playSpeed - 4));
        }
        return str.toString();
    }

    private void sendPlayCmd() {
        if (this.mFileBeans == null) {
            LogUtils.e("mFileBeans == null");
        } else {
            new Thread(new Runnable() {
                public void run() {
                    CmdManager.getInstance().setPlayBackFile(VideoPlayActivity.this.mFileBeans.fileName);
                    CmdManager.getInstance().playPlayBackFile();
                }
            }).start();
        }
    }

    private void startCamera() {
        new Thread(new Runnable() {
            public void run() {
                if (!VideoPlayActivity.this.mUvcCamera.isInit()) {
                    VideoPlayActivity.this.mUvcCamera.setPkgName("com.fvision.camera");
                    VideoPlayActivity.this.mUvcCamera.initUvccamera();
                }
                if (VideoPlayActivity.this.mUvcCamera.isInit()) {
                    if (!VideoPlayActivity.this.mUvcCamera.isPreviewing()) {
                        VideoPlayActivity.this.mUvcCamera.startPreview();
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public int getContentViewId() {
        return R.layout.activity_video_play;
    }

    /* access modifiers changed from: protected */
    public void init() {
    }

    private void togglePlay() {
        final boolean isPlaying = CmdManager.getInstance().getCurrentState().isCam_play_state();
        new Thread(new Runnable() {
            public void run() {
                if (isPlaying) {
                    int unused = VideoPlayActivity.this.lastBtnType = 0;
                    CmdManager.getInstance().pausePlayBackFile();
                    return;
                }
                if (VideoPlayActivity.this.seekbar.getProgress() == 0) {
                    boolean unused2 = VideoPlayActivity.this.isDownFastButton = false;
                    CmdManager.getInstance().setPlayBackFile(VideoPlayActivity.this.mFileBeans.fileName);
                }
                int unused3 = VideoPlayActivity.this.lastBtnType = 1;
                CmdManager.getInstance().playPlayBackFile();
            }
        }).start();
        CameraStateIml.getInstance().setOnCameraStateOnlyOnceListner(new ICameraStateChange() {
            public void stateChange() {
                boolean isPlaying = CmdManager.getInstance().getCurrentState().isCam_play_state();
                int sava_1 = CmdManager.getInstance().getCurrentState().getSave_1();
                if (VideoPlayActivity.this.lastBtnType == 1 && !isPlaying && sava_1 == 3) {
                    new Thread(new Runnable() {
                        public void run() {
                            CmdManager.getInstance().playPlayBackFile();
                        }
                    }).start();
                }
            }
        });
    }

    private void lock() {
        if (!this.isLock) {
            if (CmdManager.getInstance().lockBackPlay(this.mFileBeans.fileName)) {
                ToastUtils.showLongToast(getApplicationContext(), getString(R.string.lock_success));
                this.lock.setBackgroundResource(R.mipmap.lock_press);
                this.isLock = true;
                return;
            }
            this.isLock = false;
            ToastUtils.showLongToast(getApplicationContext(), getString(R.string.lock_fail));
        }
    }

    private void back() {
        Intent intent = new Intent(this, PlaybackActivity.class);
        intent.putExtra(KEY_IS_LOCK, this.isLock);
        intent.putExtra(KEY_FILE_BEAN, this.mFileBeans);
        setResult(11, intent);
        finish();
    }

    private synchronized void releaseAudioTrack() {
        this.mAudioTrack.stop();
        this.mAudioTrack.release();
        new Thread(new Runnable() {
            public void run() {
                CmdManager.getInstance().pausePlayBackFile();
            }
        }).start();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                releaseAudioTrack();
                back();
                return;
            case R.id.rew:
                this.isDownFastButton = true;
                CmdManager.getInstance().fastBackward();
                return;
            case R.id.iv_play:
                togglePlay();
                return;
            case R.id.ffwd:
                this.isDownFastButton = true;
                CmdManager.getInstance().fastForward();
                return;
            case R.id.lock:
                lock();
                return;
            default:
                return;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        back();
        return true;
    }
}
