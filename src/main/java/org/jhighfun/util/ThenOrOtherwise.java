package org.jhighfun.util;


public interface ThenOrOtherwise<I, O> {

    WhenOrOtherwise<I, O> then(Function<I, O> condition);

    WhenOrOtherwise<I, O> then(O output);

    ObjectFunctionChain<O> otherwise(Function<I, O> task);

    ObjectFunctionChain<O> otherwise(O output);
}
