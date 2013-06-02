package org.jhighfun.util.batch;


import org.jhighfun.util.Function;
import org.jhighfun.util.Tuple2;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Lazy proxy for existing Iterator
 *
 * @author Piyush Katariya
 */
public class LazyIterator<INIT, OBJ> implements Iterator<OBJ> {

    protected INIT initialInput;
    protected final Function<INIT, Tuple2<INIT, OBJ>> function;
    protected final Function<INIT, Boolean> predicate;
    private boolean hasNextCalledForCurrent = false;

    public LazyIterator(INIT initialInput, Function<INIT, Tuple2<INIT, OBJ>> function, Function<INIT, Boolean> predicate) {
        this.initialInput = initialInput;
        this.function = function;
        this.predicate = predicate;
    }

    public final boolean hasNext() {
        Boolean nextElementPossible = predicate.apply(initialInput);
        hasNextCalledForCurrent = true;
        return nextElementPossible;
    }

    public final OBJ next() {

        boolean hasNext = false;

        if (!hasNextCalledForCurrent) {
            hasNext = hasNext();
        }

        if (hasNext) {
            Tuple2<INIT, OBJ> tuple2 = function.apply(initialInput);
            initialInput = tuple2._1;
            hasNextCalledForCurrent = false;
            return tuple2._2;
        } else {
            throw new NoSuchElementException();
        }
    }

    public void remove() {

    }
}
