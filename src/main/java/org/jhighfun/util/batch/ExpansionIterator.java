package org.jhighfun.util.batch;


import org.jhighfun.util.Function;

import java.util.Iterator;

public class ExpansionIterator<T, IN> implements Iterator<T> {

    private final Iterator<IN> iterator;
    private final Function<IN, Iterable<T>> function;

    private Iterator<T> currentIterator;
    private Boolean innerIteratorExhausted = true;

    public ExpansionIterator(Iterator<IN> iterator, Function<IN, Iterable<T>> function) {
        this.iterator = iterator;
        this.function = function;
    }

    public final boolean hasNext() {
        if (innerIteratorExhausted && iterator.hasNext()) {
            IN next = iterator.next();
            currentIterator = function.apply(next).iterator();
            innerIteratorExhausted = false;
        }
        boolean hasNext = currentIterator.hasNext();

        if (hasNext == false) {
            innerIteratorExhausted = true;
        }

        return hasNext;
    }

    public final T next() {
        return currentIterator.next();
    }

    public void remove() {

    }
}
