package org.jhighfun.util.batch;

import java.util.Iterator;

/**
 * @author Piyush Katariya
 */
public class DynamicIterable<T> implements Iterable<T> {

    private final Iterator<T> iterator;

    public DynamicIterable(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    public Iterator<T> iterator() {
        return iterator;
    }

    @Override
    public String toString() {

        StringBuilder string = new StringBuilder("[");
        if(iterator.hasNext()){
            string.append(iterator.next().toString());
        }
        while(iterator.hasNext()){
            string.append(", ");
            string.append(iterator.next().toString());
        }
        string.append("]");

        return string.toString();
    }
}
