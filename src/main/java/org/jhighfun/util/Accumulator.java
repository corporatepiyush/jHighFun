package org.jhighfun.util;

/**
 * A monoid function which accepts carry over as first and current element of Iterable structure
 * as second input element and returns carry over
 *
 * @author Piyush Katariya
 */

public interface Accumulator<ACCUM, EL> {

    public ACCUM accumulate(ACCUM accumulator, EL element);

}
