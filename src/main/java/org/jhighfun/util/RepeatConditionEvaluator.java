package org.jhighfun.util;


public class RepeatConditionEvaluator<T> {

    private final RepeatableTask<T> task;

    public RepeatConditionEvaluator(RepeatableTask<T> task) {
        this.task = task;
    }

    public void times(int times) {
        for (int loop = 0; loop < times; loop++) {
            this.task.execute();
        }
    }

    public void times(int times, WorkDivisionStrategy strategy) {
        FunctionUtil.each(CollectionUtil.IntRange(1, times), new RecordProcessor<Integer>() {
            public void process(Integer i) {
                task.execute();
            }
        }, strategy);
    }

    public void asLongAs(Function<T, Boolean> function) {
        while (function.apply(this.task.execute())) ;
    }

    public void untilWhen(Function<T, Boolean> function) {
        while (!function.apply(this.task.execute())) ;
    }

}
