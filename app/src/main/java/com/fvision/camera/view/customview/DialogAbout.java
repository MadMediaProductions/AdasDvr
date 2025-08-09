package com.fvision.camera.view.customview;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.fvision.camera.R;
import com.fvision.camera.utils.GetPicUtil;

public class DialogAbout extends Dialog implements View.OnClickListener {
    private TextView appversion;
    private TextView back;
    private TextView checkDevUpdate;
    private TextView checkupdate;
    private TextView deviceversion;
    private TextView get_pic;
    private View.OnClickListener mAppUpdateClick;
    private View.OnClickListener mDevUpdateClick;
    public long time;

    public void setAppUpdateClick(View.OnClickListener AppUpdateClick) {
        this.mAppUpdateClick = AppUpdateClick;
    }

    public void setDevUpdateClick(View.OnClickListener DevUpdateClick) {
        this.mDevUpdateClick = DevUpdateClick;
    }

    public DialogAbout(Context context) {
        this(context, R.style.dialog);
    }

    public DialogAbout(Context context, int themeResId) {
        super(context, themeResId);
        this.mAppUpdateClick = null;
        this.mDevUpdateClick = null;
        this.time = System.currentTimeMillis() - 10000;
        int width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_about, (ViewGroup) null), new ViewGroup.LayoutParams(context.getResources().getDimensionPixelOffset(R.dimen.dimen_about_width), -2));
        this.back = (TextView) findViewById(R.id.back);
        this.checkupdate = (TextView) findViewById(R.id.check_update);
        this.deviceversion = (TextView) findViewById(R.id.deviceversion);
        this.appversion = (TextView) findViewById(R.id.appversion);
        this.checkDevUpdate = (TextView) findViewById(R.id.check_dev_update);
        this.get_pic = (TextView) findViewById(R.id.test_get_pic);
        getWindow().setGravity(17);
        setCanceledOnTouchOutside(false);
        setCancelable(true);
        init();
    }

    private void init() {
        this.back.setOnClickListener(this);
        this.checkupdate.setOnClickListener(this);
        this.checkDevUpdate.setOnClickListener(this);
        this.get_pic.setOnClickListener(this);
    }

    public DialogAbout setAppVersion(String appVersion) {
        this.appversion.setText(appVersion);
        return this;
    }

    public DialogAbout setDeviceVersion(String deviceVersion) {
        this.deviceversion.setText(deviceVersion);
        return this;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                dismiss();
                return;
            case R.id.check_update:
                if (this.mAppUpdateClick != null) {
                    this.mAppUpdateClick.onClick(v);
                    return;
                }
                return;
            case R.id.check_dev_update:
                if (this.mDevUpdateClick != null) {
                    this.mDevUpdateClick.onClick(v);
                    Log.e("dialogAbout", "开始点击1");
                    return;
                }
                return;
            case R.id.test_get_pic:
                Log.e("time", "" + GetPicUtil.getDate2String(this.time));
                return;
            default:
                return;
        }
    }
}
