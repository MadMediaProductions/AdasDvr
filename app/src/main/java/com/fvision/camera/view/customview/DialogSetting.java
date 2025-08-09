package com.fvision.camera.view.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import com.fvision.camera.R;
import com.fvision.camera.manager.CmdManager;
import com.fvision.camera.utils.SharedPreferencesUtil;

public class DialogSetting extends Dialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public static final String KEY_SCREENSAVER = "screensaver";
    public static final String KEY_SNAPSHOTVOICE = "snapshotvoice";
    private Button btn_get_pic;
    private CheckBox cbsnapshowvoice;
    private View.OnClickListener mFormatClick;
    private RadioGroup video_duration;
    private RelativeLayout video_durtion_layout;

    public void setFormatClick(View.OnClickListener formatClick) {
        this.mFormatClick = formatClick;
    }

    public DialogSetting(Context context) {
        this(context, R.style.dialog);
    }

    public DialogSetting(Context context, int themeResId) {
        super(context, themeResId);
        this.mFormatClick = null;
        int width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_setting, (ViewGroup) null), new ViewGroup.LayoutParams(context.getResources().getDimensionPixelOffset(R.dimen.dimen_about_width), -2));
        this.cbsnapshowvoice = (CheckBox) findViewById(R.id.cb_snapshow_voice);
        getWindow().setGravity(17);
        setCancelable(true);
        init();
    }

    private void init() {
        this.cbsnapshowvoice.setChecked(SharedPreferencesUtil.isSnapSound(getContext()));
        this.cbsnapshowvoice.setOnCheckedChangeListener(this);
        this.video_durtion_layout = (RelativeLayout) findViewById(R.id.video_durtion_layout);
        this.video_duration = (RadioGroup) findViewById(R.id.video_durtion);
        if (CmdManager.getInstance().isSupportVideoDuration()) {
            this.video_durtion_layout.setVisibility(0);
        }
        switch (CmdManager.getInstance().getVideoDuration()) {
            case 1:
                this.video_duration.check(R.id.minute_1);
                break;
            case 2:
                this.video_duration.check(R.id.minute_2);
                break;
            case 3:
                this.video_duration.check(R.id.minute_3);
                break;
        }
        this.video_duration.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.minute_1:
                        CmdManager.getInstance().setVideoDuration(1);
                        return;
                    case R.id.minute_2:
                        CmdManager.getInstance().setVideoDuration(2);
                        return;
                    case R.id.minute_3:
                        CmdManager.getInstance().setVideoDuration(3);
                        return;
                    default:
                        return;
                }
            }
        });
    }

    public void onClick(View v) {
        v.getId();
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_snapshow_voice:
                SharedPreferencesUtil.setSnapSound(getContext(), isChecked);
                return;
            default:
                return;
        }
    }
}
