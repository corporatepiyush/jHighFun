package org.jhighfun.util.stream;

import org.jhighfun.util.Tuple2;

import java.util.LinkedList;
import java.util.List;

public class CrossProductStreamIterator<T, O> extends AbstractStreamIterator<Tuple2<T, O>> {

    private final AbstractStreamIterator<T> iterator;
    private AbstractStreamIterator<O> secondIterator;
    private boolean secondIteratorListInitialized = false;
    private boolean secondIteratorInProgress = false;
    private List<O> secondIteratorList = new LinkedList<O>();
    private Tuple2<T, O> tuple2 = new Tuple2<T, O>(null, null);

    public CrossProductStreamIterator(AbstractStreamIterator<T> iterator, AbstractStreamIterator<O> secondIterator) {
        this.iterator = iterator;
        this.secondIterator = secondIterator;
    }

    @Override
    public boolean hasNext() {

        if (!this.secondIteratorListInitialized) {
            while (this.secondIterator.hasNext()) {
                this.secondIteratorList.add(this.secondIterator.next());
            }
            this.secondIterator = new AbstractStreamIteratorAdapter<O>(this.secondIteratorList.iterator());
            this.secondIteratorListInitialized = true;
        }

        boolean hasNext = false;
        if (this.secondIterator.hasNext()) {
            this.tuple2._2 = this.secondIterator.next();
            hasNext = true;
        } else {
            this.secondIteratorInProgress = false;
            this.secondIterator = new AbstractStreamIteratorAdapter<O>(this.secondIteratorList.iterator());

            if (this.secondIterator.hasNext()) {
                this.tuple2._2 = this.secondIterator.next();
                hasNext = true;
            }
        }

        if (!this.secondIteratorInProgress && this.iterator.hasNext()) {
            this.tuple2._1 = this.iterator.next();
            this.secondIteratorInProgress = true;
        }

        return hasNext;
    }

    @Override
    public Tuple2<T, O> next() {
        return tuple2;
    }

    @Override
    public void closeResources() {
        this.tuple2 = null;
        this.secondIteratorList.clear();
        this.iterator.closeResources();
        this.secondIterator.closeResources();
    }
}
