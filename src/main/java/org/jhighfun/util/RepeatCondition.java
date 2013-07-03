package org.jhighfun.util;


public class RepeatCondition<T> {

    private final RepeatableTask<T> task;

    public RepeatCondition(RepeatableTask<T> task) {
        this.task = task;
    }

    public void times(Integer times) {
        for (int loop = 0; loop < times; loop++) {
            task.execute();
        }
    }

    public void times(Integer times, WorkDivisionStrategy strategy) {
        FunctionUtil.each(CollectionUtil.IntRange(1, times), new RecordProcessor<Integer>() {
            public void process(Integer i) {
                task.execute();
            }
        }, strategy);
    }

    public void asLongAs(Function<T, Boolean> function) {
        while (function.apply(task.execute())) ;
    }

    public void untilWhen(Function<T, Boolean> function) {
        while (!function.apply(task.execute())) ;
    }

}
