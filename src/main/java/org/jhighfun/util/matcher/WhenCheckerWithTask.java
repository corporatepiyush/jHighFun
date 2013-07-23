package org.jhighfun.util.matcher;

import org.jhighfun.util.Function;
import org.jhighfun.util.Task;

public interface WhenCheckerWithTask<IN> {
    ThenTaskExecutor<IN> ifEquals(IN matchingInput);

    ThenTaskExecutor<IN> ifEquals(Function<IN, Boolean> condition);

    public void otherwiseExecute(Task<IN> task);
}
