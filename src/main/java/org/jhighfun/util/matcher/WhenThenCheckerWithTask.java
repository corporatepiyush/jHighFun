package org.jhighfun.util.matcher;

import org.jhighfun.util.Function;
import org.jhighfun.util.Task;

import java.util.LinkedList;
import java.util.List;

public final class WhenThenCheckerWithTask<IN> implements WhenCheckerWithTask<IN>, ThenTaskExecutor<IN> {

    private final IN inputObject;
    private final List<Function<IN, Boolean>> conditions = new LinkedList<Function<IN, Boolean>>();
    private final List<Task<IN>> tasks = new LinkedList<Task<IN>>();

    public WhenThenCheckerWithTask(IN inputObject, Function<IN, Boolean> condition, Task<IN> task) {
        this.inputObject = inputObject;
        this.conditions.add(condition);
        this.tasks.add(task);
    }

    public ThenTaskExecutor<IN> ifEquals(final IN matchingInput) {
        this.conditions.add(new Function<IN, Boolean>() {
            public Boolean apply(IN arg) {
                return matchingInput != null && matchingInput.equals(inputObject);
            }
        });
        return this;
    }

    public ThenTaskExecutor<IN> ifEquals(Function<IN, Boolean> function) {
        this.conditions.add(function);
        return this;
    }

    public WhenCheckerWithTask<IN> thenExecute(Task<IN> task) {
        this.tasks.add(task);
        return this;
    }

    public void otherwiseExecute(Task<IN> task) {
        int index = 0;
        for (Function<IN, Boolean> condition : this.conditions) {
            if (condition.apply(this.inputObject)) {
                break;
            }
            index++;
        }

        if (index == this.conditions.size()) {
            task.execute(this.inputObject);
        } else {
            this.tasks.get(0).execute(this.inputObject);
        }
    }


}
