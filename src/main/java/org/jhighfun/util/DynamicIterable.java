package org.jhighfun.util;

import java.util.Iterator;

public class DynamicIterable<T> implements Iterable<T>{

    private final Iterator<T> iterator;

    public DynamicIterable(Iterator<T> iterator){
       this.iterator = iterator;
    }


    public Iterator<T> iterator() {
        return iterator;
    }
}
