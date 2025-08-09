package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.HalfSerializer;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableWithLatestFromMany<T, R> extends AbstractFlowableWithUpstream<T, R> {
    final Function<? super Object[], R> combiner;
    @Nullable
    final Publisher<?>[] otherArray;
    @Nullable
    final Iterable<? extends Publisher<?>> otherIterable;

    public FlowableWithLatestFromMany(@NonNull Flowable<T> source, @NonNull Publisher<?>[] otherArray2, Function<? super Object[], R> combiner2) {
        super(source);
        this.otherArray = otherArray2;
        this.otherIterable = null;
        this.combiner = combiner2;
    }

    public FlowableWithLatestFromMany(@NonNull Flowable<T> source, @NonNull Iterable<? extends Publisher<?>> otherIterable2, @NonNull Function<? super Object[], R> combiner2) {
        super(source);
        this.otherArray = null;
        this.otherIterable = otherIterable2;
        this.combiner = combiner2;
    }

    /* JADX WARNING: type inference failed for: r8v9, types: [java.lang.Object[]] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void subscribeActual(Subscriber<? super R> r12) {
        /*
            r11 = this;
            org.reactivestreams.Publisher<?>[] r5 = r11.otherArray
            r3 = 0
            if (r5 != 0) goto L_0x004b
            r8 = 8
            org.reactivestreams.Publisher[] r5 = new org.reactivestreams.Publisher[r8]
            java.lang.Iterable<? extends org.reactivestreams.Publisher<?>> r8 = r11.otherIterable     // Catch:{ Throwable -> 0x0043 }
            java.util.Iterator r2 = r8.iterator()     // Catch:{ Throwable -> 0x0043 }
            r4 = r3
        L_0x0010:
            boolean r8 = r2.hasNext()     // Catch:{ Throwable -> 0x0060 }
            if (r8 == 0) goto L_0x0030
            java.lang.Object r6 = r2.next()     // Catch:{ Throwable -> 0x0060 }
            org.reactivestreams.Publisher r6 = (org.reactivestreams.Publisher) r6     // Catch:{ Throwable -> 0x0060 }
            int r8 = r5.length     // Catch:{ Throwable -> 0x0060 }
            if (r4 != r8) goto L_0x002a
            int r8 = r4 >> 1
            int r8 = r8 + r4
            java.lang.Object[] r8 = java.util.Arrays.copyOf(r5, r8)     // Catch:{ Throwable -> 0x0060 }
            r0 = r8
            org.reactivestreams.Publisher[] r0 = (org.reactivestreams.Publisher[]) r0     // Catch:{ Throwable -> 0x0060 }
            r5 = r0
        L_0x002a:
            int r3 = r4 + 1
            r5[r4] = r6     // Catch:{ Throwable -> 0x0043 }
            r4 = r3
            goto L_0x0010
        L_0x0030:
            r3 = r4
        L_0x0031:
            if (r3 != 0) goto L_0x004d
            io.reactivex.internal.operators.flowable.FlowableMap r8 = new io.reactivex.internal.operators.flowable.FlowableMap
            io.reactivex.Flowable r9 = r11.source
            io.reactivex.internal.operators.flowable.FlowableWithLatestFromMany$SingletonArrayFunc r10 = new io.reactivex.internal.operators.flowable.FlowableWithLatestFromMany$SingletonArrayFunc
            r10.<init>()
            r8.<init>(r9, r10)
            r8.subscribeActual(r12)
        L_0x0042:
            return
        L_0x0043:
            r1 = move-exception
        L_0x0044:
            io.reactivex.exceptions.Exceptions.throwIfFatal(r1)
            io.reactivex.internal.subscriptions.EmptySubscription.error(r1, r12)
            goto L_0x0042
        L_0x004b:
            int r3 = r5.length
            goto L_0x0031
        L_0x004d:
            io.reactivex.internal.operators.flowable.FlowableWithLatestFromMany$WithLatestFromSubscriber r7 = new io.reactivex.internal.operators.flowable.FlowableWithLatestFromMany$WithLatestFromSubscriber
            io.reactivex.functions.Function<? super java.lang.Object[], R> r8 = r11.combiner
            r7.<init>(r12, r8, r3)
            r12.onSubscribe(r7)
            r7.subscribe(r5, r3)
            io.reactivex.Flowable r8 = r11.source
            r8.subscribe(r7)
            goto L_0x0042
        L_0x0060:
            r1 = move-exception
            r3 = r4
            goto L_0x0044
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableWithLatestFromMany.subscribeActual(org.reactivestreams.Subscriber):void");
    }

    static final class WithLatestFromSubscriber<T, R> extends AtomicInteger implements FlowableSubscriber<T>, Subscription {
        private static final long serialVersionUID = 1577321883966341961L;
        final Subscriber<? super R> actual;
        final Function<? super Object[], R> combiner;
        volatile boolean done;
        final AtomicThrowable error;
        final AtomicLong requested;
        final AtomicReference<Subscription> s;
        final WithLatestInnerSubscriber[] subscribers;
        final AtomicReferenceArray<Object> values;

        WithLatestFromSubscriber(Subscriber<? super R> actual2, Function<? super Object[], R> combiner2, int n) {
            this.actual = actual2;
            this.combiner = combiner2;
            WithLatestInnerSubscriber[] s2 = new WithLatestInnerSubscriber[n];
            for (int i = 0; i < n; i++) {
                s2[i] = new WithLatestInnerSubscriber(this, i);
            }
            this.subscribers = s2;
            this.values = new AtomicReferenceArray<>(n);
            this.s = new AtomicReference<>();
            this.requested = new AtomicLong();
            this.error = new AtomicThrowable();
        }

        /* access modifiers changed from: package-private */
        public void subscribe(Publisher<?>[] others, int n) {
            WithLatestInnerSubscriber[] subscribers2 = this.subscribers;
            AtomicReference<Subscription> s2 = this.s;
            for (int i = 0; i < n && !SubscriptionHelper.isCancelled(s2.get()) && !this.done; i++) {
                others[i].subscribe(subscribers2[i]);
            }
        }

        public void onSubscribe(Subscription s2) {
            SubscriptionHelper.deferredSetOnce(this.s, this.requested, s2);
        }

        public void onNext(T t) {
            if (!this.done) {
                AtomicReferenceArray<Object> ara = this.values;
                int n = ara.length();
                Object[] objects = new Object[(n + 1)];
                objects[0] = t;
                int i = 0;
                while (i < n) {
                    Object o = ara.get(i);
                    if (o == null) {
                        this.s.get().request(1);
                        return;
                    } else {
                        objects[i + 1] = o;
                        i++;
                    }
                }
                try {
                    HalfSerializer.onNext(this.actual, ObjectHelper.requireNonNull(this.combiner.apply(objects), "combiner returned a null value"), (AtomicInteger) this, this.error);
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    cancel();
                    onError(ex);
                }
            }
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.done = true;
            cancelAllBut(-1);
            HalfSerializer.onError((Subscriber<?>) this.actual, t, (AtomicInteger) this, this.error);
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                cancelAllBut(-1);
                HalfSerializer.onComplete((Subscriber<?>) this.actual, (AtomicInteger) this, this.error);
            }
        }

        public void request(long n) {
            SubscriptionHelper.deferredRequest(this.s, this.requested, n);
        }

        public void cancel() {
            SubscriptionHelper.cancel(this.s);
            for (Disposable s2 : this.subscribers) {
                s2.dispose();
            }
        }

        /* access modifiers changed from: package-private */
        public void innerNext(int index, Object o) {
            this.values.set(index, o);
        }

        /* access modifiers changed from: package-private */
        public void innerError(int index, Throwable t) {
            this.done = true;
            SubscriptionHelper.cancel(this.s);
            cancelAllBut(index);
            HalfSerializer.onError((Subscriber<?>) this.actual, t, (AtomicInteger) this, this.error);
        }

        /* access modifiers changed from: package-private */
        public void innerComplete(int index, boolean nonEmpty) {
            if (!nonEmpty) {
                this.done = true;
                cancelAllBut(index);
                HalfSerializer.onComplete((Subscriber<?>) this.actual, (AtomicInteger) this, this.error);
            }
        }

        /* access modifiers changed from: package-private */
        public void cancelAllBut(int index) {
            WithLatestInnerSubscriber[] subscribers2 = this.subscribers;
            for (int i = 0; i < subscribers2.length; i++) {
                if (i != index) {
                    subscribers2[i].dispose();
                }
            }
        }
    }

    static final class WithLatestInnerSubscriber extends AtomicReference<Subscription> implements FlowableSubscriber<Object>, Disposable {
        private static final long serialVersionUID = 3256684027868224024L;
        boolean hasValue;
        final int index;
        final WithLatestFromSubscriber<?, ?> parent;

        WithLatestInnerSubscriber(WithLatestFromSubscriber<?, ?> parent2, int index2) {
            this.parent = parent2;
            this.index = index2;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.setOnce(this, s)) {
                s.request(Long.MAX_VALUE);
            }
        }

        public void onNext(Object t) {
            if (!this.hasValue) {
                this.hasValue = true;
            }
            this.parent.innerNext(this.index, t);
        }

        public void onError(Throwable t) {
            this.parent.innerError(this.index, t);
        }

        public void onComplete() {
            this.parent.innerComplete(this.index, this.hasValue);
        }

        public boolean isDisposed() {
            return SubscriptionHelper.isCancelled((Subscription) get());
        }

        public void dispose() {
            SubscriptionHelper.cancel(this);
        }
    }

    final class SingletonArrayFunc implements Function<T, R> {
        SingletonArrayFunc() {
        }

        public R apply(T t) throws Exception {
            return FlowableWithLatestFromMany.this.combiner.apply(new Object[]{t});
        }
    }
}
