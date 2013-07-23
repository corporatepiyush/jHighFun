package org.jhighfun.util.matcher;


import org.jhighfun.util.Function;

public interface WhenFunctionExecutor<IN, OUT> {

    public ThenFunctionExecutor<IN, OUT> ifEquals(final IN matchingInput);

    public ThenFunctionExecutor<IN, OUT> ifEquals(Function<IN, Boolean> condition);

    public OUT otherwiseReturn(OUT outputObject);

    public OUT otherwiseReturn(Function<IN, OUT> function);
}
