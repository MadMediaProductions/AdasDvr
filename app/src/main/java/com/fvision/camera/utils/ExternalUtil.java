package com.fvision.camera.utils;

import android.content.Context;
import android.content.Intent;
import com.fvision.camera.manager.CmdManager;
import com.fvision.camera.util.LogUtils;
import com.huiying.cameramjpeg.UvcCamera;

public class ExternalUtil {
    public static final String EXTRA_CAM_STATE = "EXTRA_CAM_STATE";
    public static final String EXTRA_CAM_VERSION_STATE = "EXTRA_CAM_VERSION_STATE";
    public static final String EXTRA_DURATION = "EXTRA_DURATION";
    public static final String EXTRA_FILE = "EXTRA_FILE";
    public static final String EXTRA_GET_PIC_FROM_BACKPLAY = "EXTRA_GET_PIC_FROM_BACKPLAY";
    public static final String EXTRA_LAT = "EXTRA_LAT";
    public static final String EXTRA_LNG = "EXTRA_LNG";
    public static final String EXTRA_MEDIA_TYPE = "EXTRA_MEDIA_TYPE";
    public static final int EXTRA_MEDIA_TYPE_PIC = 0;
    public static final int EXTRA_MEDIA_TYPE_VIDEO = 1;
    public static final String EXTRA_MODEL = "EXTRA_MODEL";
    public static final String EXTRA_OP = "EXTRA_OP";
    public static final int EXTRA_OP_CLOSE_DEV = 1;
    public static final int EXTRA_OP_GET_PIC_FROM_BACKPLAY = 7;
    public static final int EXTRA_OP_OPEN_DEV = 0;
    public static final int EXTRA_OP_REMOTE_PHOTO = 2;
    public static final int EXTRA_OP_START_RECORD = 5;
    public static final int EXTRA_OP_START_REMOTE_VIDEO = 3;
    public static final int EXTRA_OP_STOP_RECORD = 6;
    public static final int EXTRA_OP_STOP_REMOTE_VIDEO = 4;
    public static final String EXTRA_PACKAGE = "EXTRA_PACKAGE";
    public static final String EXTRA_REASON = "EXTRA_REASON";
    public static final String EXTRA_RECORD_STATUS = "EXTRA_RECORD_STATUS";
    public static final int EXTRA_RECORD_STATUS_OFF = 0;
    public static final int EXTRA_RECORD_STATUS_ON = 1;
    public static final String EXTRA_REC_STATE = "EXTRA_REC_STATE";
    public static final String EXTRA_STATUS = "EXTRA_STATUS";
    public static final int EXTRA_STATUS_OFF = 0;
    public static final int EXTRA_STATUS_ON = 2;
    public static final int EXTRA_STATUS_OTHER = 1;
    public static final String EXTRA_TIME = "EXTRA_TIME";
    public static final String EXTRA_VENDOR = "EXTRA_VENDOR";
    public static final String HUIYING_BROADCAST_RECV = "HUIYING_JIANRONG_NEWUI_BROADCAST_RECV";
    public static final String HUIYING_BROADCAST_SEND = "HUIYING_JIANRONG_NEWUI_BROADCAST_SEND";
    public static final String HUIYING_JIANRONG_BROADCAST_SEND_TO_VST = "HUIYING_JIANRONG_BROADCAST_SEND_TO_VST";
    public static final String KEY_TYPE = "KEY_TYPE";
    public static final int KEY_TYPE_OP = 1003;
    public static final int KEY_TYPE_QUERY_STATE = 1001;
    public static final int KEY_TYPE_RETURN_OP = 1004;
    public static final int KEY_TYPE_RETURN_STATE = 1002;

    public static void sendRemoteStateBroadcast(Context context) {
        int recordStatus;
        int devStatus;
        LogUtils.d("HuiYing", "第三方应用发送广播");
        if (CmdManager.getInstance().getCurrentState().isCam_rec_state()) {
            recordStatus = 1;
        } else {
            recordStatus = 0;
        }
        if (UvcCamera.getInstance().isInit()) {
            devStatus = 2;
        } else {
            devStatus = 0;
        }
        Intent intent = new Intent();
        intent.setAction(HUIYING_BROADCAST_RECV);
        intent.putExtra(EXTRA_PACKAGE, "com.fvision.camera");
        intent.putExtra(EXTRA_STATUS, devStatus);
        intent.putExtra(EXTRA_VENDOR, "Hui Ying");
        intent.putExtra(EXTRA_MODEL, "Jian Rong New Ui");
        intent.putExtra(EXTRA_GET_PIC_FROM_BACKPLAY, EXTRA_GET_PIC_FROM_BACKPLAY);
        intent.putExtra(KEY_TYPE, 1002);
        intent.putExtra(EXTRA_RECORD_STATUS, recordStatus);
        intent.setFlags(32);
        context.sendBroadcast(intent);
    }

    public static void sendOpReturnBroadcast(Context context, boolean status, String reason) {
        Intent takeIntent = new Intent();
        takeIntent.setAction(HUIYING_BROADCAST_RECV);
        takeIntent.putExtra(EXTRA_MEDIA_TYPE, 0);
        takeIntent.putExtra(KEY_TYPE, 1004);
        takeIntent.putExtra(EXTRA_TIME, System.currentTimeMillis());
        takeIntent.putExtra(EXTRA_STATUS, status);
        takeIntent.putExtra(EXTRA_REASON, reason);
        takeIntent.setFlags(32);
        context.sendBroadcast(takeIntent);
    }

    public static void sendOpReturnBroadcast(Context context, boolean isCamConnect, int state, boolean isRec) {
        Intent takeIntent = new Intent();
        takeIntent.setAction(HUIYING_BROADCAST_RECV);
        takeIntent.putExtra(EXTRA_CAM_STATE, isCamConnect);
        takeIntent.putExtra(EXTRA_CAM_VERSION_STATE, state);
        takeIntent.putExtra(EXTRA_REC_STATE, isRec);
        takeIntent.setFlags(32);
        context.sendBroadcast(takeIntent);
    }
}
