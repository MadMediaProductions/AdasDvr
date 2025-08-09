package com.fvision.camera.view.customview;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.fvision.camera.R;
import com.fvision.camera.service.ForegroundService;
import com.fvision.camera.utils.SharedPreferencesUtil;

public class DialogAdasSetting extends BaseDialog {
    private ToggleButton adas_switch;
    private RadioGroup adas_warn_rg;
    /* access modifiers changed from: private */
    public OnAdasSetListener mOnAdasSetListener;
    private LinearLayout set_adas_adjust_view;
    private ToggleButton set_warn_fcw_switch;
    private ToggleButton set_warn_ldw_switch;
    private ToggleButton set_warn_stopgo_switch;
    private SeekBar warn_fcw_speed_bar;
    /* access modifiers changed from: private */
    public TextView warn_fcw_speed_tv;
    /* access modifiers changed from: private */
    public RadioButton warn_high_rb;
    private SeekBar warn_ldw_speed_bar;
    /* access modifiers changed from: private */
    public TextView warn_ldw_speed_tv;
    /* access modifiers changed from: private */
    public RadioButton warn_low_rb;
    /* access modifiers changed from: private */
    public RadioButton warn_middle_rb;

    public interface OnAdasSetListener {
        void adasAdjust(boolean z);

        void adasToggle(boolean z);
    }

    @SuppressLint({"ValidFragment"})
    public DialogAdasSetting(ForegroundService service) {
        super(service);
    }

    public DialogAdasSetting() {
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.settings_adas, (ViewGroup) null);
        builder.setView(view);
        initView(view);
        initDefultValue();
        initListner();
        this.mIsInit = true;
        return builder.create();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(1, R.style.MyDialog);
    }

    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) getResources().getDimension(R.dimen.settings_dialog_width);
        lp.height = (int) getResources().getDimension(R.dimen.settings_dialog_height);
        lp.dimAmount = 0.0f;
        window.setAttributes(lp);
    }

    private void initView(View view) {
        this.set_adas_adjust_view = (LinearLayout) view.findViewById(R.id.set_adas_adjust_view);
        this.warn_ldw_speed_tv = (TextView) view.findViewById(R.id.warn_ldw_speed_tv);
        this.warn_fcw_speed_tv = (TextView) view.findViewById(R.id.warn_fcw_speed_tv);
        this.adas_warn_rg = (RadioGroup) view.findViewById(R.id.adas_warn_rg);
        this.warn_low_rb = (RadioButton) view.findViewById(R.id.warn_low_rb);
        this.warn_middle_rb = (RadioButton) view.findViewById(R.id.warn_middle_rb);
        this.warn_high_rb = (RadioButton) view.findViewById(R.id.warn_high_rb);
        this.set_warn_ldw_switch = (ToggleButton) view.findViewById(R.id.set_warn_ldw_switch);
        this.set_warn_fcw_switch = (ToggleButton) view.findViewById(R.id.set_warn_fcw_switch);
        this.adas_switch = (ToggleButton) view.findViewById(R.id.adas_switch);
        this.set_warn_stopgo_switch = (ToggleButton) view.findViewById(R.id.set_warn_stopgo_switch);
        this.warn_ldw_speed_bar = (SeekBar) view.findViewById(R.id.warn_ldw_speed_bar);
        this.warn_fcw_speed_bar = (SeekBar) view.findViewById(R.id.warn_fcw_speed_bar);
    }

    /* access modifiers changed from: private */
    public void enableView(boolean isEnable) {
        boolean z;
        boolean z2;
        boolean z3 = true;
        if (isEnable) {
            if (this.mService != null && this.mService.getAdasInterfaceImp() != null && this.mService.getAdasInterfaceImp().getAdasConfig() != null) {
                this.adas_switch.setChecked(SharedPreferencesUtil.getAdasToggle(this.mService));
                int ldwMinVelocity = this.mService.getAdasInterfaceImp().getAdasConfig().getLdwMinVelocity();
                int fcwMinVelocity = this.mService.getAdasInterfaceImp().getAdasConfig().getFcwMinVelocity();
                this.warn_ldw_speed_tv.setText(String.format(getString(R.string.warn_ldw_speed), new Object[]{Integer.valueOf(ldwMinVelocity)}));
                this.warn_fcw_speed_tv.setText(String.format(getString(R.string.warn_fcw_speed), new Object[]{Integer.valueOf(fcwMinVelocity)}));
                this.warn_ldw_speed_bar.setProgress(ldwMinVelocity);
                this.warn_fcw_speed_bar.setProgress(fcwMinVelocity);
                Log.e("enableView", "" + this.mService.getAdasInterfaceImp().getAdasConfig().getIsFcwEnable());
                ToggleButton toggleButton = this.set_warn_ldw_switch;
                if (this.mService.getAdasInterfaceImp().getAdasConfig().getIsLdwEnable() == 1) {
                    z = true;
                } else {
                    z = false;
                }
                toggleButton.setChecked(z);
                ToggleButton toggleButton2 = this.set_warn_fcw_switch;
                if (this.mService.getAdasInterfaceImp().getAdasConfig().getIsFcwEnable() == 1) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                toggleButton2.setChecked(z2);
                ToggleButton toggleButton3 = this.set_warn_stopgo_switch;
                if (this.mService.getAdasInterfaceImp().getAdasConfig().getIsStopgoEnable() != 1) {
                    z3 = false;
                }
                toggleButton3.setChecked(z3);
                switch (this.mService.getAdasInterfaceImp().getAdasConfig().getFcwSensitivity()) {
                    case 0:
                        this.adas_warn_rg.check(R.id.warn_low_rb);
                        break;
                    case 1:
                        this.adas_warn_rg.check(R.id.warn_middle_rb);
                        break;
                    case 2:
                        this.adas_warn_rg.check(R.id.warn_high_rb);
                        break;
                }
            } else {
                return;
            }
        }
        this.set_adas_adjust_view.setEnabled(isEnable);
        this.warn_ldw_speed_tv.setEnabled(isEnable);
        this.warn_fcw_speed_tv.setEnabled(isEnable);
        this.adas_warn_rg.setEnabled(isEnable);
        this.warn_low_rb.setEnabled(isEnable);
        this.warn_middle_rb.setEnabled(isEnable);
        this.warn_high_rb.setEnabled(isEnable);
        this.set_warn_ldw_switch.setEnabled(isEnable);
        this.set_warn_fcw_switch.setEnabled(isEnable);
        this.set_warn_stopgo_switch.setEnabled(isEnable);
        this.warn_ldw_speed_bar.setEnabled(isEnable);
        this.warn_fcw_speed_bar.setEnabled(isEnable);
    }

    private void initListner() {
        this.adas_warn_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int lever = -1;
                if (checkedId == DialogAdasSetting.this.warn_low_rb.getId()) {
                    lever = 0;
                } else if (checkedId == DialogAdasSetting.this.warn_middle_rb.getId()) {
                    lever = 1;
                } else if (checkedId == DialogAdasSetting.this.warn_high_rb.getId()) {
                    lever = 2;
                }
                DialogAdasSetting.this.mService.getAdasInterfaceImp().setAdasSensor(lever);
                SharedPreferencesUtil.setAdasSensor(DialogAdasSetting.this.getActivity(), lever);
            }
        });
        SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (DialogAdasSetting.this.mIsInit) {
                    switch (seekBar.getId()) {
                        case R.id.warn_ldw_speed_bar:
                            int ldwDistance = progress;
                            DialogAdasSetting.this.warn_ldw_speed_tv.setText(String.format(DialogAdasSetting.this.getString(R.string.warn_ldw_speed), new Object[]{Integer.valueOf(ldwDistance)}));
                            SharedPreferencesUtil.setLdwMinVelocity(DialogAdasSetting.this.getActivity(), ldwDistance);
                            return;
                        case R.id.warn_fcw_speed_bar:
                            int fcwDistance = progress;
                            DialogAdasSetting.this.warn_fcw_speed_tv.setText(String.format(DialogAdasSetting.this.getString(R.string.warn_fcw_speed), new Object[]{Integer.valueOf(fcwDistance)}));
                            SharedPreferencesUtil.setLdwMinVelocity(DialogAdasSetting.this.getActivity(), fcwDistance);
                            return;
                        default:
                            return;
                    }
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (DialogAdasSetting.this.mIsInit) {
                    switch (seekBar.getId()) {
                        case R.id.warn_ldw_speed_bar:
                            int ldwDistance = seekBar.getProgress();
                            DialogAdasSetting.this.mService.getAdasInterfaceImp().setLdwMinVelocity(ldwDistance);
                            SharedPreferencesUtil.setLdwMinVelocity(DialogAdasSetting.this.getActivity(), ldwDistance);
                            return;
                        case R.id.warn_fcw_speed_bar:
                            int fcwDistance = seekBar.getProgress();
                            DialogAdasSetting.this.mService.getAdasInterfaceImp().setFcwMinVelocity(fcwDistance);
                            SharedPreferencesUtil.setFcwMinVelocity(DialogAdasSetting.this.getActivity(), fcwDistance);
                            return;
                        default:
                            return;
                    }
                }
            }
        };
        this.warn_ldw_speed_bar.setOnSeekBarChangeListener(seekListener);
        this.warn_fcw_speed_bar.setOnSeekBarChangeListener(seekListener);
        CompoundButton.OnCheckedChangeListener toggleListner = new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.adas_switch:
                        if (DialogAdasSetting.this.mOnAdasSetListener != null) {
                            DialogAdasSetting.this.mOnAdasSetListener.adasToggle(isChecked);
                        }
                        Log.e("adasSetting", "" + isChecked);
                        SharedPreferencesUtil.setAdasToggle(DialogAdasSetting.this.getActivity(), isChecked);
                        DialogAdasSetting.this.enableView(isChecked);
                        return;
                    case R.id.set_warn_ldw_switch:
                        DialogAdasSetting.this.mService.getAdasInterfaceImp().setLdwEnable(isChecked);
                        SharedPreferencesUtil.setLdwEnable(DialogAdasSetting.this.getActivity(), isChecked);
                        return;
                    case R.id.set_warn_fcw_switch:
                        DialogAdasSetting.this.mService.getAdasInterfaceImp().setFcwEnable(isChecked);
                        SharedPreferencesUtil.setFcwEnable(DialogAdasSetting.this.getActivity(), isChecked);
                        return;
                    case R.id.set_warn_stopgo_switch:
                        DialogAdasSetting.this.mService.getAdasInterfaceImp().setStgEnable(isChecked);
                        SharedPreferencesUtil.setStgEnable(DialogAdasSetting.this.getActivity(), isChecked);
                        return;
                    default:
                        return;
                }
            }
        };
        this.set_warn_ldw_switch.setOnCheckedChangeListener(toggleListner);
        this.set_warn_fcw_switch.setOnCheckedChangeListener(toggleListner);
        this.set_warn_stopgo_switch.setOnCheckedChangeListener(toggleListner);
        this.adas_switch.setOnCheckedChangeListener(toggleListner);
        this.set_adas_adjust_view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (DialogAdasSetting.this.mOnAdasSetListener != null) {
                    DialogAdasSetting.this.mOnAdasSetListener.adasAdjust(true);
                }
                DialogAdasSetting.this.dismiss();
            }
        });
    }

    private void initDefultValue() {
        Log.e("initDefultValue", "" + SharedPreferencesUtil.getAdasToggle(getActivity()));
        enableView(SharedPreferencesUtil.getAdasToggle(getActivity()));
    }

    public void setOnAdasSetListener(OnAdasSetListener listener) {
        this.mOnAdasSetListener = listener;
    }
}
