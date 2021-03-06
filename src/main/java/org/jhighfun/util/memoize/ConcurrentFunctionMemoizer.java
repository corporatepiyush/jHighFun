package org.jhighfun.util.memoize;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.jhighfun.internal.CacheObject;
import org.jhighfun.util.Function;

public class ConcurrentFunctionMemoizer<I, O> extends Function<I, O> {

    private final Map<CacheObject<I>, Future<CacheObject<O>>> memo = new ConcurrentHashMap<CacheObject<I>, Future<CacheObject<O>>>(100, 0.6f, 32);
    private final Function<I, O> function;

    public ConcurrentFunctionMemoizer(Function<I, O> function) {
        this.function = function;
    }

    @Override
    public O apply(final I input) {
        final CacheObject<I> inputCacheObject = new CacheObject<I>(input);
        final Future<CacheObject<O>> memoizedOutput = this.memo.get(inputCacheObject);
        try {
            if (memoizedOutput != null && memoizedOutput.get() != null) {
                return memoizedOutput.get().get();
            } else {
                FutureTask<CacheObject<O>> futureTask = new FutureTask<CacheObject<O>>(new Callable<CacheObject<O>>() {
                    public CacheObject<O> call() throws Exception {
                        return new CacheObject<O>(function.apply(input));
                    }
                });

                this.memo.put(inputCacheObject, futureTask);
                futureTask.run();
                return futureTask.get().get();
            }
        } catch (Throwable e) {
            return this.function.apply(input);
        }
    }
}
