package com.fvision.camera.view.activity;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fvision.camera.App;
import com.fvision.camera.R;
import com.fvision.camera.base.BaseActivity;
import com.fvision.camera.bean.FileBeans;
import com.fvision.camera.iface.IProgressBack;
import com.fvision.camera.manager.CmdManager;
import com.fvision.camera.manager.VideoDownloadManager;
import com.fvision.camera.utils.CameraStateUtil;
import com.fvision.camera.utils.CmdUtil;
import com.fvision.camera.utils.LogUtils;
import com.fvision.camera.utils.ToastUtils;
import com.fvision.camera.view.fragment.VideoListFragment;
import com.huiying.cameramjpeg.UvcCamera;

public class PlaybackActivity extends BaseActivity implements VideoListFragment.OnListFragmentInteractionListener, View.OnClickListener {
    public static final int REQUEST_VIDEO_PLAY = 11;
    private LinearLayout activityplayback;
    private ImageView back;
    private FrameLayout content;
    private byte[] files = null;
    private VideoListFragment fragment_lock;
    private VideoListFragment fragment_normal;
    private VideoListFragment fragment_photo;
    int i = 0;
    private long length;
    private VideoListFragment mCurrentFragment;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    PlaybackActivity.this.showProgress(msg.arg1);
                    return;
                default:
                    return;
            }
        }
    };
    private ProgressDialog mProgressDialog;
    private TextView recordlock;
    private TextView recordnormal;
    private View rootview;
    private TextView takepicture;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d("PlaybackActivity " + CmdManager.getInstance().getCurrentState().toString());
        resetPreview(this.rootview);
        loadFile();
        initProgressDialog();
        if (UvcCamera.getInstance().isInit()) {
            initVideo();
        } else if (!isFinishing()) {
            finish();
        }
    }

    public byte[] loadFile() {
        int fileSize = CmdManager.getInstance().getCurrentState().getFile_index();
        boolean isPlistState = CmdManager.getInstance().getCurrentState().isCam_plist_state();
        LogUtils.e(" isPlistState " + isPlistState);
        if (fileSize >= 0 || isPlistState) {
            this.files = new byte[(fileSize * 6)];
            CmdManager.getInstance().getFiles(this.files);
            LogUtils.d("PlaybackActivity  fileByte " + CameraStateUtil.bytesToHexString(this.files));
            return this.files;
        }
        finish();
        return null;
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.takepicture = (TextView) findViewById(R.id.take_picture);
        this.back = (ImageView) findViewById(R.id.back);
        this.activityplayback = (LinearLayout) findViewById(R.id.activity_playback);
        this.content = (FrameLayout) findViewById(R.id.content);
        this.recordlock = (TextView) findViewById(R.id.record_lock);
        this.recordnormal = (TextView) findViewById(R.id.record_normal);
        this.rootview = findViewById(R.id.rootview);
    }

    /* access modifiers changed from: protected */
    public void initListener() {
        this.recordlock.setOnClickListener(this);
        this.recordnormal.setOnClickListener(this);
        this.takepicture.setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    /* access modifiers changed from: protected */
    public void initData() {
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (!CmdManager.getInstance().getCurrentState().isCam_sd_state()) {
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public int getContentViewId() {
        return R.layout.activity_playback;
    }

    /* access modifiers changed from: protected */
    public void init() {
    }

    private void initVideo() {
        this.fragment_normal = VideoListFragment.newInstance(this.files, 0);
        this.fragment_lock = VideoListFragment.newInstance(this.files, 2);
        this.fragment_photo = VideoListFragment.newInstance(this.files, 1);
        this.recordnormal.performClick();
    }

    private void showFragment(Fragment fragment) {
        this.mCurrentFragment = (VideoListFragment) fragment;
        getFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
    }

    public void onListFragmentInteraction(final FileBeans item) {
        if (CmdUtil.versionCompareTo("4.0") <= 0) {
            Log.e("msg", "1111");
            if (item.fileType == 2 || item.fileType == 0) {
                Intent playfile = new Intent(this, VideoPlayActivity.class);
                playfile.putExtra(VideoPlayActivity.KEY_FILEBEAN, item);
                startActivityForResult(playfile, 11);
            } else if (item.fileType == 1) {
                String path = App.JPG_PATH + item.fileName;
                Log.e("msg", "1111" + path);
                openPhoto1(path);
            }
        } else if (item.fileType == 2 || item.fileType == 0) {
            Intent playfile2 = new Intent(this, VideoPlayActivity.class);
            playfile2.putExtra(VideoPlayActivity.KEY_FILEBEAN, item);
            startActivityForResult(playfile2, 11);
        } else if (item.fileType == 1) {
            VideoDownloadManager.getInstance().setDownloadDir(App.JPG_PATH);
            VideoDownloadManager.getInstance().downloadFileThread(item.fileName, new IProgressBack() {
                public void onFail(int i, String s) {
                    if (i == 0) {
                        PlaybackActivity.this.openPhoto1(VideoDownloadManager.getInstance().getDownloadDir() + item.fileName);
                    }
                }

                public void onProgress(float v) {
                }

                public void onSuccess(String picturePath) {
                    Log.e("openPhoto1", "" + picturePath);
                    PlaybackActivity.this.openPhoto1(picturePath);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void openPhoto1(String path) {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra(PhotoActivity.KEY_CURRENT_PHOTO, path);
        startActivity(intent);
    }

    public void onDownLoad(FileBeans item) {
        if (item != null) {
            new Thread(new Runnable() {
                public void run() {
                }
            }).start();
        }
    }

    public void onDelete(FileBeans item) {
        if (this.mCurrentFragment != null) {
            this.mCurrentFragment.showDeleteFile(item);
        }
    }

    private void initProgressDialog() {
        this.mProgressDialog = new ProgressDialog(this);
        this.mProgressDialog.setProgressStyle(1);
        this.mProgressDialog.setCancelable(false);
        this.mProgressDialog.setMax(100);
    }

    /* access modifiers changed from: private */
    public void showProgress(int progress) {
        Log.e("ondownloading", "progress=" + progress);
        if (this.mProgressDialog != null) {
            this.mProgressDialog.setProgress(progress);
            this.mProgressDialog.show();
        }
    }

    private void hideProgress() {
        if (this.mProgressDialog != null) {
            this.mProgressDialog.dismiss();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                return;
            case R.id.record_normal:
                showFragment(this.fragment_normal);
                this.recordnormal.setSelected(true);
                this.recordlock.setSelected(false);
                this.takepicture.setSelected(false);
                return;
            case R.id.record_lock:
                showFragment(this.fragment_lock);
                this.recordnormal.setSelected(false);
                this.recordlock.setSelected(true);
                this.takepicture.setSelected(false);
                return;
            case R.id.take_picture:
                showFragment(this.fragment_photo);
                this.recordnormal.setSelected(false);
                this.recordlock.setSelected(false);
                this.takepicture.setSelected(true);
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 11:
                boolean isLock = false;
                FileBeans bean = null;
                if (data != null) {
                    isLock = data.getBooleanExtra(VideoPlayActivity.KEY_IS_LOCK, false);
                    bean = (FileBeans) data.getSerializableExtra(VideoPlayActivity.KEY_FILE_BEAN);
                }
                if (isLock) {
                    ToastUtils.showLongToast(getApplicationContext(), getString(R.string.video_moved_to_lock));
                    loadFile();
                    this.fragment_normal.delItem(bean.fileName);
                    bean.fileName = bean.fileName.replace("MOV", "LOK");
                    bean.fileType = 2;
                    this.fragment_lock.addItem(bean);
                    return;
                }
                return;
            default:
                return;
        }
    }
}
