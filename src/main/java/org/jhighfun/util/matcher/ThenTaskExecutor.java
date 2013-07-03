package org.jhighfun.util.matcher;

import org.jhighfun.util.Task;

public interface ThenTaskExecutor<IN> {

    public WhenCheckerWithTask<IN> then(Task<IN> task);

}
