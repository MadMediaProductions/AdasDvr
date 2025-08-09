package com.tencent.bugly.crashreport.crash.anr;

import android.app.ActivityManager;
import android.content.Context;
import android.os.FileObserver;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;
import com.fvision.camera.utils.DoCmdUtil;
import com.tencent.bugly.crashreport.common.info.a;
import com.tencent.bugly.crashreport.crash.CrashDetailBean;
import com.tencent.bugly.crashreport.crash.c;
import com.tencent.bugly.proguard.aa;
import com.tencent.bugly.proguard.ab;
import com.tencent.bugly.proguard.ac;
import com.tencent.bugly.proguard.w;
import com.tencent.bugly.proguard.x;
import com.tencent.bugly.proguard.y;
import com.tencent.bugly.proguard.z;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/* compiled from: BUGLY */
public final class b implements ac {
    private AtomicInteger a = new AtomicInteger(0);
    private long b = -1;
    private final Context c;
    private final a d;
    private final w e;
    private final com.tencent.bugly.crashreport.common.strategy.a f;
    private final String g;
    private final com.tencent.bugly.crashreport.crash.b h;
    private FileObserver i;
    private boolean j = true;
    private ab k;
    private int l;
    private ActivityManager.ProcessErrorStateInfo m;

    public b(Context context, com.tencent.bugly.crashreport.common.strategy.a aVar, a aVar2, w wVar, com.tencent.bugly.crashreport.crash.b bVar) {
        this.c = z.a(context);
        this.g = context.getDir("bugly", 0).getAbsolutePath();
        this.d = aVar2;
        this.e = wVar;
        this.f = aVar;
        this.h = bVar;
        this.m = new ActivityManager.ProcessErrorStateInfo();
    }

    private ActivityManager.ProcessErrorStateInfo a(Context context, long j2) {
        long j3 = 0;
        int i2 = 0;
        if (10000 >= 0) {
            j3 = 10000;
        }
        try {
            x.c("to find!", new Object[0]);
            ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
            long j4 = j3 / 500;
            while (true) {
                int i3 = i2;
                x.c("waiting!", new Object[0]);
                List<ActivityManager.ProcessErrorStateInfo> processesInErrorState = activityManager.getProcessesInErrorState();
                if (processesInErrorState != null) {
                    for (ActivityManager.ProcessErrorStateInfo next : processesInErrorState) {
                        if (next.condition == 2) {
                            x.c("found!", new Object[0]);
                            return next;
                        }
                    }
                }
                z.b(500);
                i2 = i3 + 1;
                if (((long) i3) >= j4) {
                    x.c("end!", new Object[0]);
                    break;
                }
            }
        } catch (Exception e2) {
            x.b(e2);
        } catch (OutOfMemoryError e3) {
            this.m.pid = Process.myPid();
            this.m.shortMsg = "bugly sdk waitForAnrProcessStateChanged encount error:" + e3.getMessage();
            return this.m;
        }
        return null;
    }

    private CrashDetailBean a(a aVar) {
        CrashDetailBean crashDetailBean = new CrashDetailBean();
        try {
            crashDetailBean.C = com.tencent.bugly.crashreport.common.info.b.k();
            crashDetailBean.D = com.tencent.bugly.crashreport.common.info.b.i();
            crashDetailBean.E = com.tencent.bugly.crashreport.common.info.b.m();
            crashDetailBean.F = this.d.p();
            crashDetailBean.G = this.d.o();
            crashDetailBean.H = this.d.q();
            Context context = this.c;
            if (!com.tencent.bugly.crashreport.common.info.b.t()) {
                crashDetailBean.w = z.a(this.c, c.e, (String) null);
            }
            crashDetailBean.b = 3;
            crashDetailBean.e = this.d.h();
            crashDetailBean.f = this.d.k;
            crashDetailBean.g = this.d.w();
            crashDetailBean.m = this.d.g();
            crashDetailBean.n = "ANR_EXCEPTION";
            crashDetailBean.o = aVar.f;
            crashDetailBean.q = aVar.g;
            crashDetailBean.P = new HashMap();
            crashDetailBean.P.put("BUGLY_CR_01", aVar.e);
            int i2 = -1;
            if (crashDetailBean.q != null) {
                i2 = crashDetailBean.q.indexOf(DoCmdUtil.COMMAND_LINE_END);
            }
            crashDetailBean.p = i2 > 0 ? crashDetailBean.q.substring(0, i2) : "GET_FAIL";
            crashDetailBean.r = aVar.c;
            if (crashDetailBean.q != null) {
                crashDetailBean.u = z.b(crashDetailBean.q.getBytes());
            }
            crashDetailBean.z = aVar.b;
            crashDetailBean.A = aVar.a;
            crashDetailBean.B = "main(1)";
            crashDetailBean.I = this.d.y();
            crashDetailBean.h = this.d.v();
            crashDetailBean.i = this.d.J();
            crashDetailBean.v = aVar.d;
            crashDetailBean.L = this.d.o;
            crashDetailBean.M = this.d.a;
            crashDetailBean.N = this.d.a();
            crashDetailBean.Q = this.d.H();
            crashDetailBean.R = this.d.I();
            crashDetailBean.S = this.d.B();
            crashDetailBean.T = this.d.G();
            Context context2 = this.c;
            if (!com.tencent.bugly.crashreport.common.info.b.t()) {
                this.h.d(crashDetailBean);
            }
            crashDetailBean.y = y.a();
        } catch (Throwable th) {
            if (!x.a(th)) {
                th.printStackTrace();
            }
        }
        return crashDetailBean;
    }

    /* JADX WARNING: Removed duplicated region for block: B:49:0x017d A[Catch:{ all -> 0x01e7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x01b0 A[SYNTHETIC, Splitter:B:52:0x01b0] */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x01d6 A[SYNTHETIC, Splitter:B:69:0x01d6] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean a(String r12, String r13, String r14) {
        /*
            r11 = 3
            r5 = 2
            r3 = 1
            r4 = 0
            com.tencent.bugly.crashreport.crash.anr.TraceFileHelper$a r6 = com.tencent.bugly.crashreport.crash.anr.TraceFileHelper.readTargetDumpInfo(r14, r12, r3)
            if (r6 == 0) goto L_0x0016
            java.util.Map<java.lang.String, java.lang.String[]> r1 = r6.d
            if (r1 == 0) goto L_0x0016
            java.util.Map<java.lang.String, java.lang.String[]> r1 = r6.d
            int r1 = r1.size()
            if (r1 > 0) goto L_0x0021
        L_0x0016:
            java.lang.String r1 = "not found trace dump for %s"
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r4] = r14
            com.tencent.bugly.proguard.x.e(r1, r2)
            r1 = r4
        L_0x0020:
            return r1
        L_0x0021:
            java.io.File r1 = new java.io.File
            r1.<init>(r13)
            boolean r2 = r1.exists()     // Catch:{ Exception -> 0x0057 }
            if (r2 != 0) goto L_0x0040
            java.io.File r2 = r1.getParentFile()     // Catch:{ Exception -> 0x0057 }
            boolean r2 = r2.exists()     // Catch:{ Exception -> 0x0057 }
            if (r2 != 0) goto L_0x003d
            java.io.File r2 = r1.getParentFile()     // Catch:{ Exception -> 0x0057 }
            r2.mkdirs()     // Catch:{ Exception -> 0x0057 }
        L_0x003d:
            r1.createNewFile()     // Catch:{ Exception -> 0x0057 }
        L_0x0040:
            boolean r2 = r1.exists()
            if (r2 == 0) goto L_0x004c
            boolean r2 = r1.canWrite()
            if (r2 != 0) goto L_0x0091
        L_0x004c:
            java.lang.String r1 = "backup file create fail %s"
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r4] = r13
            com.tencent.bugly.proguard.x.e(r1, r2)
            r1 = r4
            goto L_0x0020
        L_0x0057:
            r1 = move-exception
            boolean r2 = com.tencent.bugly.proguard.x.a(r1)
            if (r2 != 0) goto L_0x0061
            r1.printStackTrace()
        L_0x0061:
            java.lang.String r2 = "backup file create error! %s  %s"
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.Class r7 = r1.getClass()
            java.lang.String r7 = r7.getName()
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r7 = ":"
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r1 = r1.getMessage()
            java.lang.StringBuilder r1 = r6.append(r1)
            java.lang.String r1 = r1.toString()
            r5[r4] = r1
            r5[r3] = r13
            com.tencent.bugly.proguard.x.e(r2, r5)
            r1 = r4
            goto L_0x0020
        L_0x0091:
            r2 = 0
            java.io.BufferedWriter r5 = new java.io.BufferedWriter     // Catch:{ IOException -> 0x01ea, all -> 0x01d2 }
            java.io.FileWriter r7 = new java.io.FileWriter     // Catch:{ IOException -> 0x01ea, all -> 0x01d2 }
            r8 = 0
            r7.<init>(r1, r8)     // Catch:{ IOException -> 0x01ea, all -> 0x01d2 }
            r5.<init>(r7)     // Catch:{ IOException -> 0x01ea, all -> 0x01d2 }
            java.util.Map<java.lang.String, java.lang.String[]> r1 = r6.d     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String r2 = "main"
            java.lang.Object r1 = r1.get(r2)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String[] r1 = (java.lang.String[]) r1     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            if (r1 == 0) goto L_0x00e4
            int r2 = r1.length     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            if (r2 < r11) goto L_0x00e4
            r2 = 0
            r2 = r1[r2]     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            r7 = 1
            r7 = r1[r7]     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            r8 = 2
            r1 = r1[r8]     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String r9 = "\"main\" tid="
            r8.<init>(r9)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.StringBuilder r1 = r8.append(r1)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String r8 = " :\n"
            java.lang.StringBuilder r1 = r1.append(r8)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String r2 = "\n"
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.StringBuilder r1 = r1.append(r7)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String r2 = "\n\n"
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String r1 = r1.toString()     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            r5.write(r1)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            r5.flush()     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
        L_0x00e4:
            java.util.Map<java.lang.String, java.lang.String[]> r1 = r6.d     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.util.Set r1 = r1.entrySet()     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.util.Iterator r6 = r1.iterator()     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
        L_0x00ee:
            boolean r1 = r6.hasNext()     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            if (r1 == 0) goto L_0x01b6
            java.lang.Object r1 = r6.next()     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            r0 = r1
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            r2 = r0
            java.lang.Object r1 = r2.getKey()     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String r7 = "main"
            boolean r1 = r1.equals(r7)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            if (r1 != 0) goto L_0x00ee
            java.lang.Object r1 = r2.getValue()     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            if (r1 == 0) goto L_0x00ee
            java.lang.Object r1 = r2.getValue()     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String[] r1 = (java.lang.String[]) r1     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            int r1 = r1.length     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            if (r1 < r11) goto L_0x00ee
            java.lang.Object r1 = r2.getValue()     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String[] r1 = (java.lang.String[]) r1     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            r7 = 0
            r7 = r1[r7]     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.Object r1 = r2.getValue()     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String[] r1 = (java.lang.String[]) r1     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            r8 = 1
            r8 = r1[r8]     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.Object r1 = r2.getValue()     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String[] r1 = (java.lang.String[]) r1     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            r9 = 2
            r9 = r1[r9]     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String r1 = "\""
            r10.<init>(r1)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.Object r1 = r2.getKey()     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.StringBuilder r1 = r10.append(r1)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String r2 = "\" tid="
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.StringBuilder r1 = r1.append(r9)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String r2 = " :\n"
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.StringBuilder r1 = r1.append(r7)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String r2 = "\n"
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.StringBuilder r1 = r1.append(r8)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String r2 = "\n\n"
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            java.lang.String r1 = r1.toString()     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            r5.write(r1)     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            r5.flush()     // Catch:{ IOException -> 0x0175, all -> 0x01e5 }
            goto L_0x00ee
        L_0x0175:
            r1 = move-exception
            r2 = r5
        L_0x0177:
            boolean r3 = com.tencent.bugly.proguard.x.a(r1)     // Catch:{ all -> 0x01e7 }
            if (r3 != 0) goto L_0x0180
            r1.printStackTrace()     // Catch:{ all -> 0x01e7 }
        L_0x0180:
            java.lang.String r3 = "dump trace fail %s"
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x01e7 }
            r6 = 0
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e7 }
            r7.<init>()     // Catch:{ all -> 0x01e7 }
            java.lang.Class r8 = r1.getClass()     // Catch:{ all -> 0x01e7 }
            java.lang.String r8 = r8.getName()     // Catch:{ all -> 0x01e7 }
            java.lang.StringBuilder r7 = r7.append(r8)     // Catch:{ all -> 0x01e7 }
            java.lang.String r8 = ":"
            java.lang.StringBuilder r7 = r7.append(r8)     // Catch:{ all -> 0x01e7 }
            java.lang.String r1 = r1.getMessage()     // Catch:{ all -> 0x01e7 }
            java.lang.StringBuilder r1 = r7.append(r1)     // Catch:{ all -> 0x01e7 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x01e7 }
            r5[r6] = r1     // Catch:{ all -> 0x01e7 }
            com.tencent.bugly.proguard.x.e(r3, r5)     // Catch:{ all -> 0x01e7 }
            if (r2 == 0) goto L_0x01b3
            r2.close()     // Catch:{ IOException -> 0x01c7 }
        L_0x01b3:
            r1 = r4
            goto L_0x0020
        L_0x01b6:
            r5.close()     // Catch:{ IOException -> 0x01bc }
        L_0x01b9:
            r1 = r3
            goto L_0x0020
        L_0x01bc:
            r1 = move-exception
            boolean r2 = com.tencent.bugly.proguard.x.a(r1)
            if (r2 != 0) goto L_0x01b9
            r1.printStackTrace()
            goto L_0x01b9
        L_0x01c7:
            r1 = move-exception
            boolean r2 = com.tencent.bugly.proguard.x.a(r1)
            if (r2 != 0) goto L_0x01b3
            r1.printStackTrace()
            goto L_0x01b3
        L_0x01d2:
            r1 = move-exception
            r5 = r2
        L_0x01d4:
            if (r5 == 0) goto L_0x01d9
            r5.close()     // Catch:{ IOException -> 0x01da }
        L_0x01d9:
            throw r1
        L_0x01da:
            r2 = move-exception
            boolean r3 = com.tencent.bugly.proguard.x.a(r2)
            if (r3 != 0) goto L_0x01d9
            r2.printStackTrace()
            goto L_0x01d9
        L_0x01e5:
            r1 = move-exception
            goto L_0x01d4
        L_0x01e7:
            r1 = move-exception
            r5 = r2
            goto L_0x01d4
        L_0x01ea:
            r1 = move-exception
            goto L_0x0177
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tencent.bugly.crashreport.crash.anr.b.a(java.lang.String, java.lang.String, java.lang.String):boolean");
    }

    public final boolean a() {
        return this.a.get() != 0;
    }

    private boolean a(Context context, String str, ActivityManager.ProcessErrorStateInfo processErrorStateInfo, long j2, Map<String, String> map) {
        File file = new File(context.getFilesDir(), "bugly/bugly_trace_" + j2 + ".txt");
        a aVar = new a();
        aVar.c = j2;
        aVar.d = file.getAbsolutePath();
        aVar.a = processErrorStateInfo != null ? processErrorStateInfo.processName : "";
        aVar.f = processErrorStateInfo != null ? processErrorStateInfo.shortMsg : "";
        aVar.e = processErrorStateInfo != null ? processErrorStateInfo.longMsg : "";
        aVar.b = map;
        Thread thread = Looper.getMainLooper().getThread();
        if (map != null) {
            Iterator<String> it = map.keySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                String next = it.next();
                if (next.startsWith(thread.getName())) {
                    aVar.g = map.get(next);
                    break;
                }
            }
        }
        if (TextUtils.isEmpty(aVar.g)) {
            aVar.g = "main stack is null , some error may be encountered.";
        }
        Object[] objArr = new Object[7];
        objArr[0] = Long.valueOf(aVar.c);
        objArr[1] = aVar.d;
        objArr[2] = aVar.a;
        objArr[3] = aVar.g;
        objArr[4] = aVar.f;
        objArr[5] = aVar.e;
        objArr[6] = Integer.valueOf(aVar.b == null ? 0 : aVar.b.size());
        x.c("anr tm:%d\ntr:%s\nproc:%s\nmain stack:%s\nsMsg:%s\n lMsg:%s\n threads:%d", objArr);
        if (!this.f.c().j) {
            x.d("ANR Report is closed! print local for helpful!", new Object[0]);
            com.tencent.bugly.crashreport.crash.b.a("ANR", z.a(), aVar.a, "main", aVar.g, (CrashDetailBean) null);
            return false;
        }
        x.a("found visiable anr , start to upload!", new Object[0]);
        CrashDetailBean a2 = a(aVar);
        if (a2 == null) {
            x.e("pack anr fail!", new Object[0]);
            return false;
        }
        c.a().a(a2);
        if (a2.a >= 0) {
            x.a("backup anr record success!", new Object[0]);
        } else {
            x.d("backup anr record fail!", new Object[0]);
        }
        if (str != null && new File(str).exists()) {
            this.a.set(3);
            if (a(str, aVar.d, aVar.a)) {
                x.a("backup trace success", new Object[0]);
            }
        }
        com.tencent.bugly.crashreport.crash.b.a("ANR", z.a(), aVar.a, "main", aVar.g, a2);
        if (!this.h.a(a2)) {
            this.h.a(a2, 3000, true);
        }
        this.h.c(a2);
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        com.tencent.bugly.proguard.x.c("read trace first dump for create time!", new java.lang.Object[0]);
        r2 = com.tencent.bugly.crashreport.crash.anr.TraceFileHelper.readFirstDumpInfo(r11, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002c, code lost:
        if (r2 == null) goto L_0x011e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002e, code lost:
        r4 = r2.c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0032, code lost:
        if (r4 != -1) goto L_0x0040;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0034, code lost:
        com.tencent.bugly.proguard.x.d("trace dump fail could not get time!", new java.lang.Object[0]);
        r4 = java.lang.System.currentTimeMillis();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x004a, code lost:
        if (java.lang.Math.abs(r4 - r10.b) >= 10000) goto L_0x0066;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004c, code lost:
        com.tencent.bugly.proguard.x.d("should not process ANR too Fre in %d", 10000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005d, code lost:
        r10.a.set(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        r10.b = r4;
        r10.a.set(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
        r6 = com.tencent.bugly.proguard.z.a(com.tencent.bugly.crashreport.crash.c.f, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0075, code lost:
        if (r6 == null) goto L_0x007d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x007b, code lost:
        if (r6.size() > 0) goto L_0x009e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x007d, code lost:
        com.tencent.bugly.proguard.x.d("can't get all thread skip this anr", new java.lang.Object[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0085, code lost:
        r10.a.set(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x008b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:?, code lost:
        com.tencent.bugly.proguard.x.a(r0);
        com.tencent.bugly.proguard.x.e("get all thread stack fail!", new java.lang.Object[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0097, code lost:
        r10.a.set(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:?, code lost:
        r10.m = a(r10.c, 10000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00aa, code lost:
        if (r10.m != null) goto L_0x00bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00ac, code lost:
        com.tencent.bugly.proguard.x.c("proc state is unvisiable!", new java.lang.Object[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00b4, code lost:
        r10.a.set(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00c3, code lost:
        if (r10.m.pid == android.os.Process.myPid()) goto L_0x00db;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00c5, code lost:
        com.tencent.bugly.proguard.x.c("not mind proc!", r10.m.processName);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00d4, code lost:
        r10.a.set(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:?, code lost:
        com.tencent.bugly.proguard.x.a("found visiable anr , start to process!", new java.lang.Object[0]);
        a(r10.c, r11, r10.m, r4, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00ec, code lost:
        r10.a.set(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00f3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00f8, code lost:
        if (com.tencent.bugly.proguard.x.a(r0) == false) goto L_0x00fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00fa, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00fd, code lost:
        com.tencent.bugly.proguard.x.e("handle anr error %s", r0.getClass().toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0117, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0118, code lost:
        r10.a.set(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x011d, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x011e, code lost:
        r4 = -1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:?, code lost:
        return;
     */
    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void a(String r11) {
        /*
            r10 = this;
            r8 = 10000(0x2710, double:4.9407E-320)
            r0 = -1
            r7 = 0
            monitor-enter(r10)
            java.util.concurrent.atomic.AtomicInteger r2 = r10.a     // Catch:{ all -> 0x0063 }
            int r2 = r2.get()     // Catch:{ all -> 0x0063 }
            if (r2 == 0) goto L_0x0018
            java.lang.String r0 = "trace started return "
            r1 = 0
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x0063 }
            com.tencent.bugly.proguard.x.c(r0, r1)     // Catch:{ all -> 0x0063 }
            monitor-exit(r10)     // Catch:{ all -> 0x0063 }
        L_0x0017:
            return
        L_0x0018:
            java.util.concurrent.atomic.AtomicInteger r2 = r10.a     // Catch:{ all -> 0x0063 }
            r3 = 1
            r2.set(r3)     // Catch:{ all -> 0x0063 }
            monitor-exit(r10)     // Catch:{ all -> 0x0063 }
            java.lang.String r2 = "read trace first dump for create time!"
            r3 = 0
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ Throwable -> 0x00f3 }
            com.tencent.bugly.proguard.x.c(r2, r3)     // Catch:{ Throwable -> 0x00f3 }
            r2 = 0
            com.tencent.bugly.crashreport.crash.anr.TraceFileHelper$a r2 = com.tencent.bugly.crashreport.crash.anr.TraceFileHelper.readFirstDumpInfo(r11, r2)     // Catch:{ Throwable -> 0x00f3 }
            if (r2 == 0) goto L_0x011e
            long r4 = r2.c     // Catch:{ Throwable -> 0x00f3 }
        L_0x0030:
            int r0 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r0 != 0) goto L_0x0040
            java.lang.String r0 = "trace dump fail could not get time!"
            r1 = 0
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ Throwable -> 0x00f3 }
            com.tencent.bugly.proguard.x.d(r0, r1)     // Catch:{ Throwable -> 0x00f3 }
            long r4 = java.lang.System.currentTimeMillis()     // Catch:{ Throwable -> 0x00f3 }
        L_0x0040:
            long r0 = r10.b     // Catch:{ Throwable -> 0x00f3 }
            long r0 = r4 - r0
            long r0 = java.lang.Math.abs(r0)     // Catch:{ Throwable -> 0x00f3 }
            int r0 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
            if (r0 >= 0) goto L_0x0066
            java.lang.String r0 = "should not process ANR too Fre in %d"
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ Throwable -> 0x00f3 }
            r2 = 0
            r3 = 10000(0x2710, float:1.4013E-41)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ Throwable -> 0x00f3 }
            r1[r2] = r3     // Catch:{ Throwable -> 0x00f3 }
            com.tencent.bugly.proguard.x.d(r0, r1)     // Catch:{ Throwable -> 0x00f3 }
            java.util.concurrent.atomic.AtomicInteger r0 = r10.a
            r0.set(r7)
            goto L_0x0017
        L_0x0063:
            r0 = move-exception
            monitor-exit(r10)
            throw r0
        L_0x0066:
            r10.b = r4     // Catch:{ Throwable -> 0x00f3 }
            java.util.concurrent.atomic.AtomicInteger r0 = r10.a     // Catch:{ Throwable -> 0x00f3 }
            r1 = 1
            r0.set(r1)     // Catch:{ Throwable -> 0x00f3 }
            int r0 = com.tencent.bugly.crashreport.crash.c.f     // Catch:{ Throwable -> 0x008b }
            r1 = 0
            java.util.Map r6 = com.tencent.bugly.proguard.z.a((int) r0, (boolean) r1)     // Catch:{ Throwable -> 0x008b }
            if (r6 == 0) goto L_0x007d
            int r0 = r6.size()     // Catch:{ Throwable -> 0x00f3 }
            if (r0 > 0) goto L_0x009e
        L_0x007d:
            java.lang.String r0 = "can't get all thread skip this anr"
            r1 = 0
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ Throwable -> 0x00f3 }
            com.tencent.bugly.proguard.x.d(r0, r1)     // Catch:{ Throwable -> 0x00f3 }
            java.util.concurrent.atomic.AtomicInteger r0 = r10.a
            r0.set(r7)
            goto L_0x0017
        L_0x008b:
            r0 = move-exception
            com.tencent.bugly.proguard.x.a(r0)     // Catch:{ Throwable -> 0x00f3 }
            java.lang.String r0 = "get all thread stack fail!"
            r1 = 0
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ Throwable -> 0x00f3 }
            com.tencent.bugly.proguard.x.e(r0, r1)     // Catch:{ Throwable -> 0x00f3 }
            java.util.concurrent.atomic.AtomicInteger r0 = r10.a
            r0.set(r7)
            goto L_0x0017
        L_0x009e:
            android.content.Context r0 = r10.c     // Catch:{ Throwable -> 0x00f3 }
            r2 = 10000(0x2710, double:4.9407E-320)
            android.app.ActivityManager$ProcessErrorStateInfo r0 = r10.a(r0, r2)     // Catch:{ Throwable -> 0x00f3 }
            r10.m = r0     // Catch:{ Throwable -> 0x00f3 }
            android.app.ActivityManager$ProcessErrorStateInfo r0 = r10.m     // Catch:{ Throwable -> 0x00f3 }
            if (r0 != 0) goto L_0x00bb
            java.lang.String r0 = "proc state is unvisiable!"
            r1 = 0
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ Throwable -> 0x00f3 }
            com.tencent.bugly.proguard.x.c(r0, r1)     // Catch:{ Throwable -> 0x00f3 }
            java.util.concurrent.atomic.AtomicInteger r0 = r10.a
            r0.set(r7)
            goto L_0x0017
        L_0x00bb:
            android.app.ActivityManager$ProcessErrorStateInfo r0 = r10.m     // Catch:{ Throwable -> 0x00f3 }
            int r0 = r0.pid     // Catch:{ Throwable -> 0x00f3 }
            int r1 = android.os.Process.myPid()     // Catch:{ Throwable -> 0x00f3 }
            if (r0 == r1) goto L_0x00db
            java.lang.String r0 = "not mind proc!"
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ Throwable -> 0x00f3 }
            r2 = 0
            android.app.ActivityManager$ProcessErrorStateInfo r3 = r10.m     // Catch:{ Throwable -> 0x00f3 }
            java.lang.String r3 = r3.processName     // Catch:{ Throwable -> 0x00f3 }
            r1[r2] = r3     // Catch:{ Throwable -> 0x00f3 }
            com.tencent.bugly.proguard.x.c(r0, r1)     // Catch:{ Throwable -> 0x00f3 }
            java.util.concurrent.atomic.AtomicInteger r0 = r10.a
            r0.set(r7)
            goto L_0x0017
        L_0x00db:
            java.lang.String r0 = "found visiable anr , start to process!"
            r1 = 0
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ Throwable -> 0x00f3 }
            com.tencent.bugly.proguard.x.a(r0, r1)     // Catch:{ Throwable -> 0x00f3 }
            android.content.Context r1 = r10.c     // Catch:{ Throwable -> 0x00f3 }
            android.app.ActivityManager$ProcessErrorStateInfo r3 = r10.m     // Catch:{ Throwable -> 0x00f3 }
            r0 = r10
            r2 = r11
            r0.a(r1, r2, r3, r4, r6)     // Catch:{ Throwable -> 0x00f3 }
            java.util.concurrent.atomic.AtomicInteger r0 = r10.a
            r0.set(r7)
            goto L_0x0017
        L_0x00f3:
            r0 = move-exception
            boolean r1 = com.tencent.bugly.proguard.x.a(r0)     // Catch:{ all -> 0x0117 }
            if (r1 != 0) goto L_0x00fd
            r0.printStackTrace()     // Catch:{ all -> 0x0117 }
        L_0x00fd:
            java.lang.String r1 = "handle anr error %s"
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0117 }
            r3 = 0
            java.lang.Class r0 = r0.getClass()     // Catch:{ all -> 0x0117 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0117 }
            r2[r3] = r0     // Catch:{ all -> 0x0117 }
            com.tencent.bugly.proguard.x.e(r1, r2)     // Catch:{ all -> 0x0117 }
            java.util.concurrent.atomic.AtomicInteger r0 = r10.a
            r0.set(r7)
            goto L_0x0017
        L_0x0117:
            r0 = move-exception
            java.util.concurrent.atomic.AtomicInteger r1 = r10.a
            r1.set(r7)
            throw r0
        L_0x011e:
            r4 = r0
            goto L_0x0030
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tencent.bugly.crashreport.crash.anr.b.a(java.lang.String):void");
    }

    private synchronized void f() {
        if (h()) {
            x.d("start when started!", new Object[0]);
        } else {
            this.i = new FileObserver("/data/anr/", 8) {
                public final void onEvent(int i, String str) {
                    if (str != null) {
                        String str2 = "/data/anr/" + str;
                        if (!str2.contains("trace")) {
                            x.d("not anr file %s", str2);
                            return;
                        }
                        b.this.a(str2);
                    }
                }
            };
            try {
                this.i.startWatching();
                x.a("start anr monitor!", new Object[0]);
                this.e.a(new Runnable() {
                    public final void run() {
                        b.this.b();
                    }
                });
            } catch (Throwable th) {
                this.i = null;
                x.d("start anr monitor failed!", new Object[0]);
                if (!x.a(th)) {
                    th.printStackTrace();
                }
            }
        }
    }

    private synchronized void g() {
        if (!h()) {
            x.d("close when closed!", new Object[0]);
        } else {
            try {
                this.i.stopWatching();
                this.i = null;
                x.d("close anr monitor!", new Object[0]);
            } catch (Throwable th) {
                x.d("stop anr monitor failed!", new Object[0]);
                if (!x.a(th)) {
                    th.printStackTrace();
                }
            }
        }
    }

    private synchronized boolean h() {
        return this.i != null;
    }

    private synchronized void b(boolean z) {
        if (z) {
            f();
        } else {
            g();
        }
    }

    private synchronized boolean i() {
        return this.j;
    }

    private synchronized void c(boolean z) {
        if (this.j != z) {
            x.a("user change anr %b", Boolean.valueOf(z));
            this.j = z;
        }
    }

    public final void a(boolean z) {
        c(z);
        boolean i2 = i();
        com.tencent.bugly.crashreport.common.strategy.a a2 = com.tencent.bugly.crashreport.common.strategy.a.a();
        if (a2 != null) {
            i2 = i2 && a2.c().g;
        }
        if (i2 != h()) {
            x.a("anr changed to %b", Boolean.valueOf(i2));
            b(i2);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void b() {
        /*
            r13 = this;
            r0 = 0
            long r2 = com.tencent.bugly.proguard.z.b()
            long r4 = com.tencent.bugly.crashreport.crash.c.g
            long r2 = r2 - r4
            java.io.File r1 = new java.io.File
            java.lang.String r4 = r13.g
            r1.<init>(r4)
            boolean r4 = r1.exists()
            if (r4 == 0) goto L_0x0024
            boolean r4 = r1.isDirectory()
            if (r4 == 0) goto L_0x0024
            java.io.File[] r4 = r1.listFiles()     // Catch:{ Throwable -> 0x0087 }
            if (r4 == 0) goto L_0x0024
            int r1 = r4.length     // Catch:{ Throwable -> 0x0087 }
            if (r1 != 0) goto L_0x0025
        L_0x0024:
            return
        L_0x0025:
            java.lang.String r5 = "bugly_trace_"
            java.lang.String r6 = ".txt"
            int r7 = r5.length()     // Catch:{ Throwable -> 0x0087 }
            int r8 = r4.length     // Catch:{ Throwable -> 0x0087 }
            r1 = r0
        L_0x002f:
            if (r1 >= r8) goto L_0x0071
            r9 = r4[r1]     // Catch:{ Throwable -> 0x0087 }
            java.lang.String r10 = r9.getName()     // Catch:{ Throwable -> 0x0087 }
            boolean r11 = r10.startsWith(r5)     // Catch:{ Throwable -> 0x0087 }
            if (r11 == 0) goto L_0x004f
            int r11 = r10.indexOf(r6)     // Catch:{ Throwable -> 0x0052 }
            if (r11 <= 0) goto L_0x0068
            java.lang.String r11 = r10.substring(r7, r11)     // Catch:{ Throwable -> 0x0052 }
            long r10 = java.lang.Long.parseLong(r11)     // Catch:{ Throwable -> 0x0052 }
            int r10 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1))
            if (r10 < 0) goto L_0x0068
        L_0x004f:
            int r1 = r1 + 1
            goto L_0x002f
        L_0x0052:
            r11 = move-exception
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0087 }
            java.lang.String r12 = "Trace file that has invalid format: "
            r11.<init>(r12)     // Catch:{ Throwable -> 0x0087 }
            java.lang.StringBuilder r10 = r11.append(r10)     // Catch:{ Throwable -> 0x0087 }
            java.lang.String r10 = r10.toString()     // Catch:{ Throwable -> 0x0087 }
            r11 = 0
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ Throwable -> 0x0087 }
            com.tencent.bugly.proguard.x.c(r10, r11)     // Catch:{ Throwable -> 0x0087 }
        L_0x0068:
            boolean r9 = r9.delete()     // Catch:{ Throwable -> 0x0087 }
            if (r9 == 0) goto L_0x004f
            int r0 = r0 + 1
            goto L_0x004f
        L_0x0071:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0087 }
            java.lang.String r2 = "Number of overdue trace files that has deleted: "
            r1.<init>(r2)     // Catch:{ Throwable -> 0x0087 }
            java.lang.StringBuilder r0 = r1.append(r0)     // Catch:{ Throwable -> 0x0087 }
            java.lang.String r0 = r0.toString()     // Catch:{ Throwable -> 0x0087 }
            r1 = 0
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ Throwable -> 0x0087 }
            com.tencent.bugly.proguard.x.c(r0, r1)     // Catch:{ Throwable -> 0x0087 }
            goto L_0x0024
        L_0x0087:
            r0 = move-exception
            com.tencent.bugly.proguard.x.a(r0)
            goto L_0x0024
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tencent.bugly.crashreport.crash.anr.b.b():void");
    }

    public final synchronized void c() {
        x.d("customer decides whether to open or close.", new Object[0]);
    }

    public final boolean a(aa aaVar) {
        boolean z;
        Map hashMap = new HashMap();
        if (aaVar.e().equals(Looper.getMainLooper())) {
            z = true;
        } else {
            z = false;
        }
        if (z) {
            this.m = a(this.c, 10000);
            if (this.m == null) {
                x.c("anr handler onThreadBlock proc state is unvisiable!", new Object[0]);
                return false;
            } else if (this.m.pid != Process.myPid()) {
                x.c("onThreadBlock not mind proc!", this.m.processName);
                return false;
            } else {
                try {
                    hashMap = z.a(200000, false);
                } catch (Throwable th) {
                    x.b(th);
                    hashMap.put("main", th.getMessage());
                }
                x.c("onThreadBlock found visiable anr , start to process!", new Object[0]);
                a(this.c, "", this.m, System.currentTimeMillis(), hashMap);
            }
        } else {
            x.c("anr handler onThreadBlock only care main thread ,current thread is: %s", aaVar.d());
        }
        return true;
    }

    public final boolean d() {
        if (this.k != null && this.k.isAlive()) {
            return false;
        }
        this.k = new ab();
        ab abVar = this.k;
        StringBuilder sb = new StringBuilder("Bugly-ThreadMonitor");
        int i2 = this.l;
        this.l = i2 + 1;
        abVar.setName(sb.append(i2).toString());
        this.k.a();
        this.k.a(this);
        return this.k.d();
    }

    public final boolean e() {
        if (this.k == null) {
            return false;
        }
        boolean c2 = this.k.c();
        this.k.b();
        this.k.b(this);
        this.k = null;
        return c2;
    }
}
