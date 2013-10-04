package org.jhighfun.util;

import java.util.Iterator;

import org.jhighfun.util.stream.AbstractStreamIterator;

public class IterableAdapter<T> implements Iterable<T> {

    private final AbstractStreamIterator<T> streamIterator;

    public IterableAdapter(AbstractStreamIterator<T> streamIterator) {
        this.streamIterator = streamIterator;

    }

    public Iterator iterator() {
        return new Iterator<T>() {
            public boolean hasNext() {
                return streamIterator.hasNext();
            }

            public T next() {
                return streamIterator.next();
            }

            public void remove() {
                streamIterator.skip();
            }
        };
    }
}
