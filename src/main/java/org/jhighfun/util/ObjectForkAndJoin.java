package org.jhighfun.util;

import java.util.ArrayList;
import java.util.List;

/**
 *  Cascading interface which enables writing execution of independent tasks concurrently
 *  where each task has any object as input and compatible with ObjectFunctionChain
 *
 *  @author Piyush Katariya
 *
 **/

public final class ObjectForkAndJoin<T> {

    private ObjectFunctionChain<T> objectFunctionChain;
    private final List<Task<T>> taskList = new ArrayList<Task<T>>();

    public ObjectForkAndJoin(ObjectFunctionChain<T> objectFunctionChain) {
        this.objectFunctionChain = objectFunctionChain;
    }

    public ObjectForkAndJoin execute(Task<T> task) {
        taskList.add(task);
        return this;
    }

    public ObjectFunctionChain<T> join() {
        FunctionUtil.each(taskList, new RecordProcessor<Task<T>>() {
            public void process(Task<T> task) {
                task.execute(objectFunctionChain.extract());
            }
        }, FunctionUtil.parallel(taskList.size()));
        return objectFunctionChain;
    }
}
