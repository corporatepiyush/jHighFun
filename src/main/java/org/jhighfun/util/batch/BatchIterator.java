package org.jhighfun.util.batch;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Bulk fetch proxy for existing Iterator
 *
 * @author Piyush Katariya
 */

public class BatchIterator<T> extends AbstractIterator<List<T>> {

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
        final List<T> batch = new LinkedList<T>();
        int i = 0;
        while (recordIterator.hasNext() && i < batchSize) {
            batch.add(recordIterator.next());
            i++;
        }

        return batch;
    }

    public void remove() {
        int i = 0;
        while (i < batchSize) {
            recordIterator.remove();
            i++;
        }
    }
}
