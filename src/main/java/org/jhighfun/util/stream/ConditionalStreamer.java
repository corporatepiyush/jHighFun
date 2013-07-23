package org.jhighfun.util.stream;


import org.jhighfun.util.Function;
import org.jhighfun.util.Task;

import java.util.NoSuchElementException;

/**
 * Condition aware proxy for existing Iterator
 *
 * @author Piyush Katariya
 */

public final class ConditionalStreamer<T> extends AbstractStreamer<T> {

    private final AbstractStreamer<T> conditionalIterator;
    private final Function<T, Boolean> predicate;
    private T current;
    private Task<T> task;

    public ConditionalStreamer(AbstractStreamer<T> conditionalIterator, Function<T, Boolean> predicate) {
        this.conditionalIterator = conditionalIterator;
        this.predicate = predicate;
    }

    public ConditionalStreamer(AbstractStreamer<T> iterator, Function<T, Boolean> predicate, Task<T> task) {
        this.conditionalIterator = iterator;
        this.predicate = predicate;
        this.task = task;
    }

    public boolean hasNext() {
        if (this.current != null) {
            return true;
        }

        boolean hasNext = false;
        while (conditionalIterator.hasNext()) {
            T current = conditionalIterator.next();
            if (predicate.apply(current)) {
                hasNext = true;
                this.current = current;
                break;
            } else if (task != null) {
                task.execute(current);
            }
        }
        return hasNext;
    }

    public T next() {
        if (this.current == null) {
            if (hasNext()) {
                return this.current;
            } else {
                throw new NoSuchElementException();
            }
        } else {
            T current = this.current;
            this.current = null;
            return current;
        }
    }

}