package org.jhighfun.util.memoize;


import org.jhighfun.util.Function;

import java.util.Map;
import java.util.WeakHashMap;

public class BasicFunctionMemoizer<I, O> extends Function<I, O> {

    private final Map<I, O> memo = new WeakHashMap<I, O>();
    private final Function<I, O> function;

    public BasicFunctionMemoizer(Function<I, O> function) {
        this.function = function;
    }

    @Override
    public O apply(final I input) {
        final O memoizedOutput = this.memo.get(input);
        try {
            if (memoizedOutput != null && memoizedOutput != null) {
                return memoizedOutput;
            } else {
                O cacheObject = this.function.apply(input);
                this.memo.put(input, cacheObject);
                return cacheObject;
            }
        } catch (Throwable e) {
            return this.function.apply(input);
        }
    }
}
