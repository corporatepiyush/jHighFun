package org.jhighfun.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Cascading interface which enables writing execution of independent tasks concurrently
 * where each task has List data structure as input.
 *
 * @author Piyush Katariya
 */

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

        final ConcurrentLinkedQueue<List<T>> queue = new ConcurrentLinkedQueue<List<T>>();
        for (int i = 0; i < taskList.size(); i++)
            queue.offer(copyCollection());

        FunctionUtil.each(taskList, new RecordProcessor<Task<List<T>>>() {
            public void process(Task<List<T>> task) {
                task.execute(queue.poll());
            }
        }, FunctionUtil.parallel(taskList.size()));
        return collectionFunctionChain;
    }

    private List<T> copyCollection() {
        List<T> list = new LinkedList<T>();
        for (T t : collectionFunctionChain.extract()) {
            list.add(t);
        }
        return list;
    }
}
