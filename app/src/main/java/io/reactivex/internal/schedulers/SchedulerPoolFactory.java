package io.reactivex.internal.schedulers;

import io.reactivex.plugins.RxJavaPlugins;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class SchedulerPoolFactory {
    static final Map<ScheduledThreadPoolExecutor, Object> POOLS = new ConcurrentHashMap();
    public static final boolean PURGE_ENABLED;
    static final String PURGE_ENABLED_KEY = "rx2.purge-enabled";
    public static final int PURGE_PERIOD_SECONDS;
    static final String PURGE_PERIOD_SECONDS_KEY = "rx2.purge-period-seconds";
    static final AtomicReference<ScheduledExecutorService> PURGE_THREAD = new AtomicReference<>();

    private SchedulerPoolFactory() {
        throw new IllegalStateException("No instances!");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x001c, code lost:
        r1 = java.lang.Boolean.getBoolean(PURGE_ENABLED_KEY);
     */
    static {
        /*
            java.util.concurrent.atomic.AtomicReference r3 = new java.util.concurrent.atomic.AtomicReference
            r3.<init>()
            PURGE_THREAD = r3
            java.util.concurrent.ConcurrentHashMap r3 = new java.util.concurrent.ConcurrentHashMap
            r3.<init>()
            POOLS = r3
            r1 = 1
            r2 = 1
            java.util.Properties r0 = java.lang.System.getProperties()
            java.lang.String r3 = "rx2.purge-enabled"
            boolean r3 = r0.containsKey(r3)
            if (r3 == 0) goto L_0x0036
            java.lang.String r3 = "rx2.purge-enabled"
            boolean r1 = java.lang.Boolean.getBoolean(r3)
            if (r1 == 0) goto L_0x0036
            java.lang.String r3 = "rx2.purge-period-seconds"
            boolean r3 = r0.containsKey(r3)
            if (r3 == 0) goto L_0x0036
            java.lang.String r3 = "rx2.purge-period-seconds"
            java.lang.Integer r3 = java.lang.Integer.getInteger(r3, r2)
            int r2 = r3.intValue()
        L_0x0036:
            PURGE_ENABLED = r1
            PURGE_PERIOD_SECONDS = r2
            start()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.schedulers.SchedulerPoolFactory.<clinit>():void");
    }

    public static void start() {
        while (true) {
            ScheduledExecutorService curr = PURGE_THREAD.get();
            if (curr == null || curr.isShutdown()) {
                ScheduledExecutorService next = Executors.newScheduledThreadPool(1, new RxThreadFactory("RxSchedulerPurge"));
                if (PURGE_THREAD.compareAndSet(curr, next)) {
                    next.scheduleAtFixedRate(new ScheduledTask(), (long) PURGE_PERIOD_SECONDS, (long) PURGE_PERIOD_SECONDS, TimeUnit.SECONDS);
                    return;
                }
                next.shutdownNow();
            } else {
                return;
            }
        }
    }

    public static void shutdown() {
        PURGE_THREAD.get().shutdownNow();
        POOLS.clear();
    }

    public static ScheduledExecutorService create(ThreadFactory factory) {
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1, factory);
        if (exec instanceof ScheduledThreadPoolExecutor) {
            POOLS.put((ScheduledThreadPoolExecutor) exec, exec);
        }
        return exec;
    }

    static final class ScheduledTask implements Runnable {
        ScheduledTask() {
        }

        public void run() {
            try {
                Iterator i$ = new ArrayList(SchedulerPoolFactory.POOLS.keySet()).iterator();
                while (i$.hasNext()) {
                    ScheduledThreadPoolExecutor e = (ScheduledThreadPoolExecutor) i$.next();
                    if (e.isShutdown()) {
                        SchedulerPoolFactory.POOLS.remove(e);
                    } else {
                        e.purge();
                    }
                }
            } catch (Throwable e2) {
                RxJavaPlugins.onError(e2);
            }
        }
    }
}
