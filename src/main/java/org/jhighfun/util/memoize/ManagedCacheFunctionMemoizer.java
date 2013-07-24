package org.jhighfun.util.memoize;


import org.jhighfun.util.Function;
import org.jhighfun.util.ManagedCache;

public class ManagedCacheFunctionMemoizer<I, O> extends Function<I, O> {

    private final Function<I, O> function;
    private final ManagedCache managedCache;

    public ManagedCacheFunctionMemoizer(Function<I, O> function, ManagedCache managedCache) {
        this.function = function;
        this.managedCache = managedCache;
    }

    @Override
    public O apply(I input) {

        final O memoizedOutput = (O) managedCache.get(input);
        try {
            if (memoizedOutput != null) {
                return memoizedOutput;
            } else {
                O output = function.apply(input);
                managedCache.put(input, output);
                return output;
            }
        } catch (Throwable e) {
            return function.apply(input);
        }
    }
}
