package com.serenegiant.usb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Spinner;
import com.huiying.uvccameralib.R;
import java.util.ArrayList;
import java.util.List;

public class CameraDialog extends DialogFragment {
    private static final String TAG = CameraDialog.class.getSimpleName();
    private DeviceListAdapter mDeviceListAdapter;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case 16908315:
                    CameraDialog.this.updateDevices();
                    return;
                default:
                    return;
            }
        }
    };
    private final DialogInterface.OnClickListener mOnDialogClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case -2:
                    ((CameraDialogParent) CameraDialog.this.getActivity()).onDialogResult(true);
                    return;
                case -1:
                    Object item = CameraDialog.this.mSpinner.getSelectedItem();
                    if (item instanceof UsbDevice) {
                        CameraDialog.this.mUSBMonitor.requestPermission((UsbDevice) item);
                        ((CameraDialogParent) CameraDialog.this.getActivity()).onDialogResult(false);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };
    /* access modifiers changed from: private */
    public Spinner mSpinner;
    protected USBMonitor mUSBMonitor;

    public interface CameraDialogParent {
        USBMonitor getUSBMonitor();

        void onDialogResult(boolean z);
    }

    public static CameraDialog showDialog(Activity parent) {
        CameraDialog dialog = newInstance();
        try {
            dialog.show(parent.getFragmentManager(), TAG);
            return dialog;
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public static CameraDialog newInstance() {
        CameraDialog dialog = new CameraDialog();
        dialog.setArguments(new Bundle());
        return dialog;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (this.mUSBMonitor == null) {
            try {
                this.mUSBMonitor = ((CameraDialogParent) activity).getUSBMonitor();
            } catch (ClassCastException | NullPointerException e) {
            }
        }
        if (this.mUSBMonitor == null) {
            throw new ClassCastException(activity.toString() + " must implement CameraDialogParent#getUSBController");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle savedInstanceState2 = getArguments();
        }
    }

    public void onSaveInstanceState(Bundle saveInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            saveInstanceState.putAll(args);
        }
        super.onSaveInstanceState(saveInstanceState);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(initView());
        builder.setTitle(R.string.select);
        builder.setPositiveButton(17039370, this.mOnDialogClickListener);
        builder.setNegativeButton(17039360, this.mOnDialogClickListener);
        builder.setNeutralButton(R.string.refresh, (DialogInterface.OnClickListener) null);
        Dialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    private final View initView() {
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_camera, (ViewGroup) null);
        this.mSpinner = (Spinner) rootView.findViewById(R.id.spinner1);
        this.mSpinner.setEmptyView(rootView.findViewById(16908292));
        return rootView;
    }

    public void onResume() {
        super.onResume();
        updateDevices();
        Button button = (Button) getDialog().findViewById(16908315);
        if (button != null) {
            button.setOnClickListener(this.mOnClickListener);
        }
    }

    public void onCancel(DialogInterface dialog) {
        ((CameraDialogParent) getActivity()).onDialogResult(true);
        super.onCancel(dialog);
    }

    public void updateDevices() {
        this.mDeviceListAdapter = new DeviceListAdapter(getActivity(), this.mUSBMonitor.getDeviceList(DeviceFilter.getDeviceFilters(getActivity(), R.xml.device_filter).get(0)));
        this.mSpinner.setAdapter(this.mDeviceListAdapter);
    }

    private static final class DeviceListAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private final List<UsbDevice> mList;

        public DeviceListAdapter(Context context, List<UsbDevice> list) {
            this.mInflater = LayoutInflater.from(context);
            this.mList = list == null ? new ArrayList<>() : list;
        }

        public int getCount() {
            return this.mList.size();
        }

        public UsbDevice getItem(int position) {
            if (position < 0 || position >= this.mList.size()) {
                return null;
            }
            return this.mList.get(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = this.mInflater.inflate(R.layout.listitem_device, parent, false);
            }
            if (convertView instanceof CheckedTextView) {
                UsbDevice device = getItem(position);
                ((CheckedTextView) convertView).setText(String.format("UVC Camera:(%x:%x:%s)", new Object[]{Integer.valueOf(device.getVendorId()), Integer.valueOf(device.getProductId()), device.getDeviceName()}));
            }
            return convertView;
        }
    }
}
