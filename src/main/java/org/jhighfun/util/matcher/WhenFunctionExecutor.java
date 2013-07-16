package org.jhighfun.util.matcher;


import org.jhighfun.util.Function;

public interface WhenFunctionExecutor<IN, OUT> {

    public ThenFunctionExecutor<IN, OUT> whenMatchesWith(final IN matchingInput);

    public ThenFunctionExecutor<IN, OUT> whenMatchesWith(Function<IN, Boolean> condition);

    public OUT otherwiseReturn(OUT outputObject);

    public OUT otherwiseReturn(Function<IN, OUT> function);
}
