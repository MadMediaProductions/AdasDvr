package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.subscribers.FullArbiterSubscriber;
import io.reactivex.internal.subscriptions.FullArbiter;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subscribers.SerializedSubscriber;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableTimeoutTimed<T> extends AbstractFlowableWithUpstream<T, T> {
    static final Disposable NEW_TIMER = new EmptyDispose();
    final Publisher<? extends T> other;
    final Scheduler scheduler;
    final long timeout;
    final TimeUnit unit;

    public FlowableTimeoutTimed(Flowable<T> source, long timeout2, TimeUnit unit2, Scheduler scheduler2, Publisher<? extends T> other2) {
        super(source);
        this.timeout = timeout2;
        this.unit = unit2;
        this.scheduler = scheduler2;
        this.other = other2;
    }

    /* access modifiers changed from: protected */
    public void subscribeActual(Subscriber<? super T> s) {
        if (this.other == null) {
            this.source.subscribe(new TimeoutTimedSubscriber(new SerializedSubscriber(s), this.timeout, this.unit, this.scheduler.createWorker()));
            return;
        }
        this.source.subscribe(new TimeoutTimedOtherSubscriber(s, this.timeout, this.unit, this.scheduler.createWorker(), this.other));
    }

    static final class TimeoutTimedOtherSubscriber<T> implements FlowableSubscriber<T>, Disposable {
        final Subscriber<? super T> actual;
        final FullArbiter<T> arbiter;
        volatile boolean done;
        volatile long index;
        final Publisher<? extends T> other;
        Subscription s;
        final long timeout;
        final AtomicReference<Disposable> timer = new AtomicReference<>();
        final TimeUnit unit;
        final Scheduler.Worker worker;

        TimeoutTimedOtherSubscriber(Subscriber<? super T> actual2, long timeout2, TimeUnit unit2, Scheduler.Worker worker2, Publisher<? extends T> other2) {
            this.actual = actual2;
            this.timeout = timeout2;
            this.unit = unit2;
            this.worker = worker2;
            this.other = other2;
            this.arbiter = new FullArbiter<>(actual2, this, 8);
        }

        public void onSubscribe(Subscription s2) {
            if (SubscriptionHelper.validate(this.s, s2)) {
                this.s = s2;
                if (this.arbiter.setSubscription(s2)) {
                    this.actual.onSubscribe(this.arbiter);
                    scheduleTimeout(0);
                }
            }
        }

        public void onNext(T t) {
            if (!this.done) {
                long idx = this.index + 1;
                this.index = idx;
                if (this.arbiter.onNext(t, this.s)) {
                    scheduleTimeout(idx);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void scheduleTimeout(long idx) {
            Disposable d = this.timer.get();
            if (d != null) {
                d.dispose();
            }
            if (this.timer.compareAndSet(d, FlowableTimeoutTimed.NEW_TIMER)) {
                DisposableHelper.replace(this.timer, this.worker.schedule(new TimeoutTask(idx), this.timeout, this.unit));
            }
        }

        /* access modifiers changed from: package-private */
        public void subscribeNext() {
            this.other.subscribe(new FullArbiterSubscriber(this.arbiter));
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.done = true;
            this.arbiter.onError(t, this.s);
            this.worker.dispose();
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                this.arbiter.onComplete(this.s);
                this.worker.dispose();
            }
        }

        public void dispose() {
            this.s.cancel();
            this.worker.dispose();
        }

        public boolean isDisposed() {
            return this.worker.isDisposed();
        }

        final class TimeoutTask implements Runnable {
            private final long idx;

            TimeoutTask(long idx2) {
                this.idx = idx2;
            }

            public void run() {
                if (this.idx == TimeoutTimedOtherSubscriber.this.index) {
                    TimeoutTimedOtherSubscriber.this.done = true;
                    TimeoutTimedOtherSubscriber.this.s.cancel();
                    DisposableHelper.dispose(TimeoutTimedOtherSubscriber.this.timer);
                    TimeoutTimedOtherSubscriber.this.subscribeNext();
                    TimeoutTimedOtherSubscriber.this.worker.dispose();
                }
            }
        }
    }

    static final class TimeoutTimedSubscriber<T> implements FlowableSubscriber<T>, Disposable, Subscription {
        final Subscriber<? super T> actual;
        volatile boolean done;
        volatile long index;
        Subscription s;
        final long timeout;
        final AtomicReference<Disposable> timer = new AtomicReference<>();
        final TimeUnit unit;
        final Scheduler.Worker worker;

        TimeoutTimedSubscriber(Subscriber<? super T> actual2, long timeout2, TimeUnit unit2, Scheduler.Worker worker2) {
            this.actual = actual2;
            this.timeout = timeout2;
            this.unit = unit2;
            this.worker = worker2;
        }

        public void onSubscribe(Subscription s2) {
            if (SubscriptionHelper.validate(this.s, s2)) {
                this.s = s2;
                this.actual.onSubscribe(this);
                scheduleTimeout(0);
            }
        }

        public void onNext(T t) {
            if (!this.done) {
                long idx = this.index + 1;
                this.index = idx;
                this.actual.onNext(t);
                scheduleTimeout(idx);
            }
        }

        /* access modifiers changed from: package-private */
        public void scheduleTimeout(long idx) {
            Disposable d = this.timer.get();
            if (d != null) {
                d.dispose();
            }
            if (this.timer.compareAndSet(d, FlowableTimeoutTimed.NEW_TIMER)) {
                DisposableHelper.replace(this.timer, this.worker.schedule(new TimeoutTask(idx), this.timeout, this.unit));
            }
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.done = true;
            this.actual.onError(t);
            this.worker.dispose();
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                this.actual.onComplete();
                this.worker.dispose();
            }
        }

        public void dispose() {
            this.s.cancel();
            this.worker.dispose();
        }

        public boolean isDisposed() {
            return this.worker.isDisposed();
        }

        public void request(long n) {
            this.s.request(n);
        }

        public void cancel() {
            dispose();
        }

        final class TimeoutTask implements Runnable {
            private final long idx;

            TimeoutTask(long idx2) {
                this.idx = idx2;
            }

            public void run() {
                if (this.idx == TimeoutTimedSubscriber.this.index) {
                    TimeoutTimedSubscriber.this.done = true;
                    TimeoutTimedSubscriber.this.dispose();
                    TimeoutTimedSubscriber.this.actual.onError(new TimeoutException());
                }
            }
        }
    }

    static final class EmptyDispose implements Disposable {
        EmptyDispose() {
        }

        public void dispose() {
        }

        public boolean isDisposed() {
            return true;
        }
    }
}
