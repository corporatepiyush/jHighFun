package org.jhighfun.util.matcher;

import org.jhighfun.util.Function;
import org.jhighfun.util.Task;

public interface WhenCheckerWithTask<IN> {
    ThenTaskExecutor<IN> when(IN matchingInput);

    ThenTaskExecutor<IN> when(Function<IN, Boolean> condition);

    public void otherwise(Task<IN> task);
}
