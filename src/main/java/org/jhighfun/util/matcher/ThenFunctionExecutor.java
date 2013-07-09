package org.jhighfun.util.matcher;


import org.jhighfun.util.Function;

public interface ThenFunctionExecutor<IN, OUT> {

    public WhenFunctionExecutor<IN, OUT> then(OUT outputObject);

    public WhenFunctionExecutor<IN, OUT> then(Function<IN, OUT> function);

}