package com.adasplus.adas.util;

import android.os.Build;
import android.text.TextUtils;
import com.adasplus.adas.adas.AdasConstants;
import com.fvision.camera.utils.DoCmdUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.ZipInputStream;

public class FileUtils {
    /* JADX WARNING: Unknown top exception splitter block from list: {B:13:0x0020=Splitter:B:13:0x0020, B:25:0x0040=Splitter:B:25:0x0040} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void copyFile(File r8, File r9) {
        /*
            r2 = 0
            r4 = 0
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x006a, IOException -> 0x003f }
            r3.<init>(r8)     // Catch:{ FileNotFoundException -> 0x006a, IOException -> 0x003f }
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ FileNotFoundException -> 0x006c, IOException -> 0x0063, all -> 0x005c }
            r5.<init>(r9)     // Catch:{ FileNotFoundException -> 0x006c, IOException -> 0x0063, all -> 0x005c }
            r7 = 4096(0x1000, float:5.74E-42)
            byte[] r0 = new byte[r7]     // Catch:{ FileNotFoundException -> 0x001d, IOException -> 0x0066, all -> 0x005f }
            r6 = -1
        L_0x0011:
            int r6 = r3.read(r0)     // Catch:{ FileNotFoundException -> 0x001d, IOException -> 0x0066, all -> 0x005f }
            r7 = -1
            if (r6 == r7) goto L_0x002a
            r7 = 0
            r5.write(r0, r7, r6)     // Catch:{ FileNotFoundException -> 0x001d, IOException -> 0x0066, all -> 0x005f }
            goto L_0x0011
        L_0x001d:
            r1 = move-exception
            r4 = r5
            r2 = r3
        L_0x0020:
            r1.printStackTrace()     // Catch:{ all -> 0x004f }
            r2.close()     // Catch:{ IOException -> 0x003a }
            r4.close()     // Catch:{ IOException -> 0x003a }
        L_0x0029:
            return
        L_0x002a:
            r3.close()     // Catch:{ IOException -> 0x0033 }
            r5.close()     // Catch:{ IOException -> 0x0033 }
            r4 = r5
            r2 = r3
            goto L_0x0029
        L_0x0033:
            r1 = move-exception
            r1.printStackTrace()
            r4 = r5
            r2 = r3
            goto L_0x0029
        L_0x003a:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x0029
        L_0x003f:
            r1 = move-exception
        L_0x0040:
            r1.printStackTrace()     // Catch:{ all -> 0x004f }
            r2.close()     // Catch:{ IOException -> 0x004a }
            r4.close()     // Catch:{ IOException -> 0x004a }
            goto L_0x0029
        L_0x004a:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x0029
        L_0x004f:
            r7 = move-exception
        L_0x0050:
            r2.close()     // Catch:{ IOException -> 0x0057 }
            r4.close()     // Catch:{ IOException -> 0x0057 }
        L_0x0056:
            throw r7
        L_0x0057:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x0056
        L_0x005c:
            r7 = move-exception
            r2 = r3
            goto L_0x0050
        L_0x005f:
            r7 = move-exception
            r4 = r5
            r2 = r3
            goto L_0x0050
        L_0x0063:
            r1 = move-exception
            r2 = r3
            goto L_0x0040
        L_0x0066:
            r1 = move-exception
            r4 = r5
            r2 = r3
            goto L_0x0040
        L_0x006a:
            r1 = move-exception
            goto L_0x0020
        L_0x006c:
            r1 = move-exception
            r2 = r3
            goto L_0x0020
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adasplus.adas.util.FileUtils.copyFile(java.io.File, java.io.File):void");
    }

    public static void copyFile(InputStream is, File dest) {
        FileOutputStream fos = null;
        try {
            FileOutputStream fos2 = new FileOutputStream(dest);
            try {
                byte[] buffer = new byte[4096];
                while (true) {
                    int len = is.read(buffer);
                    if (len != -1) {
                        fos2.write(buffer, 0, len);
                    } else {
                        try {
                            fos2.close();
                            FileOutputStream fileOutputStream = fos2;
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                            FileOutputStream fileOutputStream2 = fos2;
                            return;
                        }
                    }
                }
            } catch (Exception e2) {
                e = e2;
                fos = fos2;
                try {
                    e.printStackTrace();
                    try {
                        fos.close();
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                } catch (Throwable th) {
                    th = th;
                    try {
                        fos.close();
                    } catch (Exception e4) {
                        e4.printStackTrace();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                fos = fos2;
                fos.close();
                throw th;
            }
        } catch (Exception e5) {
            e = e5;
            e.printStackTrace();
            fos.close();
        }
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:18:0x0022=Splitter:B:18:0x0022, B:11:0x0015=Splitter:B:11:0x0015} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void copyFile(byte[] r4, File r5) {
        /*
            r1 = 0
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ FileNotFoundException -> 0x0014, IOException -> 0x0021 }
            r2.<init>(r5)     // Catch:{ FileNotFoundException -> 0x0014, IOException -> 0x0021 }
            r2.write(r4)     // Catch:{ FileNotFoundException -> 0x003e, IOException -> 0x003b, all -> 0x0038 }
            r2.close()     // Catch:{ IOException -> 0x000e }
            r1 = r2
        L_0x000d:
            return
        L_0x000e:
            r0 = move-exception
            r0.printStackTrace()
            r1 = r2
            goto L_0x000d
        L_0x0014:
            r0 = move-exception
        L_0x0015:
            r0.printStackTrace()     // Catch:{ all -> 0x002e }
            r1.close()     // Catch:{ IOException -> 0x001c }
            goto L_0x000d
        L_0x001c:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x000d
        L_0x0021:
            r0 = move-exception
        L_0x0022:
            r0.printStackTrace()     // Catch:{ all -> 0x002e }
            r1.close()     // Catch:{ IOException -> 0x0029 }
            goto L_0x000d
        L_0x0029:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x000d
        L_0x002e:
            r3 = move-exception
        L_0x002f:
            r1.close()     // Catch:{ IOException -> 0x0033 }
        L_0x0032:
            throw r3
        L_0x0033:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x0032
        L_0x0038:
            r3 = move-exception
            r1 = r2
            goto L_0x002f
        L_0x003b:
            r0 = move-exception
            r1 = r2
            goto L_0x0022
        L_0x003e:
            r0 = move-exception
            r1 = r2
            goto L_0x0015
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adasplus.adas.util.FileUtils.copyFile(byte[], java.io.File):void");
    }

    public static void copyFile(ZipInputStream zis, File dest) {
        FileOutputStream fos = null;
        try {
            FileOutputStream fos2 = new FileOutputStream(dest);
            try {
                byte[] buffer = new byte[4096];
                while (true) {
                    int len = zis.read(buffer);
                    if (len != -1) {
                        fos2.write(buffer, 0, len);
                    } else {
                        try {
                            fos2.close();
                            FileOutputStream fileOutputStream = fos2;
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                            FileOutputStream fileOutputStream2 = fos2;
                            return;
                        }
                    }
                }
            } catch (FileNotFoundException e2) {
                fos = fos2;
                try {
                    fos.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            } catch (IOException e4) {
                fos = fos2;
                try {
                    fos.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            } catch (Throwable th) {
                th = th;
                fos = fos2;
                try {
                    fos.close();
                } catch (IOException e6) {
                    e6.printStackTrace();
                }
                throw th;
            }
        } catch (FileNotFoundException e7) {
            fos.close();
        } catch (IOException e8) {
            fos.close();
        } catch (Throwable th2) {
            th = th2;
            fos.close();
            throw th;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0022 A[SYNTHETIC, Splitter:B:14:0x0022] */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0027 A[SYNTHETIC, Splitter:B:17:0x0027] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0051 A[SYNTHETIC, Splitter:B:36:0x0051] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0056 A[SYNTHETIC, Splitter:B:39:0x0056] */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0067 A[SYNTHETIC, Splitter:B:47:0x0067] */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x006c A[SYNTHETIC, Splitter:B:50:0x006c] */
    /* JADX WARNING: Removed duplicated region for block: B:71:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:75:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void copyFile(String r8, String r9) {
        /*
            r2 = 0
            r4 = 0
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x0088, IOException -> 0x004e, all -> 0x0064 }
            r3.<init>(r8)     // Catch:{ FileNotFoundException -> 0x0088, IOException -> 0x004e, all -> 0x0064 }
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ FileNotFoundException -> 0x008a, IOException -> 0x0081, all -> 0x007a }
            r5.<init>(r9)     // Catch:{ FileNotFoundException -> 0x008a, IOException -> 0x0081, all -> 0x007a }
            r6 = 0
            r7 = 4096(0x1000, float:5.74E-42)
            byte[] r0 = new byte[r7]     // Catch:{ FileNotFoundException -> 0x001d, IOException -> 0x0084, all -> 0x007d }
        L_0x0011:
            int r6 = r3.read(r0)     // Catch:{ FileNotFoundException -> 0x001d, IOException -> 0x0084, all -> 0x007d }
            r7 = -1
            if (r6 == r7) goto L_0x002b
            r7 = 0
            r5.write(r0, r7, r6)     // Catch:{ FileNotFoundException -> 0x001d, IOException -> 0x0084, all -> 0x007d }
            goto L_0x0011
        L_0x001d:
            r7 = move-exception
            r4 = r5
            r2 = r3
        L_0x0020:
            if (r4 == 0) goto L_0x0025
            r4.close()     // Catch:{ IOException -> 0x0044 }
        L_0x0025:
            if (r2 == 0) goto L_0x002a
            r2.close()     // Catch:{ IOException -> 0x0049 }
        L_0x002a:
            return
        L_0x002b:
            if (r5 == 0) goto L_0x0030
            r5.close()     // Catch:{ IOException -> 0x0038 }
        L_0x0030:
            if (r3 == 0) goto L_0x008d
            r3.close()     // Catch:{ IOException -> 0x003d }
            r4 = r5
            r2 = r3
            goto L_0x002a
        L_0x0038:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x0030
        L_0x003d:
            r1 = move-exception
            r1.printStackTrace()
            r4 = r5
            r2 = r3
            goto L_0x002a
        L_0x0044:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x0025
        L_0x0049:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x002a
        L_0x004e:
            r7 = move-exception
        L_0x004f:
            if (r4 == 0) goto L_0x0054
            r4.close()     // Catch:{ IOException -> 0x005f }
        L_0x0054:
            if (r2 == 0) goto L_0x002a
            r2.close()     // Catch:{ IOException -> 0x005a }
            goto L_0x002a
        L_0x005a:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x002a
        L_0x005f:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x0054
        L_0x0064:
            r7 = move-exception
        L_0x0065:
            if (r4 == 0) goto L_0x006a
            r4.close()     // Catch:{ IOException -> 0x0070 }
        L_0x006a:
            if (r2 == 0) goto L_0x006f
            r2.close()     // Catch:{ IOException -> 0x0075 }
        L_0x006f:
            throw r7
        L_0x0070:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x006a
        L_0x0075:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x006f
        L_0x007a:
            r7 = move-exception
            r2 = r3
            goto L_0x0065
        L_0x007d:
            r7 = move-exception
            r4 = r5
            r2 = r3
            goto L_0x0065
        L_0x0081:
            r7 = move-exception
            r2 = r3
            goto L_0x004f
        L_0x0084:
            r7 = move-exception
            r4 = r5
            r2 = r3
            goto L_0x004f
        L_0x0088:
            r7 = move-exception
            goto L_0x0020
        L_0x008a:
            r7 = move-exception
            r2 = r3
            goto L_0x0020
        L_0x008d:
            r4 = r5
            r2 = r3
            goto L_0x002a
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adasplus.adas.util.FileUtils.copyFile(java.lang.String, java.lang.String):void");
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

    public static void clearDir(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                clearDir(file);
            } else {
                file.delete();
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x002f A[SYNTHETIC, Splitter:B:15:0x002f] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x003e A[SYNTHETIC, Splitter:B:23:0x003e] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x004a A[SYNTHETIC, Splitter:B:29:0x004a] */
    /* JADX WARNING: Removed duplicated region for block: B:43:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:45:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:20:0x0039=Splitter:B:20:0x0039, B:12:0x002a=Splitter:B:12:0x002a} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static String getFileString(File r8) {
        /*
            r5 = 0
            r2 = 0
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x0029, IOException -> 0x0038 }
            r3.<init>(r8)     // Catch:{ FileNotFoundException -> 0x0029, IOException -> 0x0038 }
            int r4 = r3.available()     // Catch:{ FileNotFoundException -> 0x0059, IOException -> 0x0056, all -> 0x0053 }
            byte[] r0 = new byte[r4]     // Catch:{ FileNotFoundException -> 0x0059, IOException -> 0x0056, all -> 0x0053 }
            r3.read(r0)     // Catch:{ FileNotFoundException -> 0x0059, IOException -> 0x0056, all -> 0x0053 }
            r3.close()     // Catch:{ FileNotFoundException -> 0x0059, IOException -> 0x0056, all -> 0x0053 }
            java.lang.String r6 = new java.lang.String     // Catch:{ FileNotFoundException -> 0x0059, IOException -> 0x0056, all -> 0x0053 }
            java.lang.String r7 = "UTF-8"
            r6.<init>(r0, r7)     // Catch:{ FileNotFoundException -> 0x0059, IOException -> 0x0056, all -> 0x0053 }
            if (r3 == 0) goto L_0x005c
            r3.close()     // Catch:{ IOException -> 0x0022 }
            r2 = r3
            r5 = r6
        L_0x0021:
            return r5
        L_0x0022:
            r1 = move-exception
            r1.printStackTrace()
            r2 = r3
            r5 = r6
            goto L_0x0021
        L_0x0029:
            r1 = move-exception
        L_0x002a:
            r1.printStackTrace()     // Catch:{ all -> 0x0047 }
            if (r2 == 0) goto L_0x0021
            r2.close()     // Catch:{ IOException -> 0x0033 }
            goto L_0x0021
        L_0x0033:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x0021
        L_0x0038:
            r1 = move-exception
        L_0x0039:
            r1.printStackTrace()     // Catch:{ all -> 0x0047 }
            if (r2 == 0) goto L_0x0021
            r2.close()     // Catch:{ IOException -> 0x0042 }
            goto L_0x0021
        L_0x0042:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x0021
        L_0x0047:
            r7 = move-exception
        L_0x0048:
            if (r2 == 0) goto L_0x004d
            r2.close()     // Catch:{ IOException -> 0x004e }
        L_0x004d:
            throw r7
        L_0x004e:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x004d
        L_0x0053:
            r7 = move-exception
            r2 = r3
            goto L_0x0048
        L_0x0056:
            r1 = move-exception
            r2 = r3
            goto L_0x0039
        L_0x0059:
            r1 = move-exception
            r2 = r3
            goto L_0x002a
        L_0x005c:
            r2 = r3
            r5 = r6
            goto L_0x0021
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adasplus.adas.util.FileUtils.getFileString(java.io.File):java.lang.String");
    }

    public static void clearBackupDir(File file) {
        for (File f : file.listFiles()) {
            if (!f.getName().equals("adas_0.0.5_001.zip")) {
                f.delete();
            }
        }
    }

    public static boolean verifyTotalFile(File file, HashMap<String, String> array) throws IOException {
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        StringBuffer sb = new StringBuffer();
        while (true) {
            String key = br.readLine();
            if (key == null) {
                break;
            }
            String value = br.readLine();
            array.put(key.split(":")[1], value.split(":")[1]);
            if (!key.split(":")[1].equals(AdasConstants.STR_TOTAL)) {
                sb.append(key + DoCmdUtil.COMMAND_LINE_END);
                sb.append(value + DoCmdUtil.COMMAND_LINE_END);
            }
        }
        br.close();
        reader.close();
        String md5 = Util.getByteMd5(sb.toString().getBytes()).toLowerCase();
        if (TextUtils.isEmpty(array.get(AdasConstants.STR_TOTAL)) || !array.get(AdasConstants.STR_TOTAL).equals(md5)) {
            return false;
        }
        return true;
    }

    public static boolean verifyItemFile(HashMap<String, String> array, String filePath) {
        String name;
        try {
            File file = new File(filePath);
            String md5 = Util.getMd5ByFile(file);
            String name2 = null;
            if (file.getName().equals(AdasConstants.LIB_ADAS) || file.getName().equals(AdasConstants.LIB_SENSOR)) {
                name2 = Build.CPU_ABI + File.separator;
            }
            if (TextUtils.isEmpty(name2)) {
                name = file.getName();
            } else {
                name = name2 + file.getName();
            }
            if (TextUtils.isEmpty(array.get(name)) || !array.get(name).equals(md5)) {
                return false;
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
