package org.jhighfun.util.batch;


import org.jhighfun.util.Function;

import java.util.Iterator;

public class ExpansionIterator<IN, OUT> extends AbstractIterator<OUT> {

    private final Iterator<IN> iterator;
    private final Function<IN, Iterable<OUT>> function;

    private Iterator<OUT> currentIterator;
    private Boolean innerIteratorExhausted = true;

    public ExpansionIterator(Iterator<IN> iterator, Function<IN, Iterable<OUT>> function) {
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

        if (!hasNext) {
            innerIteratorExhausted = true;
        }

        return hasNext;
    }

    public final OUT next() {
        return currentIterator.next();
    }

    public void remove() {

    }
}
