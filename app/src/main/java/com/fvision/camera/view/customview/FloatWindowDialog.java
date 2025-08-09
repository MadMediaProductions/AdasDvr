package com.fvision.camera.view.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.fvision.camera.R;

public class FloatWindowDialog extends Dialog {
    private TextView content = null;
    private Button dialogCancel = null;
    private Button dialogOk = null;
    LayoutInflater inflater;
    private Context mContext;
    /* access modifiers changed from: private */
    public OnFloatWindonsClickLinteners mOnCancelClick = null;
    private TextView title = null;
    private View view;

    public interface OnFloatWindonsClickLinteners {
        void onCancel(View view);

        void onOk(View view);
    }

    public FloatWindowDialog(Context context) {
        super(context, R.style.dialog);
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.view = this.inflater.inflate(R.layout.float_windows_tips_dialog, (ViewGroup) null);
        setContentView(this.view);
        setCancelable(false);
        initView();
    }

    private void initView() {
        this.title = (TextView) this.view.findViewById(R.id.title);
        this.content = (TextView) this.view.findViewById(R.id.content);
        this.dialogCancel = (Button) this.view.findViewById(R.id.cancel);
        this.dialogOk = (Button) this.view.findViewById(R.id.ok);
        this.content.setText(String.format(this.mContext.getString(R.string.float_windows_tips_content), new Object[]{this.mContext.getString(R.string.app_name)}));
        this.dialogCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FloatWindowDialog.this.dismiss();
                if (FloatWindowDialog.this.mOnCancelClick != null) {
                    FloatWindowDialog.this.mOnCancelClick.onCancel(view);
                }
            }
        });
        this.dialogOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FloatWindowDialog.this.dismiss();
                if (FloatWindowDialog.this.mOnCancelClick != null) {
                    FloatWindowDialog.this.mOnCancelClick.onOk(view);
                }
            }
        });
    }

    public void setOnFloatWindonsClickLinteners(OnFloatWindonsClickLinteners onCancelClick) {
        this.mOnCancelClick = onCancelClick;
    }

    public void show() {
        super.show();
    }

    public void setTitle(String msg) {
        this.title.setText(msg);
    }

    public void setTitle(int strid) {
        this.title.setText(strid);
    }

    public void dismiss() {
        super.dismiss();
    }
}
