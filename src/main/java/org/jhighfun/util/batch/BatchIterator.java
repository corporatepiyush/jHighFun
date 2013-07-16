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

    private final Iterator<T> batchIterator;
    private final int batchSize;

    public BatchIterator(Iterator<T> iterator, int batchSize) {
        this.batchIterator = iterator;
        this.batchSize = Math.abs(batchSize);
    }

    public boolean hasNext() {
        return this.batchIterator.hasNext();
    }

    public List<T> next() {
        final List<T> batch = new LinkedList<T>();
        int i = 0;
        while (this.batchIterator.hasNext() && i < this.batchSize) {
            batch.add(this.batchIterator.next());
            i++;
        }

        return batch;
    }

    public void remove() {
        int i = 0;
        while (i < this.batchSize) {
            this.batchIterator.remove();
            i++;
        }
    }
}
