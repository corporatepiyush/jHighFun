package org.jhighfun.util.batch;


import org.jhighfun.util.Function;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Condition aware proxy for existing Iterator
 *
 * @author Piyush Katariya
 */

public final class ConditionalIterator<T> implements Iterator<T> {

    private final Iterator<T> iterator;
    private final Function<T, Boolean> predicate;
    private T current;

    public ConditionalIterator(Iterator<T> iterator, Function<T, Boolean> predicate) {
        this.iterator = iterator;
        this.predicate = predicate;
    }

    public boolean hasNext() {
        boolean hasNext = false;
        while (iterator.hasNext()) {
            T current = iterator.next();
            if (predicate.apply(current)) {
                hasNext = true;
                this.current = current;
                break;
            }
        }
        return hasNext;
    }

    public T next() {
        if (current == null) {
            if (hasNext()) {
                return this.current;
            } else {
                throw new NoSuchElementException();
            }
        } else {
            return this.current;
        }
    }

    public void remove() {
        iterator.remove();
    }
}
