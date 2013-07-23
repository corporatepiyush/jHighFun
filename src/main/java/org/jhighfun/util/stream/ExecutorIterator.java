package org.jhighfun.util.stream;


import org.jhighfun.util.Task;

import java.util.Iterator;

public class ExecutorIterator<T> extends AbstractIterator<T> {

    private final AbstractIterator<T> executorIterator;
    private final Task<T> task;

    public ExecutorIterator(AbstractIterator<T> iterator, Task<T> task) {
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
