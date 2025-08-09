package com.tencent.bugly.proguard;

import android.content.Context;
import android.os.Process;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* compiled from: BUGLY */
public final class y {
    public static boolean a = true;
    private static boolean b = true;
    private static SimpleDateFormat c;
    private static int d = 30720;
    private static StringBuilder e;
    private static StringBuilder f;
    private static boolean g;
    private static a h;
    private static String i;
    private static String j;
    private static Context k;
    private static String l;
    private static boolean m;
    private static boolean n = false;
    private static ExecutorService o;
    private static int p;
    private static final Object q = new Object();

    static {
        c = null;
        try {
            c = new SimpleDateFormat("MM-dd HH:mm:ss");
        } catch (Throwable th) {
            x.b(th.getCause());
        }
    }

    public static synchronized void a(Context context) {
        synchronized (y.class) {
            if (!m && context != null && a) {
                try {
                    o = Executors.newSingleThreadExecutor();
                    f = new StringBuilder(0);
                    e = new StringBuilder(0);
                    k = context;
                    com.tencent.bugly.crashreport.common.info.a a2 = com.tencent.bugly.crashreport.common.info.a.a(context);
                    i = a2.d;
                    a2.getClass();
                    j = "";
                    l = k.getFilesDir().getPath() + "/buglylog_" + i + "_" + j + ".txt";
                    p = Process.myPid();
                } catch (Throwable th) {
                }
                m = true;
            }
        }
    }

    public static void a(int i2) {
        synchronized (q) {
            d = i2;
            if (i2 < 0) {
                d = 0;
            } else if (i2 > 30720) {
                d = 30720;
            }
        }
    }

    public static void a(String str, String str2, Throwable th) {
        if (th != null) {
            String message = th.getMessage();
            if (message == null) {
                message = "";
            }
            a(str, str2, message + 10 + z.b(th));
        }
    }

    public static synchronized void a(final String str, final String str2, final String str3) {
        synchronized (y.class) {
            if (m && a) {
                try {
                    o.execute(new Runnable() {
                        public final void run() {
                            y.c(str, str2, str3);
                        }
                    });
                } catch (Exception e2) {
                    x.b(e2);
                }
            }
        }
        return;
    }

    /* access modifiers changed from: private */
    public static synchronized void c(String str, String str2, String str3) {
        synchronized (y.class) {
            if (b) {
                d(str, str2, str3);
            } else {
                e(str, str2, str3);
            }
        }
    }

    private static synchronized void d(String str, String str2, String str3) {
        synchronized (y.class) {
            String a2 = a(str, str2, str3, (long) Process.myTid());
            synchronized (q) {
                try {
                    f.append(a2);
                    if (f.length() >= d) {
                        f = f.delete(0, f.indexOf("\u0001\r\n") + 1);
                    }
                } catch (Throwable th) {
                    if (!x.b(th)) {
                        th.printStackTrace();
                    }
                }
            }
        }
    }

    private static synchronized void e(String str, String str2, String str3) {
        synchronized (y.class) {
            String a2 = a(str, str2, str3, (long) Process.myTid());
            synchronized (q) {
                try {
                    f.append(a2);
                    if (f.length() > d) {
                        if (!g) {
                            g = true;
                            if (h == null) {
                                h = new a(l);
                            } else if (h.b == null || h.b.length() + ((long) f.length()) > h.e) {
                                boolean unused = h.a();
                            }
                            if (h.a(f.toString())) {
                                f.setLength(0);
                                g = false;
                            }
                        }
                    }
                } catch (Throwable th) {
                }
            }
        }
    }

    private static String a(String str, String str2, String str3, long j2) {
        String date;
        e.setLength(0);
        if (str3.length() > 30720) {
            str3 = str3.substring(str3.length() - 30720, str3.length() - 1);
        }
        Date date2 = new Date();
        if (c != null) {
            date = c.format(date2);
        } else {
            date = date2.toString();
        }
        e.append(date).append(" ").append(p).append(" ").append(j2).append(" ").append(str).append(" ").append(str2).append(": ").append(str3).append("\u0001\r\n");
        return e.toString();
    }

    public static byte[] a() {
        if (!b) {
            return b();
        }
        if (!a) {
            return null;
        }
        return z.a((File) null, f.toString(), "BuglyLog.txt");
    }

    private static byte[] b() {
        if (!a) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        synchronized (q) {
            if (h != null && h.a && h.b != null && h.b.length() > 0) {
                sb.append(z.a(h.b, 30720, true));
            }
            if (f != null && f.length() > 0) {
                sb.append(f.toString());
            }
        }
        return z.a((File) null, sb.toString(), "BuglyLog.txt");
    }

    /* compiled from: BUGLY */
    public static class a {
        /* access modifiers changed from: private */
        public boolean a;
        /* access modifiers changed from: private */
        public File b;
        private String c;
        private long d;
        /* access modifiers changed from: private */
        public long e = 30720;

        public a(String str) {
            if (str != null && !str.equals("")) {
                this.c = str;
                this.a = a();
            }
        }

        /* access modifiers changed from: private */
        public boolean a() {
            try {
                this.b = new File(this.c);
                if (this.b.exists() && !this.b.delete()) {
                    this.a = false;
                    return false;
                } else if (this.b.createNewFile()) {
                    return true;
                } else {
                    this.a = false;
                    return false;
                }
            } catch (Throwable th) {
                x.a(th);
                this.a = false;
                return false;
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:16:0x0038 A[SYNTHETIC, Splitter:B:16:0x0038] */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x0042 A[SYNTHETIC, Splitter:B:22:0x0042] */
        /* JADX WARNING: Removed duplicated region for block: B:31:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final boolean a(String r9) {
            /*
                r8 = this;
                r1 = 1
                r0 = 0
                boolean r2 = r8.a
                if (r2 != 0) goto L_0x0007
            L_0x0006:
                return r0
            L_0x0007:
                r3 = 0
                java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ Throwable -> 0x002e, all -> 0x003e }
                java.io.File r4 = r8.b     // Catch:{ Throwable -> 0x002e, all -> 0x003e }
                r5 = 1
                r2.<init>(r4, r5)     // Catch:{ Throwable -> 0x002e, all -> 0x003e }
                java.lang.String r3 = "UTF-8"
                byte[] r3 = r9.getBytes(r3)     // Catch:{ Throwable -> 0x004c }
                r2.write(r3)     // Catch:{ Throwable -> 0x004c }
                r2.flush()     // Catch:{ Throwable -> 0x004c }
                r2.close()     // Catch:{ Throwable -> 0x004c }
                long r4 = r8.d     // Catch:{ Throwable -> 0x004c }
                int r3 = r3.length     // Catch:{ Throwable -> 0x004c }
                long r6 = (long) r3     // Catch:{ Throwable -> 0x004c }
                long r4 = r4 + r6
                r8.d = r4     // Catch:{ Throwable -> 0x004c }
                r3 = 1
                r8.a = r3     // Catch:{ Throwable -> 0x004c }
                r2.close()     // Catch:{ IOException -> 0x0046 }
            L_0x002c:
                r0 = r1
                goto L_0x0006
            L_0x002e:
                r1 = move-exception
                r2 = r3
            L_0x0030:
                com.tencent.bugly.proguard.x.a(r1)     // Catch:{ all -> 0x004a }
                r1 = 0
                r8.a = r1     // Catch:{ all -> 0x004a }
                if (r2 == 0) goto L_0x0006
                r2.close()     // Catch:{ IOException -> 0x003c }
                goto L_0x0006
            L_0x003c:
                r1 = move-exception
                goto L_0x0006
            L_0x003e:
                r0 = move-exception
                r2 = r3
            L_0x0040:
                if (r2 == 0) goto L_0x0045
                r2.close()     // Catch:{ IOException -> 0x0048 }
            L_0x0045:
                throw r0
            L_0x0046:
                r0 = move-exception
                goto L_0x002c
            L_0x0048:
                r1 = move-exception
                goto L_0x0045
            L_0x004a:
                r0 = move-exception
                goto L_0x0040
            L_0x004c:
                r1 = move-exception
                goto L_0x0030
            */
            throw new UnsupportedOperationException("Method not decompiled: com.tencent.bugly.proguard.y.a.a(java.lang.String):boolean");
        }
    }
}
