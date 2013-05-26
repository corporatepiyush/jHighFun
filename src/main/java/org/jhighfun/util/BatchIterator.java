package org.jhighfun.util;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BatchIterator<T> implements Iterator<List<T>> {

    private final Iterator<T> recordIterator;
    private final int batchSize;

    public BatchIterator(Iterator<T> recordIterator, int batchSize) {
        this.recordIterator = recordIterator;
        this.batchSize = batchSize;
    }

    public boolean hasNext() {
        return recordIterator.hasNext();
    }

    public List<T> next() {
        List<T> chunk = new LinkedList<T>();
        int i = 0;
        while (recordIterator.hasNext() && i < batchSize) {
            chunk.add(recordIterator.next());
            i++;
        }

        return chunk;
    }

    public void remove() {
        int i = 0;
        while (i < batchSize) {
            recordIterator.remove();
            i++;
        }
    }
}
