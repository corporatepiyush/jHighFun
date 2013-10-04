package org.jhighfun.util;

public class FunctionComposer<I, O, CO> extends Function<I, CO> {

    private final Function<I, O> function1;
    private final Function<O, CO> function2;

    public FunctionComposer(Function<I, O> function1, Function<O, CO> function2) {
        this.function1 = function1;
        this.function2 = function2;
    }

    @Override
    public CO apply(I input) {
        return this.function2.apply(this.function1.apply(input));
    }
}
