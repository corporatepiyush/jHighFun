package org.highfun;

public interface Accumulator<ACCUM, EL> {

    public ACCUM accumulate(ACCUM accumulator, EL element);

}
