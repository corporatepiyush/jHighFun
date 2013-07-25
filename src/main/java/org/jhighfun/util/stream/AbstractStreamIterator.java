package org.jhighfun.util.stream;

import java.util.NoSuchElementException;

public abstract class AbstractStreamIterator<T> {

    public abstract boolean hasNext();

    public abstract T next();

    public void remove() {
        if (hasNext())
            next();
        else
            throw new NoSuchElementException();
    }
}
