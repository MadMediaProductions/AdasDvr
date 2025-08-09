package com.adasplus.adas.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

public class Util {
    public static boolean isNetworkConnected(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static String getStrMd5(String msg) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = msg.getBytes();
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(btInput);
            char[] str = new char[(j * 2)];
            int k = 0;
            for (byte byte0 : digest.digest()) {
                int k2 = k + 1;
                str[k] = hexDigits[(byte0 >>> 4) & 15];
                k = k2 + 1;
                str[k2] = hexDigits[byte0 & 15];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getByteMd5(byte[] array) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = array;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(btInput);
            char[] str = new char[(j * 2)];
            int k = 0;
            for (byte byte0 : digest.digest()) {
                int k2 = k + 1;
                str[k] = hexDigits[(byte0 >>> 4) & 15];
                k = k2 + 1;
                str[k2] = hexDigits[byte0 & 15];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getMd5ByFile(File file) throws FileNotFoundException {
        String str;
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        FileInputStream in = new FileInputStream(file);
        try {
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            char[] str2 = new char[(j * 2)];
            int k = 0;
            for (byte byte0 : md5.digest()) {
                int k2 = k + 1;
                str2[k] = hexDigits[(byte0 >>> 4) & 15];
                k = k2 + 1;
                str2[k2] = hexDigits[byte0 & 15];
            }
            str = new String(str2).toLowerCase();
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            str = null;
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
        return str;
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0033 A[SYNTHETIC, Splitter:B:19:0x0033] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0038 A[Catch:{ Exception -> 0x003d }] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0045 A[SYNTHETIC, Splitter:B:28:0x0045] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x004a A[Catch:{ Exception -> 0x004e }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static String getDeviceCode(Context r9) {
        /*
            r0 = 0
            r4 = 0
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x002d }
            java.io.File r7 = r9.getFilesDir()     // Catch:{ Exception -> 0x002d }
            java.lang.String r8 = "adas/imei"
            r3.<init>(r7, r8)     // Catch:{ Exception -> 0x002d }
            java.io.FileReader r5 = new java.io.FileReader     // Catch:{ Exception -> 0x002d }
            r5.<init>(r3)     // Catch:{ Exception -> 0x002d }
            java.io.BufferedReader r1 = new java.io.BufferedReader     // Catch:{ Exception -> 0x005a, all -> 0x0053 }
            r1.<init>(r5)     // Catch:{ Exception -> 0x005a, all -> 0x0053 }
            java.lang.String r6 = r1.readLine()     // Catch:{ Exception -> 0x005d, all -> 0x0056 }
            if (r5 == 0) goto L_0x0020
            r5.close()     // Catch:{ Exception -> 0x0028 }
        L_0x0020:
            if (r1 == 0) goto L_0x0025
            r1.close()     // Catch:{ Exception -> 0x0028 }
        L_0x0025:
            r4 = r5
            r0 = r1
        L_0x0027:
            return r6
        L_0x0028:
            r2 = move-exception
            r2.printStackTrace()
            goto L_0x0025
        L_0x002d:
            r2 = move-exception
        L_0x002e:
            r2.printStackTrace()     // Catch:{ all -> 0x0042 }
            if (r4 == 0) goto L_0x0036
            r4.close()     // Catch:{ Exception -> 0x003d }
        L_0x0036:
            if (r0 == 0) goto L_0x003b
            r0.close()     // Catch:{ Exception -> 0x003d }
        L_0x003b:
            r6 = 0
            goto L_0x0027
        L_0x003d:
            r2 = move-exception
            r2.printStackTrace()
            goto L_0x003b
        L_0x0042:
            r7 = move-exception
        L_0x0043:
            if (r4 == 0) goto L_0x0048
            r4.close()     // Catch:{ Exception -> 0x004e }
        L_0x0048:
            if (r0 == 0) goto L_0x004d
            r0.close()     // Catch:{ Exception -> 0x004e }
        L_0x004d:
            throw r7
        L_0x004e:
            r2 = move-exception
            r2.printStackTrace()
            goto L_0x004d
        L_0x0053:
            r7 = move-exception
            r4 = r5
            goto L_0x0043
        L_0x0056:
            r7 = move-exception
            r4 = r5
            r0 = r1
            goto L_0x0043
        L_0x005a:
            r2 = move-exception
            r4 = r5
            goto L_0x002e
        L_0x005d:
            r2 = move-exception
            r4 = r5
            r0 = r1
            goto L_0x002e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adasplus.adas.util.Util.getDeviceCode(android.content.Context):java.lang.String");
    }
}
