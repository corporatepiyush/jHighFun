package org.jhighfun.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ForkAndJoin<I> {

    private CollectionFunctionChain<I> collectionFunctionChain;
    private List<Task<Collection<I>>> taskList = new ArrayList<Task<Collection<I>>>();

    public ForkAndJoin(CollectionFunctionChain<I> collectionFunctionChain) {
        this.collectionFunctionChain = collectionFunctionChain;
    }

    public ForkAndJoin execute(Task<Collection<I>> task) {
        taskList.add(task);
        return this;
    }

    public CollectionFunctionChain<I> join() {
        FunctionUtil.each(taskList, new RecordProcessor<Task<Collection<I>>>() {
            public void process(Task<Collection<I>> task) {
                task.execute(new LinkedList<I>(collectionFunctionChain.extract()));
            }
        }, taskList.size());
        return collectionFunctionChain;
    }
}
