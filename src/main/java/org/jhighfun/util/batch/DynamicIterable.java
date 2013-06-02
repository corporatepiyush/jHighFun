package org.jhighfun.util.batch;

import java.util.Iterator;

/**
 *
 *
 * @author Piyush Katariya
 *
 */
public class DynamicIterable<T> implements Iterable<T> {

    private final Iterator<T> iterator;

    public DynamicIterable(Iterator<T> iterator){
        this.iterator = iterator;
    }

    public Iterator<T> iterator() {
        return iterator;
    }
}
