package org.jhighfun.util;

public class FunctionComposer<I, T, O> extends Function<I, O> {

    private final Function<I, T> function1;
    private final Function<T, O> function2;

    public FunctionComposer(Function<I, T> function1, Function<T, O> function2) {
        this.function1 = function1;
        this.function2 = function2;
    }

    @Override
    public O apply(I input) {
        return this.function2.apply(this.function1.apply(input));
    }
}
