package org.jhighfun.util.batch;


import org.jhighfun.util.Task;

import java.util.Iterator;

public class ExecutorIterator<T> extends AbstractIterator<T> {

    private final Iterator<T> iterator;
    private final Task<T> task;

    public ExecutorIterator(Iterator<T> iterator, Task<T> task) {
        this.iterator = iterator;
        this.task = task;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public T next() {
        T next = iterator.next();
        task.execute(next);
        return next;
    }
}
