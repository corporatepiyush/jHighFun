package org.jhighfun.util.stream;


import org.jhighfun.util.Function;

import java.util.Iterator;

public class MapperIterator<I, O> extends AbstractIterator<O> {

    private final Iterator<I> mapperIterator;
    private final Function<I, O> function;

    public MapperIterator(Iterator<I> iterator, Function<I, O> function) {
        this.mapperIterator = iterator;
        this.function = function;
    }

    public boolean hasNext() {
        return mapperIterator.hasNext();
    }

    public O next() {
        return function.apply(mapperIterator.next());
    }

}
