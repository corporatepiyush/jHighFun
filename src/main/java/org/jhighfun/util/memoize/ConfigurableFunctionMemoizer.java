package org.jhighfun.util.memoize;

import org.jhighfun.internal.CacheObject;
import org.jhighfun.util.Accumulator;
import org.jhighfun.util.Function;
import org.jhighfun.util.FunctionUtil;
import org.jhighfun.util.Tuple3;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConfigurableFunctionMemoizer<I, O> extends Function<I, O> {

    private final Function<I, O> function;
    private final Map<CacheObject<I>, Future<CacheObject<Tuple3<Long, Long, O>>>> memo = new ConcurrentHashMap<CacheObject<I>, Future<CacheObject<Tuple3<Long, Long, O>>>>(100, 0.6f, 32);
    private final AtomicBoolean isLRUDeletionInProgress = new AtomicBoolean(false);
    private final MemoizeConfig config;
    private Long maxPersistenceTime;

    public ConfigurableFunctionMemoizer(Function<I, O> function, MemoizeConfig config) {
        this.function = function;
        this.config = config;
        this.maxPersistenceTime = config.getTimeUnit().toMillis(config.getTimeValue());
    }


    @Override
    public O apply(final I input) {

        final CacheObject<I> inputCacheObject = new CacheObject<I>(input);
        final long currentTimeinMillis = System.currentTimeMillis();

        try {
            final Future<CacheObject<Tuple3<Long, Long, O>>> memoizedFutureOutput = this.memo.get(inputCacheObject);
            final CacheObject<Tuple3<Long, Long, O>> memoizedOutput;
            if (memoizedFutureOutput != null
                    && (memoizedOutput = memoizedFutureOutput.get()) != null
                    && (currentTimeinMillis - memoizedOutput.get()._1) <= this.maxPersistenceTime) {

                memoizedOutput.get()._2 = currentTimeinMillis;

                if (this.memo.size() > this.config.getSize() && !this.isLRUDeletionInProgress.get()) {
                    new Thread(new Runnable() {
                        public void run() {
                            isLRUDeletionInProgress.set(true);
                            while (memo.size() > config.getSize()) {
                                removeLeastRecentlyUsedRecord();
                            }
                            isLRUDeletionInProgress.set(false);
                        }
                    }).start();
                }

                return memoizedOutput.get()._3;
            } else {

                FutureTask<CacheObject<Tuple3<Long, Long, O>>> futureTask = new FutureTask<CacheObject<Tuple3<Long, Long, O>>>(new Callable<CacheObject<Tuple3<Long, Long, O>>>() {
                    public CacheObject<Tuple3<Long, Long, O>> call() throws Exception {
                        return new CacheObject<Tuple3<Long, Long, O>>(new Tuple3<Long, Long, O>(currentTimeinMillis, currentTimeinMillis, function.apply(input)));
                    }
                });

                this.memo.put(inputCacheObject, futureTask);
                futureTask.run();
                return futureTask.get().get()._3;
            }
        } catch (Throwable e) {
            return this.function.apply(input);
        }
    }

    private void removeLeastRecentlyUsedRecord() {
        try {
            Map.Entry<CacheObject<I>, Future<CacheObject<Tuple3<Long, Long, O>>>> toBeRemoved = FunctionUtil.reduce(memo.entrySet(), new Accumulator<Map.Entry<CacheObject<I>, Future<CacheObject<Tuple3<Long, Long, O>>>>, Map.Entry<CacheObject<I>, Future<CacheObject<Tuple3<Long, Long, O>>>>>() {
                public Map.Entry<CacheObject<I>, Future<CacheObject<Tuple3<Long, Long, O>>>> accumulate(Map.Entry<CacheObject<I>, Future<CacheObject<Tuple3<Long, Long, O>>>> former, Map.Entry<CacheObject<I>, Future<CacheObject<Tuple3<Long, Long, O>>>> latter) {

                    try {
                        final Long lastAccessTimeFormer = former.getValue().get().get()._2;
                        final Long lastAccessTimeLatter = latter.getValue().get().get()._2;

                        if (lastAccessTimeFormer <= lastAccessTimeLatter) {
                            return former;
                        } else {
                            return latter;
                        }

                    } catch (Throwable e) {
                        return former;
                    }
                }
            });

            memo.remove(toBeRemoved.getKey());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
