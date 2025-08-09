package com.fvision.camera.appupgrade;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FileOper {
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0029 A[SYNTHETIC, Splitter:B:18:0x0029] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0035 A[SYNTHETIC, Splitter:B:24:0x0035] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean stringInertFile(String r8, File r9) {
        /*
            r6 = 0
            byte[] r0 = r8.getBytes()
            r4 = 0
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0022 }
            r7 = 1
            r3.<init>(r9, r7)     // Catch:{ Exception -> 0x0022 }
            java.io.BufferedOutputStream r5 = new java.io.BufferedOutputStream     // Catch:{ Exception -> 0x0022 }
            r5.<init>(r3)     // Catch:{ Exception -> 0x0022 }
            r5.write(r0)     // Catch:{ Exception -> 0x0041, all -> 0x003e }
            r6 = 1
            if (r5 == 0) goto L_0x0044
            r5.close()     // Catch:{ IOException -> 0x001c }
            r4 = r5
        L_0x001b:
            return r6
        L_0x001c:
            r2 = move-exception
            r2.printStackTrace()
            r4 = r5
            goto L_0x001b
        L_0x0022:
            r1 = move-exception
        L_0x0023:
            r1.printStackTrace()     // Catch:{ all -> 0x0032 }
            r6 = 0
            if (r4 == 0) goto L_0x001b
            r4.close()     // Catch:{ IOException -> 0x002d }
            goto L_0x001b
        L_0x002d:
            r2 = move-exception
            r2.printStackTrace()
            goto L_0x001b
        L_0x0032:
            r7 = move-exception
        L_0x0033:
            if (r4 == 0) goto L_0x0038
            r4.close()     // Catch:{ IOException -> 0x0039 }
        L_0x0038:
            throw r7
        L_0x0039:
            r2 = move-exception
            r2.printStackTrace()
            goto L_0x0038
        L_0x003e:
            r7 = move-exception
            r4 = r5
            goto L_0x0033
        L_0x0041:
            r1 = move-exception
            r4 = r5
            goto L_0x0023
        L_0x0044:
            r4 = r5
            goto L_0x001b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fvision.camera.appupgrade.FileOper.stringInertFile(java.lang.String, java.io.File):boolean");
    }

    public static void download(final String url, final String folder, final String filename, final FileDownloadInterface mFileDownloadInterface) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setRequestProperty("Accept-Encoding", "identity");
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();
                    String romCacheDir = folder;
                    File rom = new File(romCacheDir);
                    if (!rom.exists()) {
                        rom.mkdirs();
                    }
                    String filepath = romCacheDir + "/" + filename;
                    FileOutputStream fos = new FileOutputStream(new File(filepath));
                    byte[] buf = new byte[1024];
                    float process = 0.0f;
                    int lastProcess = 0;
                    while (true) {
                        int numread = is.read(buf);
                        if (numread > 0) {
                            process += (float) numread;
                            float currentProcess = process / ((float) length);
                            if (((int) (100.0f * currentProcess)) != lastProcess) {
                                mFileDownloadInterface.progress(100.0f * currentProcess);
                            }
                            lastProcess = (int) (100.0f * currentProcess);
                            fos.write(buf, 0, numread);
                            if (0 != 0) {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    fos.close();
                    is.close();
                    mFileDownloadInterface.complete(filepath);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e2) {
                    mFileDownloadInterface.fail(0, "");
                    e2.printStackTrace();
                }
            }
        }).start();
    }
}
