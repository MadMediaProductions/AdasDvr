package com.trello.rxlifecycle2;

import io.reactivex.Completable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import java.util.concurrent.CancellationException;

final class Functions {
    static final Function<Object, Completable> CANCEL_COMPLETABLE = new Function<Object, Completable>() {
        public Completable apply(Object ignore) throws Exception {
            return Completable.error((Throwable) new CancellationException());
        }
    };
    static final Function<Throwable, Boolean> RESUME_FUNCTION = new Function<Throwable, Boolean>() {
        public Boolean apply(Throwable throwable) throws Exception {
            if (throwable instanceof OutsideLifecycleException) {
                return true;
            }
            Exceptions.propagate(throwable);
            return false;
        }
    };
    static final Predicate<Boolean> SHOULD_COMPLETE = new Predicate<Boolean>() {
        public boolean test(Boolean shouldComplete) throws Exception {
            return shouldComplete.booleanValue();
        }
    };

    private Functions() {
        throw new AssertionError("No instances!");
    }
}
