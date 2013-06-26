package org.jhighfun.util.batch;


import org.jhighfun.util.Function;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MapperIterator<I, O> extends AbstractIterator<O> {

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
        if (hasNext())
            next();
        else
            throw new NoSuchElementException();
    }
}
