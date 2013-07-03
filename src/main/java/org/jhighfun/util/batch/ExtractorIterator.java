package org.jhighfun.util.batch;


import org.jhighfun.util.Function;

import java.util.Iterator;
import java.util.List;

public class ExtractorIterator<T> extends AbstractIterator<T> {

    private final Iterator<T> iterator;
    private final Function<List<T>, Boolean> function;

    public ExtractorIterator(Iterator<T> iterator, Function<List<T>, Boolean> function) {
        this.iterator = iterator;
        this.function = function;
    }

    public boolean hasNext() {

        return false;
    }

    public T next() {
        return null;
    }
}
