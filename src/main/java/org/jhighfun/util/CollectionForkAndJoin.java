package org.jhighfun.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CollectionForkAndJoin<T> {

    private CollectionFunctionChain<T> collectionFunctionChain;
    private final List<Task<Collection<T>>> taskList = new ArrayList<Task<Collection<T>>>();

    public CollectionForkAndJoin(CollectionFunctionChain<T> collectionFunctionChain) {
        this.collectionFunctionChain = collectionFunctionChain;
    }

    public CollectionForkAndJoin execute(Task<Collection<T>> task) {
        taskList.add(task);
        return this;
    }

    public CollectionFunctionChain<T> join() {
        FunctionUtil.each(taskList, new RecordProcessor<Task<Collection<T>>>() {
            public void process(Task<Collection<T>> task) {
                task.execute(new LinkedList<T>(collectionFunctionChain.extract()));
            }
        }, FunctionUtil.parallel(taskList.size()));
        return collectionFunctionChain;
    }
}
