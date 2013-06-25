package org.jhighfun.util;


public interface WhenOrOtherwise<I, O> {

    ThenOrOtherwise<I, O> when(Function<I, Boolean> condition);

    ThenOrOtherwise<I, O> when(I input);

    ObjectFunctionChain<O> otherwise(Function<I, O> task);

    ObjectFunctionChain<O> otherwise(O output);
}
