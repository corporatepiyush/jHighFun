package org.jhighfun.util.memoize;


import java.util.Map;
import java.util.WeakHashMap;

import org.jhighfun.util.Accumulator;
import org.jhighfun.util.Pair;

public class BasicAccumulatorMemoizer<ACCUM, EL> extends Accumulator<ACCUM, EL> {

    private final Map<Pair<ACCUM, EL>, ACCUM> memo = new WeakHashMap<Pair<ACCUM, EL>, ACCUM>();
    private final Accumulator<ACCUM, EL> accumulator;

    public BasicAccumulatorMemoizer(Accumulator<ACCUM, EL> accumulator) {
        this.accumulator = accumulator;
    }

    @Override
    public ACCUM accumulate(final ACCUM accum, final EL el) {
        final Pair<ACCUM, EL> pairCacheObject = new Pair<ACCUM, EL>(accum, el);
        final ACCUM memoizedOutput = memo.get(pairCacheObject);
        try {
            if (memoizedOutput != null) {
                return memoizedOutput;
            } else {
                ACCUM output = accumulator.accumulate(accum, el);
                memo.put(pairCacheObject, output);
                return output;
            }
        } catch (Throwable e) {
            return accumulator.accumulate(accum, el);
        }
    }
}
