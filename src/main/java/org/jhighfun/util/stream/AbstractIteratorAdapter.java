package org.jhighfun.util.stream;

import java.util.Iterator;

public class AbstractIteratorAdapter<T> extends AbstractIterator<T> {

    private final Iterator<T> iterator;

    public AbstractIteratorAdapter(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    public AbstractIteratorAdapter(Iterable<T> iterable) {
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
