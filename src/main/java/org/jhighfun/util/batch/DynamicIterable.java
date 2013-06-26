package org.jhighfun.util.batch;

import org.jhighfun.util.Function;
import org.jhighfun.util.Tuple2;

import java.util.Iterator;
import java.util.List;

/**
 * @author Piyush Katariya
 */
public class DynamicIterable<IN> implements Iterable<IN> {

    private final Iterator<IN> iterator;

    public DynamicIterable(Iterator<IN> iterator) {
        this.iterator = iterator;
    }

    public DynamicIterable(Iterable<IN> iterable) {
        this.iterator = iterable.iterator();
    }

    public <INIT> DynamicIterable(INIT initialInput, Function<INIT, Tuple2<INIT, IN>> function, Function<INIT, Boolean> predicate) {
        this.iterator = new LazyIterator<INIT, IN>(initialInput, function, predicate);
    }

    public <OUT> DynamicIterable<OUT> expand(Function<IN, Iterable<OUT>> function) {
        return new DynamicIterable<OUT>(new ExpansionIterator<IN, OUT>(iterator, function));
    }

    public DynamicIterable<IN> filter(Function<IN, Boolean> function) {
        return new DynamicIterable<IN>(new ConditionalIterator<IN>(iterator, function));
    }

    public <OUT> DynamicIterable<OUT> map(Function<IN, OUT> function) {
        return new DynamicIterable<OUT>(new MapperIterator<IN, OUT>(iterator, function));
    }

    public DynamicIterable<List<IN>> batch(int batchSize) {
        return new DynamicIterable<List<IN>>(new BatchIterator<IN>(iterator, batchSize));
    }

    public DynamicIterable<IN> ensureThreadSafety() {
        return new DynamicIterable<IN>(new ConcurrentIterator<IN>(iterator));
    }

    public Iterator<IN> iterator() {
        return iterator;
    }

    @Override
    public String toString() {

        StringBuilder string = new StringBuilder("[");
        if (iterator.hasNext()) {
            string.append(iterator.next().toString());
        }
        while (iterator.hasNext()) {
            string.append(", ");
            string.append(iterator.next().toString());
        }
        string.append("]");

        return string.toString();
    }
}
