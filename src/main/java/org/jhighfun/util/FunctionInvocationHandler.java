package org.jhighfun.util;

public interface FunctionInvocationHandler<I, O> {

    public O invoke(Function<I, O> function, I input);

}
