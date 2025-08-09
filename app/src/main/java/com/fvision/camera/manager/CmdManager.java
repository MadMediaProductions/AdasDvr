package com.fvision.camera.manager;

import android.text.TextUtils;
import android.util.Log;
import com.fvision.camera.bean.CameraStateBean;
import com.fvision.camera.bean.DataPaket;
import com.huiying.cameramjpeg.UvcCamera;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CmdManager {
    public static final int CMD_STATE_EXTRACT = -2;
    public static final int CMD_STATE_NOR = 0;
    public static String SPACE = "0";
    private static CmdManager _instance;
    private CameraStateBean cameraState;
    private int cmdState = 0;
    private boolean isSyncTimeSuccess = false;
    private long lastSyncTime = 0;

    public static CmdManager getInstance() {
        if (_instance == null) {
            _instance = new CmdManager();
        }
        return _instance;
    }

    public int getCmdState() {
        return this.cmdState;
    }

    public void setCmdState(int cmdState2) {
        this.cmdState = cmdState2;
    }

    public CameraStateBean getCurrentState() {
        if (this.cameraState == null) {
            this.cameraState = new CameraStateBean();
        }
        return this.cameraState;
    }

    public void setCurrentState(CameraStateBean State) {
        this.cameraState = State;
    }

    public boolean getIsSyncTimeSuccess() {
        return this.isSyncTimeSuccess;
    }

    public boolean recSoundToggle() {
        int value;
        boolean z = true;
        if (this.cameraState == null) {
            return false;
        }
        if (this.cameraState.isCam_mute_state()) {
            value = 0;
        } else {
            value = 1;
        }
        byte[] buffer = value == 0 ? new byte[]{0} : new byte[]{1};
        if (sendCommand(6, buffer.length, buffer) < 0) {
            z = false;
        }
        return z;
    }

    public boolean recToggle() {
        int value;
        boolean z = true;
        if (this.cameraState == null) {
            return false;
        }
        if (this.cameraState.isCam_rec_state()) {
            value = 0;
        } else {
            value = 1;
        }
        byte[] buffer = value == 0 ? new byte[]{0} : new byte[]{1};
        if (sendCommand(1, buffer.length, buffer) < 0) {
            z = false;
        }
        return z;
    }

    public boolean lockToggle() {
        int value;
        boolean z = true;
        if (this.cameraState == null) {
            return false;
        }
        if (this.cameraState.isCam_lock_state()) {
            value = 0;
        } else {
            value = 1;
        }
        byte[] buffer = value == 0 ? new byte[]{0} : new byte[]{1};
        if (sendCommand(2, buffer.length, buffer) < 0) {
            z = false;
        }
        return z;
    }

    public boolean lockBackPlay(String fileName) {
        return sendCommand(19, fileName.getBytes().length, fileName.getBytes()) >= 0;
    }

    public boolean modelToggle() {
        int value = 0;
        if (this.cameraState == null) {
            return false;
        }
        if (!this.cameraState.isCam_mode_state()) {
            value = 1;
        }
        return modelToggle(value);
    }

    public boolean modelToggle(int model) {
        byte[] buffer = {(byte) model};
        if (sendCommand(0, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public int getFiles(byte[] data) {
        if (UvcCamera.getInstance().isInit()) {
            return UvcCamera.getInstance().getFile(data);
        }
        return -1;
    }

    public boolean setPlayBackFile(int fileIndex) {
        byte[] buffer = {(byte) fileIndex};
        if (sendCommand(12, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public boolean setPlayBackFile(String fileName) {
        byte[] buffer = fileName.getBytes();
        return sendCommand(12, buffer.length, buffer) >= 0;
    }

    public boolean playPlayBackFile() {
        byte[] buffer = {1};
        if (sendCommand(3, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public boolean getCamIsConnectState() {
        byte[] buffer = new byte[1];
        if (sendCommand(47, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public boolean pausePlayBackFile() {
        byte[] buffer = {0};
        if (sendCommand(3, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public boolean takePictures() {
        byte[] buffer = {1};
        if (sendCommand(7, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public boolean syncTime() {
        long now = System.currentTimeMillis();
        if (now - this.lastSyncTime < 10000) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(1);
        byte[] data = {(byte) (year & 255), (byte) ((year >> 8) & 255), (byte) (calendar.get(2) + 1), (byte) calendar.get(5), (byte) calendar.get(11), (byte) calendar.get(12), (byte) calendar.get(13)};
        if (year < 2018) {
            return false;
        }
        this.isSyncTimeSuccess = sendCommand(11, data.length, data) >= 0;
        if (this.isSyncTimeSuccess) {
            this.lastSyncTime = now;
        }
        return this.isSyncTimeSuccess;
    }

    public boolean formatTf() {
        byte[] buffer = {1};
        if (sendCommand(15, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public boolean fastForward() {
        byte[] buffer = {1};
        if (sendCommand(20, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public boolean fastBackward() {
        byte[] buffer = {1};
        if (sendCommand(21, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public boolean deleteFile(int fileIndex) {
        byte[] buffer = {(byte) fileIndex};
        if (sendCommand(18, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public boolean deleteFile(String fileName) {
        byte[] buffer = fileName.getBytes();
        return sendCommand(18, buffer.length, buffer) >= 0;
    }

    public boolean isSupportDel() {
        String currentVer = getCurrentState().getPasswd();
        if (TextUtils.isEmpty(currentVer)) {
            return false;
        }
        int startPos = currentVer.indexOf("_v");
        if (currentVer.substring(startPos + 2, startPos + 5).compareTo("2.5") > 0) {
            return true;
        }
        return false;
    }

    public boolean isSupportBackPlayLock() {
        String currentVer = getCurrentState().getPasswd();
        if (TextUtils.isEmpty(currentVer)) {
            return false;
        }
        int startPos = currentVer.indexOf("_v");
        if (currentVer.substring(startPos + 2, startPos + 5).compareTo("2.7") >= 0) {
            return true;
        }
        return false;
    }

    public boolean isSupportSanction() {
        String currentVer = getCurrentState().getPasswd();
        if (TextUtils.isEmpty(currentVer)) {
            return false;
        }
        int startPos = currentVer.indexOf("_v");
        if (currentVer.substring(startPos + 2, startPos + 5).compareTo("3.1") >= 0) {
            return true;
        }
        return false;
    }

    public boolean isSupportVideoDuration() {
        String currentVer = getCurrentState().getPasswd();
        if (TextUtils.isEmpty(currentVer)) {
            return false;
        }
        int startPos = currentVer.indexOf("_v");
        if (currentVer.substring(startPos + 2, startPos + 5).compareTo("3.2") >= 0) {
            return true;
        }
        return false;
    }

    public String getDVRUid() {
        byte[] buffer = new byte[16];
        if (isSuccess(sendCommand(26, buffer.length, buffer))) {
            return bytesToHexString(buffer).replace(" ", "");
        }
        return null;
    }

    public String getUUIDCode() {
        byte[] buffer = new byte[16];
        if (!isSuccess(sendCommand(25, buffer.length, buffer))) {
            return null;
        }
        String uid = bytesToHexString(buffer).replace(" ", "").toUpperCase().substring(0, buffer.length);
        if (uid == null || !uid.startsWith("511") || uid.length() <= 14) {
            return uid;
        }
        return "@" + uid.substring(0, 14);
    }

    public boolean setUUIDCode(String code) {
        if (code != null && code.startsWith("511") && code.length() == 14) {
            code = code + SPACE + SPACE;
        }
        if (code == null || code.length() / 2 > 10) {
            return false;
        }
        byte[] bt = new byte[16];
        int j = 0;
        for (int i = 0; i < code.length() / 2; i++) {
            bt[i] = (byte) strToInt(code.substring(j, j + 2));
            j += 2;
        }
        return isSuccess(sendCommand(24, bt.length, bt));
    }

    public boolean writeKeyStore(String key) {
        return isSuccess(sendCommand(22, 64, key.getBytes()));
    }

    public String readKeyStore() {
        byte[] buffer = new byte[64];
        if (isSuccess(sendCommand(23, buffer.length, buffer))) {
            return new String(buffer).trim();
        }
        return null;
    }

    public boolean isSupportKaiYiAdas() {
        String adasid = getUUIDCode();
        if (!TextUtils.isEmpty(adasid) && adasid.contains("AD48")) {
            return true;
        }
        return false;
    }

    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            String hv = Integer.toHexString(b & 255);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append("" + hv);
        }
        return stringBuilder.toString();
    }

    public long getLastSyncTime() {
        return this.lastSyncTime;
    }

    public int getVendor() {
        byte[] buffer = new byte[4];
        if (isSuccess(sendCommand(28, buffer.length, buffer))) {
            return bytesToInt_hight2low(buffer, 0);
        }
        return -1;
    }

    public boolean setVendor(int vendor) {
        return isSuccess(sendCommand(27, 4, intToBytes_hight2low(vendor)));
    }

    public int getVideoDuration() {
        byte[] buffer = new byte[4];
        if (isSuccess(sendCommand(30, buffer.length, buffer))) {
            return bytesToInt_hight2low(buffer, 0);
        }
        return -1;
    }

    public List<Integer> getCardSize() {
        byte[] total = new byte[4];
        byte[] rest = new byte[4];
        byte[] buffer = new byte[8];
        List<Integer> list = new ArrayList<>();
        if (!isSuccess(sendCommand(48, buffer.length, buffer))) {
            return null;
        }
        System.arraycopy(buffer, 0, total, 0, 4);
        System.arraycopy(buffer, 4, rest, 0, 4);
        list.add(Integer.valueOf(bytesToInt_hight2low(total, 0)));
        list.add(Integer.valueOf(bytesToInt_hight2low(rest, 0)));
        return list;
    }

    public int getAdasType() {
        byte[] buffer = new byte[1];
        if (isSuccess(sendCommand(31, buffer.length, buffer))) {
            return buffer[0] & 255;
        }
        return -1;
    }

    public boolean setVideoDuration(int duration) {
        return isSuccess(sendCommand(29, 4, intToBytes_hight2low(duration)));
    }

    public static int bytesToInt_hight2low(byte[] src, int offset) {
        return (src[offset] & 255) | ((src[offset + 1] & 255) << 8) | ((src[offset + 2] & 255) << 16) | ((src[offset + 3] & 255) << 24);
    }

    public static byte[] intToBytes_hight2low(int num) {
        return new byte[]{(byte) num, (byte) ((num >> 8) & 255), (byte) ((num >> 16) & 255), (byte) ((num >> 24) & 255)};
    }

    public static byte[] intToBytes(int num) {
        byte[] bt = new byte[4];
        bt[3] = (byte) num;
        bt[2] = (byte) ((num >> 8) & 255);
        bt[1] = (byte) ((num >> 16) & 255);
        bt[0] = (byte) ((num >> 24) & 255);
        return bt;
    }

    public int sendCommand(int request, int length, byte[] data) {
        if (!UvcCamera.getInstance().isInit()) {
            return -1;
        }
        if (this.cmdState != -2 || request == 33 || request == 35 || request == 36 || request == 37 || request == 38 || request == 39 || request == 40 || request == 41 || request == 43 || request == 44 || request == 45 || request == 46) {
            return UvcCamera.getInstance().sendCmd(-1, request, -1, -1, length, data);
        }
        return -2;
    }

    private boolean isSuccess(int ret) {
        return ret >= 0;
    }

    private int strToInt(String str) {
        String a = "";
        for (char c : str.toCharArray()) {
            switch (c) {
                case '0':
                    a = a + "0,";
                    break;
                case '1':
                    a = a + "1,";
                    break;
                case '2':
                    a = a + "2,";
                    break;
                case '3':
                    a = a + "3,";
                    break;
                case '4':
                    a = a + "4,";
                    break;
                case '5':
                    a = a + "5,";
                    break;
                case '6':
                    a = a + "6,";
                    break;
                case '7':
                    a = a + "7,";
                    break;
                case '8':
                    a = a + "8,";
                    break;
                case '9':
                    a = a + "9,";
                    break;
                case 'A':
                    a = a + "10,";
                    break;
                case 'B':
                    a = a + "11,";
                    break;
                case 'C':
                    a = a + "12,";
                    break;
                case 'D':
                    a = a + "13,";
                    break;
                case 'E':
                    a = a + "14,";
                    break;
                case 'F':
                    a = a + "15,";
                    break;
            }
        }
        String[] b = a.split(",");
        return (Integer.valueOf(b[0]).intValue() * 16) + (Integer.valueOf(b[1]).intValue() * 1);
    }

    public int checkUpgradeFile() {
        byte[] buffer = new byte[1];
        if (!UvcCamera.getInstance().isInit()) {
            return -1;
        }
        int ret = sendCommand(37, buffer.length, buffer);
        if (ret == 0) {
            return buffer[0] & 255;
        }
        return ret;
    }

    public int startUpgrade() {
        byte[] buffer = new byte[4];
        int ret = sendCommand(38, 1, buffer);
        if (isSuccess(ret)) {
            return bytesToInt_hight2low(buffer, 0);
        }
        return ret;
    }

    public boolean isSupperDevUpgrade() {
        String currentVersion = getCurrentState().getDevVersion();
        if (TextUtils.isEmpty(currentVersion)) {
            return false;
        }
        int startPos = currentVersion.indexOf("_v");
        if (currentVersion.substring(startPos + 2, startPos + 6).compareTo("2.3") >= 0) {
            return true;
        }
        return false;
    }

    public boolean openWriteFile() {
        return openFile();
    }

    private boolean openFile() {
        return isSuccess(sendCommand(33, 1, new byte[4]));
    }

    public int writeFileData(byte[] data, int offset) {
        return sendCommand(35, data.length, data);
    }

    public boolean closeReadFile() {
        if (sendCommand(36, 1, new byte[1]) >= 0) {
            return true;
        }
        return false;
    }

    public DataPaket getFileData(int offset, int length) {
        byte[] buffer = new byte[length];
        if (!isSuccess(sendCommand(40, buffer.length, buffer))) {
            return null;
        }
        DataPaket paket = new DataPaket();
        paket.data = buffer;
        paket.length = buffer.length;
        return paket;
    }

    public DataPaket getVideoFileData(int offset, int length) {
        byte[] buffer = new byte[length];
        if (!isSuccess(sendCommand(45, buffer.length, buffer))) {
            return null;
        }
        DataPaket paket = new DataPaket();
        paket.data = buffer;
        paket.length = buffer.length;
        return paket;
    }

    public boolean closeDownloadFile() {
        byte[] buffer = new byte[1];
        if (sendCommand(41, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public boolean closeDownloadZhenFile() {
        byte[] buffer = new byte[1];
        if (sendCommand(46, buffer.length, buffer) >= 0) {
            return true;
        }
        return false;
    }

    public boolean openWriteFiles() {
        return openFiles();
    }

    public int openReadFile(int type, String filename) {
        return openFiles(filename);
    }

    private boolean openFiles() {
        return isSuccess(sendCommand(33, 1, new byte[4]));
    }

    public int openReadFiles(String filename) {
        return openFiles(filename);
    }

    private int openFiles(String fileName) {
        byte[] fileSizeArr = new byte[4];
        byte[] filedata = fileName.getBytes();
        int ret = sendCommand(39, filedata.length, filedata);
        if (!isSuccess(ret)) {
            return ret;
        }
        System.arraycopy(filedata, 0, fileSizeArr, 0, fileSizeArr.length);
        return bytesToInt_hight2low(fileSizeArr, 0);
    }

    public int getVideoDurationTime(String fileName) {
        byte[] fileSizeArr = new byte[4];
        Log.e("downfile 打开文件 ", "downfile 打开文件 " + fileName);
        byte[] filedata = fileName.getBytes();
        int ret = sendCommand(43, filedata.length, filedata);
        Log.e("发送成功还是失败", "cmd_fd:" + UvcCamera.getInstance().cmd_fd_error + "  " + UvcCamera.getInstance().fd_error);
        if (ret >= 0) {
            System.arraycopy(filedata, 0, fileSizeArr, 0, fileSizeArr.length);
            Log.e("downfile 文件大小byte[] ", "downfile 文件大小byte[] " + bytesToHexString(fileSizeArr));
            return bytesToInt_hight2low(fileSizeArr, 0);
        }
        Log.e("downfile 获取文件的大小 ", "downfile 获取文件的大小 " + ret + UvcCamera.getInstance().cmd_fd_error);
        return ret;
    }

    public int getVideoZhenTime(int num) {
        byte[] fileSizeArr = new byte[4];
        byte[] filedata = intToBytes_hight2low(num);
        Log.e("发送帧数 ", "发送帧数 " + num + "  " + bytesToHexString(filedata));
        int ret = sendCommand(44, fileSizeArr.length, filedata);
        Log.e("发送成功还是失败2", "cmd_fd:" + UvcCamera.getInstance().cmd_fd_error + "  " + UvcCamera.getInstance().fd_error);
        if (ret >= 0) {
            System.arraycopy(filedata, 0, fileSizeArr, 0, fileSizeArr.length);
            Log.e("发送帧数文件大小byte[] ", "发送帧数 文件大小byte[] " + bytesToHexString(fileSizeArr));
            return bytesToInt_hight2low(fileSizeArr, 0);
        }
        Log.e("发送帧数 获取文件的大小 ", "发送帧数 获取文件的大小 " + ret + UvcCamera.getInstance().cmd_fd_error);
        return ret;
    }

    public static byte[] strToByteArray(String str) {
        if (str == null) {
            return null;
        }
        return str.getBytes();
    }
}
