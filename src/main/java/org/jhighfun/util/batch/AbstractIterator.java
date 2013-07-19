package org.jhighfun.util.batch;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractIterator<T> implements Iterator<T> {

    public void remove() {
        if (hasNext())
            next();
        else
            throw new NoSuchElementException();
    }
}
