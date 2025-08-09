package com.adasplus.adas.adas;

import android.app.IntentService;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.adasplus.adas.adas.net.RequestManager;
import com.adasplus.adas.util.FileUtils;
import com.adasplus.adas.util.LogUtil;
import com.adasplus.adas.util.Util;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AdasService extends IntentService {
    private String mBackupFilePath;
    private String mFileDirPath;
    private SharedPreferences mSharedPreferences;
    private String mVersion;

    public AdasService() {
        super("AdasService");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0329, code lost:
        r14 = r15;
        r9 = r10;
     */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x02da A[Catch:{ IOException -> 0x02f7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x02f0 A[Catch:{ IOException -> 0x02f7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x032c A[SYNTHETIC, Splitter:B:69:0x032c] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onHandleIntent(android.content.Intent r40) {
        /*
            r39 = this;
            java.lang.String r36 = "Adas"
            java.lang.String r37 = "AdasService start!"
            android.util.Log.e(r36, r37)
            java.io.File r36 = new java.io.File
            java.io.File r37 = r39.getFilesDir()
            java.lang.String r38 = "/adas/Uuid"
            r36.<init>(r37, r38)
            java.lang.String r33 = com.adasplus.adas.util.FileUtils.getFileString(r36)
            boolean r36 = android.text.TextUtils.isEmpty(r33)
            if (r36 != 0) goto L_0x0028
            java.lang.String r36 = ","
            r0 = r33
            r1 = r36
            boolean r36 = r0.contains(r1)
            if (r36 != 0) goto L_0x002e
        L_0x0028:
            java.lang.String r36 = "Cannot find uuid!"
            com.adasplus.adas.util.LogUtil.logE(r36)
        L_0x002d:
            return
        L_0x002e:
            java.lang.StringBuilder r36 = new java.lang.StringBuilder
            r36.<init>()
            java.io.File r37 = r39.getFilesDir()
            java.lang.StringBuilder r36 = r36.append(r37)
            java.lang.String r37 = java.io.File.separator
            java.lang.StringBuilder r36 = r36.append(r37)
            java.lang.String r37 = "adas"
            java.lang.StringBuilder r36 = r36.append(r37)
            java.lang.String r37 = java.io.File.separator
            java.lang.StringBuilder r36 = r36.append(r37)
            java.lang.String r37 = "backup"
            java.lang.StringBuilder r36 = r36.append(r37)
            java.lang.String r36 = r36.toString()
            r0 = r36
            r1 = r39
            r1.mBackupFilePath = r0
            java.lang.StringBuilder r36 = new java.lang.StringBuilder
            r36.<init>()
            java.io.File r37 = r39.getFilesDir()
            java.lang.StringBuilder r36 = r36.append(r37)
            java.lang.String r37 = java.io.File.separator
            java.lang.StringBuilder r36 = r36.append(r37)
            java.lang.String r37 = "adas"
            java.lang.StringBuilder r36 = r36.append(r37)
            java.lang.String r37 = java.io.File.separator
            java.lang.StringBuilder r36 = r36.append(r37)
            java.lang.String r36 = r36.toString()
            r0 = r36
            r1 = r39
            r1.mFileDirPath = r0
            java.lang.String r36 = "AdasConfig"
            r37 = 0
            r0 = r39
            r1 = r36
            r2 = r37
            android.content.SharedPreferences r36 = r0.getSharedPreferences(r1, r2)
            r0 = r36
            r1 = r39
            r1.mSharedPreferences = r0
            r0 = r39
            android.content.SharedPreferences r0 = r0.mSharedPreferences
            r36 = r0
            java.lang.String r37 = "version"
            java.lang.String r38 = "adas_30.0.5_004"
            java.lang.String r34 = r36.getString(r37, r38)
            java.lang.String r36 = ","
            r0 = r33
            r1 = r36
            java.lang.String[] r36 = r0.split(r1)
            r37 = 0
            r33 = r36[r37]
            java.util.HashMap r27 = new java.util.HashMap
            r27.<init>()
            java.lang.String r36 = "version"
            r0 = r27
            r1 = r36
            r2 = r34
            r0.put(r1, r2)
            java.lang.String r36 = "uuid"
            r0 = r27
            r1 = r36
            r2 = r33
            r0.put(r1, r2)
            java.lang.String r36 = "merchant_id"
            java.lang.String r37 = "huiying20171012"
            r0 = r27
            r1 = r36
            r2 = r37
            r0.put(r1, r2)
            long r36 = java.lang.System.currentTimeMillis()
            java.lang.String r31 = java.lang.String.valueOf(r36)
            java.lang.String r36 = "timestamp"
            r0 = r27
            r1 = r36
            r2 = r31
            r0.put(r1, r2)
            java.lang.String r36 = "sign"
            java.lang.StringBuilder r37 = new java.lang.StringBuilder
            r37.<init>()
            r0 = r37
            r1 = r31
            java.lang.StringBuilder r37 = r0.append(r1)
            java.lang.String r38 = "huiying20171012"
            java.lang.String r38 = com.adasplus.adas.util.Util.getStrMd5(r38)
            java.lang.String r38 = r38.toLowerCase()
            java.lang.StringBuilder r37 = r37.append(r38)
            java.lang.String r37 = r37.toString()
            java.lang.String r37 = com.adasplus.adas.util.Util.getStrMd5(r37)
            java.lang.String r37 = r37.toLowerCase()
            r0 = r27
            r1 = r36
            r2 = r37
            r0.put(r1, r2)
            boolean r36 = com.adasplus.adas.util.Util.isNetworkConnected(r39)
            if (r36 == 0) goto L_0x01c1
            r9 = 0
            r14 = 0
            r29 = 0
            r30 = 0
            r12 = 0
            r11 = 1
            r25 = 0
            r26 = r25
            r15 = r14
            r10 = r9
        L_0x0138:
            java.lang.String r36 = "part_num"
            int r25 = r26 + 1
            java.lang.String r37 = java.lang.String.valueOf(r26)     // Catch:{ IOException -> 0x01bb }
            r0 = r27
            r1 = r36
            r2 = r37
            r0.put(r1, r2)     // Catch:{ IOException -> 0x01bb }
            com.adasplus.adas.adas.net.RequestManager r36 = com.adasplus.adas.adas.net.RequestManager.getInstance(r39)     // Catch:{ IOException -> 0x01bb }
            java.lang.String r37 = "http://androidsdk.adasplus.com:80/download_app_version"
            r0 = r36
            r1 = r37
            r2 = r27
            java.io.InputStream r18 = r0.getInputStream(r1, r2)     // Catch:{ IOException -> 0x01bb }
            if (r18 == 0) goto L_0x05ae
            r36 = 10
            r0 = r36
            byte[] r0 = new byte[r0]     // Catch:{ IOException -> 0x01bb }
            r16 = r0
            r0 = r18
            r1 = r16
            r0.read(r1)     // Catch:{ IOException -> 0x01bb }
            java.lang.String r17 = new java.lang.String     // Catch:{ IOException -> 0x01bb }
            r0 = r17
            r1 = r16
            r0.<init>(r1)     // Catch:{ IOException -> 0x01bb }
            boolean r36 = android.text.TextUtils.isEmpty(r17)     // Catch:{ IOException -> 0x01bb }
            if (r36 != 0) goto L_0x0196
            boolean r36 = android.text.TextUtils.isEmpty(r17)     // Catch:{ IOException -> 0x01bb }
            if (r36 != 0) goto L_0x01c8
            java.lang.String r36 = "="
            r0 = r17
            r1 = r36
            java.lang.String[] r36 = r0.split(r1)     // Catch:{ IOException -> 0x01bb }
            r0 = r36
            int r0 = r0.length     // Catch:{ IOException -> 0x01bb }
            r36 = r0
            r37 = 2
            r0 = r36
            r1 = r37
            if (r0 == r1) goto L_0x01c8
        L_0x0196:
            java.lang.String r36 = "Adas"
            java.lang.StringBuilder r37 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01bb }
            r37.<init>()     // Catch:{ IOException -> 0x01bb }
            java.lang.String r38 = "Split error:"
            java.lang.StringBuilder r37 = r37.append(r38)     // Catch:{ IOException -> 0x01bb }
            r0 = r37
            r1 = r17
            java.lang.StringBuilder r37 = r0.append(r1)     // Catch:{ IOException -> 0x01bb }
            java.lang.String r37 = r37.toString()     // Catch:{ IOException -> 0x01bb }
            android.util.Log.e(r36, r37)     // Catch:{ IOException -> 0x01bb }
            java.lang.String r36 = "Adas"
            java.lang.String r37 = "AdasService over."
            android.util.Log.e(r36, r37)     // Catch:{ IOException -> 0x01bb }
            goto L_0x002d
        L_0x01bb:
            r6 = move-exception
            r14 = r15
            r9 = r10
        L_0x01be:
            r6.printStackTrace()
        L_0x01c1:
            java.lang.String r36 = "AdasService over!"
            com.adasplus.adas.util.LogUtil.logE(r36)
            goto L_0x002d
        L_0x01c8:
            java.lang.String r36 = "="
            r0 = r17
            r1 = r36
            java.lang.String[] r36 = r0.split(r1)     // Catch:{ IOException -> 0x01bb }
            r37 = 1
            r17 = r36[r37]     // Catch:{ IOException -> 0x01bb }
            java.lang.Integer r36 = java.lang.Integer.valueOf(r17)     // Catch:{ IOException -> 0x01bb }
            int r20 = r36.intValue()     // Catch:{ IOException -> 0x01bb }
            r0 = r20
            byte[] r0 = new byte[r0]     // Catch:{ IOException -> 0x01bb }
            r19 = r0
            r18.read(r19)     // Catch:{ IOException -> 0x01bb }
            java.lang.String r21 = new java.lang.String     // Catch:{ IOException -> 0x01bb }
            r0 = r21
            r1 = r19
            r0.<init>(r1)     // Catch:{ IOException -> 0x01bb }
            org.json.JSONObject r28 = new org.json.JSONObject     // Catch:{ Exception -> 0x05a5 }
            r0 = r28
            r1 = r21
            r0.<init>(r1)     // Catch:{ Exception -> 0x05a5 }
            java.lang.String r36 = "ret"
            r0 = r28
            r1 = r36
            int r36 = r0.getInt(r1)     // Catch:{ Exception -> 0x05a5 }
            if (r36 != 0) goto L_0x0303
            java.lang.String r36 = "file_end"
            r0 = r28
            r1 = r36
            int r36 = r0.getInt(r1)     // Catch:{ Exception -> 0x05a5 }
            r37 = -1
            r0 = r36
            r1 = r37
            if (r0 == r1) goto L_0x0303
            java.lang.String r36 = "version"
            r0 = r28
            r1 = r36
            java.lang.String r36 = r0.getString(r1)     // Catch:{ Exception -> 0x05a5 }
            r0 = r36
            r1 = r39
            r1.mVersion = r0     // Catch:{ Exception -> 0x05a5 }
            java.lang.String r36 = "systime"
            r0 = r28
            r1 = r36
            java.lang.String r30 = r0.getString(r1)     // Catch:{ Exception -> 0x05a5 }
            java.lang.String r36 = "_"
            r0 = r34
            r1 = r36
            java.lang.String[] r36 = r0.split(r1)     // Catch:{ Exception -> 0x05a5 }
            r37 = 2
            r36 = r36[r37]     // Catch:{ Exception -> 0x05a5 }
            r0 = r39
            java.lang.String r0 = r0.mVersion     // Catch:{ Exception -> 0x05a5 }
            r37 = r0
            java.lang.String r38 = "_"
            java.lang.String[] r37 = r37.split(r38)     // Catch:{ Exception -> 0x05a5 }
            r38 = 2
            r37 = r37[r38]     // Catch:{ Exception -> 0x05a5 }
            boolean r36 = r36.equals(r37)     // Catch:{ Exception -> 0x05a5 }
            if (r36 != 0) goto L_0x05ae
            if (r11 == 0) goto L_0x05b2
            java.io.File r36 = new java.io.File     // Catch:{ Exception -> 0x05a5 }
            r0 = r39
            java.lang.String r0 = r0.mBackupFilePath     // Catch:{ Exception -> 0x05a5 }
            r37 = r0
            r36.<init>(r37)     // Catch:{ Exception -> 0x05a5 }
            com.adasplus.adas.util.FileUtils.clearBackupDir(r36)     // Catch:{ Exception -> 0x05a5 }
            java.io.File r9 = new java.io.File     // Catch:{ Exception -> 0x05a5 }
            r0 = r39
            java.lang.String r0 = r0.mBackupFilePath     // Catch:{ Exception -> 0x05a5 }
            r36 = r0
            java.lang.StringBuilder r37 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05a5 }
            r37.<init>()     // Catch:{ Exception -> 0x05a5 }
            java.lang.String r38 = "version"
            r0 = r28
            r1 = r38
            java.lang.String r38 = r0.getString(r1)     // Catch:{ Exception -> 0x05a5 }
            java.lang.StringBuilder r37 = r37.append(r38)     // Catch:{ Exception -> 0x05a5 }
            java.lang.String r38 = ".zip"
            java.lang.StringBuilder r37 = r37.append(r38)     // Catch:{ Exception -> 0x05a5 }
            java.lang.String r37 = r37.toString()     // Catch:{ Exception -> 0x05a5 }
            r0 = r36
            r1 = r37
            r9.<init>(r0, r1)     // Catch:{ Exception -> 0x05a5 }
            java.io.FileOutputStream r14 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x05aa, IOException -> 0x05a1 }
            r14.<init>(r9)     // Catch:{ Exception -> 0x05aa, IOException -> 0x05a1 }
            r11 = 0
            java.lang.String r36 = "md5"
            r0 = r28
            r1 = r36
            java.lang.String r29 = r0.getString(r1)     // Catch:{ Exception -> 0x02c9 }
            java.lang.String r36 = "filesize"
            r0 = r28
            r1 = r36
            long r12 = r0.getLong(r1)     // Catch:{ Exception -> 0x02c9 }
        L_0x02ab:
            r36 = 4096(0x1000, float:5.74E-42)
            r0 = r36
            byte[] r5 = new byte[r0]     // Catch:{ Exception -> 0x02c9 }
        L_0x02b1:
            r0 = r18
            int r22 = r0.read(r5)     // Catch:{ Exception -> 0x02c9 }
            r36 = -1
            r0 = r22
            r1 = r36
            if (r0 == r1) goto L_0x02fa
            r36 = 0
            r0 = r36
            r1 = r22
            r14.write(r5, r0, r1)     // Catch:{ Exception -> 0x02c9 }
            goto L_0x02b1
        L_0x02c9:
            r6 = move-exception
        L_0x02ca:
            java.io.File r36 = new java.io.File     // Catch:{ IOException -> 0x02f7 }
            r0 = r39
            java.lang.String r0 = r0.mBackupFilePath     // Catch:{ IOException -> 0x02f7 }
            r37 = r0
            r36.<init>(r37)     // Catch:{ IOException -> 0x02f7 }
            com.adasplus.adas.util.FileUtils.clearBackupDir(r36)     // Catch:{ IOException -> 0x02f7 }
            if (r14 == 0) goto L_0x02dd
            r14.close()     // Catch:{ IOException -> 0x02f7 }
        L_0x02dd:
            r6.printStackTrace()     // Catch:{ IOException -> 0x02f7 }
        L_0x02e0:
            if (r9 == 0) goto L_0x02f0
            r36 = 0
            int r36 = (r12 > r36 ? 1 : (r12 == r36 ? 0 : -1))
            if (r36 == 0) goto L_0x02f0
            long r36 = r9.length()     // Catch:{ IOException -> 0x02f7 }
            int r36 = (r12 > r36 ? 1 : (r12 == r36 ? 0 : -1))
            if (r36 == 0) goto L_0x032c
        L_0x02f0:
            java.lang.String r36 = "File error Service over!"
            com.adasplus.adas.util.LogUtil.logE(r36)     // Catch:{ IOException -> 0x02f7 }
            goto L_0x002d
        L_0x02f7:
            r6 = move-exception
            goto L_0x01be
        L_0x02fa:
            r18.close()     // Catch:{ Exception -> 0x02c9 }
        L_0x02fd:
            r26 = r25
            r15 = r14
            r10 = r9
            goto L_0x0138
        L_0x0303:
            java.lang.String r36 = "ret"
            r0 = r28
            r1 = r36
            int r36 = r0.getInt(r1)     // Catch:{ Exception -> 0x05a5 }
            if (r36 != 0) goto L_0x0321
            java.lang.String r36 = "file_end"
            r0 = r28
            r1 = r36
            int r36 = r0.getInt(r1)     // Catch:{ Exception -> 0x05a5 }
            r37 = -1
            r0 = r36
            r1 = r37
            if (r0 != r1) goto L_0x05ae
        L_0x0321:
            if (r15 == 0) goto L_0x0329
            r15.flush()     // Catch:{ Exception -> 0x05a5 }
            r15.close()     // Catch:{ Exception -> 0x05a5 }
        L_0x0329:
            r14 = r15
            r9 = r10
            goto L_0x02e0
        L_0x032c:
            java.lang.String r24 = com.adasplus.adas.util.Util.getMd5ByFile(r9)     // Catch:{ IOException -> 0x02f7 }
            r0 = r24
            r1 = r29
            boolean r36 = r0.equals(r1)     // Catch:{ IOException -> 0x02f7 }
            if (r36 == 0) goto L_0x0590
            r0 = r39
            android.content.SharedPreferences r0 = r0.mSharedPreferences     // Catch:{ IOException -> 0x02f7 }
            r36 = r0
            java.lang.String r37 = "load"
            java.lang.String r38 = "load0"
            java.lang.String r23 = r36.getString(r37, r38)     // Catch:{ IOException -> 0x02f7 }
            r32 = 0
            java.lang.String r36 = "load0"
            r0 = r23
            r1 = r36
            boolean r36 = r0.equals(r1)     // Catch:{ IOException -> 0x02f7 }
            if (r36 == 0) goto L_0x03d4
            java.lang.StringBuilder r36 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x02f7 }
            r36.<init>()     // Catch:{ IOException -> 0x02f7 }
            r0 = r39
            java.lang.String r0 = r0.mFileDirPath     // Catch:{ IOException -> 0x02f7 }
            r37 = r0
            java.lang.StringBuilder r36 = r36.append(r37)     // Catch:{ IOException -> 0x02f7 }
            java.lang.String r37 = "load1"
            java.lang.StringBuilder r36 = r36.append(r37)     // Catch:{ IOException -> 0x02f7 }
            java.lang.String r32 = r36.toString()     // Catch:{ IOException -> 0x02f7 }
            java.io.File r36 = new java.io.File     // Catch:{ IOException -> 0x02f7 }
            r0 = r36
            r1 = r32
            r0.<init>(r1)     // Catch:{ IOException -> 0x02f7 }
            com.adasplus.adas.util.FileUtils.clearDir(r36)     // Catch:{ IOException -> 0x02f7 }
        L_0x037b:
            java.util.zip.ZipInputStream r35 = new java.util.zip.ZipInputStream     // Catch:{ IOException -> 0x03bb }
            java.io.FileInputStream r36 = new java.io.FileInputStream     // Catch:{ IOException -> 0x03bb }
            r0 = r36
            r0.<init>(r9)     // Catch:{ IOException -> 0x03bb }
            r35.<init>(r36)     // Catch:{ IOException -> 0x03bb }
        L_0x0387:
            java.util.zip.ZipEntry r8 = r35.getNextEntry()     // Catch:{ IOException -> 0x03bb }
            if (r8 == 0) goto L_0x0430
            boolean r36 = r8.isDirectory()     // Catch:{ IOException -> 0x03bb }
            if (r36 != 0) goto L_0x0407
            java.io.File r36 = new java.io.File     // Catch:{ IOException -> 0x03bb }
            java.lang.StringBuilder r37 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x03bb }
            r37.<init>()     // Catch:{ IOException -> 0x03bb }
            r0 = r37
            r1 = r32
            java.lang.StringBuilder r37 = r0.append(r1)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r38 = java.io.File.separator     // Catch:{ IOException -> 0x03bb }
            java.lang.StringBuilder r37 = r37.append(r38)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r38 = r8.getName()     // Catch:{ IOException -> 0x03bb }
            java.lang.StringBuilder r37 = r37.append(r38)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r37 = r37.toString()     // Catch:{ IOException -> 0x03bb }
            r36.<init>(r37)     // Catch:{ IOException -> 0x03bb }
            com.adasplus.adas.util.FileUtils.copyFile((java.util.zip.ZipInputStream) r35, (java.io.File) r36)     // Catch:{ IOException -> 0x03bb }
            goto L_0x0387
        L_0x03bb:
            r6 = move-exception
            r6.printStackTrace()     // Catch:{ IOException -> 0x02f7 }
            java.io.File r36 = new java.io.File     // Catch:{ IOException -> 0x02f7 }
            r0 = r36
            r1 = r32
            r0.<init>(r1)     // Catch:{ IOException -> 0x02f7 }
            com.adasplus.adas.util.FileUtils.clearDir(r36)     // Catch:{ IOException -> 0x02f7 }
            java.lang.String r36 = r6.getLocalizedMessage()     // Catch:{ IOException -> 0x02f7 }
            com.adasplus.adas.util.LogUtil.logE(r36)     // Catch:{ IOException -> 0x02f7 }
            goto L_0x01c1
        L_0x03d4:
            java.lang.String r36 = "load1"
            r0 = r23
            r1 = r36
            boolean r36 = r0.equals(r1)     // Catch:{ IOException -> 0x02f7 }
            if (r36 == 0) goto L_0x037b
            java.lang.StringBuilder r36 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x02f7 }
            r36.<init>()     // Catch:{ IOException -> 0x02f7 }
            r0 = r39
            java.lang.String r0 = r0.mFileDirPath     // Catch:{ IOException -> 0x02f7 }
            r37 = r0
            java.lang.StringBuilder r36 = r36.append(r37)     // Catch:{ IOException -> 0x02f7 }
            java.lang.String r37 = "load0"
            java.lang.StringBuilder r36 = r36.append(r37)     // Catch:{ IOException -> 0x02f7 }
            java.lang.String r32 = r36.toString()     // Catch:{ IOException -> 0x02f7 }
            java.io.File r36 = new java.io.File     // Catch:{ IOException -> 0x02f7 }
            r0 = r36
            r1 = r32
            r0.<init>(r1)     // Catch:{ IOException -> 0x02f7 }
            com.adasplus.adas.util.FileUtils.clearDir(r36)     // Catch:{ IOException -> 0x02f7 }
            goto L_0x037b
        L_0x0407:
            java.io.File r36 = new java.io.File     // Catch:{ IOException -> 0x03bb }
            java.lang.StringBuilder r37 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x03bb }
            r37.<init>()     // Catch:{ IOException -> 0x03bb }
            r0 = r37
            r1 = r32
            java.lang.StringBuilder r37 = r0.append(r1)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r38 = java.io.File.separator     // Catch:{ IOException -> 0x03bb }
            java.lang.StringBuilder r37 = r37.append(r38)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r38 = r8.getName()     // Catch:{ IOException -> 0x03bb }
            java.lang.StringBuilder r37 = r37.append(r38)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r37 = r37.toString()     // Catch:{ IOException -> 0x03bb }
            r36.<init>(r37)     // Catch:{ IOException -> 0x03bb }
            r36.mkdirs()     // Catch:{ IOException -> 0x03bb }
            goto L_0x0387
        L_0x0430:
            java.util.HashMap r4 = new java.util.HashMap     // Catch:{ IOException -> 0x03bb }
            r4.<init>()     // Catch:{ IOException -> 0x03bb }
            java.io.File r36 = new java.io.File     // Catch:{ IOException -> 0x03bb }
            java.lang.StringBuilder r37 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x03bb }
            r37.<init>()     // Catch:{ IOException -> 0x03bb }
            r0 = r37
            r1 = r32
            java.lang.StringBuilder r37 = r0.append(r1)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r38 = java.io.File.separator     // Catch:{ IOException -> 0x03bb }
            java.lang.StringBuilder r37 = r37.append(r38)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r38 = "MANIFEST.MF"
            java.lang.StringBuilder r37 = r37.append(r38)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r37 = r37.toString()     // Catch:{ IOException -> 0x03bb }
            r36.<init>(r37)     // Catch:{ IOException -> 0x03bb }
            r0 = r36
            boolean r36 = com.adasplus.adas.util.FileUtils.verifyTotalFile(r0, r4)     // Catch:{ IOException -> 0x03bb }
            if (r36 == 0) goto L_0x0530
            java.lang.StringBuilder r36 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x03bb }
            r36.<init>()     // Catch:{ IOException -> 0x03bb }
            r0 = r36
            r1 = r32
            java.lang.StringBuilder r36 = r0.append(r1)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r37 = java.io.File.separator     // Catch:{ IOException -> 0x03bb }
            java.lang.StringBuilder r36 = r36.append(r37)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r37 = "adas.dex"
            java.lang.StringBuilder r36 = r36.append(r37)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r36 = r36.toString()     // Catch:{ IOException -> 0x03bb }
            r0 = r36
            boolean r36 = com.adasplus.adas.util.FileUtils.verifyItemFile(r4, r0)     // Catch:{ IOException -> 0x03bb }
            if (r36 == 0) goto L_0x0530
            java.lang.StringBuilder r36 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x03bb }
            r36.<init>()     // Catch:{ IOException -> 0x03bb }
            r0 = r36
            r1 = r32
            java.lang.StringBuilder r36 = r0.append(r1)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r37 = java.io.File.separator     // Catch:{ IOException -> 0x03bb }
            java.lang.StringBuilder r36 = r36.append(r37)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r37 = android.os.Build.CPU_ABI     // Catch:{ IOException -> 0x03bb }
            java.lang.StringBuilder r36 = r36.append(r37)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r37 = java.io.File.separator     // Catch:{ IOException -> 0x03bb }
            java.lang.StringBuilder r36 = r36.append(r37)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r37 = "libadas_lib.so"
            java.lang.StringBuilder r36 = r36.append(r37)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r36 = r36.toString()     // Catch:{ IOException -> 0x03bb }
            r0 = r36
            boolean r36 = com.adasplus.adas.util.FileUtils.verifyItemFile(r4, r0)     // Catch:{ IOException -> 0x03bb }
            if (r36 == 0) goto L_0x0530
            java.lang.StringBuilder r36 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x03bb }
            r36.<init>()     // Catch:{ IOException -> 0x03bb }
            r0 = r36
            r1 = r32
            java.lang.StringBuilder r36 = r0.append(r1)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r37 = java.io.File.separator     // Catch:{ IOException -> 0x03bb }
            java.lang.StringBuilder r36 = r36.append(r37)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r37 = android.os.Build.CPU_ABI     // Catch:{ IOException -> 0x03bb }
            java.lang.StringBuilder r36 = r36.append(r37)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r37 = java.io.File.separator     // Catch:{ IOException -> 0x03bb }
            java.lang.StringBuilder r36 = r36.append(r37)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r37 = "libAdasLib.so"
            java.lang.StringBuilder r36 = r36.append(r37)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r36 = r36.toString()     // Catch:{ IOException -> 0x03bb }
            r0 = r36
            boolean r36 = com.adasplus.adas.util.FileUtils.verifyItemFile(r4, r0)     // Catch:{ IOException -> 0x03bb }
            if (r36 == 0) goto L_0x0530
            java.lang.StringBuilder r36 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x03bb }
            r36.<init>()     // Catch:{ IOException -> 0x03bb }
            r0 = r36
            r1 = r32
            java.lang.StringBuilder r36 = r0.append(r1)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r37 = java.io.File.separator     // Catch:{ IOException -> 0x03bb }
            java.lang.StringBuilder r36 = r36.append(r37)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r37 = "210213640546278.dat"
            java.lang.StringBuilder r36 = r36.append(r37)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r36 = r36.toString()     // Catch:{ IOException -> 0x03bb }
            r0 = r36
            boolean r36 = com.adasplus.adas.util.FileUtils.verifyItemFile(r4, r0)     // Catch:{ IOException -> 0x03bb }
            if (r36 == 0) goto L_0x0530
            java.lang.StringBuilder r36 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x03bb }
            r36.<init>()     // Catch:{ IOException -> 0x03bb }
            r0 = r36
            r1 = r32
            java.lang.StringBuilder r36 = r0.append(r1)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r37 = java.io.File.separator     // Catch:{ IOException -> 0x03bb }
            java.lang.StringBuilder r36 = r36.append(r37)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r37 = "2102136405363718.dat"
            java.lang.StringBuilder r36 = r36.append(r37)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r36 = r36.toString()     // Catch:{ IOException -> 0x03bb }
            r0 = r36
            boolean r36 = com.adasplus.adas.util.FileUtils.verifyItemFile(r4, r0)     // Catch:{ IOException -> 0x03bb }
            if (r36 != 0) goto L_0x0543
        L_0x0530:
            java.lang.String r36 = "Verify new file fail!"
            com.adasplus.adas.util.LogUtil.logE(r36)     // Catch:{ IOException -> 0x03bb }
            java.io.File r36 = new java.io.File     // Catch:{ IOException -> 0x03bb }
            r0 = r36
            r1 = r32
            r0.<init>(r1)     // Catch:{ IOException -> 0x03bb }
            com.adasplus.adas.util.FileUtils.clearDir(r36)     // Catch:{ IOException -> 0x03bb }
            goto L_0x002d
        L_0x0543:
            r0 = r39
            android.content.SharedPreferences r0 = r0.mSharedPreferences     // Catch:{ IOException -> 0x03bb }
            r36 = r0
            android.content.SharedPreferences$Editor r7 = r36.edit()     // Catch:{ IOException -> 0x03bb }
            java.lang.String r36 = "load"
            java.lang.String r37 = "/"
            r0 = r32
            r1 = r37
            int r37 = r0.lastIndexOf(r1)     // Catch:{ IOException -> 0x03bb }
            int r37 = r37 + 1
            r0 = r32
            r1 = r37
            java.lang.String r37 = r0.substring(r1)     // Catch:{ IOException -> 0x03bb }
            r0 = r36
            r1 = r37
            r7.putString(r0, r1)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r36 = "systime"
            r0 = r36
            r1 = r30
            r7.putString(r0, r1)     // Catch:{ IOException -> 0x03bb }
            java.lang.String r36 = "version"
            r0 = r39
            java.lang.String r0 = r0.mVersion     // Catch:{ IOException -> 0x03bb }
            r37 = r0
            r0 = r36
            r1 = r37
            r7.putString(r0, r1)     // Catch:{ IOException -> 0x03bb }
            r7.commit()     // Catch:{ IOException -> 0x03bb }
            r36 = 1
            r0 = r39
            r1 = r36
            r0.uploadErrorVersion(r1)     // Catch:{ IOException -> 0x03bb }
            goto L_0x01c1
        L_0x0590:
            java.lang.String r36 = "md5 not equal!"
            com.adasplus.adas.util.LogUtil.logE(r36)     // Catch:{ IOException -> 0x02f7 }
            r9.delete()     // Catch:{ IOException -> 0x02f7 }
            goto L_0x01c1
        L_0x059a:
            r6 = move-exception
            r25 = r26
            r14 = r15
            r9 = r10
            goto L_0x01be
        L_0x05a1:
            r6 = move-exception
            r14 = r15
            goto L_0x01be
        L_0x05a5:
            r6 = move-exception
            r14 = r15
            r9 = r10
            goto L_0x02ca
        L_0x05aa:
            r6 = move-exception
            r14 = r15
            goto L_0x02ca
        L_0x05ae:
            r14 = r15
            r9 = r10
            goto L_0x02fd
        L_0x05b2:
            r14 = r15
            r9 = r10
            goto L_0x02ab
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adasplus.adas.adas.AdasService.onHandleIntent(android.content.Intent):void");
    }

    private void uploadErrorVersion(boolean success) {
        String version = this.mSharedPreferences.getString("version", "adas_30.0.5_004");
        String uuid = FileUtils.getFileString(new File(getFilesDir(), "/adas/Uuid"));
        if (TextUtils.isEmpty(uuid) || !uuid.contains(",")) {
            LogUtil.logE("Cannot find uuid!");
            return;
        }
        String uuid2 = uuid.split(",")[0];
        Map<String, String> params = new HashMap<>();
        params.put("current_verison", version);
        params.put(AdasConstants.STR_UUID, uuid2);
        params.put(AdasConstants.STR_MERCHAINT_ID, BuildConfig.ADAS_VERSION_MERCHANTID);
        String time = String.valueOf(System.currentTimeMillis());
        params.put(AdasConstants.STR_TIMESTAMP, time);
        params.put(AdasConstants.STR_SIGN, Util.getStrMd5(time + Util.getStrMd5(BuildConfig.ADAS_VERSION_MERCHANTID).toLowerCase()).toLowerCase());
        params.put("updata_flag", success ? String.valueOf(0) : String.valueOf(1));
        RequestManager.getInstance(getApplicationContext()).getReponseByPostMethod(AdasConstants.UPDATE_APP_VERSION_URL, params);
    }
}
