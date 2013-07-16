package org.jhighfun.util.matcher;


import org.jhighfun.util.Function;

public interface ThenFunctionExecutor<IN, OUT> {

    public WhenFunctionExecutor<IN, OUT> thenReturn(OUT outputObject);

    public WhenFunctionExecutor<IN, OUT> thenReturn(Function<IN, OUT> function);

}
