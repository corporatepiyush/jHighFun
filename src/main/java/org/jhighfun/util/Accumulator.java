package org.jhighfun.util;

public interface Accumulator<ACCUM, EL> {

    public ACCUM accumulate(ACCUM accumulator, EL element);

}
