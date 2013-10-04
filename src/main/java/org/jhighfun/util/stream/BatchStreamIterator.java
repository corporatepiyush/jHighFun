package org.jhighfun.util.stream;


import java.util.ArrayList;

/**
 * Bulk fetch proxy for existing Iterator
 *
 * @author Piyush Katariya
 */

public class BatchStreamIterator<T> extends AbstractStreamIterator<ArrayList<T>> {

    private final AbstractStreamIterator<T> batchIterator;
    private final int batchSize;

    public BatchStreamIterator(AbstractStreamIterator<T> iterator, int batchSize) {
        this.batchIterator = iterator;
        this.batchSize = Math.abs(batchSize);
    }

    public boolean hasNext() {
        return this.batchIterator.hasNext();
    }

    public ArrayList<T> next() {
        final ArrayList<T> batch = new ArrayList<T>();
        int i = 0;
        while (this.batchIterator.hasNext() && i < this.batchSize) {
            batch.add(this.batchIterator.next());
            i++;
        }

        return batch;
    }

    @Override
    public void closeResources() {
        this.batchIterator.closeResources();
    }

}
