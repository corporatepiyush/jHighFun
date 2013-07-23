package org.jhighfun.util.stream;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractStreamer<T> {

    public abstract boolean hasNext();

    public abstract T next();

    public void remove() {
        if (hasNext())
            next();
        else
            throw new NoSuchElementException();
    }
}
