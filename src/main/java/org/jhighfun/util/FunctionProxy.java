package org.jhighfun.util;

public final class FunctionProxy<I, O> extends Function<I, O> {

    private final Function<I, O> function;
    private final FunctionInvocationHandler<I, O> handler;

    public FunctionProxy(Function<I, O> function, FunctionInvocationHandler<I, O> handler) {
        this.function = function;
        this.handler = handler;
    }

    @Override
    public O apply(I input) {
        return this.handler.invoke(this.function, input);
    }
}
