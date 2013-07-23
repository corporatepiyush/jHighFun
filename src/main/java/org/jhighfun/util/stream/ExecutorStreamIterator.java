package org.jhighfun.util.stream;


import org.jhighfun.util.Task;

public class ExecutorStreamIterator<T> extends AbstractStreamIterator<T> {

    private final AbstractStreamIterator<T> executorIterator;
    private final Task<T> task;

    public ExecutorStreamIterator(AbstractStreamIterator<T> iterator, Task<T> task) {
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
