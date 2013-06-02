package org.jhighfun.util;

/**
 *  A function is a relation between a set of inputs and a set of permissible outputs
 *  with the property that each input is related to exactly one output
 *
 *  @author Piyush Katariya
 *
 **/

public interface Function<I, O> {

    public O apply(I arg);

}
