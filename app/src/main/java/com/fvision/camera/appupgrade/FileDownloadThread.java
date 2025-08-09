package com.fvision.camera.appupgrade;

import android.util.Log;
import com.alibaba.sdk.android.oss.common.utils.HttpHeaders;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

public class FileDownloadThread extends Thread {
    private static final int BUFFER_SIZE = 1024;
    private int curPosition;
    private int downloadSize = 0;
    private int endPosition;
    private File file;
    private boolean finished = false;
    private int startPosition;
    private URL url;

    public FileDownloadThread(URL url2, File file2, int startPosition2, int endPosition2) {
        this.url = url2;
        this.file = file2;
        this.startPosition = startPosition2;
        this.curPosition = startPosition2;
        this.endPosition = endPosition2;
    }

    public void run() {
        int len;
        byte[] buf = new byte[1024];
        try {
            URLConnection con = this.url.openConnection();
            con.setAllowUserInteraction(true);
            con.setRequestProperty(HttpHeaders.RANGE, "bytes=" + this.startPosition + "-" + this.endPosition);
            RandomAccessFile fos = new RandomAccessFile(this.file, "rw");
            try {
                fos.seek((long) this.startPosition);
                BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
                while (this.curPosition < this.endPosition && (len = bis.read(buf, 0, 1024)) != -1) {
                    try {
                        fos.write(buf, 0, len);
                        this.curPosition += len;
                        if (this.curPosition > this.endPosition) {
                            this.downloadSize += (len - (this.curPosition - this.endPosition)) + 1;
                        } else {
                            this.downloadSize += len;
                        }
                    } catch (IOException e) {
                        e = e;
                        RandomAccessFile randomAccessFile = fos;
                        BufferedInputStream bufferedInputStream = bis;
                    }
                }
                this.finished = true;
                bis.close();
                fos.close();
                RandomAccessFile randomAccessFile2 = fos;
                BufferedInputStream bufferedInputStream2 = bis;
            } catch (IOException e2) {
                e = e2;
                RandomAccessFile randomAccessFile3 = fos;
                Log.d(getName() + " Error:", e.getMessage());
            }
        } catch (IOException e3) {
            e = e3;
            Log.d(getName() + " Error:", e.getMessage());
        }
    }

    public boolean isFinished() {
        return this.finished;
    }

    public int getDownloadSize() {
        return this.downloadSize;
    }
}
