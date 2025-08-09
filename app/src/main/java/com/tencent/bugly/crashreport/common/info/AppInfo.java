package com.tencent.bugly.crashreport.common.info;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.os.EnvironmentCompat;
import com.fvision.camera.utils.DoCmdUtil;
import com.tencent.bugly.proguard.x;
import com.tencent.bugly.proguard.z;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* compiled from: BUGLY */
public class AppInfo {
    private static ActivityManager a;

    static {
        "@buglyAllChannel@".split(",");
        "@buglyAllChannelPriority@".split(",");
    }

    public static String a(Context context) {
        if (context == null) {
            return null;
        }
        try {
            return context.getPackageName();
        } catch (Throwable th) {
            if (!x.a(th)) {
                th.printStackTrace();
            }
            return "fail";
        }
    }

    public static PackageInfo b(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(a(context), 0);
        } catch (Throwable th) {
            if (!x.a(th)) {
                th.printStackTrace();
            }
            return null;
        }
    }

    public static boolean a(Context context, String str) {
        if (context == null || str == null || str.trim().length() <= 0) {
            return false;
        }
        try {
            String[] strArr = context.getPackageManager().getPackageInfo(context.getPackageName(), 4096).requestedPermissions;
            if (strArr == null) {
                return false;
            }
            for (String equals : strArr) {
                if (str.equals(equals)) {
                    return true;
                }
            }
            return false;
        } catch (Throwable th) {
            if (x.a(th)) {
                return false;
            }
            th.printStackTrace();
            return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0043 A[Catch:{ all -> 0x005e }] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x004c A[SYNTHETIC, Splitter:B:23:0x004c] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0056 A[SYNTHETIC, Splitter:B:29:0x0056] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static String a(int r5) {
        /*
            r0 = 0
            r2 = 0
            java.io.FileReader r1 = new java.io.FileReader     // Catch:{ Throwable -> 0x003b, all -> 0x0052 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x003b, all -> 0x0052 }
            java.lang.String r4 = "/proc/"
            r3.<init>(r4)     // Catch:{ Throwable -> 0x003b, all -> 0x0052 }
            java.lang.StringBuilder r3 = r3.append(r5)     // Catch:{ Throwable -> 0x003b, all -> 0x0052 }
            java.lang.String r4 = "/cmdline"
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ Throwable -> 0x003b, all -> 0x0052 }
            java.lang.String r3 = r3.toString()     // Catch:{ Throwable -> 0x003b, all -> 0x0052 }
            r1.<init>(r3)     // Catch:{ Throwable -> 0x003b, all -> 0x0052 }
            r2 = 512(0x200, float:7.175E-43)
            char[] r2 = new char[r2]     // Catch:{ Throwable -> 0x0060 }
            r1.read(r2)     // Catch:{ Throwable -> 0x0060 }
        L_0x0023:
            int r3 = r2.length     // Catch:{ Throwable -> 0x0060 }
            if (r0 >= r3) goto L_0x002d
            char r3 = r2[r0]     // Catch:{ Throwable -> 0x0060 }
            if (r3 == 0) goto L_0x002d
            int r0 = r0 + 1
            goto L_0x0023
        L_0x002d:
            java.lang.String r3 = new java.lang.String     // Catch:{ Throwable -> 0x0060 }
            r3.<init>(r2)     // Catch:{ Throwable -> 0x0060 }
            r2 = 0
            java.lang.String r0 = r3.substring(r2, r0)     // Catch:{ Throwable -> 0x0060 }
            r1.close()     // Catch:{ Throwable -> 0x005a }
        L_0x003a:
            return r0
        L_0x003b:
            r0 = move-exception
            r1 = r2
        L_0x003d:
            boolean r2 = com.tencent.bugly.proguard.x.a(r0)     // Catch:{ all -> 0x005e }
            if (r2 != 0) goto L_0x0046
            r0.printStackTrace()     // Catch:{ all -> 0x005e }
        L_0x0046:
            java.lang.String r0 = java.lang.String.valueOf(r5)     // Catch:{ all -> 0x005e }
            if (r1 == 0) goto L_0x003a
            r1.close()     // Catch:{ Throwable -> 0x0050 }
            goto L_0x003a
        L_0x0050:
            r1 = move-exception
            goto L_0x003a
        L_0x0052:
            r0 = move-exception
            r1 = r2
        L_0x0054:
            if (r1 == 0) goto L_0x0059
            r1.close()     // Catch:{ Throwable -> 0x005c }
        L_0x0059:
            throw r0
        L_0x005a:
            r1 = move-exception
            goto L_0x003a
        L_0x005c:
            r1 = move-exception
            goto L_0x0059
        L_0x005e:
            r0 = move-exception
            goto L_0x0054
        L_0x0060:
            r0 = move-exception
            goto L_0x003d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tencent.bugly.crashreport.common.info.AppInfo.a(int):java.lang.String");
    }

    public static String c(Context context) {
        CharSequence applicationLabel;
        if (context == null) {
            return null;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = context.getApplicationInfo();
            if (packageManager == null || applicationInfo == null || (applicationLabel = packageManager.getApplicationLabel(applicationInfo)) == null) {
                return null;
            }
            return applicationLabel.toString();
        } catch (Throwable th) {
            if (x.a(th)) {
                return null;
            }
            th.printStackTrace();
            return null;
        }
    }

    public static Map<String, String> d(Context context) {
        HashMap hashMap;
        if (context == null) {
            return null;
        }
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
            if (applicationInfo.metaData != null) {
                hashMap = new HashMap();
                Object obj = applicationInfo.metaData.get("BUGLY_DISABLE");
                if (obj != null) {
                    hashMap.put("BUGLY_DISABLE", obj.toString());
                }
                Object obj2 = applicationInfo.metaData.get("BUGLY_APPID");
                if (obj2 != null) {
                    hashMap.put("BUGLY_APPID", obj2.toString());
                }
                Object obj3 = applicationInfo.metaData.get("BUGLY_APP_CHANNEL");
                if (obj3 != null) {
                    hashMap.put("BUGLY_APP_CHANNEL", obj3.toString());
                }
                Object obj4 = applicationInfo.metaData.get("BUGLY_APP_VERSION");
                if (obj4 != null) {
                    hashMap.put("BUGLY_APP_VERSION", obj4.toString());
                }
                Object obj5 = applicationInfo.metaData.get("BUGLY_ENABLE_DEBUG");
                if (obj5 != null) {
                    hashMap.put("BUGLY_ENABLE_DEBUG", obj5.toString());
                }
                Object obj6 = applicationInfo.metaData.get("com.tencent.rdm.uuid");
                if (obj6 != null) {
                    hashMap.put("com.tencent.rdm.uuid", obj6.toString());
                }
                Object obj7 = applicationInfo.metaData.get("BUGLY_APP_BUILD_NO");
                if (obj7 != null) {
                    hashMap.put("BUGLY_APP_BUILD_NO", obj7.toString());
                }
                Object obj8 = applicationInfo.metaData.get("BUGLY_AREA");
                if (obj8 != null) {
                    hashMap.put("BUGLY_AREA", obj8.toString());
                }
            } else {
                hashMap = null;
            }
            return hashMap;
        } catch (Throwable th) {
            if (x.a(th)) {
                return null;
            }
            th.printStackTrace();
            return null;
        }
    }

    public static List<String> a(Map<String, String> map) {
        if (map == null) {
            return null;
        }
        try {
            String str = map.get("BUGLY_DISABLE");
            if (str == null || str.length() == 0) {
                return null;
            }
            String[] split = str.split(",");
            for (int i = 0; i < split.length; i++) {
                split[i] = split[i].trim();
            }
            return Arrays.asList(split);
        } catch (Throwable th) {
            if (!x.a(th)) {
                th.printStackTrace();
            }
            return null;
        }
    }

    private static String a(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        if (bArr != null && bArr.length > 0) {
            try {
                CertificateFactory instance = CertificateFactory.getInstance("X.509");
                if (instance == null) {
                    return null;
                }
                X509Certificate x509Certificate = (X509Certificate) instance.generateCertificate(new ByteArrayInputStream(bArr));
                if (x509Certificate == null) {
                    return null;
                }
                sb.append("Issuer|");
                Principal issuerDN = x509Certificate.getIssuerDN();
                if (issuerDN != null) {
                    sb.append(issuerDN.toString());
                } else {
                    sb.append(EnvironmentCompat.MEDIA_UNKNOWN);
                }
                sb.append(DoCmdUtil.COMMAND_LINE_END);
                sb.append("SerialNumber|");
                BigInteger serialNumber = x509Certificate.getSerialNumber();
                if (issuerDN != null) {
                    sb.append(serialNumber.toString(16));
                } else {
                    sb.append(EnvironmentCompat.MEDIA_UNKNOWN);
                }
                sb.append(DoCmdUtil.COMMAND_LINE_END);
                sb.append("NotBefore|");
                Date notBefore = x509Certificate.getNotBefore();
                if (issuerDN != null) {
                    sb.append(notBefore.toString());
                } else {
                    sb.append(EnvironmentCompat.MEDIA_UNKNOWN);
                }
                sb.append(DoCmdUtil.COMMAND_LINE_END);
                sb.append("NotAfter|");
                Date notAfter = x509Certificate.getNotAfter();
                if (issuerDN != null) {
                    sb.append(notAfter.toString());
                } else {
                    sb.append(EnvironmentCompat.MEDIA_UNKNOWN);
                }
                sb.append(DoCmdUtil.COMMAND_LINE_END);
                sb.append("SHA1|");
                String a2 = z.a(MessageDigest.getInstance("SHA1").digest(x509Certificate.getEncoded()));
                if (a2 == null || a2.length() <= 0) {
                    sb.append(EnvironmentCompat.MEDIA_UNKNOWN);
                } else {
                    sb.append(a2.toString());
                }
                sb.append(DoCmdUtil.COMMAND_LINE_END);
                sb.append("MD5|");
                String a3 = z.a(MessageDigest.getInstance("MD5").digest(x509Certificate.getEncoded()));
                if (a3 == null || a3.length() <= 0) {
                    sb.append(EnvironmentCompat.MEDIA_UNKNOWN);
                } else {
                    sb.append(a3.toString());
                }
            } catch (CertificateException e) {
                if (!x.a(e)) {
                    e.printStackTrace();
                }
            } catch (Throwable th) {
                if (!x.a(th)) {
                    th.printStackTrace();
                }
            }
        }
        if (sb.length() == 0) {
            return EnvironmentCompat.MEDIA_UNKNOWN;
        }
        return sb.toString();
    }

    public static String e(Context context) {
        Signature[] signatureArr;
        String a2 = a(context);
        if (a2 == null) {
            return null;
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(a2, 64);
            if (packageInfo == null || (signatureArr = packageInfo.signatures) == null || signatureArr.length == 0) {
                return null;
            }
            return a(packageInfo.signatures[0].toByteArray());
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static boolean f(Context context) {
        if (context == null) {
            return false;
        }
        if (a == null) {
            a = (ActivityManager) context.getSystemService("activity");
        }
        try {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            a.getMemoryInfo(memoryInfo);
            if (!memoryInfo.lowMemory) {
                return false;
            }
            x.c("Memory is low.", new Object[0]);
            return true;
        } catch (Throwable th) {
            if (!x.a(th)) {
                th.printStackTrace();
            }
            return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x009d A[SYNTHETIC, Splitter:B:38:0x009d] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static String h(Context r6) {
        /*
            java.lang.String r2 = ""
            java.lang.String r0 = "DENGTA_META"
            android.content.SharedPreferences r1 = com.tencent.bugly.proguard.z.a((java.lang.String) r0, (android.content.Context) r6)
            r0 = 0
            java.lang.String r3 = "key_channelpath"
            java.lang.String r4 = ""
            java.lang.String r1 = r1.getString(r3, r4)     // Catch:{ Exception -> 0x0081, all -> 0x0097 }
            boolean r3 = com.tencent.bugly.proguard.z.a((java.lang.String) r1)     // Catch:{ Exception -> 0x0081, all -> 0x0097 }
            if (r3 == 0) goto L_0x0019
            java.lang.String r1 = "channel.ini"
        L_0x0019:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0081, all -> 0x0097 }
            java.lang.String r4 = "[AppInfo] Beacon channel file path: "
            r3.<init>(r4)     // Catch:{ Exception -> 0x0081, all -> 0x0097 }
            java.lang.StringBuilder r3 = r3.append(r1)     // Catch:{ Exception -> 0x0081, all -> 0x0097 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0081, all -> 0x0097 }
            r4 = 0
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x0081, all -> 0x0097 }
            com.tencent.bugly.proguard.x.a(r3, r4)     // Catch:{ Exception -> 0x0081, all -> 0x0097 }
            java.lang.String r3 = ""
            boolean r3 = r1.equals(r3)     // Catch:{ Exception -> 0x0081, all -> 0x0097 }
            if (r3 != 0) goto L_0x0074
            android.content.res.AssetManager r3 = r6.getAssets()     // Catch:{ Exception -> 0x0081, all -> 0x0097 }
            java.io.InputStream r1 = r3.open(r1)     // Catch:{ Exception -> 0x0081, all -> 0x0097 }
            java.util.Properties r0 = new java.util.Properties     // Catch:{ Exception -> 0x00a8 }
            r0.<init>()     // Catch:{ Exception -> 0x00a8 }
            r0.load(r1)     // Catch:{ Exception -> 0x00a8 }
            java.lang.String r3 = "CHANNEL"
            java.lang.String r4 = ""
            java.lang.String r0 = r0.getProperty(r3, r4)     // Catch:{ Exception -> 0x00a8 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00ab }
            java.lang.String r3 = "[AppInfo] Beacon channel read from assert: "
            r2.<init>(r3)     // Catch:{ Exception -> 0x00ab }
            java.lang.StringBuilder r2 = r2.append(r0)     // Catch:{ Exception -> 0x00ab }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00ab }
            r3 = 0
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x00ab }
            com.tencent.bugly.proguard.x.a(r2, r3)     // Catch:{ Exception -> 0x00ab }
            boolean r2 = com.tencent.bugly.proguard.z.a((java.lang.String) r0)     // Catch:{ Exception -> 0x00ab }
            if (r2 != 0) goto L_0x0076
            if (r1 == 0) goto L_0x006e
            r1.close()     // Catch:{ IOException -> 0x006f }
        L_0x006e:
            return r0
        L_0x006f:
            r1 = move-exception
            com.tencent.bugly.proguard.x.a(r1)
            goto L_0x006e
        L_0x0074:
            r1 = r0
            r0 = r2
        L_0x0076:
            if (r1 == 0) goto L_0x006e
            r1.close()     // Catch:{ IOException -> 0x007c }
            goto L_0x006e
        L_0x007c:
            r1 = move-exception
            com.tencent.bugly.proguard.x.a(r1)
            goto L_0x006e
        L_0x0081:
            r1 = move-exception
            r1 = r0
            r0 = r2
        L_0x0084:
            java.lang.String r2 = "[AppInfo] Failed to get get beacon channel"
            r3 = 0
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x00a6 }
            com.tencent.bugly.proguard.x.d(r2, r3)     // Catch:{ all -> 0x00a6 }
            if (r1 == 0) goto L_0x006e
            r1.close()     // Catch:{ IOException -> 0x0092 }
            goto L_0x006e
        L_0x0092:
            r1 = move-exception
            com.tencent.bugly.proguard.x.a(r1)
            goto L_0x006e
        L_0x0097:
            r1 = move-exception
            r5 = r1
            r1 = r0
            r0 = r5
        L_0x009b:
            if (r1 == 0) goto L_0x00a0
            r1.close()     // Catch:{ IOException -> 0x00a1 }
        L_0x00a0:
            throw r0
        L_0x00a1:
            r1 = move-exception
            com.tencent.bugly.proguard.x.a(r1)
            goto L_0x00a0
        L_0x00a6:
            r0 = move-exception
            goto L_0x009b
        L_0x00a8:
            r0 = move-exception
            r0 = r2
            goto L_0x0084
        L_0x00ab:
            r2 = move-exception
            goto L_0x0084
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tencent.bugly.crashreport.common.info.AppInfo.h(android.content.Context):java.lang.String");
    }

    private static String i(Context context) {
        try {
            Object obj = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData.get("CHANNEL_DENGTA");
            if (obj != null) {
                return obj.toString();
            }
            return "";
        } catch (Throwable th) {
            x.d("[AppInfo] Failed to read beacon channel from manifest.", new Object[0]);
            return "";
        }
    }

    public static String g(Context context) {
        if (context == null) {
            return "";
        }
        String h = h(context);
        if (z.a(h)) {
            return i(context);
        }
        return h;
    }
}
