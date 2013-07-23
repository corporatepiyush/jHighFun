package org.jhighfun.util.stream;


import org.jhighfun.util.Function;

import java.util.Iterator;

public class ExpansionStreamer<IN, OUT> extends AbstractStreamer<OUT> {

    private final AbstractStreamer<IN> expansionIterator;
    private final Function<IN, Iterable<OUT>> function;

    private Iterator<OUT> currentIterator;
    private Boolean innerIteratorExhausted = true;

    public ExpansionStreamer(AbstractStreamer<IN> iterator, Function<IN, Iterable<OUT>> function) {
        this.expansionIterator = iterator;
        this.function = function;
    }

    public final boolean hasNext() {
        if (this.innerIteratorExhausted && this.expansionIterator.hasNext()) {
            this.currentIterator = this.function.apply(this.expansionIterator.next()).iterator();
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
