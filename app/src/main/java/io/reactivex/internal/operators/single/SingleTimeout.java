package io.reactivex.internal.operators.single;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SingleTimeout<T> extends Single<T> {
    final SingleSource<? extends T> other;
    final Scheduler scheduler;
    final SingleSource<T> source;
    final long timeout;
    final TimeUnit unit;

    public SingleTimeout(SingleSource<T> source2, long timeout2, TimeUnit unit2, Scheduler scheduler2, SingleSource<? extends T> other2) {
        this.source = source2;
        this.timeout = timeout2;
        this.unit = unit2;
        this.scheduler = scheduler2;
        this.other = other2;
    }

    /* access modifiers changed from: protected */
    public void subscribeActual(SingleObserver<? super T> s) {
        CompositeDisposable set = new CompositeDisposable();
        s.onSubscribe(set);
        AtomicBoolean once = new AtomicBoolean();
        set.add(this.scheduler.scheduleDirect(new TimeoutDispose(once, set, s), this.timeout, this.unit));
        this.source.subscribe(new TimeoutObserver(once, set, s));
    }

    final class TimeoutDispose implements Runnable {
        private final AtomicBoolean once;
        final SingleObserver<? super T> s;
        final CompositeDisposable set;

        TimeoutDispose(AtomicBoolean once2, CompositeDisposable set2, SingleObserver<? super T> s2) {
            this.once = once2;
            this.set = set2;
            this.s = s2;
        }

        public void run() {
            if (!this.once.compareAndSet(false, true)) {
                return;
            }
            if (SingleTimeout.this.other != null) {
                this.set.clear();
                SingleTimeout.this.other.subscribe(new TimeoutObserver());
                return;
            }
            this.set.dispose();
            this.s.onError(new TimeoutException());
        }

        final class TimeoutObserver implements SingleObserver<T> {
            TimeoutObserver() {
            }

            public void onError(Throwable e) {
                TimeoutDispose.this.set.dispose();
                TimeoutDispose.this.s.onError(e);
            }

            public void onSubscribe(Disposable d) {
                TimeoutDispose.this.set.add(d);
            }

            public void onSuccess(T value) {
                TimeoutDispose.this.set.dispose();
                TimeoutDispose.this.s.onSuccess(value);
            }
        }
    }

    final class TimeoutObserver implements SingleObserver<T> {
        private final AtomicBoolean once;
        private final SingleObserver<? super T> s;
        private final CompositeDisposable set;

        TimeoutObserver(AtomicBoolean once2, CompositeDisposable set2, SingleObserver<? super T> s2) {
            this.once = once2;
            this.set = set2;
            this.s = s2;
        }

        public void onError(Throwable e) {
            if (this.once.compareAndSet(false, true)) {
                this.set.dispose();
                this.s.onError(e);
            }
        }

        public void onSubscribe(Disposable d) {
            this.set.add(d);
        }

        public void onSuccess(T value) {
            if (this.once.compareAndSet(false, true)) {
                this.set.dispose();
                this.s.onSuccess(value);
            }
        }
    }
}
