package com.fvision.camera.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.Log;
import com.fvision.camera.bean.FileBeans;
import com.fvision.camera.manager.CmdManager;
import com.huiying.cameramjpeg.UvcCamera;
import com.serenegiant.usb.USBMonitor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.opencv.imgproc.Imgproc;

public class CmdUtil {
    private static final String TAG = "cmdutils";
    private static CmdUtil _instance;
    private String camera_version = "";
    /* access modifiers changed from: private */
    public USBMonitor.UsbControlBlock mUsbControlBlock;

    public interface ByteCallback {
        void onCallback(byte[] bArr, boolean z);
    }

    public static CmdUtil getInstance() {
        if (_instance == null) {
            _instance = new CmdUtil();
        }
        return _instance;
    }

    public void setCamera_version(String camera_version2) {
        this.camera_version = camera_version2;
    }

    private int byte2int(byte[] bytes) {
        int length = 0;
        if (bytes.length > 0) {
            length = 0 + (bytes[0] & 255);
        }
        if (bytes.length > 1) {
            length += (bytes[1] << 8) & SupportMenu.USER_MASK;
        }
        if (bytes.length > 2) {
            length += (bytes[2] << 16) & ViewCompat.MEASURED_SIZE_MASK;
        }
        if (bytes.length > 3) {
            return length + ((bytes[3] << 24) & -1);
        }
        return length;
    }

    public boolean changeMode(int value) {
        Log.e("9527", "changeMode value = " + value);
        byte[] buffer = value == 0 ? new byte[]{0} : new byte[]{1};
        if (sendCommand(66, 0, 0, 0, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public int getFileCount(int type) {
        byte[] buffer = new byte[4];
        if (sendCommand(194, 5, type, 0, 4, buffer) > 0) {
            return (buffer[0] & 255) + ((buffer[1] << 8) & 65535) + ((buffer[2] << 16) & ViewCompat.MEASURED_SIZE_MASK) + ((buffer[3] << 24) & -1);
        }
        return -1;
    }

    public int getFileCount() {
        byte[] buffer = new byte[512];
        if (getFileCount(buffer) != 0) {
            return 0;
        }
        int count = (buffer[0] & 255) + ((buffer[1] << 8) & 65535);
        Log.e(TAG, "9527 getFileInfos: count = " + count);
        return count;
    }

    public String getFileName(int type, int index) {
        byte[] buffer = new byte[64];
        if (sendCommand(194, 6, type, index, 64, buffer) > 0) {
            return new String(buffer).trim();
        }
        return null;
    }

    public List<FileBeans> getFileInfos(int type, int start, byte length) {
        byte[] buffer = new byte[468];
        buffer[0] = length;
        int ret = sendCommand(194, 6, type, start, buffer.length, buffer);
        Log.e(TAG, "ryujin getFileInfos: ret = " + ret);
        if (ret <= 0) {
            return null;
        }
        List<FileBeans> infos = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat dayformat = new SimpleDateFormat("yyyyMMddHH");
        for (int i = 0; i < length; i++) {
            int startindex = 26 * i;
            byte[] name = new byte[14];
            byte[] time = new byte[12];
            System.arraycopy(buffer, startindex, name, 0, name.length);
            System.arraycopy(buffer, name.length + startindex, time, 0, time.length);
            FileBeans info = new FileBeans();
            info.fileName = new String(name);
            info.year = time[0] & ((time[1] << 8) + 255) & 65535;
            info.month = time[2] & ((time[3] << 8) + 255) & 65535;
            info.day = time[4] & ((time[5] << 8) + 255) & 65535;
            info.hour = time[6] & ((time[7] << 8) + 255) & 65535;
            info.minute = time[8] & ((time[9] << 8) + 255) & 65535;
            info.sencond = time[10] & ((time[11] << 8) + 255) & 65535;
            String stime = String.format("%04d%02d%02d%02d%02d%02d", new Object[]{Integer.valueOf(info.year), Integer.valueOf(info.month), Integer.valueOf(info.day), Integer.valueOf(info.hour), Integer.valueOf(info.minute), Integer.valueOf(info.sencond)});
            try {
                info.sorttime = dateFormat.parse(stime).getTime();
                info.dayTime = dayformat.parse(stime.substring(0, 10)).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            infos.add(info);
        }
        Log.e(TAG, "ryujin getFileInfos: " + infos.toString());
        return infos;
    }

    public int getFileInfos() {
        int ret = getFile(new byte[512]);
        Log.e(TAG, "9527 getFileInfos: ret = " + ret);
        if (ret > 0) {
            return ret;
        }
        return 0;
    }

    public boolean playFile(int type, String filename) {
        Log.e("9527", "playFile");
        byte[] buffer = {1, 0};
        if (sendCommand(66, 12, 0, 0, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public boolean startPlayFile() {
        Log.e("9527", "startPlayFile");
        byte[] buffer = {1};
        if (sendCommand(66, 3, 0, 0, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public boolean pausePlay() {
        if (sendCommand(66, 9, 0, 0, 0, (byte[]) null) >= 0) {
            return true;
        }
        return false;
    }

    public boolean stopPlay() {
        if (sendCommand(66, 10, 0, 0, 0, (byte[]) null) >= 0) {
            return true;
        }
        return false;
    }

    public int queryRemainingTime() {
        byte[] buffer = new byte[4];
        if (sendCommand(194, 11, 0, 0, buffer.length, buffer) > 0) {
            return (buffer[0] & 255) + ((buffer[1] << 8) & 65535) + ((buffer[2] << 16) & ViewCompat.MEASURED_SIZE_MASK) + ((buffer[3] << 24) & -1);
        }
        return -1;
    }

    public boolean resumePlay() {
        if (sendCommand(66, 12, 0, 0, 0, (byte[]) null) >= 0) {
            return true;
        }
        return false;
    }

    public boolean setResolution(int value) {
        if (sendCommand(66, 15, value, 0, 0, (byte[]) null) >= 0) {
            return true;
        }
        return false;
    }

    public boolean setRecordTime(int minite) {
        if (sendCommand(66, 16, minite, 0, 0, (byte[]) null) >= 0) {
            return true;
        }
        return false;
    }

    public boolean setFrequence(int value) {
        if (sendCommand(66, 17, value, 0, 0, (byte[]) null) >= 0) {
            return true;
        }
        return false;
    }

    public boolean snapShot() {
        if (sendCommand(66, 20, 0, 0, 0, (byte[]) null) >= 0) {
            return true;
        }
        return false;
    }

    public boolean startRecord() {
        Log.e("9527", "startRecord ");
        byte[] buffer = {1};
        if (sendCommand(0, 1, 0, 0, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public boolean stopRecord() {
        Log.e("9527", "stopRecord ");
        byte[] buffer = {0};
        if (sendCommand(66, 1, 0, 0, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public boolean changeAudioRecord(int value) {
        String filename;
        Log.e("9527", "changeAudioRecord value = " + value);
        if (value == 0) {
            filename = "0";
        } else {
            filename = "1";
        }
        byte[] buffer = filename.getBytes();
        if (sendCommand(66, 6, 0, 0, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public boolean setExposure(int value) {
        if (sendCommand(66, 112, value, 0, 0, (byte[]) null) >= 0) {
            return true;
        }
        return false;
    }

    public int getExposure() {
        byte[] data = new byte[4];
        if (sendCommand(194, 96, 0, 0, data.length, data) >= 0) {
            return byte2int(data);
        }
        return -1;
    }

    public boolean formatTFcard() {
        Log.e("9527", "formatTFcard");
        byte[] buffer = "1".getBytes();
        if (sendCommand(66, 23, 0, 0, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public boolean setCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(1);
        int month = calendar.get(2) + 1;
        int day = calendar.get(5);
        int hour = calendar.get(11);
        int minute = calendar.get(12);
        int sencond = calendar.get(13);
        byte[] data = {(byte) (year & 255), (byte) ((year >> 8) & 255), (byte) (month & 255), (byte) ((month >> 8) & 255), (byte) (day & 255), (byte) ((day >> 8) & 255), (byte) (hour & 255), (byte) ((hour >> 8) & 255), (byte) (minute & 255), (byte) ((minute >> 8) & 255), (byte) (sencond & 255), (byte) ((sencond >> 8) & 255)};
        Log.e(TAG, "year=" + year + ",month=" + month + ",day=" + day + ",hour=" + hour + ",minute=" + minute + ",sencond=" + sencond);
        return sendCommand(66, 28, 0, 0, data.length, data) >= 0 && year >= 2000;
    }

    public int getVideoRecordState() {
        byte[] buffer = new byte[4];
        if (sendCommand(194, 29, 0, 0, buffer.length, buffer) > 0) {
            if (!checkCameraversion("v1.1")) {
                return buffer[0];
            }
            if ((buffer[1] & 255) == 255) {
                return buffer[0];
            }
        }
        return -1;
    }

    public String getCameraVersion() {
        byte[] buffer = new byte[15];
        if (sendCommand(194, 73, 0, 0, buffer.length, buffer) <= 0) {
            return "";
        }
        String version = new String(buffer).trim();
        this.camera_version = version;
        return version;
    }

    public byte[] getAudioRecordState() {
        byte[] buffer = new byte[64];
        if (sendCommand(194, 30, 8527, 0, buffer.length, buffer) > 0) {
            return buffer;
        }
        return null;
    }

    public int getAudioRecordState1() {
        byte[] buffer = new byte[4];
        if (sendCommand(194, 30, 0, 0, buffer.length, buffer) > 0) {
            if (!checkCameraversion("v1.1")) {
                return buffer[0];
            }
            if ((buffer[1] & 255) == 255) {
                return buffer[0];
            }
        }
        return -1;
    }

    public int getTFState() {
        byte[] buffer = new byte[4];
        if (sendCommand(194, 31, 0, 0, buffer.length, buffer) > 0) {
            if (!checkCameraversion("v1.1")) {
                return buffer[0];
            }
            if ((buffer[1] & 255) == 255) {
                return buffer[0];
            }
        }
        return -1;
    }

    public int getTFState1() {
        byte[] buffer = new byte[4];
        if (sendCommand(194, 51, 0, 0, buffer.length, buffer) > 0) {
            return buffer[0];
        }
        return -1;
    }

    private boolean checkCameraversion(String version) {
        if (!TextUtils.isEmpty(this.camera_version)) {
            String[] versions = this.camera_version.split("_");
            if (versions.length > 1) {
                if (versions[0].toLowerCase().compareTo(version.toLowerCase()) >= 0) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public int getGsensorState() {
        byte[] buffer = new byte[4];
        if (sendCommand(194, 32, 0, 0, buffer.length, buffer) > 0) {
            if (!checkCameraversion("v1.1")) {
                return buffer[0];
            }
            if ((buffer[1] & 255) == 255) {
                return buffer[0];
            }
        }
        return -1;
    }

    public byte[] getAllState() {
        byte[] buffer = new byte[4];
        if (sendCommand(194, 85, 0, 0, buffer.length, buffer) > 0) {
            return buffer;
        }
        return null;
    }

    public boolean deleteFile(int type, String filename) {
        byte[] data = new byte[64];
        byte[] filedata = filename.getBytes();
        System.arraycopy(filedata, 0, data, 0, filedata.length);
        if (sendCommand(66, 33, type, 0, 64, data) >= 0) {
            return true;
        }
        return false;
    }

    public int getResolution() {
        byte[] buffer = new byte[4];
        if (sendCommand(194, 47, 0, 0, buffer.length, buffer) > 0) {
            return buffer[0];
        }
        return -1;
    }

    public int getRecordLength() {
        byte[] buffer = new byte[4];
        if (sendCommand(194, 48, 0, 0, buffer.length, buffer) > 0) {
            return buffer[0];
        }
        return -1;
    }

    public int getFrequence() {
        byte[] buffer = new byte[4];
        if (sendCommand(194, 49, 0, 0, buffer.length, buffer) > 0) {
            return buffer[0];
        }
        return -1;
    }

    public boolean seekPlayTime(int seconds) {
        byte[] data = {(byte) (seconds & 255), (byte) ((seconds >> 8) & 255), (byte) ((seconds >> 16) & 255), (byte) ((seconds >> 24) & 255)};
        if (sendCommand(66, 50, 0, 0, data.length, data) >= 0) {
            return true;
        }
        return false;
    }

    public boolean openReadFile(int type, String filename) {
        byte[] data = new byte[64];
        byte[] filedata = filename.getBytes();
        System.arraycopy(filedata, 0, data, 0, filedata.length);
        if (sendCommand(66, 80, type, 0, 64, data) >= 0) {
            return true;
        }
        return false;
    }

    public int getReadFileLenght() {
        byte[] buffer = new byte[4];
        if (sendCommand(194, 81, 0, 0, buffer.length, buffer) > 0) {
            return (buffer[0] & 255) + ((buffer[1] << 8) & 65535) + ((buffer[2] << 16) & ViewCompat.MEASURED_SIZE_MASK) + ((buffer[3] << 24) & -1);
        }
        return -1;
    }

    public boolean closeReadFile() {
        if (sendCommand(66, 83, 0, 0, 0, (byte[]) null) >= 0) {
            return true;
        }
        return false;
    }

    public boolean openStream() {
        if (sendCommand(33, 1, 2, 512, 26, new byte[]{0, 0, 1, 1, 21, 22, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -92, 31, 0, 0, 2, 0, 0}) >= 0) {
            return true;
        }
        return false;
    }

    public boolean closeStream() {
        if (sendCommand(0, 1, 0, Imgproc.COLOR_RGB2YUV_YV12, 0, (byte[]) null) >= 0) {
            return true;
        }
        return false;
    }

    public boolean setTurnTopBottom(boolean turn) {
        int i;
        int i2;
        byte[] data = new byte[4];
        if (turn) {
            i = 1;
        } else {
            i = 0;
        }
        data[0] = (byte) i;
        data[1] = 0;
        data[2] = 0;
        data[3] = 0;
        if (turn) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        if (sendCommand(66, 69, i2, 0, 4, data) >= 0) {
            return true;
        }
        return false;
    }

    public boolean setTurnLeftRight(boolean turn) {
        int i;
        int i2;
        byte[] data = new byte[4];
        if (turn) {
            i = 1;
        } else {
            i = 0;
        }
        data[0] = (byte) i;
        data[1] = 0;
        data[2] = 0;
        data[3] = 0;
        if (turn) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        if (sendCommand(66, 70, i2, 0, 4, data) >= 0) {
            return true;
        }
        return false;
    }

    public boolean setLock(int lock) {
        Log.e("9527", "setLock");
        byte[] buffer = "1".getBytes();
        int ret = sendCommand(66, 0, 0, 0, buffer.length, buffer);
        Log.e("9527", "ret= " + ret);
        if (ret >= 0) {
            return true;
        }
        return false;
    }

    public int getTurnTopBottom() {
        byte[] buffer = new byte[4];
        if (sendCommand(194, 64, 0, 0, buffer.length, buffer) > 0) {
            return buffer[0];
        }
        return -1;
    }

    public int getTurnLeftRight() {
        byte[] buffer = new byte[4];
        if (sendCommand(194, 65, 0, 0, buffer.length, buffer) > 0) {
            return buffer[0];
        }
        return -1;
    }

    private int sendCommand(int requesttype, int request, int value, int index, int length, byte[] data) {
        if (UvcCamera.getInstance().isInit()) {
            return UvcCamera.getInstance().sendCmd(requesttype, request, value, index, length, data);
        }
        return -1;
    }

    private int getFile(byte[] data) {
        if (UvcCamera.getInstance().isInit()) {
            return UvcCamera.getInstance().getFile(data);
        }
        return -1;
    }

    private int getFileCount(byte[] data) {
        if (UvcCamera.getInstance().isInit()) {
            return UvcCamera.getInstance().getFileCount(data);
        }
        return -1;
    }

    private void sendCommand(int requesttype, int request, int value, int index, int length, byte[] data, ByteCallback callback) {
        new BulkThread(requesttype, request, value, index, data, length, callback).start();
    }

    class BulkThread extends Thread {
        byte[] data;
        int index;
        int length;
        ByteCallback mCallback;
        private Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (BulkThread.this.mCallback != null) {
                    BulkThread.this.mCallback.onCallback((byte[]) msg.obj, msg.arg1 == 0);
                }
            }
        };
        int request;
        int requesttype;
        int value;

        public BulkThread(int requsttype, int request2, int value2, int index2, byte[] data2, int length2, ByteCallback callback) {
            this.requesttype = requsttype;
            this.request = request2;
            this.value = value2;
            this.index = index2;
            this.data = data2;
            this.length = length2;
            this.mCallback = callback;
        }

        public void run() {
            int out;
            if (CmdUtil.this.mUsbControlBlock == null || (out = CmdUtil.this.mUsbControlBlock.getUsbDeviceConnection().controlTransfer(this.requesttype, this.request, this.value, this.index, this.data, this.length, 3000)) < 0) {
                Message msg = this.mHandler.obtainMessage();
                msg.arg1 = -1;
                this.mHandler.sendMessage(msg);
                return;
            }
            Message msg2 = this.mHandler.obtainMessage();
            msg2.arg1 = 0;
            if (out > 0) {
                msg2.obj = this.data;
            }
            this.mHandler.sendMessage(msg2);
        }
    }

    class CmdThread extends Thread {
        byte[] data;
        private Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (CmdThread.this.mCallback != null) {
                    CmdThread.this.mCallback.onCallback((byte[]) msg.obj, msg.arg1 == 0);
                }
            }
        };
        int index;
        int length;
        ByteCallback mCallback;
        int request;
        int requesttype;
        int value;

        public CmdThread(int requsttype, int request2, int value2, int index2, byte[] data2, int length2, ByteCallback callback) {
            this.mCallback = callback;
            this.requesttype = requsttype;
            this.request = request2;
            this.value = value2;
            this.index = index2;
            this.data = data2;
            this.length = length2;
        }

        public void run() {
            int i = 0;
            if (CmdUtil.this.mUsbControlBlock != null) {
                int ret = CmdUtil.this.mUsbControlBlock.getUsbDeviceConnection().controlTransfer(this.requesttype, this.request, this.value, this.index, this.data, this.length, 0);
                Message msg = this.handler.obtainMessage();
                if (ret < 0) {
                    i = -1;
                }
                msg.arg1 = i;
                this.handler.sendMessage(msg);
                return;
            }
            Message msg2 = this.handler.obtainMessage();
            msg2.arg1 = -1;
            this.handler.sendMessage(msg2);
        }
    }

    public static int versionCompareTo(String version) {
        int startPos;
        String currentVersion = CmdManager.getInstance().getCurrentState().getPasswd();
        if (currentVersion == null || currentVersion.length() <= 8 || (startPos = currentVersion.indexOf("_v")) <= 0) {
            return -1;
        }
        return currentVersion.substring(startPos + 2, startPos + 5).compareTo(version);
    }

    public static boolean isZh(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage().endsWith("zh");
    }

    public static boolean isVN(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage().endsWith("vi");
    }

    public static boolean isEn(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage().endsWith("en");
    }

    public static boolean isRu(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage().endsWith("ru");
    }
}
