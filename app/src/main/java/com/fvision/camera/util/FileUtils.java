package com.fvision.camera.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    private static String TAG = "FileUtils";

    public static boolean isSDCardMounted() {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    public static String getSDCardRoot() {
        System.out.println(isSDCardMounted() + Environment.getExternalStorageState());
        if (isSDCardMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return "";
    }

    public static String createMkdirsAndFiles(String path, String filename) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("路径为空");
        }
        File file = new File(getSDCardRoot() + path);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                throw new RuntimeException("创建文件夹不成功");
            }
        }
        File f = new File(file, filename);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e2) {
                throw new RuntimeException("创建文件不成功");
            }
        }
        return f.getAbsolutePath();
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0027 A[SYNTHETIC, Splitter:B:15:0x0027] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0033 A[SYNTHETIC, Splitter:B:21:0x0033] */
    /* JADX WARNING: Removed duplicated region for block: B:33:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void write2File(String r4, String r5, boolean r6) {
        /*
            r0 = 0
            java.io.BufferedWriter r1 = new java.io.BufferedWriter     // Catch:{ IOException -> 0x0021 }
            java.io.FileWriter r3 = new java.io.FileWriter     // Catch:{ IOException -> 0x0021 }
            r3.<init>(r4, r6)     // Catch:{ IOException -> 0x0021 }
            r1.<init>(r3)     // Catch:{ IOException -> 0x0021 }
            r1.write(r5)     // Catch:{ IOException -> 0x003f, all -> 0x003c }
            r1.newLine()     // Catch:{ IOException -> 0x003f, all -> 0x003c }
            r1.flush()     // Catch:{ IOException -> 0x003f, all -> 0x003c }
            if (r1 == 0) goto L_0x0042
            r1.close()     // Catch:{ IOException -> 0x001b }
            r0 = r1
        L_0x001a:
            return
        L_0x001b:
            r2 = move-exception
            r2.printStackTrace()
            r0 = r1
            goto L_0x001a
        L_0x0021:
            r2 = move-exception
        L_0x0022:
            r2.printStackTrace()     // Catch:{ all -> 0x0030 }
            if (r0 == 0) goto L_0x001a
            r0.close()     // Catch:{ IOException -> 0x002b }
            goto L_0x001a
        L_0x002b:
            r2 = move-exception
            r2.printStackTrace()
            goto L_0x001a
        L_0x0030:
            r3 = move-exception
        L_0x0031:
            if (r0 == 0) goto L_0x0036
            r0.close()     // Catch:{ IOException -> 0x0037 }
        L_0x0036:
            throw r3
        L_0x0037:
            r2 = move-exception
            r2.printStackTrace()
            goto L_0x0036
        L_0x003c:
            r3 = move-exception
            r0 = r1
            goto L_0x0031
        L_0x003f:
            r2 = move-exception
            r0 = r1
            goto L_0x0022
        L_0x0042:
            r0 = r1
            goto L_0x001a
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fvision.camera.util.FileUtils.write2File(java.lang.String, java.lang.String, boolean):void");
    }

    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("路径为空");
        }
        File file = new File(path);
        if (file.exists()) {
            try {
                file.delete();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean Assets2Sd(Context context, String fileAssetPath, String fileSdPath) {
        if (!new File(fileSdPath).exists()) {
            Log.d(TAG, "************文件不存在,文件创建");
            try {
                copyBigDataToSD(context, fileAssetPath, fileSdPath);
                Log.d(TAG, "************拷贝成功");
                return true;
            } catch (IOException e) {
                Log.d(TAG, "************拷贝失败");
                e.printStackTrace();
                return false;
            }
        } else {
            Log.d(TAG, "************文件夹存在,文件存在");
            return true;
        }
    }

    public static void copyBigDataToSD(Context context, String fileAssetPath, String strOutFileName) throws IOException {
        OutputStream myOutput = new FileOutputStream(strOutFileName);
        InputStream myInput = context.getAssets().open(fileAssetPath);
        byte[] buffer = new byte[1024];
        for (int length = myInput.read(buffer); length > 0; length = myInput.read(buffer)) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myInput.close();
        myOutput.close();
    }

    public static byte[] readFile(String filePath) {
        byte[] buffer = null;
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file.exists()) {
                buffer = null;
                try {
                    FileInputStream fis = new FileInputStream(file);
                    try {
                        buffer = new byte[fis.available()];
                        fis.read(buffer);
                        fis.close();
                        FileInputStream fileInputStream = fis;
                    } catch (FileNotFoundException e) {
                        e = e;
                        FileInputStream fileInputStream2 = fis;
                        e.printStackTrace();
                        return buffer;
                    } catch (IOException e2) {
                        e = e2;
                        FileInputStream fileInputStream3 = fis;
                        e.printStackTrace();
                        return buffer;
                    }
                } catch (FileNotFoundException e3) {
                    e = e3;
                    e.printStackTrace();
                    return buffer;
                } catch (IOException e4) {
                    e = e4;
                    e.printStackTrace();
                    return buffer;
                }
            }
        }
        return buffer;
    }

    public static void saveFile(String fileDir, String fileName, byte[] bt) {
        String filePath;
        if (!TextUtils.isEmpty(fileDir) && !TextUtils.isEmpty(fileName)) {
            try {
                File dir = new File(fileDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                if (fileDir.substring(fileDir.length() - 1, fileDir.length()).equals("/")) {
                    filePath = fileDir + fileName;
                } else {
                    filePath = fileDir + "/" + fileName;
                }
                File log = new File(filePath);
                if (!log.exists()) {
                    log.createNewFile();
                }
                FileOutputStream outStream = new FileOutputStream(log);
                outStream.write(bt);
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
