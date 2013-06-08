package org.jhighfun.util.batch;


import org.jhighfun.util.Function;

import java.util.Iterator;

public class MapperIterator<I, O> implements Iterator<O> {

    private final Iterator<I> iterator;
    private final Function<I, O> function;

    public MapperIterator(Iterator<I> iterator, Function<I, O> function) {
        this.iterator = iterator;
        this.function = function;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public O next() {
        return function.apply(iterator.next());
    }

    public void remove() {
        iterator.remove();
    }
}
