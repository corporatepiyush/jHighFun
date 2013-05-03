package org.jhighfun.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class CollectionForkAndJoin<T> {

    private CollectionFunctionChain<T> collectionFunctionChain;
    private final List<Task<List<T>>> taskList = new ArrayList<Task<List<T>>>();

    public CollectionForkAndJoin(CollectionFunctionChain<T> collectionFunctionChain) {
        this.collectionFunctionChain = collectionFunctionChain;
    }

    public CollectionForkAndJoin execute(Task<List<T>> task) {
        taskList.add(task);
        return this;
    }

    public CollectionFunctionChain<T> join() {
        FunctionUtil.each(taskList, new RecordProcessor<Task<List<T>>>() {
            public void process(Task<List<T>> task) {
                task.execute(new LinkedList<T>(collectionFunctionChain.extract()));
            }
        }, FunctionUtil.parallel(taskList.size()));
        return collectionFunctionChain;
    }
}
