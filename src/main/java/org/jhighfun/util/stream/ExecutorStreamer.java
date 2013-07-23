package org.jhighfun.util.stream;


import org.jhighfun.util.Task;

public class ExecutorStreamer<T> extends AbstractStreamer<T> {

    private final AbstractStreamer<T> executorIterator;
    private final Task<T> task;

    public ExecutorStreamer(AbstractStreamer<T> iterator, Task<T> task) {
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
