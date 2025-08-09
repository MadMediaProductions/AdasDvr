package retrofit2.adapter.rxjava2;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

final class CallEnqueueObservable<T> extends Observable<Response<T>> {
    private final Call<T> originalCall;

    CallEnqueueObservable(Call<T> originalCall2) {
        this.originalCall = originalCall2;
    }

    /* access modifiers changed from: protected */
    public void subscribeActual(Observer<? super Response<T>> observer) {
        Call<T> call = this.originalCall.clone();
        CallCallback<T> callback = new CallCallback<>(call, observer);
        observer.onSubscribe(callback);
        call.enqueue(callback);
    }

    private static final class CallCallback<T> implements Disposable, Callback<T> {
        private final Call<?> call;
        private final Observer<? super Response<T>> observer;
        boolean terminated = false;

        CallCallback(Call<?> call2, Observer<? super Response<T>> observer2) {
            this.call = call2;
            this.observer = observer2;
        }

        public void onResponse(Call<T> call2, Response<T> response) {
            if (!call2.isCanceled()) {
                try {
                    this.observer.onNext(response);
                    if (!call2.isCanceled()) {
                        this.terminated = true;
                        this.observer.onComplete();
                    }
                } catch (Throwable inner) {
                    Exceptions.throwIfFatal(inner);
                    RxJavaPlugins.onError(new CompositeException(t, inner));
                }
            }
        }

        public void onFailure(Call<T> call2, Throwable t) {
            if (!call2.isCanceled()) {
                try {
                    this.observer.onError(t);
                } catch (Throwable inner) {
                    Exceptions.throwIfFatal(inner);
                    RxJavaPlugins.onError(new CompositeException(t, inner));
                }
            }
        }

        public void dispose() {
            this.call.cancel();
        }

        public boolean isDisposed() {
            return this.call.isCanceled();
        }
    }
}
