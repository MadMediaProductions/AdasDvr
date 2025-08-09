package com.fvision.camera.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.adasplus.adas.adas.AdasConstants;
import com.fvision.camera.R;
import com.fvision.camera.base.BaseActivity;
import com.fvision.camera.bean.AdasResult;
import com.fvision.camera.manager.CmdManager;
import com.fvision.camera.presenter.SettingPresenter;
import com.fvision.camera.utils.DensityUtils;
import com.fvision.camera.utils.SharedPreferencesUtil;
import com.fvision.camera.utils.ToastUtils;
import com.fvision.camera.view.customview.SettingLayout;
import com.fvision.camera.view.iface.ISettingView;
import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends BaseActivity implements ISettingView {
    public static final int REQUEST_CODE_EDOG = 0;
    public static final int REQUEST_PERMISSIONS_CODE = 1;
    private final int WHAT_SHOW_TOAST = 4;
    List<SettingLayout.Item> items;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 4:
                    ToastUtils.showToast(SettingActivity.this.getApplicationContext(), (String) msg.obj);
                    return;
                default:
                    return;
            }
        }
    };
    SettingPresenter presenter = new SettingPresenter(this, this);
    SettingLayout s;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /* access modifiers changed from: protected */
    public int getContentViewId() {
        return R.layout.activity_setting;
    }

    /* access modifiers changed from: protected */
    public void init() {
        Log.e("msms", "设置界面");
    }

    /* access modifiers changed from: protected */
    public void initView() {
        initTitle();
        SettingLayout.IClickListener listener = new SettingLayout.IClickListener() {
            public void click(SettingLayout layout, SettingLayout.Item item) {
                switch (item.titleStrId) {
                    case R.string.adas:
                        SettingActivity.this.presenter.checkPermission(1);
                        Log.e(AdasConstants.FILE_ADAS, "设置界面点击adas");
                        SettingActivity.this.presenter.adasSwitchClick(item.isCheck);
                        break;
                    case R.string.adas_isplay_back:
                        SettingActivity.this.presenter.adasIsackPlay(item.isCheck);
                        break;
                    case R.string.edog:
                        SettingActivity.this.showLoadingDialog();
                        SettingActivity.this.presenter.edogSwitchCheck();
                        break;
                    case R.string.edog_isplay_back:
                        SettingActivity.this.presenter.edogIsackPlay(item.isCheck);
                        break;
                    case R.string.edog_setting_red_light:
                        item.isCheck = SettingActivity.this.presenter.edogRedLightClick();
                        break;
                    case R.string.edog_setting_verspeed:
                        item.isCheck = SettingActivity.this.presenter.edogSpeedingClick();
                        break;
                    case R.string.snapshot_voice:
                        item.isCheck = SettingActivity.this.presenter.snapShotVoiceClick();
                        break;
                    case R.string.sound_model:
                        SettingActivity.this.presenter.selectSoundModel();
                        break;
                    case R.string.video_duration:
                        SettingActivity.this.presenter.selectVideoDuration();
                        break;
                }
                layout.refreshItem(item);
            }
        };
        this.items = new ArrayList();
        boolean edogIsCheck = this.presenter.edogIsCheck();
        boolean adasIsCheck = this.presenter.getAdasSwitch();
        boolean edogIsBackPlayToggle = SharedPreferencesUtil.getEdogIsBackPlayToggle(this);
        boolean adasIsBackPlayToggle = SharedPreferencesUtil.getAdasIsBackPlayToggle(this);
        Log.e("edogIsCheck", ":edogIsCheck" + edogIsCheck + " adasIsCheck:" + adasIsCheck);
        String soundModel = "";
        String[] arr = getResources().getStringArray(R.array.array_sound_model);
        switch (SharedPreferencesUtil.getSoundModel(this).intValue()) {
            case 0:
                soundModel = arr[1];
                break;
            case 1:
                soundModel = arr[0];
                break;
        }
        this.items.add(new SettingLayout.Item(R.string.snapshot_voice, 0, (String) null, (String) null, true, SettingLayout.SEP.NO, this.presenter.getSnapShotVoice(), true, true, listener));
        this.items.add(new SettingLayout.Item(R.string.sound_model, 0, soundModel, (String) null, true, SettingLayout.SEP.NO, false, false, true, listener));
        if (CmdManager.getInstance().isSupportVideoDuration()) {
            String videoDuration = "";
            String[] videoDurationArr = getResources().getStringArray(R.array.array_video_duration);
            switch (CmdManager.getInstance().getVideoDuration()) {
                case 1:
                    videoDuration = videoDurationArr[0];
                    break;
                case 2:
                    videoDuration = videoDurationArr[1];
                    break;
                case 3:
                    videoDuration = videoDurationArr[2];
                    break;
            }
            this.items.add(new SettingLayout.Item(R.string.video_duration, 0, videoDuration, (String) null, true, SettingLayout.SEP.NO, false, false, true, listener));
        }
        this.items.add(new SettingLayout.Item(R.string.adas, 0, (String) null, (String) null, true, SettingLayout.SEP.AFTERICON, adasIsCheck, true, true, listener));
        this.items.add(new SettingLayout.Item(R.string.edog, 0, (String) null, (String) null, true, SettingLayout.SEP.AFTERICON, this.presenter.edogIsCheck(), true, true, listener));
        this.items.add(new SettingLayout.Item(R.string.edog_setting_red_light, 0, (String) null, (String) null, true, SettingLayout.SEP.AFTERICON, this.presenter.isEdogRedLightAlarm(), true, edogIsCheck, listener));
        this.items.add(new SettingLayout.Item(R.string.edog_setting_verspeed, 0, (String) null, (String) null, true, SettingLayout.SEP.AFTERICON, this.presenter.isEdogSpeedingAlarmSound(), true, edogIsCheck, listener));
        this.items.add(new SettingLayout.Item(R.string.edog_setting_security_info, 0, (String) null, (String) null, true, SettingLayout.SEP.AFTERICON, this.presenter.isEdogSecurityInfo(), true, edogIsCheck, listener));
        this.items.add(new SettingLayout.Item(R.string.edog_setting_gps_edog, 0, (String) null, (String) null, true, SettingLayout.SEP.NO, this.presenter.isEdogGpsFixedAlarmPoint(), true, edogIsCheck, listener));
        this.s = (SettingLayout) findViewById(R.id.svg_set);
        for (SettingLayout.Item item : this.items) {
            if (item.titleStrId == R.string.snapshot_voice) {
                this.s.addSpace(DensityUtils.dp2px(this, 12.0f));
                this.s.addHeadTips(getString(R.string.device_setting));
            }
            if (item.titleStrId == R.string.adas) {
                this.s.addSpace(DensityUtils.dp2px(this.mContext, 12.0f));
                this.s.addHeadTips(getString(R.string.adas_setting));
            }
            if (item.titleStrId == R.string.edog) {
                this.s.addSpace(DensityUtils.dp2px(this.mContext, 12.0f));
                this.s.addHeadTips(getString(R.string.edog_setting));
            }
            this.s.addItem(item);
        }
        this.s.addSpace(DensityUtils.dp2px(this.mContext, 48.0f));
    }

    private void initTitle() {
        ((TextView) findViewById(R.id.title)).setText(getString(R.string.setting));
        ((RelativeLayout) findViewById(R.id.right_layout)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void initListener() {
    }

    /* access modifiers changed from: protected */
    public void initData() {
    }

    public void showLoading() {
        showLoadingDialog();
    }

    public void closeLoading() {
        dismisLoadingDialog();
    }

    public void showToast(String msg) {
        Message message = new Message();
        message.what = 4;
        message.obj = msg;
        this.mHandler.sendMessage(message);
    }

    public void showResult(AdasResult result) {
        ToastUtils.showToast(this.mContext, result.toString());
    }

    /* access modifiers changed from: private */
    public SettingLayout.Item getItem(int titleStrId) {
        for (SettingLayout.Item item : this.items) {
            if (item.titleStrId == titleStrId) {
                return item;
            }
        }
        return null;
    }

    public void resrefhSoundModel() {
        SettingLayout.Item modelItem = getItem(R.string.sound_model);
        if (modelItem != null) {
            String soundModel = "";
            String[] arr = getResources().getStringArray(R.array.array_sound_model);
            switch (SharedPreferencesUtil.getSoundModel(this).intValue()) {
                case 0:
                    soundModel = arr[1];
                    break;
                case 1:
                    soundModel = arr[0];
                    break;
            }
            modelItem.contentStr = soundModel;
            this.s.refreshItem(modelItem);
        }
    }

    private void setAdasParamEnable(boolean isEnable) {
        for (SettingLayout.Item item : this.items) {
            switch (item.titleStrId) {
                case R.string.adas_fvd:
                case R.string.adas_lane:
                case R.string.adas_sound:
                case R.string.adas_sound_type:
                case R.string.adas_vehicle:
                    item.isEnable = isEnable;
                    this.s.refreshItem(item);
                    break;
            }
        }
    }

    public void authResult(final boolean isSuccess, String error) {
        runOnUiThread(new Runnable() {
            public void run() {
                Log.e(AdasConstants.FILE_ADAS, "回调过来的" + isSuccess);
                SettingActivity.this.getItem(R.string.adas).isCheck = isSuccess;
                if (!isSuccess) {
                    SharedPreferencesUtil.setAdasToggle(SettingActivity.this, false);
                }
                SettingActivity.this.s.refreshItem(SettingActivity.this.getItem(R.string.adas));
            }
        });
    }

    public void edogResult(final boolean isSuccess, String error) {
        closeLoading();
        SharedPreferencesUtil.setEdogAuthToggle(this.mContext, isSuccess);
        runOnUiThread(new Runnable() {
            public void run() {
                SettingActivity.this.getItem(R.string.edog).isCheck = isSuccess;
                SettingActivity.this.setEdogParamEnable(isSuccess);
                SettingActivity.this.s.refreshItem(SettingActivity.this.getItem(R.string.edog));
            }
        });
    }

    public void edogIsPlayBack(boolean isbackplay) {
        getItem(R.string.edog_isplay_back).isCheck = isbackplay;
        this.s.refreshItem(getItem(R.string.edog_isplay_back));
    }

    public void adasIsplayBack(boolean isbackplay) {
        getItem(R.string.adas_isplay_back).isCheck = isbackplay;
        this.s.refreshItem(getItem(R.string.adas_isplay_back));
    }

    /* access modifiers changed from: private */
    public void setEdogParamEnable(boolean isEnable) {
        for (SettingLayout.Item item : this.items) {
            switch (item.titleStrId) {
                case R.string.edog_setting_gps_edog:
                case R.string.edog_setting_red_light:
                case R.string.edog_setting_security_info:
                case R.string.edog_setting_verspeed:
                    item.isEnable = isEnable;
                    this.s.refreshItem(item);
                    break;
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length <= 0 || grantResults[0] != 0) {
            ToastUtils.showToast((Context) this, getString(R.string.request_rejected));
        } else {
            ToastUtils.showToast((Context) this, getString(R.string.request_granted) + " " + requestCode);
        }
    }

    public void resrefhVideoDuration() {
        SettingLayout.Item modelItem = getItem(R.string.video_duration);
        if (modelItem != null) {
            String videoDuration = "";
            String[] arr = getResources().getStringArray(R.array.array_video_duration);
            switch (CmdManager.getInstance().getVideoDuration()) {
                case 1:
                    videoDuration = arr[0];
                    break;
                case 2:
                    videoDuration = arr[1];
                    break;
                case 3:
                    videoDuration = arr[2];
                    break;
            }
            modelItem.contentStr = videoDuration;
            this.s.refreshItem(modelItem);
        }
    }
}
