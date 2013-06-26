package org.jhighfun.util.batch;


import org.jhighfun.util.Function;
import org.jhighfun.util.Tuple2;

import java.util.NoSuchElementException;

/**
 * Lazy proxy for existing Iterator
 *
 * @author Piyush Katariya
 */
public class LazyIterator<INIT, OBJ> extends AbstractIterator<OBJ> {

    protected INIT initialInput;
    protected final Function<INIT, Tuple2<INIT, OBJ>> function;
    protected final Function<INIT, Boolean> predicate;

    public LazyIterator(INIT initialInput, Function<INIT, Tuple2<INIT, OBJ>> function, Function<INIT, Boolean> predicate) {
        this.initialInput = initialInput;
        this.function = function;
        this.predicate = predicate;
    }

    public final boolean hasNext() {
        return predicate.apply(initialInput);
    }

    public final OBJ next() {
        Tuple2<INIT, OBJ> tuple2 = function.apply(initialInput);
        initialInput = tuple2._1;
        return tuple2._2;
    }

    public void remove() {
        if (hasNext())
            next();
        else
            throw new NoSuchElementException();
    }
}
