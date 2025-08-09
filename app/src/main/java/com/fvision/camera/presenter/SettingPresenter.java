package com.fvision.camera.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import com.adasplus.adas.adas.AdasConstants;
import com.fvision.camera.App;
import com.fvision.camera.R;
import com.fvision.camera.base.BasePresenter;
import com.fvision.camera.manager.CmdManager;
import com.fvision.camera.manager.SoundManager;
import com.fvision.camera.util.FileUtils;
import com.fvision.camera.utils.LogUtils;
import com.fvision.camera.utils.SharedPreferencesUtil;
import com.fvision.camera.utils.ToastUtils;
import com.fvision.camera.view.activity.SettingActivity;
import com.fvision.camera.view.iface.ISettingView;
import java.io.File;
import pub.devrel.easypermissions.EasyPermissions;

public class SettingPresenter extends BasePresenter<ISettingView, SettingActivity> {
    public static final String[] PERMISSIONS = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};
    boolean adasIsAuth = false;
    private ISettingView iView;
    /* access modifiers changed from: private */
    public SettingActivity mActivity;

    public SettingPresenter(ISettingView view, SettingActivity activity) {
        super(view, activity);
        this.mActivity = activity;
        this.iView = view;
    }

    public boolean snapShotVoiceClick() {
        boolean z;
        boolean snapshot_voice = SharedPreferencesUtil.getSnapShotSound(this.mActivity);
        SettingActivity settingActivity = this.mActivity;
        if (!snapshot_voice) {
            z = true;
        } else {
            z = false;
        }
        SharedPreferencesUtil.setSnapShotSound(settingActivity, z);
        if (!snapshot_voice) {
            return true;
        }
        return false;
    }

    public boolean getSnapShotVoice() {
        return ((Boolean) SharedPreferencesUtil.getData(this.mActivity, SharedPreferencesUtil.SNAPSHOT_VOICE, true)).booleanValue();
    }

    public boolean edogIsCheck() {
        return SharedPreferencesUtil.getEdogAuthToggle(this.mActivity);
    }

    public boolean isEdogRedLightAlarm() {
        return ((Boolean) SharedPreferencesUtil.getData(this.mActivity, SharedPreferencesUtil.EDOG_RED_LIGHT, true)).booleanValue();
    }

    public boolean isEdogSpeedingAlarmSound() {
        return ((Boolean) SharedPreferencesUtil.getData(this.mActivity, SharedPreferencesUtil.EDOG_SPEEDING_ALARM, true)).booleanValue();
    }

    public boolean isEdogSecurityInfo() {
        return ((Boolean) SharedPreferencesUtil.getData(this.mActivity, SharedPreferencesUtil.EDOG_SECURITY_INFO, true)).booleanValue();
    }

    public boolean isEdogGpsFixedAlarmPoint() {
        return ((Boolean) SharedPreferencesUtil.getData(this.mActivity, SharedPreferencesUtil.EDOG_FIXED_ALARM_POINT, true)).booleanValue();
    }

    public void edogSwitchCheck() {
        boolean isn = SharedPreferencesUtil.getEdogAuthToggle(this.mActivity);
        LogUtils.d("edog " + isn);
        if (isn) {
            SharedPreferencesUtil.setEdogEnableToggle(this.mActivity, false);
            SharedPreferencesUtil.setEdogAuthToggle(this.mActivity, false);
            App.getInstance().stopTuzhiService();
            ((ISettingView) this.mView).edogResult(false, (String) null);
            return;
        }
        new Thread(new Runnable() {
            public void run() {
                boolean isCopySuccess;
                if (EasyPermissions.hasPermissions(SettingPresenter.this.mActivity, SettingPresenter.PERMISSIONS)) {
                    if (new File(App.EDOG_PATH + "map03apk.bin").exists()) {
                        isCopySuccess = true;
                    } else {
                        isCopySuccess = FileUtils.Assets2Sd(SettingPresenter.this.mActivity, "map03apk.bin", App.EDOG_PATH + "map03apk.bin");
                    }
                    if (isCopySuccess) {
                        ((ISettingView) SettingPresenter.this.mView).edogResult(true, (String) null);
                    } else {
                        ((ISettingView) SettingPresenter.this.mView).edogResult(false, (String) null);
                    }
                } else {
                    ((ISettingView) SettingPresenter.this.mView).edogResult(false, (String) null);
                    ToastUtils.showToast((Context) SettingPresenter.this.mActivity, SettingPresenter.this.mActivity.getString(R.string.no_permission));
                    EasyPermissions.requestPermissions((Activity) SettingPresenter.this.mActivity, SettingPresenter.this.mActivity.getString(R.string.app_upgrade_prompt), 0, SettingPresenter.PERMISSIONS);
                }
            }
        }).start();
    }

    public boolean edogRedLightClick() {
        boolean z;
        boolean isn = ((Boolean) SharedPreferencesUtil.getData(this.mActivity, SharedPreferencesUtil.EDOG_RED_LIGHT, true)).booleanValue();
        SettingActivity settingActivity = this.mActivity;
        if (!isn) {
            z = true;
        } else {
            z = false;
        }
        SharedPreferencesUtil.saveData(settingActivity, SharedPreferencesUtil.EDOG_RED_LIGHT, Boolean.valueOf(z));
        if (!isn) {
            return true;
        }
        return false;
    }

    public boolean edogSpeedingClick() {
        boolean z;
        boolean isn = ((Boolean) SharedPreferencesUtil.getData(this.mActivity, SharedPreferencesUtil.EDOG_SPEEDING_ALARM, true)).booleanValue();
        SettingActivity settingActivity = this.mActivity;
        if (!isn) {
            z = true;
        } else {
            z = false;
        }
        SharedPreferencesUtil.saveData(settingActivity, SharedPreferencesUtil.EDOG_SPEEDING_ALARM, Boolean.valueOf(z));
        if (!isn) {
            return true;
        }
        return false;
    }

    public void selectSoundModel() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getActivity(), 16973939);
        builder.setItems(((SettingActivity) getActivity()).getResources().getStringArray(R.array.array_sound_model), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        SharedPreferencesUtil.setSoundModel((Context) SettingPresenter.this.getActivity(), 1);
                        break;
                    case 1:
                        SharedPreferencesUtil.setSoundModel((Context) SettingPresenter.this.getActivity(), 0);
                        break;
                }
                SoundManager.getInstance().setSoundModel(SharedPreferencesUtil.getSoundModel((Context) SettingPresenter.this.getActivity()).intValue());
                ((SettingActivity) SettingPresenter.this.getActivity()).resrefhSoundModel();
            }
        });
        builder.show();
    }

    private String boolean2String(boolean isn) {
        return isn ? this.mActivity.getString(R.string.open) : this.mActivity.getString(R.string.close);
    }

    public void selectVideoDuration() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getActivity(), 16973939);
        builder.setItems(((SettingActivity) getActivity()).getResources().getStringArray(R.array.array_video_duration), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        CmdManager.getInstance().setVideoDuration(1);
                        break;
                    case 1:
                        CmdManager.getInstance().setVideoDuration(2);
                        break;
                    case 2:
                        CmdManager.getInstance().setVideoDuration(3);
                        break;
                }
                ((SettingActivity) SettingPresenter.this.getActivity()).resrefhVideoDuration();
            }
        });
        builder.show();
    }

    public boolean checkPermission(int request_code) {
        boolean hasPermiss = EasyPermissions.hasPermissions(this.mActivity, PERMISSIONS);
        if (!hasPermiss) {
            EasyPermissions.requestPermissions((Activity) this.mActivity, this.mActivity.getString(R.string.app_upgrade_prompt), request_code, PERMISSIONS);
        }
        return hasPermiss;
    }

    public void adasSwitchClick(boolean isCheck) {
        Log.e(AdasConstants.FILE_ADAS, "是否打开" + isCheck);
        if (!isCheck) {
            SharedPreferencesUtil.setAdasEnableToggle(this.mActivity, true);
        } else {
            SharedPreferencesUtil.setAdasEnableToggle(this.mActivity, false);
        }
        Log.e(AdasConstants.FILE_ADAS, "" + SharedPreferencesUtil.getAdasEnableToggle(this.mActivity));
        ((ISettingView) this.mView).authResult(SharedPreferencesUtil.getAdasEnableToggle(this.mActivity), (String) null);
    }

    public boolean getAdasSwitch() {
        return SharedPreferencesUtil.getAdasEnableToggle(this.mActivity);
    }

    public void adasIsackPlay(boolean isCheck) {
        Log.e(AdasConstants.FILE_ADAS, "后台播报" + isCheck);
        if (!isCheck) {
            SharedPreferencesUtil.setAdasIsBackPlayToggle(this.mActivity, true);
        } else {
            SharedPreferencesUtil.setAdasIsBackPlayToggle(this.mActivity, false);
        }
        ((ISettingView) this.mView).adasIsplayBack(SharedPreferencesUtil.getAdasIsBackPlayToggle(this.mActivity));
    }

    public void edogIsackPlay(boolean isCheck) {
        Log.e(AdasConstants.FILE_ADAS, "后台播报" + isCheck);
        if (!isCheck) {
            SharedPreferencesUtil.setEdogIsBackPlayToggle(this.mActivity, true);
        } else {
            SharedPreferencesUtil.setEdogIsBackPlayToggle(this.mActivity, false);
        }
        ((ISettingView) this.mView).edogIsPlayBack(SharedPreferencesUtil.getEdogIsBackPlayToggle(this.mActivity));
    }
}
