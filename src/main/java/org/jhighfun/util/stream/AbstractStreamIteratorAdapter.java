package org.jhighfun.util.stream;

import java.util.Iterator;

public class AbstractStreamIteratorAdapter<T> extends AbstractStreamIterator<T> {

    private final Iterator<T> iterator;

    public AbstractStreamIteratorAdapter(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    public AbstractStreamIteratorAdapter(Iterable<T> iterable) {
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

    public void closeResources() {

    }
}
