package org.jhighfun.util.stream;


import org.jhighfun.util.Function;

import java.util.Iterator;

public class ExpansionIterator<IN, OUT> extends AbstractIterator<OUT> {

    private final Iterator<IN> expansionIterator;
    private final Function<IN, Iterable<OUT>> function;

    private Iterator<OUT> currentIterator;
    private Boolean innerIteratorExhausted = true;

    public ExpansionIterator(Iterator<IN> iterator, Function<IN, Iterable<OUT>> function) {
        this.expansionIterator = iterator;
        this.function = function;
    }

    public final boolean hasNext() {
        if (this.innerIteratorExhausted && this.expansionIterator.hasNext()) {
            IN next = this.expansionIterator.next();
            this.currentIterator = this.function.apply(next).iterator();
            this.innerIteratorExhausted = false;
        }
        boolean hasNext = this.currentIterator.hasNext();

        if (!hasNext) {
            this.innerIteratorExhausted = true;
        }

        return hasNext;
    }

    public final OUT next() {
        return this.currentIterator.next();
    }

}
