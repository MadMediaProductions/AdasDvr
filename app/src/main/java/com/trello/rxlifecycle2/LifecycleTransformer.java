package com.trello.rxlifecycle2;

import com.trello.rxlifecycle2.internal.Preconditions;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import javax.annotation.ParametersAreNonnullByDefault;
import org.reactivestreams.Publisher;

@ParametersAreNonnullByDefault
public final class LifecycleTransformer<T> implements ObservableTransformer<T, T>, FlowableTransformer<T, T>, SingleTransformer<T, T>, MaybeTransformer<T, T>, CompletableTransformer {
    final Observable<?> observable;

    LifecycleTransformer(Observable<?> observable2) {
        Preconditions.checkNotNull(observable2, "observable == null");
        this.observable = observable2;
    }

    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.takeUntil((ObservableSource<U>) this.observable);
    }

    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream.takeUntil((Publisher<U>) this.observable.toFlowable(BackpressureStrategy.LATEST));
    }

    public SingleSource<T> apply(Single<T> upstream) {
        return upstream.takeUntil((SingleSource<? extends E>) this.observable.firstOrError());
    }

    public MaybeSource<T> apply(Maybe<T> upstream) {
        return upstream.takeUntil((MaybeSource<U>) this.observable.firstElement());
    }

    public CompletableSource apply(Completable upstream) {
        return Completable.ambArray(upstream, this.observable.flatMapCompletable(Functions.CANCEL_COMPLETABLE));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return this.observable.equals(((LifecycleTransformer) o).observable);
    }

    public int hashCode() {
        return this.observable.hashCode();
    }

    public String toString() {
        return "LifecycleTransformer{observable=" + this.observable + '}';
    }
}
