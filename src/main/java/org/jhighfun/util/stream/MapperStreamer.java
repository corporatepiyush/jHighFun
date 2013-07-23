package org.jhighfun.util.stream;


import org.jhighfun.util.Function;

public class MapperStreamer<I, O> extends AbstractStreamer<O> {

    private final AbstractStreamer<I> mapperIterator;
    private final Function<I, O> function;

    public MapperStreamer(AbstractStreamer<I> iterator, Function<I, O> function) {
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
