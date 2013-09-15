package org.jhighfun.util.stream;


import org.jhighfun.util.Function;

public class MapperStreamIterator<I, O> extends AbstractStreamIterator<O> {

    private final AbstractStreamIterator<I> mapperIterator;
    private Function<I, O> function;

    public MapperStreamIterator(AbstractStreamIterator<I> iterator, Function<I, O> function) {
        this.mapperIterator = iterator;
        this.function = function;
    }

    public boolean hasNext() {
        return mapperIterator.hasNext();
    }

    public O next() {
        return function.apply(mapperIterator.next());
    }

    @Override
    public void closeResources() {
        this.function = null;
        this.mapperIterator.closeResources();
    }

}
