package org.jhighfun.util.stream;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractIterator<T> {

    public abstract boolean hasNext();

    public abstract T next();

    public void remove() {
        if (hasNext())
            next();
        else
            throw new NoSuchElementException();
    }
}
