package org.jhighfun.util;

import org.jhighfun.util.memoize.BasicAccumulatorMemoizer;
import org.jhighfun.util.memoize.ConcurrentAccumulatorMemoizer;

/**
 * A SAM which accepts carry over as first and current element of Iterable structure
 * as second input element and returns carry over
 *
 * @author Piyush Katariya
 */

public abstract class Accumulator<ACCUM, EL> {

    public abstract ACCUM accumulate(ACCUM accumulator, EL element);

    public final Accumulator<ACCUM, EL> memoize() {
        return new BasicAccumulatorMemoizer<ACCUM, EL>(this);
    }

    public final Accumulator<ACCUM, EL> memoize(boolean concurrent) {
        return concurrent ? new ConcurrentAccumulatorMemoizer<ACCUM, EL>(this) : new BasicAccumulatorMemoizer<ACCUM, EL>(this);
    }

}
