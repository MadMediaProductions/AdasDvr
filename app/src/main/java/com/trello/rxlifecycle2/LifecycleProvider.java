package com.trello.rxlifecycle2;

import io.reactivex.Observable;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public interface LifecycleProvider<E> {
    @CheckReturnValue
    @Nonnull
    <T> LifecycleTransformer<T> bindToLifecycle();

    @CheckReturnValue
    @Nonnull
    <T> LifecycleTransformer<T> bindUntilEvent(@Nonnull E e);

    @CheckReturnValue
    @Nonnull
    Observable<E> lifecycle();
}
