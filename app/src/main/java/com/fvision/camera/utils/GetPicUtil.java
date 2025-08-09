package com.fvision.camera.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.fvision.camera.App;
import com.fvision.camera.R;
import com.fvision.camera.bean.FileBeans;
import com.fvision.camera.bean.FileItem;
import com.fvision.camera.iface.ICameraStateChange;
import com.fvision.camera.iface.IProgressBack;
import com.fvision.camera.manager.CameraStateIml;
import com.fvision.camera.manager.CmdManager;
import com.fvision.camera.manager.VideoDownloadManager;
import com.huiying.cameramjpeg.UvcCamera;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class GetPicUtil {
    public static final String EXTRA_VIDEO_FRAME_COUNT = "EXTRA_VIDEO_FRAME_COUNT";
    public static final String EXTRA_VIDEO_GET_FRAME_PIC_BUNDLE_LIST = "EXTRA_VIDEO_GET_FRAME_PIC_BUNDLE_LIST";
    public static final String EXTRA_VIDEO_GET_FRAME_PIC_SUCCESS_COUNT = "EXTRA_VIDEO_GET_FRAME_PIC_SUCCESS_COUNT";
    public static final String HUIYING_BROADCAST_END_GETPIC_RECV = "HUIYING_JIANRONG_NEWUI_BROADCAST_END_GETPIC_RECV";
    public static final String HUIYING_BROADCAST_GETPIC_RECV = "HUIYING_JIANRONG_NEWUI_BROADCAST_GETPIC_RECV";
    public static final String HUIYING_BROADCAST_START_GETPIC_RECV = "HUIYING_JIANRONG_NEWUI_BROADCAST_START_GETPIC_RECV";
    public static final int MSG_GET_FILE_IS_EXIST = 2001;
    public static final int MSG_GET_FILE_SIZE_ERROR = 1992;
    public static final int MSG_GET_FILE_SIZE_IS_EXIST = 2000;
    public static final int MSG_GET_PIC_TIME_STRING_IS_ERROR = 2002;
    public static final int MSG_GET_PIC_TIME_STRING_IS_NULL = 1999;
    public static final int MSG_GET_VIDEO_DURATION_ERROR = 1995;
    public static final int MSG_GET_VIDEO_NOT_MATCH = 1996;
    public static final int MSG_HAS_NO_TFCARD = 1990;
    public static final int MSG_NOT_FOUND_FILE = 1991;
    private static GetPicUtil instance;
    Bundle bundle = new Bundle();
    /* access modifiers changed from: private */
    public String cmd;
    /* access modifiers changed from: private */
    public Context context;
    private byte[] files = null;
    /* access modifiers changed from: private */
    public int getPicNum = 0;
    /* access modifiers changed from: private */
    public int getPicSuccess = 0;
    private int i = 0;
    /* access modifiers changed from: private */
    public boolean isDoing = false;
    /* access modifiers changed from: private */
    public boolean isProess = false;
    ICameraStateChange listener = new ICameraStateChange() {
        public void stateChange() {
            Log.e("ICameraStateChange", "1111");
            GetPicUtil.this.loadFile();
            GetPicUtil.this.getFileList();
            GetPicUtil.this.countVideoTime(GetPicUtil.this.tm, GetPicUtil.this.cmd, GetPicUtil.this.source);
        }
    };
    private FileItem mFileItem;
    /* access modifiers changed from: private */
    public LinkedList<FileItem> mFileItemList = new LinkedList<>();
    private List<FileBeans> mFileList = new ArrayList();
    /* access modifiers changed from: private */
    public ArrayList<String> pathList = new ArrayList<>();
    private String ph = (App.path + "frame/");
    /* access modifiers changed from: private */
    public String source;
    private long startTime1 = 0;
    private long startTime2 = 0;
    /* access modifiers changed from: private */
    public LinkedList<Long> timeList = new LinkedList<>();
    private long timeLock2 = 0;
    private long timeLockl = 0;
    /* access modifiers changed from: private */
    public String tm;
    private int totalTime1 = 0;
    private int totalTime2 = 0;

    static /* synthetic */ int access$608(GetPicUtil x0) {
        int i2 = x0.getPicSuccess;
        x0.getPicSuccess = i2 + 1;
        return i2;
    }

    private GetPicUtil(Context context2) {
        this.context = context2;
    }

    public static GetPicUtil getInstance(Context context2) {
        if (instance == null) {
            instance = new GetPicUtil(context2);
        }
        return instance;
    }

    public void goPlayBackMode(Context context2, String time, String mGCmdSeq, String platformSource) {
        this.tm = time;
        this.cmd = mGCmdSeq;
        this.source = platformSource;
        CameraStateIml.getInstance().addListener(this.listener);
        if (!CmdManager.getInstance().getCurrentState().isCam_sd_state()) {
            ToastUtils.showLongToast(context2, (int) R.string.has_no_tfcard);
            ExternalUtil.sendOpReturnBroadcast(context2, false, "1990");
            Log.e("goPlayBackMode", "R.string.has_no_tfcard");
        } else if (UvcCamera.getInstance().fd_error.equals("not found file")) {
            Log.e("goPlayBackMode", "not found file");
            ExternalUtil.sendOpReturnBroadcast(context2, false, "1991");
        } else {
            Log.e("goPlayBackMode", "" + time + "  " + mGCmdSeq + "  " + platformSource);
            if (CmdManager.getInstance().getCurrentState().isCam_rec_state()) {
                CmdManager.getInstance().recToggle();
            }
        }
    }

    public byte[] loadFile() {
        int fileSize = CmdManager.getInstance().getCurrentState().getFile_index();
        boolean isPlistState = CmdManager.getInstance().getCurrentState().isCam_plist_state();
        CmdManager.getInstance().getCurrentState().print();
        Log.e("GetPicUtil+isPlise ", "" + isPlistState);
        if (fileSize >= 0 || isPlistState) {
            this.files = new byte[(fileSize * 6)];
            CmdManager.getInstance().sendCommand(42, this.files.length, this.files);
            Log.e("GetPicUtil", "" + CameraStateUtil.bytesToHexString(this.files));
            return this.files;
        }
        ExternalUtil.sendOpReturnBroadcast(this.context, false, "1992");
        return null;
    }

    public void getFileList() {
        if (this.files == null || this.files.length < 1) {
            ExternalUtil.sendOpReturnBroadcast(this.context, false, "1992");
            return;
        }
        this.mFileList.clear();
        for (int i2 = 0; i2 * 6 < this.files.length; i2++) {
            byte[] item = new byte[6];
            System.arraycopy(this.files, i2 * 6, item, 0, item.length);
            FileBeans file = byte2FileBean(item);
            file.fileIndex = i2 + 1;
            if (file.fileType == 0 && file.year > 1980) {
                this.mFileList.add(file);
            }
        }
        Log.e("GetPicUtil+getFileList", "" + this.mFileList);
    }

    private FileBeans byte2FileBean(byte[] filebyte) {
        if (filebyte.length != 6) {
            ExternalUtil.sendOpReturnBroadcast(this.context, false, "1992");
            return null;
        }
        FileBeans file = new FileBeans();
        StringBuffer fileName = new StringBuffer();
        byte[] byte_type_num = new byte[2];
        byte[] byte_year_month_day = new byte[2];
        byte[] byte_hour_minute_second = new byte[2];
        System.arraycopy(filebyte, 0, byte_type_num, 0, byte_type_num.length);
        System.arraycopy(filebyte, 2, byte_year_month_day, 0, byte_year_month_day.length);
        System.arraycopy(filebyte, 4, byte_hour_minute_second, 0, byte_hour_minute_second.length);
        short file_type_num = CameraStateUtil.bytesToShort(byte_type_num, 0);
        short file_year_month_day = CameraStateUtil.bytesToShort(byte_year_month_day, 0);
        short file_hour_minute_second = CameraStateUtil.bytesToShort(byte_hour_minute_second, 0);
        file.year = (file_year_month_day >> 9) + 1980;
        file.month = (file_year_month_day >> 5) & 15;
        file.day = file_year_month_day & 31;
        file.hour = (file_hour_minute_second >> 11) & 31;
        file.minute = (file_hour_minute_second >> 5) & 63;
        file.sencond = (file_hour_minute_second & 31) * 2;
        if ((file_type_num & 4096) > 1) {
            fileName.append("LOK");
            file.fileType = 2;
        } else if ((32768 & file_type_num) > 1) {
            fileName.append("MOV");
            file.fileType = 0;
        } else if ((file_type_num & 16384) > 1) {
            fileName.append("PHO");
            file.fileType = 1;
        } else {
            fileName.append("OTHER");
        }
        fileName.append("" + CameraStateUtil.numFormat(file_type_num & 4095));
        Calendar calendar = Calendar.getInstance();
        calendar.set(file.year, file.month - 1, file.day, file.hour, file.minute, file.sencond);
        file.dayTime = (calendar.getTimeInMillis() / 1000) * 1000;
        if ((file_type_num & 16384) > 1) {
            fileName.append(".jpg");
        } else {
            fileName.append(".avi");
        }
        file.fileName = fileName.toString();
        return file;
    }

    public void countVideoTime(String timeString, String mGCmdSeq, String platformSource) {
        if (this.mFileList.size() < 1 || this.mFileList == null || timeString == null) {
            ExternalUtil.sendOpReturnBroadcast(this.context, false, "1992");
            CameraStateIml.getInstance().delListener(this.listener);
            CmdManager.getInstance().recToggle();
        } else if (timeString.contains("：") || timeString.contains("-") || timeString.contains("_") || timeString.contains("“") || timeString.contains("”") || timeString.isEmpty() || timeString.matches("\"^[a-zA-Z]+$\"")) {
            ExternalUtil.sendOpReturnBroadcast(this.context, false, "2002");
            CameraStateIml.getInstance().delListener(this.listener);
            CmdManager.getInstance().recToggle();
        } else if (timeString.contains(":")) {
            String[] s = timeString.split(":");
            for (int i2 = 0; i2 < s.length; i2++) {
                Log.e("msg+s[i]", "i" + i2 + "  " + s[i2]);
                this.timeList.add(Long.valueOf(Long.parseLong(s[i2])));
            }
            this.getPicNum = this.timeList.size();
            proess(mGCmdSeq, platformSource);
            getAll(mGCmdSeq, platformSource);
            if (this.pathList != null) {
                Log.e("程序结束点", "" + this.pathList.size());
            }
            this.pathList.clear();
        }
    }

    /* access modifiers changed from: private */
    public void proess(String mGCmdSeq, String platformSource) {
        Log.e("timeList", "" + this.timeList + "  " + this.timeList.size());
        Collections.sort(this.timeList, new ComparatorValues());
        if (!this.isProess) {
            this.isProess = true;
            if (this.timeList.size() > 0) {
                long time = this.timeList.removeFirst().longValue();
                if (time <= this.mFileList.get(0).dayTime - 60000 || this.mFileList.get(0).dayTime <= time) {
                    getPic(time, mGCmdSeq, platformSource);
                } else {
                    getPicFirst(time, mGCmdSeq, platformSource);
                }
            } else {
                this.isProess = false;
            }
        }
    }

    public class ComparatorValues implements Comparator<Long> {
        public ComparatorValues() {
        }

        public int compare(Long o1, Long o2) {
            if (o2.longValue() < o1.longValue()) {
                return 1;
            }
            return -1;
        }
    }

    private void getFrame(int num, String fileName, long tFilename, String mGCmdSeq, String platformSource) {
        if (!new File(this.ph).exists()) {
            new File(this.ph).mkdir();
        }
        String path = this.ph + tFilename + "_" + mGCmdSeq + "_" + platformSource + ".jpeg";
        Log.e("path", "" + path + " " + tFilename);
        if (!new File(path).exists()) {
            VideoDownloadManager.getInstance().setDownloadDir(path);
            final long j = tFilename;
            final String str = mGCmdSeq;
            final String str2 = platformSource;
            VideoDownloadManager.getInstance().downloadFileThread(path, fileName.toUpperCase(), num, new IProgressBack() {
                public void onFail(int i, String s) {
                    if (i == 0) {
                        ExternalUtil.sendOpReturnBroadcast(GetPicUtil.this.context, false, "2000_" + j + "_" + str + "_" + str2 + ".jpeg");
                    }
                }

                public void onProgress(float v) {
                }

                public void onSuccess(String picturePath) {
                    Log.e("openPhoto1", "" + picturePath);
                    boolean unused = GetPicUtil.this.isProess = false;
                    boolean unused2 = GetPicUtil.this.isDoing = false;
                    GetPicUtil.access$608(GetPicUtil.this);
                    GetPicUtil.this.pathList.add(picturePath);
                    Log.e("onSuccess+size", "" + GetPicUtil.this.timeList.size() + "  " + GetPicUtil.this.mFileItemList.size() + "  " + picturePath);
                    if (GetPicUtil.this.timeList.size() > 0) {
                        GetPicUtil.this.proess(str, str2);
                    }
                    if (GetPicUtil.this.mFileItemList.size() > 0) {
                        GetPicUtil.this.getAll(str, str2);
                        return;
                    }
                    GetPicUtil.this.bundle.putStringArrayList("PATHLIST", GetPicUtil.this.pathList);
                    GetPicUtil.this.sendIntent(GetPicUtil.this.getPicNum, GetPicUtil.this.getPicSuccess, GetPicUtil.this.bundle);
                    int unused3 = GetPicUtil.this.getPicSuccess = 0;
                    CameraStateIml.getInstance().delListener(GetPicUtil.this.listener);
                    Log.e("openPhoto1+end", "end");
                    GetPicUtil.this.pathList.clear();
                    CmdManager.getInstance().recToggle();
                }
            });
            return;
        }
        this.isProess = false;
        this.isDoing = false;
        this.pathList.add(this.ph + tFilename + "_" + mGCmdSeq + "_" + platformSource + ".jpeg");
        if (this.mFileItemList.size() > 0) {
            getAll(mGCmdSeq, platformSource);
            return;
        }
        this.bundle.putStringArrayList("PATHLIST", this.pathList);
        sendIntent(this.getPicNum, this.pathList.size(), this.bundle);
        CameraStateIml.getInstance().delListener(this.listener);
        this.pathList.clear();
        CmdManager.getInstance().recToggle();
    }

    public void sendIntent(int num, int successNum, Bundle bundle2) {
        Intent takeIntent = new Intent();
        takeIntent.setAction(HUIYING_BROADCAST_GETPIC_RECV);
        takeIntent.putExtra(ExternalUtil.EXTRA_MEDIA_TYPE, 0);
        takeIntent.putExtra(ExternalUtil.KEY_TYPE, 1004);
        takeIntent.putExtra(EXTRA_VIDEO_FRAME_COUNT, num);
        takeIntent.putExtra(EXTRA_VIDEO_GET_FRAME_PIC_SUCCESS_COUNT, successNum);
        takeIntent.putExtra(EXTRA_VIDEO_GET_FRAME_PIC_BUNDLE_LIST, bundle2);
        takeIntent.setFlags(32);
        this.context.sendBroadcast(takeIntent);
    }

    public static String getDate2String(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(time));
    }

    private void getPic(long time, String mGCmdSeq, String platformSource) {
        Log.e("getPic", "time:" + time + " timeList:" + this.timeList + this.mFileList.get(0).dayTime + " " + this.mFileList.get(this.mFileList.size() - 1).dayTime + "  " + this.timeList.size());
        long ttend = this.mFileList.get(this.mFileList.size() - 1).dayTime;
        if (this.mFileList.get(0).dayTime - 60000 > time || time > ttend) {
            this.isProess = false;
            Log.e("getPic+timeList.size()7", "0000");
            if (this.pathList != null) {
                this.pathList.add("1996_" + time + "_" + mGCmdSeq + "_" + platformSource + ".jpeg");
            }
            Log.e("getPic+timeList.size()8", "0000");
            if (this.timeList.size() > 0) {
                Log.e("getPic+timeList.size()1", "0000");
                proess(mGCmdSeq, platformSource);
            } else {
                Log.e("getPic+timeList.size()2", "0000");
                if (this.pathList != null && this.pathList.size() == this.getPicNum) {
                    this.bundle.putStringArrayList("PATHLIST", this.pathList);
                    sendIntent(this.getPicNum, this.getPicSuccess, this.bundle);
                    this.pathList.clear();
                    CmdManager.getInstance().recToggle();
                    return;
                }
            }
        }
        for (int i2 = 0; i2 < this.mFileList.size(); i2++) {
            Log.e("iiiiii", "" + i2 + "  " + this.timeList.size());
            if (i2 <= this.mFileList.size() - 2 && this.mFileList.get(i2 + 1).dayTime > time && this.mFileList.get(i2).dayTime < time) {
                String fileName = this.mFileList.get(i2 + 1).fileName.toUpperCase();
                long tTime = (long) CmdManager.getInstance().getVideoDurationTime(fileName);
                Log.e("msg+fileName", "" + fileName + "  " + tTime);
                if (tTime == 0 && this.timeList.size() >= 0) {
                    if (this.pathList != null) {
                        this.pathList.add(this.ph + MSG_GET_VIDEO_DURATION_ERROR + "_" + time + "_" + mGCmdSeq + "_" + platformSource + ".jpeg");
                    }
                    this.isProess = false;
                    proess(mGCmdSeq, platformSource);
                }
                this.startTime2 = this.mFileList.get(i2 + 1).dayTime - tTime;
                Log.e("msg", "time:" + time + " startTime" + this.startTime2 + " mFileList.get(i).dayTime" + this.mFileList.get(i2).dayTime + " totalTime " + tTime);
                int num = (int) ((time - this.startTime2) / 33);
                Log.e("msg", "totalTime2 " + tTime + " startTime2  " + this.startTime2 + " num " + num);
                if (num > 0 && tTime > 0) {
                    if (num >= 1800) {
                        num = 1790;
                    }
                    this.mFileItem = new FileItem(num, time, this.mFileList.get(i2 + 1).fileName);
                    this.mFileItemList.add(this.mFileItem);
                    if (this.timeList.size() >= 0) {
                        this.isProess = false;
                        proess(mGCmdSeq, platformSource);
                    }
                } else if (this.timeList.size() >= 0) {
                    this.isProess = false;
                    proess(mGCmdSeq, platformSource);
                }
            }
        }
    }

    private void getPicFirst(long time, String mGCmdSeq, String platformSource) {
        if (time > this.mFileList.get(0).dayTime - 60000 && this.mFileList.get(0).dayTime > time) {
            String fName = this.mFileList.get(0).fileName.toUpperCase();
            long time1 = (long) CmdManager.getInstance().getVideoDurationTime(fName);
            Log.e("msg0+fileName0", "" + fName + "  " + time1);
            if (time1 == 0 && this.timeList.size() >= 0) {
                if (this.pathList != null) {
                    this.pathList.add(this.ph + MSG_GET_VIDEO_DURATION_ERROR + "_" + time + "_" + mGCmdSeq + "_" + platformSource + ".jpeg");
                }
                this.isProess = false;
                proess(mGCmdSeq, platformSource);
            }
            long stime2 = this.mFileList.get(0).dayTime - time1;
            Log.e("msg0", "time:" + time + " startTime" + stime2 + " mFileList.get(i).dayTime" + this.mFileList.get(this.i).dayTime + " totalTime " + time1);
            int num = (int) ((time - stime2) / 33);
            Log.e("msg0", "totalTime2 " + time1 + " startTime2  " + stime2 + " num " + num);
            if (num <= 0 || time1 <= 0) {
                if (this.timeList.size() >= 0) {
                    this.isProess = false;
                    proess(mGCmdSeq, platformSource);
                }
                if (this.pathList != null) {
                    this.pathList.add(this.ph + MSG_GET_VIDEO_NOT_MATCH + "_" + time + "_" + mGCmdSeq + "_" + platformSource + ".jpeg");
                    return;
                }
                return;
            }
            if (num >= 1800) {
                num = 1790;
            }
            this.mFileItem = new FileItem(num, time, this.mFileList.get(0).fileName);
            this.mFileItemList.add(this.mFileItem);
            if (this.timeList.size() >= 0) {
                this.isProess = false;
                proess(mGCmdSeq, platformSource);
            }
        }
    }

    /* access modifiers changed from: private */
    public void getAll(String mGCmdSeq, String platformSource) {
        if (!this.isDoing) {
            this.isDoing = true;
            Log.e("isDoing", "" + this.isDoing);
            if (this.mFileItemList.size() > 0) {
                FileItem item = this.mFileItemList.removeFirst();
                Log.e("FileItem", "" + item.getZhenCount() + "  " + item.getVideoTime() + "  " + item.getFileName());
                this.isDoing = false;
                getFrame(item.getZhenCount(), item.getFileName(), item.getVideoTime(), mGCmdSeq, platformSource);
                if (this.pathList != null && this.pathList.size() == this.getPicNum) {
                    this.bundle.putStringArrayList("PATHLIST", this.pathList);
                    sendIntent(this.getPicNum, this.getPicSuccess, this.bundle);
                    this.pathList.clear();
                    CmdManager.getInstance().recToggle();
                    return;
                }
                return;
            }
            this.isDoing = false;
            if (this.timeList.size() < 1 && this.mFileItemList.size() < 1) {
                CameraStateIml.getInstance().delListener(this.listener);
                CmdManager.getInstance().recToggle();
            }
        }
    }
}
