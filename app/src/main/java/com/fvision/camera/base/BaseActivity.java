package com.fvision.camera.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import com.fvision.camera.R;
import com.fvision.camera.listener.LifeCycleListener;
import com.fvision.camera.manager.ActivityStackManager;
import com.fvision.camera.utils.ToastUtils;
import com.fvision.camera.view.customview.LoadingDialog;
import com.huiying.cameramjpeg.UvcCamera;
import java.io.File;

public abstract class BaseActivity extends Activity {
    protected Context mContext;
    protected Handler mHandler = new Handler() {
    };
    public LifeCycleListener mListener;
    public LoadingDialog progressDialog;
    private Runnable timeOutCloseLoad = new Runnable() {
        public void run() {
            BaseActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    BaseActivity.this.dismisLoadingDialog();
                }
            });
        }
    };
    private Runnable timeOutCloseLoadThread = new Runnable() {
        public void run() {
            BaseActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    String path;
                    BaseActivity.this.dismisLoadingDialog();
                    if (!UvcCamera.getInstance().cmd_fd_error.equals("Success")) {
                        String cmdErrorStr = UvcCamera.getInstance().cmd_fd_error;
                        if (!cmdErrorStr.contains("Read") || !cmdErrorStr.contains("only")) {
                            path = "ActualPath: " + BaseActivity.this.getFilesPgkName(BaseActivity.this.getFileName(UvcCamera.getInstance().getDevpath()) + "Android/data/");
                        } else {
                            path = "ActualPath: " + BaseActivity.this.getApplication().getResources().getString(R.string.problem_prompt);
                        }
                        ToastUtils.showLongToast(BaseActivity.this.getApplicationContext(), " error:" + UvcCamera.getInstance().cmd_fd_error + path);
                    }
                }
            });
        }
    };
    private Runnable timeOutCloseLoadingThread = new Runnable() {
        public void run() {
            BaseActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    BaseActivity.this.dismisLoadingDialog();
                    if (!UvcCamera.getInstance().cmd_fd_error.equals("Success")) {
                        ToastUtils.showLongToast(BaseActivity.this.getApplicationContext(), "" + UvcCamera.getInstance().cmd_fd_error);
                    }
                }
            });
        }
    };

    /* access modifiers changed from: protected */
    public abstract int getContentViewId();

    /* access modifiers changed from: protected */
    public abstract void init();

    /* access modifiers changed from: protected */
    public abstract void initData();

    /* access modifiers changed from: protected */
    public abstract void initListener();

    /* access modifiers changed from: protected */
    public abstract void initView();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLoadingDialog();
        if (this.mListener != null) {
            this.mListener.onCreate(savedInstanceState);
        }
        ActivityStackManager.getManager().push(this);
        setContentView(getContentViewId());
        this.mContext = this;
        initView();
        initData();
        initListener();
        init();
    }

    /* access modifiers changed from: protected */
    public void resetPreview(View rootview) {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        if (point.x < point.y) {
            FrameLayout.LayoutParams layoutparams = (FrameLayout.LayoutParams) rootview.getLayoutParams();
            layoutparams.width = point.x;
            layoutparams.height = (point.x * 11) / 16;
            layoutparams.gravity = 16;
            Log.e("Mainactivity", "width=" + layoutparams.width + ",height=" + layoutparams.height);
            rootview.setLayoutParams(layoutparams);
        }
    }

    private void initLoadingDialog() {
        this.progressDialog = new LoadingDialog(this);
        this.progressDialog.setCancelable(false);
    }

    public void showLoadingDialog() {
        this.progressDialog.setMessage("请稍后...");
        this.progressDialog.show();
    }

    public void timeOutCloseLoadingDialog(long time) {
        this.mHandler.postDelayed(this.timeOutCloseLoadThread, time);
    }

    public void timeOutCloseLoading(long time) {
        this.mHandler.postDelayed(this.timeOutCloseLoadingThread, time);
    }

    public String getFileName(String pathandname) {
        int end = pathandname.lastIndexOf("/");
        if (end != -1) {
            return pathandname.substring(0, end + 1);
        }
        return null;
    }

    public String getFilePgkName(String pathandname) {
        String[] strBuffer = pathandname.split("/");
        try {
            return strBuffer[strBuffer.length - 1];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFilesName(String path) {
        File[] files = new File(path).listFiles();
        if (files == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (File absolutePath : files) {
            sb.append(getFilePgkName(absolutePath.getAbsolutePath()));
        }
        return sb.toString();
    }

    public String getFilesPgkName(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return getApplication().getResources().getString(R.string.problem_prompt);
            }
            File[] files = file.listFiles();
            if (files == null || files.length < 1) {
                return getApplication().getResources().getString(R.string.problem_prompt);
            }
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < files.length; i++) {
                String fileName = getFilePgkName(files[i].getAbsolutePath());
                if (fileName.contains("com")) {
                    sb.append(fileName + File.separator);
                    sb.append(getFilesName(files[i].getAbsolutePath()) + "*");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void dismisLoadingDialog() {
        this.mHandler.removeCallbacks(this.timeOutCloseLoadThread);
        this.progressDialog.dismiss();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        if (this.mListener != null) {
            this.mListener.onStart();
        }
    }

    /* access modifiers changed from: protected */
    public void onRestart() {
        super.onRestart();
        if (this.mListener != null) {
            this.mListener.onRestart();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (this.mListener != null) {
            this.mListener.onResume();
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (this.mListener != null) {
            this.mListener.onPause();
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        if (this.mListener != null) {
            this.mListener.onStop();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        if (this.mListener != null) {
            this.mListener.onDestroy();
        }
        ActivityStackManager.getManager().remove(this);
    }

    public void setOnLifeCycleListener(LifeCycleListener listener) {
        this.mListener = listener;
    }
}
