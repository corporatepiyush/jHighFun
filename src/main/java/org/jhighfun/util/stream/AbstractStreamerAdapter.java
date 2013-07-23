package org.jhighfun.util.stream;

import java.util.Iterator;

public class AbstractStreamerAdapter<T> extends AbstractStreamer<T> {

    private final Iterator<T> iterator;

    public AbstractStreamerAdapter(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    public AbstractStreamerAdapter(Iterable<T> iterable) {
        this.iterator = iterable.iterator();
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    @Override
    public T next() {
        return this.iterator.next();
    }
}
