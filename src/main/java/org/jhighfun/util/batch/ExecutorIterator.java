package org.jhighfun.util.batch;


import org.jhighfun.util.Task;

import java.util.Iterator;

public class ExecutorIterator<T> extends AbstractIterator<T> {

    private final Iterator<T> executorIterator;
    private final Task<T> task;

    public ExecutorIterator(Iterator<T> iterator, Task<T> task) {
        this.executorIterator = iterator;
        this.task = task;
    }

    public boolean hasNext() {
        return executorIterator.hasNext();
    }

    public T next() {
        T next = executorIterator.next();
        task.execute(next);
        return next;
    }
}
