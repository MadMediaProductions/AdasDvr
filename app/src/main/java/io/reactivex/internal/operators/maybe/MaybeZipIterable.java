package io.reactivex.internal.operators.maybe;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.functions.Function;

public final class MaybeZipIterable<T, R> extends Maybe<R> {
    final Iterable<? extends MaybeSource<? extends T>> sources;
    final Function<? super Object[], ? extends R> zipper;

    public MaybeZipIterable(Iterable<? extends MaybeSource<? extends T>> sources2, Function<? super Object[], ? extends R> zipper2) {
        this.sources = sources2;
        this.zipper = zipper2;
    }

    /* JADX WARNING: type inference failed for: r9v13, types: [java.lang.Object[]] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void subscribeActual(io.reactivex.MaybeObserver<? super R> r13) {
        /*
            r12 = this;
            r9 = 8
            io.reactivex.MaybeSource[] r1 = new io.reactivex.MaybeSource[r9]
            r5 = 0
            java.lang.Iterable<? extends io.reactivex.MaybeSource<? extends T>> r9 = r12.sources     // Catch:{ Throwable -> 0x003a }
            java.util.Iterator r4 = r9.iterator()     // Catch:{ Throwable -> 0x003a }
            r6 = r5
        L_0x000c:
            boolean r9 = r4.hasNext()     // Catch:{ Throwable -> 0x0081 }
            if (r9 == 0) goto L_0x0042
            java.lang.Object r8 = r4.next()     // Catch:{ Throwable -> 0x0081 }
            io.reactivex.MaybeSource r8 = (io.reactivex.MaybeSource) r8     // Catch:{ Throwable -> 0x0081 }
            if (r8 != 0) goto L_0x0026
            java.lang.NullPointerException r9 = new java.lang.NullPointerException     // Catch:{ Throwable -> 0x0081 }
            java.lang.String r10 = "One of the sources is null"
            r9.<init>(r10)     // Catch:{ Throwable -> 0x0081 }
            io.reactivex.internal.disposables.EmptyDisposable.error((java.lang.Throwable) r9, (io.reactivex.MaybeObserver<?>) r13)     // Catch:{ Throwable -> 0x0081 }
            r5 = r6
        L_0x0025:
            return
        L_0x0026:
            int r9 = r1.length     // Catch:{ Throwable -> 0x0081 }
            if (r6 != r9) goto L_0x0034
            int r9 = r6 >> 2
            int r9 = r9 + r6
            java.lang.Object[] r9 = java.util.Arrays.copyOf(r1, r9)     // Catch:{ Throwable -> 0x0081 }
            r0 = r9
            io.reactivex.MaybeSource[] r0 = (io.reactivex.MaybeSource[]) r0     // Catch:{ Throwable -> 0x0081 }
            r1 = r0
        L_0x0034:
            int r5 = r6 + 1
            r1[r6] = r8     // Catch:{ Throwable -> 0x003a }
            r6 = r5
            goto L_0x000c
        L_0x003a:
            r2 = move-exception
        L_0x003b:
            io.reactivex.exceptions.Exceptions.throwIfFatal(r2)
            io.reactivex.internal.disposables.EmptyDisposable.error((java.lang.Throwable) r2, (io.reactivex.MaybeObserver<?>) r13)
            goto L_0x0025
        L_0x0042:
            if (r6 != 0) goto L_0x0049
            io.reactivex.internal.disposables.EmptyDisposable.complete((io.reactivex.MaybeObserver<?>) r13)
            r5 = r6
            goto L_0x0025
        L_0x0049:
            r9 = 1
            if (r6 != r9) goto L_0x005e
            r9 = 0
            r9 = r1[r9]
            io.reactivex.internal.operators.maybe.MaybeMap$MapMaybeObserver r10 = new io.reactivex.internal.operators.maybe.MaybeMap$MapMaybeObserver
            io.reactivex.internal.operators.maybe.MaybeZipIterable$SingletonArrayFunc r11 = new io.reactivex.internal.operators.maybe.MaybeZipIterable$SingletonArrayFunc
            r11.<init>()
            r10.<init>(r13, r11)
            r9.subscribe(r10)
            r5 = r6
            goto L_0x0025
        L_0x005e:
            io.reactivex.internal.operators.maybe.MaybeZipArray$ZipCoordinator r7 = new io.reactivex.internal.operators.maybe.MaybeZipArray$ZipCoordinator
            io.reactivex.functions.Function<? super java.lang.Object[], ? extends R> r9 = r12.zipper
            r7.<init>(r13, r6, r9)
            r13.onSubscribe(r7)
            r3 = 0
        L_0x0069:
            if (r3 >= r6) goto L_0x007f
            boolean r9 = r7.isDisposed()
            if (r9 == 0) goto L_0x0073
            r5 = r6
            goto L_0x0025
        L_0x0073:
            r9 = r1[r3]
            io.reactivex.internal.operators.maybe.MaybeZipArray$ZipMaybeObserver<T>[] r10 = r7.observers
            r10 = r10[r3]
            r9.subscribe(r10)
            int r3 = r3 + 1
            goto L_0x0069
        L_0x007f:
            r5 = r6
            goto L_0x0025
        L_0x0081:
            r2 = move-exception
            r5 = r6
            goto L_0x003b
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.maybe.MaybeZipIterable.subscribeActual(io.reactivex.MaybeObserver):void");
    }

    final class SingletonArrayFunc implements Function<T, R> {
        SingletonArrayFunc() {
        }

        public R apply(T t) throws Exception {
            return MaybeZipIterable.this.zipper.apply(new Object[]{t});
        }
    }
}
