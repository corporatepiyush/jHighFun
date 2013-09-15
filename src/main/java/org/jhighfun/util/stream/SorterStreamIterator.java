package org.jhighfun.util.stream;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SorterStreamIterator<T> extends AbstractStreamIterator<T> {

    private AbstractStreamIterator<T> iterator;
    private Comparator<T> comparator;
    private boolean fetchingDone = false;
    private final List<T> list = new LinkedList<T>();

    public SorterStreamIterator(AbstractStreamIterator<T> iterator, Comparator<T> comparator) {
        this.iterator = iterator;
        this.comparator = comparator;
    }

    @Override
    public boolean hasNext() {

        if (!this.fetchingDone) {
            while (this.iterator.hasNext()) {
                list.add(this.iterator.next());
            }

            Collections.sort(list, comparator);
            this.iterator = new AbstractStreamIteratorAdapter<T>(list.iterator());
            this.fetchingDone = true;
        }

        return this.iterator.hasNext();
    }

    @Override
    public T next() {
        return this.iterator.next();
    }

    @Override
    public void closeResources() {
        this.comparator = null;
        this.list.clear();
        this.iterator.closeResources();
    }
}
