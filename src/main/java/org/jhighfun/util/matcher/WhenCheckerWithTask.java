package org.jhighfun.util.matcher;

import org.jhighfun.util.Function;

public interface WhenCheckerWithTask<IN> {
    ThenTaskExecutor<IN> when(IN matchingInput);

    ThenTaskExecutor<IN> when(Function<IN, Boolean> condition);
}
