package org.jhighfun.util;

import org.jhighfun.util.memoize.BasicFunctionMemoizer;
import org.jhighfun.util.memoize.ConfigurableFunctionMemoizer;
import org.jhighfun.util.memoize.ManagedCacheFunctionMemoizer;
import org.jhighfun.util.memoize.MemoizeConfig;

/**
 * A function is a relation between a set of inputs and a set of permissible outputs
 * with the property that each input is related to exactly one output
 *
 * @author Piyush Katariya
 */

public abstract class Function<I, O> {

    public abstract O apply(I arg);

    public Function<I, O> memoize() {
        return new BasicFunctionMemoizer<I, O>(this);
    }

    public Function<I, O> memoize(ManagedCache managedCache) {
        return new ManagedCacheFunctionMemoizer<I, O>(this, managedCache);
    }

    public Function<I, O> memoize(MemoizeConfig memoizeConfig) {
        return new ConfigurableFunctionMemoizer<I, O>(this, memoizeConfig);
    }

    public Function<I, O> proxy(FunctionInvocationHandler<I, O> handler) {
        return new FunctionProxy<I, O>(this, handler);
    }

    public <T> Function<I, T> compose(Function<O, T> function) {
        return new FunctionComposer<I, O, T>(this, function);
    }
}
