package com.fvision.camera.view.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.fvision.camera.R;

public class DevUpgradeDialog extends Dialog {
    public final int WHAT_AUTO_HIDE = 1;
    private LinearLayout cancelLayout;
    private TextView content = null;
    private Button dialogCancel = null;
    private Button dialogOk = null;
    LayoutInflater inflater;
    private Context mContext;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (DevUpgradeDialog.this.timeOutListener != null) {
                        DevUpgradeDialog.this.timeOutListener.onClick((View) null);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };
    /* access modifiers changed from: private */
    public OnFloatWindonsClickLinteners mOnCancelClick = null;
    private ProgressBar mProgressBar;
    private LinearLayout okLayout;
    /* access modifiers changed from: private */
    public View.OnClickListener timeOutListener;
    private TextView title = null;
    private View view;

    public interface OnFloatWindonsClickLinteners {
        void onCancel(View view);

        void onOk(View view);
    }

    public DevUpgradeDialog(Context context) {
        super(context, R.style.dialog);
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.view = this.inflater.inflate(R.layout.float_windows_tips_dialog_new, (ViewGroup) null);
        setContentView(this.view);
        setCancelable(false);
        initView();
    }

    private void initView() {
        this.title = (TextView) this.view.findViewById(R.id.title);
        this.content = (TextView) this.view.findViewById(R.id.content);
        this.dialogCancel = (Button) this.view.findViewById(R.id.cancel);
        this.cancelLayout = (LinearLayout) this.view.findViewById(R.id.cancel_layout);
        this.okLayout = (LinearLayout) this.view.findViewById(R.id.ok_layout);
        this.mProgressBar = (ProgressBar) this.view.findViewById(R.id.progressBar1);
        this.cancelLayout.setVisibility(8);
        this.dialogOk = (Button) this.view.findViewById(R.id.ok);
        this.okLayout.setVisibility(8);
        this.title.setText(R.string.dialog_format_title_prompt);
        this.dialogOk.setText(R.string.ok);
        this.dialogCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DevUpgradeDialog.this.dismiss();
                if (DevUpgradeDialog.this.mOnCancelClick != null) {
                    DevUpgradeDialog.this.mOnCancelClick.onCancel(view);
                }
            }
        });
        this.dialogOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DevUpgradeDialog.this.dismiss();
                if (DevUpgradeDialog.this.mOnCancelClick != null) {
                    DevUpgradeDialog.this.mOnCancelClick.onOk(view);
                }
            }
        });
    }

    public void setOnFloatWindonsClickLinteners(OnFloatWindonsClickLinteners onCancelClick) {
        this.mOnCancelClick = onCancelClick;
    }

    public void show() {
        super.show();
        clearTimeOutHide();
    }

    public void timeOutHide(long time) {
        clearTimeOutHide();
        this.mHandler.sendEmptyMessageDelayed(1, time);
    }

    public void timeOutHide(long time, View.OnClickListener listener) {
        this.timeOutListener = listener;
        clearTimeOutHide();
        this.mHandler.sendEmptyMessageDelayed(1, time);
    }

    public void clearTimeOutHide() {
        this.mHandler.removeMessages(1);
    }

    public void setTitle(String msg) {
        this.title.setText(msg);
    }

    public String getTitle() {
        return this.title.getText().toString();
    }

    public void setTitle(int strid) {
        this.title.setText(strid);
    }

    public void setContent(String msg) {
        this.content.setText(msg);
    }

    public void showBtnOk() {
        this.okLayout.setVisibility(0);
    }

    public void hideBtnOk() {
        this.okLayout.setVisibility(8);
    }

    public void hideProgress() {
        this.mProgressBar.setVisibility(8);
    }

    public void showProgress() {
        this.mProgressBar.setVisibility(0);
    }

    public void dismiss() {
        super.dismiss();
    }
}
