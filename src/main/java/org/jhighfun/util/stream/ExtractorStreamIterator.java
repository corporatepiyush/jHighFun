package org.jhighfun.util.stream;


import java.util.LinkedList;
import java.util.List;

import org.jhighfun.util.Function;

public class ExtractorStreamIterator<T> extends AbstractStreamIterator<List<T>> {

    private final AbstractStreamIterator<T> iterator;
    private Function<List<T>, Boolean> function;

    private List<T> current;

    public ExtractorStreamIterator(AbstractStreamIterator<T> iterator, Function<List<T>, Boolean> function) {
        this.iterator = iterator;
        this.function = function;
    }

    public boolean hasNext() {
        checkForNext();
        return this.current.size() > 0;
    }

    private void checkForNext() {
        List<T> extracts = new LinkedList<T>();
        this.current = new LinkedList<T>();

        while (this.iterator.hasNext()) {
            T next = this.iterator.next();
            extracts.add(next);
            if (this.function.apply(extracts)) {
                this.current.add(next);
            } else {
                extracts = new LinkedList<T>();
                if (this.current.size() > 0) {
                    this.current.add(next);
                    break;
                }
            }
        }
    }

    public List<T> next() {
        return this.current;
    }

    @Override
    public void closeResources() {
        this.function = null;
        if (this.current != null)
            this.current.clear();
        this.iterator.closeResources();
    }
}
