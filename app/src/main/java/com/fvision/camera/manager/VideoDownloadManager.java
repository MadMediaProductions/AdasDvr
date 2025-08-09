package com.fvision.camera.manager;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import com.fvision.camera.bean.DataPaket;
import com.fvision.camera.bean.FileBean;
import com.fvision.camera.iface.ICameraStateChange;
import com.fvision.camera.iface.IGetPlackBack;
import com.fvision.camera.iface.IProgressBack;
import com.fvision.camera.iface.ISmallVideoBack;
import com.fvision.camera.util.CameraStateUtil;
import com.fvision.camera.util.LogUtils;
import com.fvision.camera.utils.DoCmdUtil;
import com.huiying.cameramjpeg.UvcCamera;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class VideoDownloadManager {
    public static final int MOB = 5;
    public static final int WHAT_GET_BACK_LIST = 10;
    public static final int WHAT_RECORD_SMALL_VIDEO_END = 1;
    private static VideoDownloadManager _instance;
    /* access modifiers changed from: private */
    public ICameraStateChange cmdICameraStateChange = new ICameraStateChange() {
        public void stateChange() {
            LogUtils.d("small video 录像状态改变 " + CmdManager.getInstance().getCurrentState().isCam_rec_state());
            if (CmdManager.getInstance().getCurrentState().isCam_rec_state()) {
                CameraStateIml.getInstance().delListener(VideoDownloadManager.this.cmdICameraStateChange);
                if (VideoDownloadManager.this.isUseDuration) {
                    LogUtils.d("small video 开启倒计时" + (VideoDownloadManager.this.duration / 1000) + "秒");
                    VideoDownloadManager.this.mHandler.sendEmptyMessageDelayed(1, VideoDownloadManager.this.duration);
                }
            } else {
                CmdManager.getInstance().recToggle();
            }
            boolean unused = VideoDownloadManager.this.recordLock = true;
        }
    };
    private String downloadDir = (Environment.getExternalStorageDirectory() + "/uvccameramjpeg/VIDEO/");
    long duration = 0;
    private String filePath;
    private ICameraStateChange getBackListStateChange = new ICameraStateChange() {
        public void stateChange() {
            LogUtils.d("backlist 录像状态改变 " + CmdManager.getInstance().getCurrentState().isCam_rec_state());
            if (!CmdManager.getInstance().getCurrentState().isCam_rec_state()) {
                VideoDownloadManager.this.getBackList1();
            }
        }
    };
    private boolean getPlackBackLock = false;
    boolean isUseDuration = false;
    private float loadProgress = 0.0f;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (CmdManager.getInstance().getCurrentState().isCam_rec_state()) {
                        CmdManager.getInstance().recToggle();
                        CameraStateIml.getInstance().addListener(VideoDownloadManager.this.mICameraStateChange);
                        return;
                    }
                    if (VideoDownloadManager.this.mIsmallVideoBack != null) {
                        VideoDownloadManager.this.mIsmallVideoBack.fail(7, "录制小视频被终止");
                    }
                    boolean unused = VideoDownloadManager.this.recordLock = false;
                    return;
                default:
                    return;
            }
        }
    };
    /* access modifiers changed from: private */
    public ICameraStateChange mICameraStateChange = new ICameraStateChange() {
        public void stateChange() {
            if (!CmdManager.getInstance().getCurrentState().isCam_rec_state()) {
                VideoDownloadManager.this.endSmallVideoOp();
                CameraStateIml.getInstance().delListener(VideoDownloadManager.this.mICameraStateChange);
            }
        }
    };
    private IGetPlackBack mIGetPlackBack;
    /* access modifiers changed from: private */
    public ISmallVideoBack mIsmallVideoBack;
    /* access modifiers changed from: private */
    public boolean recordLock = false;

    public static VideoDownloadManager getInstance() {
        if (_instance == null) {
            _instance = new VideoDownloadManager();
        }
        return _instance;
    }

    public boolean recordSmallVideo(boolean isUseDuration2, long duration2, ISmallVideoBack back) {
        this.mIsmallVideoBack = back;
        if (duration2 > 30000 && isUseDuration2) {
            if (back != null) {
                back.fail(1, "时长不能超过30秒");
            }
            this.recordLock = false;
            return false;
        } else if (duration2 < 5000 && isUseDuration2) {
            if (back != null) {
                back.fail(2, "时长不能小于5秒");
            }
            this.recordLock = false;
            return false;
        } else if (this.recordLock) {
            if (back == null) {
                return false;
            }
            back.fail(3, "正在录制...");
            return false;
        } else if (!UvcCamera.getInstance().isInit()) {
            if (back != null) {
                back.fail(4, "未检测到行车记录仪");
            }
            this.recordLock = false;
            return false;
        } else if (!UvcCamera.getInstance().cmd_fd_error.equals("Success")) {
            if (back != null) {
                back.fail(5, "指令文件未初始化");
            }
            this.recordLock = false;
            return false;
        } else {
            if (CmdManager.getInstance().getCurrentState().isCam_rec_state()) {
                LogUtils.d("small video 关闭录像");
                CmdManager.getInstance().recToggle();
            } else {
                LogUtils.d("small video 开启录像");
                CmdManager.getInstance().recToggle();
            }
            LogUtils.d("small video 等待录像开启...");
            this.isUseDuration = isUseDuration2;
            this.duration = duration2;
            CameraStateIml.getInstance().addListener(this.cmdICameraStateChange);
            return true;
        }
    }

    public boolean startSmallVideo(long duration2, ISmallVideoBack back) {
        return recordSmallVideo(true, duration2, back);
    }

    public boolean startSmallVideo(ISmallVideoBack back) {
        return recordSmallVideo(false, -1, back);
    }

    public void endSmallVideo() {
        this.mHandler.sendEmptyMessage(1);
    }

    /* access modifiers changed from: private */
    public void endSmallVideoOp() {
    }

    public void downloadFileThread(final String fileName, final int fileType, final IProgressBack back) {
        new Thread(new Runnable() {
            public void run() {
                VideoDownloadManager.this.downloadFile(fileName, fileType, back);
            }
        }).start();
    }

    public void downloadFileThread(final String fileName, final IProgressBack back) {
        new Thread(new Runnable() {
            public void run() {
                VideoDownloadManager.this.downloadFile(fileName, back);
            }
        }).start();
    }

    public void downloadFileThread(String fileName, String feName, int num, IProgressBack back) {
        final String str = fileName;
        final String str2 = feName;
        final IProgressBack iProgressBack = back;
        final int i = num;
        new Thread(new Runnable() {
            public void run() {
                if (str != null && str2 != null && iProgressBack != null) {
                    VideoDownloadManager.this.downloadFile(str, str2, i, iProgressBack);
                }
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public void downloadFile(String fPath, String feName, int num, IProgressBack back) {
        long count;
        long dataSize;
        this.loadProgress = 0.0f;
        CmdManager.getInstance().getVideoDurationTime(feName);
        long currentTimeMillis = System.currentTimeMillis();
        LogUtils.d("small video 开始下载 ");
        long currentTimeMillis2 = System.currentTimeMillis();
        LogUtils.d(" start download time=" + System.currentTimeMillis() + " item.fileName" + fPath);
        try {
            LogUtils.d("small video filePath " + fPath);
            File file = new File(fPath);
            if (file.exists()) {
                LogUtils.e("文件已存在 " + fPath);
                if (this.mIsmallVideoBack != null) {
                    this.mIsmallVideoBack.fail(8, this.filePath);
                }
                if (back != null) {
                    back.onFail(0, this.filePath);
                }
                this.recordLock = false;
                return;
            }
            Log.e("sss下载中....", "" + UvcCamera.getInstance().cmd_fd_error);
            int filesize = CmdManager.getInstance().getVideoZhenTime(num);
            Log.e("帧图片+fileSize", "" + filesize);
            if (filesize < 1) {
                LogUtils.e("downfile 下载文件失败，打开错误 " + fPath);
                if (this.mIsmallVideoBack != null) {
                    this.mIsmallVideoBack.fail(9, "downfile 下载失败，打开文件错误");
                }
                if (back != null) {
                    back.onFail(1, "downfile 下载失败，打开文件错误");
                }
                this.recordLock = false;
                this.recordLock = false;
                return;
            }
            long length_k = (long) filesize;
            long length_kb = length_k / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
            long endDataSize = length_k % ((long) 8192);
            int offset = 0;
            if (endDataSize != 0) {
                count = (length_kb / ((long) 8)) + 1;
            } else {
                count = length_kb / ((long) 8);
            }
            LogUtils.d("downfile length_k " + length_k + " length_kb " + length_kb + " count " + count + " endDataSize " + endDataSize);
            RandomAccessFile randomAccessFile = new RandomAccessFile(fPath, "rw");
            for (int i = 0; ((long) i) < count; i++) {
                LogUtils.d("downfile small video " + i + "  " + count);
                if (this.mIsmallVideoBack != null) {
                    this.mIsmallVideoBack.downloadProgress((float) (((long) (i * 100)) / count));
                }
                if (back != null) {
                    float progress = (float) (((long) (i * 100)) / count);
                    if (progress != this.loadProgress) {
                        this.loadProgress = progress;
                        back.onProgress(this.loadProgress);
                    }
                }
                if (((long) i) < count - 1) {
                    dataSize = (long) 8192;
                } else if (endDataSize == 0) {
                    dataSize = (long) 8192;
                } else {
                    dataSize = endDataSize;
                }
                DataPaket paket = CmdManager.getInstance().getVideoFileData(i, (int) dataSize);
                Log.e("paket+dataSize", "" + ((int) dataSize));
                if (paket == null) {
                    LogUtils.d("downfile 下载失败,获取数据失败");
                    file.delete();
                    if (this.mIsmallVideoBack != null) {
                        this.mIsmallVideoBack.fail(6, "下载失败,获取数据失败");
                    }
                    if (back != null) {
                        back.onFail(2, "下载失败,获取数据失败");
                    }
                    this.recordLock = false;
                    this.recordLock = false;
                    return;
                }
                LogUtils.d("downfile  offset " + i + " paket.length " + paket.length);
                randomAccessFile.seek((long) offset);
                offset += paket.length;
                randomAccessFile.write(paket.data);
            }
            if (this.mIsmallVideoBack != null) {
                this.mIsmallVideoBack.success(fPath);
            }
            if (back != null) {
                long currentTimeMillis3 = System.currentTimeMillis();
                back.onSuccess(fPath);
            }
            randomAccessFile.close();
            this.recordLock = false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (this.mIsmallVideoBack != null) {
                this.mIsmallVideoBack.fail(100, "下载失败: FileNotFoundException " + e.getMessage());
            }
            if (back != null) {
                back.onFail(0, "下载失败:FileNotFoundException " + e.getMessage());
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            if (this.mIsmallVideoBack != null) {
                this.mIsmallVideoBack.fail(100, "下载失败: IOException " + e2.getMessage());
            }
            if (back != null) {
                back.onFail(3, "下载失败: IOException " + e2.getMessage());
            }
        } finally {
            this.recordLock = false;
        }
    }

    /* access modifiers changed from: private */
    public void downloadFile(String fileName, int fileType, IProgressBack back) {
        long count;
        long dataSize;
        this.loadProgress = 0.0f;
        UvcCamera.getInstance().stopPreview();
        long startTime = System.currentTimeMillis();
        LogUtils.d("small video 开始下载 ");
        long startDounloadTime = System.currentTimeMillis();
        Log.e("downloadFile", " start download time=" + System.currentTimeMillis() + " item.fileName" + fileName);
        try {
            this.filePath = this.downloadDir + fileName;
            LogUtils.d("small video filePath " + this.filePath);
            File video = new File(this.filePath);
            if (video.exists()) {
                video.delete();
            }
            if (video.exists()) {
                LogUtils.e("文件已存在 " + this.filePath);
                if (this.mIsmallVideoBack != null) {
                    this.mIsmallVideoBack.fail(8, this.filePath);
                }
                if (back != null) {
                    back.onFail(0, this.filePath);
                }
                UvcCamera.getInstance().startPreview();
                this.recordLock = false;
                return;
            }
            video.createNewFile();
            int filesize = CmdManager.getInstance().openReadFile(fileType, fileName);
            if (filesize < 1) {
                LogUtils.e("downfile 下载文件失败，打开错误 " + fileName);
                if (this.mIsmallVideoBack != null) {
                    this.mIsmallVideoBack.fail(9, "downfile 下载失败，打开文件错误");
                }
                if (back != null) {
                    back.onFail(1, "downfile 下载失败，打开文件错误");
                }
                UvcCamera.getInstance().startPreview();
                this.recordLock = false;
                UvcCamera.getInstance().startPreview();
                this.recordLock = false;
                return;
            }
            long length_k = (long) filesize;
            long length_kb = length_k / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
            long endDataSize = length_k % ((long) 8192);
            int offset = 0;
            if (endDataSize != 0) {
                count = (length_kb / ((long) 8)) + 1;
            } else {
                count = length_kb / ((long) 8);
            }
            Log.e("downfile length_k ", "downfile length_k " + length_k + " length_kb " + length_kb + " count " + count + " endDataSize " + endDataSize);
            RandomAccessFile randomAccessFile = new RandomAccessFile(this.filePath, "rw");
            for (int i = 0; ((long) i) < count; i++) {
                LogUtils.d("downfile small video " + i + "  " + count);
                if (this.mIsmallVideoBack != null) {
                    this.mIsmallVideoBack.downloadProgress((float) (((long) (i * 100)) / count));
                }
                if (back != null) {
                    float progress = (float) (((long) (i * 100)) / count);
                    if (progress != this.loadProgress) {
                        this.loadProgress = progress;
                        back.onProgress(this.loadProgress);
                    }
                }
                if (((long) i) < count - 1) {
                    dataSize = (long) 8192;
                } else if (endDataSize == 0) {
                    dataSize = (long) 8192;
                } else {
                    dataSize = endDataSize;
                }
                DataPaket paket = CmdManager.getInstance().getFileData(i, (int) dataSize);
                if (paket == null) {
                    LogUtils.e("downfile 下载失败,获取数据失败");
                    CmdManager.getInstance().closeReadFile();
                    video.delete();
                    if (this.mIsmallVideoBack != null) {
                        this.mIsmallVideoBack.fail(6, "下载失败,获取数据失败");
                    }
                    if (back != null) {
                        back.onFail(2, "下载失败,获取数据失败");
                    }
                    UvcCamera.getInstance().startPreview();
                    this.recordLock = false;
                    UvcCamera.getInstance().startPreview();
                    this.recordLock = false;
                    return;
                }
                LogUtils.d("downfile  offset " + i + " paket.length " + paket.length);
                randomAccessFile.seek((long) offset);
                offset += paket.length;
                randomAccessFile.write(paket.data);
            }
            if (this.mIsmallVideoBack != null) {
                this.mIsmallVideoBack.success(this.filePath);
            }
            if (back != null) {
                LogUtils.d(" 下载时长:" + ((System.currentTimeMillis() - startTime) / 1000));
                back.onSuccess(this.filePath);
            }
            randomAccessFile.close();
            CmdManager.getInstance().closeDownloadFile();
            LogUtils.d("small video download time=" + ((System.currentTimeMillis() - startDounloadTime) / 1000));
            UvcCamera.getInstance().startPreview();
            this.recordLock = false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (this.mIsmallVideoBack != null) {
                this.mIsmallVideoBack.fail(100, "下载失败: FileNotFoundException " + e.getMessage());
            }
            if (back != null) {
                back.onFail(0, "下载失败:FileNotFoundException " + e.getMessage());
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            if (this.mIsmallVideoBack != null) {
                this.mIsmallVideoBack.fail(100, "下载失败: IOException " + e2.getMessage());
            }
            if (back != null) {
                back.onFail(3, "下载失败: IOException " + e2.getMessage());
            }
        } finally {
            UvcCamera.getInstance().startPreview();
            this.recordLock = false;
        }
    }

    /* access modifiers changed from: private */
    public void downloadFile(String fileName, IProgressBack back) {
        long count;
        long dataSize;
        this.loadProgress = 0.0f;
        UvcCamera.getInstance().stopPreview();
        long startTime = System.currentTimeMillis();
        LogUtils.d("small video 开始下载 ");
        long startDounloadTime = System.currentTimeMillis();
        LogUtils.d(" start download time=" + System.currentTimeMillis() + " item.fileName" + fileName);
        try {
            this.filePath = this.downloadDir + fileName;
            LogUtils.d("small video filePath " + this.filePath);
            File video = new File(this.filePath);
            if (video.exists()) {
                video.delete();
            }
            if (video.exists()) {
                LogUtils.e("文件已存在 " + this.filePath);
                if (this.mIsmallVideoBack != null) {
                    this.mIsmallVideoBack.fail(8, this.filePath);
                }
                if (back != null) {
                    back.onFail(0, this.filePath);
                }
                UvcCamera.getInstance().startPreview();
                this.recordLock = false;
                return;
            }
            video.createNewFile();
            int filesize = CmdManager.getInstance().openReadFiles(fileName);
            if (filesize < 1) {
                LogUtils.e("downfile 下载文件失败，打开错误 " + fileName);
                if (this.mIsmallVideoBack != null) {
                    this.mIsmallVideoBack.fail(9, "downfile 下载失败，打开文件错误");
                }
                if (back != null) {
                    back.onFail(1, "downfile 下载失败，打开文件错误");
                }
                UvcCamera.getInstance().startPreview();
                this.recordLock = false;
                UvcCamera.getInstance().startPreview();
                this.recordLock = false;
                return;
            }
            long length_k = (long) filesize;
            long length_kb = length_k / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
            long endDataSize = length_k % ((long) 8192);
            int offset = 0;
            if (endDataSize != 0) {
                count = (length_kb / ((long) 8)) + 1;
            } else {
                count = length_kb / ((long) 8);
            }
            LogUtils.d("downfile length_k " + length_k + " length_kb " + length_kb + " count " + count + " endDataSize " + endDataSize);
            RandomAccessFile randomAccessFile = new RandomAccessFile(this.filePath, "rw");
            for (int i = 0; ((long) i) < count; i++) {
                LogUtils.d("downfile small video " + i + "  " + count);
                if (this.mIsmallVideoBack != null) {
                    this.mIsmallVideoBack.downloadProgress((float) (((long) (i * 100)) / count));
                }
                if (back != null) {
                    float progress = (float) (((long) (i * 100)) / count);
                    if (progress != this.loadProgress) {
                        this.loadProgress = progress;
                        back.onProgress(this.loadProgress);
                    }
                }
                if (((long) i) < count - 1) {
                    dataSize = (long) 8192;
                } else if (endDataSize == 0) {
                    dataSize = (long) 8192;
                } else {
                    dataSize = endDataSize;
                }
                DataPaket paket = CmdManager.getInstance().getFileData(i, (int) dataSize);
                if (paket == null) {
                    LogUtils.e("downfile 下载失败,获取数据失败");
                    CmdManager.getInstance().closeReadFile();
                    video.delete();
                    if (this.mIsmallVideoBack != null) {
                        this.mIsmallVideoBack.fail(6, "下载失败,获取数据失败");
                    }
                    if (back != null) {
                        back.onFail(2, "下载失败,获取数据失败");
                    }
                    UvcCamera.getInstance().startPreview();
                    this.recordLock = false;
                    UvcCamera.getInstance().startPreview();
                    this.recordLock = false;
                    return;
                }
                LogUtils.d("downfile  offset " + i + " paket.length " + paket.length);
                randomAccessFile.seek((long) offset);
                offset += paket.length;
                randomAccessFile.write(paket.data);
            }
            if (this.mIsmallVideoBack != null) {
                this.mIsmallVideoBack.success(this.filePath);
            }
            if (back != null) {
                LogUtils.d(" 下载时长:" + ((System.currentTimeMillis() - startTime) / 1000));
                back.onSuccess(this.filePath);
            }
            randomAccessFile.close();
            CmdManager.getInstance().closeDownloadFile();
            LogUtils.d("small video download time=" + ((System.currentTimeMillis() - startDounloadTime) / 1000));
            UvcCamera.getInstance().startPreview();
            this.recordLock = false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (this.mIsmallVideoBack != null) {
                this.mIsmallVideoBack.fail(100, "下载失败: FileNotFoundException " + e.getMessage());
            }
            if (back != null) {
                back.onFail(0, "下载失败:FileNotFoundException " + e.getMessage());
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            if (this.mIsmallVideoBack != null) {
                this.mIsmallVideoBack.fail(100, "下载失败: IOException " + e2.getMessage());
            }
            if (back != null) {
                back.onFail(3, "下载失败: IOException " + e2.getMessage());
            }
        } finally {
            UvcCamera.getInstance().startPreview();
            this.recordLock = false;
        }
    }

    public void setDownloadDir(String downloadDir2) {
        this.downloadDir = downloadDir2;
    }

    public String getDownloadDir() {
        return this.downloadDir;
    }

    public void getBackList(IGetPlackBack back) {
        this.mIGetPlackBack = back;
        if (UvcCamera.getInstance().isInit()) {
            this.getPlackBackLock = true;
            CameraStateIml.getInstance().addListener(this.getBackListStateChange);
            if (CmdManager.getInstance().getCurrentState().isCam_rec_state()) {
                LogUtils.d("backlist 关闭录像");
                if (!CmdManager.getInstance().recToggle() && back != null) {
                    back.onFail(104, "关闭录像失败 " + UvcCamera.getInstance().cmd_fd_error);
                    LogUtils.e("backlist", "关闭录像失败 " + UvcCamera.getInstance().cmd_fd_error);
                }
                this.getPlackBackLock = false;
                return;
            }
            getBackList1();
        } else if (back != null) {
            back.onFail(101, "未连接到记录仪");
        }
    }

    /* access modifiers changed from: private */
    public void getBackList1() {
        int fileSize = CmdManager.getInstance().getCurrentState().getFile_index();
        if (fileSize < 1) {
            if (this.mIGetPlackBack != null) {
                this.mIGetPlackBack.onFail(102, "文件数小于1 " + fileSize);
            }
            if (!CmdManager.getInstance().getCurrentState().isCam_rec_state()) {
                CmdManager.getInstance().recToggle();
            }
            this.getPlackBackLock = false;
            return;
        }
        byte[] files = new byte[(fileSize * 6)];
        if (CmdManager.getInstance().sendCommand(42, files.length, files) < 0) {
            if (this.mIGetPlackBack != null) {
                this.mIGetPlackBack.onFail(103, "指令发送失败 " + UvcCamera.getInstance().cmd_fd_error);
            }
            if (!CmdManager.getInstance().getCurrentState().isCam_rec_state()) {
                CmdManager.getInstance().recToggle();
            }
            this.getPlackBackLock = false;
            return;
        }
        LogUtils.d("" + CameraStateUtil.bytesToHexString(files));
        List<FileBean> mFileList = new ArrayList<>();
        for (int i = 0; i * 6 < files.length; i++) {
            byte[] item = new byte[6];
            System.arraycopy(files, i * 6, item, 0, item.length);
            FileBean file = byte2FileBean(item);
            file.fileIndex = i + 1;
            if (file.year > 1980) {
                mFileList.add(file);
            }
        }
        if (!CmdManager.getInstance().getCurrentState().isCam_rec_state()) {
            CmdManager.getInstance().recToggle();
        }
        if (mFileList.size() > 1) {
            if (this.mIGetPlackBack != null) {
                this.mIGetPlackBack.onSuccess(mFileList);
            }
        } else if (this.mIGetPlackBack != null) {
            this.mIGetPlackBack.onFail(105, "没有回放文件");
        }
    }

    private FileBean byte2FileBean(byte[] filebyte) {
        if (filebyte.length != 6) {
            if (this.mIGetPlackBack != null) {
                this.mIGetPlackBack.onFail(150, "filebyte.length != Cmd_Const.FILE_BYTE_SIZE");
            }
            return null;
        }
        FileBean file = new FileBean();
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
        Log.e("file_type_num", DoCmdUtil.COMMAND_LINE_END + file_type_num);
        if ((file_type_num & 4096) > 1) {
            if ((file_type_num & 8192) > 1) {
                fileName.append("LOB");
                file.fileType = 2;
            } else {
                fileName.append("LOK");
                file.fileType = 2;
            }
        } else if ((32768 & file_type_num) > 1) {
            fileName.append("MOV");
            file.fileType = 0;
        } else if ((file_type_num & 8192) > 1) {
            fileName.append("MOB");
            file.fileType = 5;
            Log.e("mob", "" + fileName.toString());
        } else if ((file_type_num & 16384) > 1) {
            fileName.append("PHO");
            file.fileType = 1;
        } else {
            fileName.append("OTHER");
        }
        fileName.append("" + CameraStateUtil.numFormat(file_type_num & 4095));
        Calendar calendar = Calendar.getInstance();
        calendar.set(file.year, file.month - 1, file.day, file.hour, file.minute, file.sencond);
        file.dayTime = calendar.getTimeInMillis();
        if ((file_type_num & 16384) > 1) {
            fileName.append(".jpg");
        } else {
            fileName.append(".AVI");
        }
        file.fileName = fileName.toString();
        Log.e("fileName", DoCmdUtil.COMMAND_LINE_END + file.fileName);
        return file;
    }
}
