package org.jhighfun.util;


import java.util.LinkedList;
import java.util.List;

public final class Matcher<I, O> implements WhenOrOtherwise<I, O>, ThenOrOtherwise<I, O>, When<I, O> {

    private final I input;
    private final List<Function<I, Boolean>> conditions = new LinkedList<Function<I, Boolean>>();
    private final List<Function<I, O>> tasks = new LinkedList<Function<I, O>>();

    public Matcher(I input) {
        this.input = input;
    }

    public ThenOrOtherwise<I, O> when(Function<I, Boolean> condition) {
        conditions.add(condition);
        return this;
    }

    public ThenOrOtherwise<I, O> when(final I input) {
        conditions.add(new Function<I, Boolean>() {
            public Boolean apply(I arg) {
                return input.equals(arg);
            }
        });
        return this;
    }

    public WhenOrOtherwise<I, O> then(Function<I, O> task) {
        this.tasks.add(task);
        return this;
    }

    public WhenOrOtherwise<I, O> then(final O output) {
        this.tasks.add(new Function<I, O>() {
            public O apply(I arg) {
                return output;
            }
        });
        return this;
    }

    public ObjectFunctionChain<O> otherwise(Function<I, O> defaultTask) {
        Function<I, O> task = taskToExecute();
        return new ObjectFunctionChain<O>(task != null ? task.apply(input) : defaultTask.apply(input));
    }

    public ObjectFunctionChain<O> otherwise(O output) {
        Function<I, O> task = taskToExecute();
        return new ObjectFunctionChain<O>(task != null ? task.apply(input) : output);
    }

    private Function<I, O> taskToExecute() {
        int i = 0;
        for (Function<I, Boolean> condition : conditions) {
            if (condition.apply(input))
                break;
            i++;
        }
        return tasks.get(i);
    }

}
