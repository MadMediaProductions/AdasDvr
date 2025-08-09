package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.SimplePlainQueue;
import io.reactivex.internal.queue.MpscLinkedQueue;
import io.reactivex.internal.subscribers.QueueDrainSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.QueueDrainHelper;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subscribers.DisposableSubscriber;
import io.reactivex.subscribers.SerializedSubscriber;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableBufferBoundary<T, U extends Collection<? super T>, Open, Close> extends AbstractFlowableWithUpstream<T, U> {
    final Function<? super Open, ? extends Publisher<? extends Close>> bufferClose;
    final Publisher<? extends Open> bufferOpen;
    final Callable<U> bufferSupplier;

    public FlowableBufferBoundary(Flowable<T> source, Publisher<? extends Open> bufferOpen2, Function<? super Open, ? extends Publisher<? extends Close>> bufferClose2, Callable<U> bufferSupplier2) {
        super(source);
        this.bufferOpen = bufferOpen2;
        this.bufferClose = bufferClose2;
        this.bufferSupplier = bufferSupplier2;
    }

    /* access modifiers changed from: protected */
    public void subscribeActual(Subscriber<? super U> s) {
        this.source.subscribe(new BufferBoundarySubscriber(new SerializedSubscriber(s), this.bufferOpen, this.bufferClose, this.bufferSupplier));
    }

    static final class BufferBoundarySubscriber<T, U extends Collection<? super T>, Open, Close> extends QueueDrainSubscriber<T, U, U> implements Subscription, Disposable {
        final Function<? super Open, ? extends Publisher<? extends Close>> bufferClose;
        final Publisher<? extends Open> bufferOpen;
        final Callable<U> bufferSupplier;
        final List<U> buffers;
        final CompositeDisposable resources;
        Subscription s;
        final AtomicInteger windows = new AtomicInteger();

        BufferBoundarySubscriber(Subscriber<? super U> actual, Publisher<? extends Open> bufferOpen2, Function<? super Open, ? extends Publisher<? extends Close>> bufferClose2, Callable<U> bufferSupplier2) {
            super(actual, new MpscLinkedQueue());
            this.bufferOpen = bufferOpen2;
            this.bufferClose = bufferClose2;
            this.bufferSupplier = bufferSupplier2;
            this.buffers = new LinkedList();
            this.resources = new CompositeDisposable();
        }

        public void onSubscribe(Subscription s2) {
            if (SubscriptionHelper.validate(this.s, s2)) {
                this.s = s2;
                BufferOpenSubscriber<T, U, Open, Close> bos = new BufferOpenSubscriber<>(this);
                this.resources.add(bos);
                this.actual.onSubscribe(this);
                this.windows.lazySet(1);
                this.bufferOpen.subscribe(bos);
                s2.request(Long.MAX_VALUE);
            }
        }

        public void onNext(T t) {
            synchronized (this) {
                for (U b : this.buffers) {
                    b.add(t);
                }
            }
        }

        public void onError(Throwable t) {
            cancel();
            this.cancelled = true;
            synchronized (this) {
                this.buffers.clear();
            }
            this.actual.onError(t);
        }

        public void onComplete() {
            if (this.windows.decrementAndGet() == 0) {
                complete();
            }
        }

        /* access modifiers changed from: package-private */
        public void complete() {
            List<U> list;
            synchronized (this) {
                list = new ArrayList<>(this.buffers);
                this.buffers.clear();
            }
            SimplePlainQueue<U> q = this.queue;
            for (U u : list) {
                q.offer(u);
            }
            this.done = true;
            if (enter()) {
                QueueDrainHelper.drainMaxLoop(q, this.actual, false, this, this);
            }
        }

        public void request(long n) {
            requested(n);
        }

        public void dispose() {
            this.resources.dispose();
        }

        public boolean isDisposed() {
            return this.resources.isDisposed();
        }

        public void cancel() {
            if (!this.cancelled) {
                this.cancelled = true;
                dispose();
            }
        }

        public boolean accept(Subscriber<? super U> a, U v) {
            a.onNext(v);
            return true;
        }

        /* access modifiers changed from: package-private */
        public void open(Open window) {
            if (!this.cancelled) {
                try {
                    U b = (Collection) ObjectHelper.requireNonNull(this.bufferSupplier.call(), "The buffer supplied is null");
                    try {
                        Publisher<? extends Close> p = (Publisher) ObjectHelper.requireNonNull(this.bufferClose.apply(window), "The buffer closing publisher is null");
                        if (!this.cancelled) {
                            synchronized (this) {
                                if (!this.cancelled) {
                                    this.buffers.add(b);
                                    BufferCloseSubscriber<T, U, Open, Close> bcs = new BufferCloseSubscriber<>(b, this);
                                    this.resources.add(bcs);
                                    this.windows.getAndIncrement();
                                    p.subscribe(bcs);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        Exceptions.throwIfFatal(e);
                        onError(e);
                    }
                } catch (Throwable e2) {
                    Exceptions.throwIfFatal(e2);
                    onError(e2);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void openFinished(Disposable d) {
            if (this.resources.remove(d) && this.windows.decrementAndGet() == 0) {
                complete();
            }
        }

        /* access modifiers changed from: package-private */
        public void close(U b, Disposable d) {
            boolean e;
            synchronized (this) {
                e = this.buffers.remove(b);
            }
            if (e) {
                fastPathOrderedEmitMax(b, false, this);
            }
            if (this.resources.remove(d) && this.windows.decrementAndGet() == 0) {
                complete();
            }
        }
    }

    static final class BufferOpenSubscriber<T, U extends Collection<? super T>, Open, Close> extends DisposableSubscriber<Open> {
        boolean done;
        final BufferBoundarySubscriber<T, U, Open, Close> parent;

        BufferOpenSubscriber(BufferBoundarySubscriber<T, U, Open, Close> parent2) {
            this.parent = parent2;
        }

        public void onNext(Open t) {
            if (!this.done) {
                this.parent.open(t);
            }
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.done = true;
            this.parent.onError(t);
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                this.parent.openFinished(this);
            }
        }
    }

    static final class BufferCloseSubscriber<T, U extends Collection<? super T>, Open, Close> extends DisposableSubscriber<Close> {
        boolean done;
        final BufferBoundarySubscriber<T, U, Open, Close> parent;
        final U value;

        BufferCloseSubscriber(U value2, BufferBoundarySubscriber<T, U, Open, Close> parent2) {
            this.parent = parent2;
            this.value = value2;
        }

        public void onNext(Close close) {
            onComplete();
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
            } else {
                this.parent.onError(t);
            }
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                this.parent.close(this.value, this);
            }
        }
    }
}
