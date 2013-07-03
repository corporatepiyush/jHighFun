package org.jhighfun.util.matcher;

import org.jhighfun.util.Function;
import org.jhighfun.util.Task;

public final class ThenExecutor<IN> {

    private final Function<IN, Boolean> condition;
    private final IN inputObject;

    public ThenExecutor(IN inputObject, Function<IN, Boolean> condition) {
        this.inputObject = inputObject;
        this.condition = condition;
    }

    public <OUT> WhenThenCheckerWithOutput<IN, OUT> then(final OUT outputObject) {
        return new WhenThenCheckerWithOutput<IN, OUT>(inputObject, condition, new Function<IN, OUT>() {
            public OUT apply(IN arg) {
                return outputObject;
            }
        });
    }

    public <OUT> WhenThenCheckerWithOutput<IN, OUT> then(Function<IN, OUT> outputFunction) {
        return new WhenThenCheckerWithOutput<IN, OUT>(inputObject, condition, outputFunction);
    }

    public WhenCheckerWithTask<IN> then(Task<IN> task) {
        return new WhenThenCheckerWithTask<IN>(inputObject, condition, task);
    }
}
