package org.jhighfun.util.matcher;


import org.jhighfun.util.Function;

public final class WhenChecker<T> {

    private final T input;

    public WhenChecker(T input) {
        this.input = input;
    }

    public ThenExecutor<T> when(final T matchingInput) {
        return new ThenExecutor<T>(input, new Function<T, Boolean>() {
            public Boolean apply(T arg) {
                return matchingInput != null && matchingInput.equals(input);
            }
        });
    }

    public ThenExecutor<T> when(Function<T, Boolean> condition) {
        return new ThenExecutor<T>(input, condition);
    }

}
