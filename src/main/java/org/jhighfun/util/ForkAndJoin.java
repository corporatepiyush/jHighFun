package org.jhighfun.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Cascading interface which enables writing execution of independent tasks concurrently
 * where each task has any object as input.
 *
 * @author Piyush Katariya
 */

public final class ForkAndJoin<T> {

    private T object;
    private final List<Task<T>> taskList = new ArrayList<Task<T>>();

    public ForkAndJoin(T object) {
        this.object = object;
    }

    public ForkAndJoin execute(Task<T> task) {
        taskList.add(task);
        return this;
    }

    public void join() {
        FunctionUtil.each(taskList, new RecordProcessor<Task<T>>() {
            public void process(Task<T> task) {
                task.execute(object);
            }
        }, FunctionUtil.parallel(taskList.size()));
    }
}
