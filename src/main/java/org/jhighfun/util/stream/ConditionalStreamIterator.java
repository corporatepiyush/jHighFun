package org.jhighfun.util.stream;


import org.jhighfun.util.Function;
import org.jhighfun.util.Task;

import java.util.NoSuchElementException;

/**
 * Condition aware proxy for existing Iterator
 *
 * @author Piyush Katariya
 */

public final class ConditionalStreamIterator<T> extends AbstractStreamIterator<T> {

    private final AbstractStreamIterator<T> conditionalIterator;
    private Function<T, Boolean> predicate;
    private T current;
    private Task<T> task;

    public ConditionalStreamIterator(AbstractStreamIterator<T> conditionalIterator, Function<T, Boolean> predicate) {
        this.conditionalIterator = conditionalIterator;
        this.predicate = predicate;
    }

    public ConditionalStreamIterator(AbstractStreamIterator<T> iterator, Function<T, Boolean> predicate, Task<T> task) {
        this.conditionalIterator = iterator;
        this.predicate = predicate;
        this.task = task;
    }

    public boolean hasNext() {
        if (this.current != null) {
            return true;
        }

        boolean hasNext = false;
        while (this.conditionalIterator.hasNext()) {
            T current = this.conditionalIterator.next();
            if (this.predicate.apply(current)) {
                hasNext = true;
                this.current = current;
                break;
            } else if (this.task != null) {
                this.task.execute(current);
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

    @Override
    public void closeResources() {
        this.predicate = null;
        this.task = null;
        this.current = null;
        this.conditionalIterator.closeResources();
    }

}
