package org.jhighfun.util.memoize;


import org.jhighfun.internal.CacheObject;
import org.jhighfun.util.Accumulator;
import org.jhighfun.util.Pair;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class BasicAccumulatorMemoizer<ACCUM, EL> extends Accumulator<ACCUM, EL> {

    private final Map<CacheObject<Pair<ACCUM, EL>>, Future<CacheObject<ACCUM>>> memo = new ConcurrentHashMap<CacheObject<Pair<ACCUM, EL>>, Future<CacheObject<ACCUM>>>(100, 0.6f, 32);
    private final Accumulator<ACCUM, EL> accumulator;

    public BasicAccumulatorMemoizer(Accumulator<ACCUM, EL> accumulator) {
        this.accumulator = accumulator;
    }

    @Override
    public ACCUM accumulate(final ACCUM accum, final EL el) {
        final CacheObject<Pair<ACCUM, EL>> pairCacheObject = new CacheObject<Pair<ACCUM, EL>>(new Pair<ACCUM, EL>(accum, el));
        final Future<CacheObject<ACCUM>> memoizedOutput = memo.get(pairCacheObject);
        try {
            if (memoizedOutput != null && memoizedOutput.get() != null) {
                return memoizedOutput.get().get();
            } else {

                FutureTask<CacheObject<ACCUM>> futureTask = new FutureTask<CacheObject<ACCUM>>(new Callable<CacheObject<ACCUM>>() {
                    public CacheObject<ACCUM> call() throws Exception {
                        return new CacheObject<ACCUM>(accumulator.accumulate(accum, el));
                    }
                });

                memo.put(pairCacheObject, futureTask);
                futureTask.run();
                return futureTask.get().get();
            }
        } catch (Throwable e) {
            return accumulator.accumulate(accum, el);
        }
    }
}
