package org.jhighfun.util.stream;


import org.jhighfun.util.Function;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ExtractorIterator<T> extends AbstractIterator<List<T>> {

    private final Iterator<T> iterator;
    private final Function<List<T>, Boolean> function;

    private List<T> current;

    public ExtractorIterator(Iterator<T> iterator, Function<List<T>, Boolean> function) {
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
}
