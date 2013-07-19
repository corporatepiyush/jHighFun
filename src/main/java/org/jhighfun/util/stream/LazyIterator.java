package org.jhighfun.util.stream;


import org.jhighfun.util.Function;
import org.jhighfun.util.Tuple2;

/**
 * Lazy customized stream iterator, useful for memory sensitive operations
 *
 * @author Piyush Katariya
 */
public class LazyIterator<INIT, OBJ> extends AbstractIterator<OBJ> {

    protected INIT initialInput;
    protected final Function<INIT, Tuple2<INIT, OBJ>> function;
    protected final Function<INIT, Boolean> predicate;

    private boolean iterationInProgress = false;
    private boolean hasNext;

    public LazyIterator(INIT initialInput, Function<INIT, Tuple2<INIT, OBJ>> function, Function<INIT, Boolean> predicate) {
        this.initialInput = initialInput;
        this.function = function;
        this.predicate = predicate;
    }

    public final boolean hasNext() {
        checkIfIterationInProgress();
        return this.hasNext;
    }

    public final OBJ next() {
        checkIfIterationInProgress();
        Tuple2<INIT, OBJ> tuple2 = this.function.apply(this.initialInput);
        this.initialInput = tuple2._1;
        this.iterationInProgress = false;
        return tuple2._2;
    }

    private void checkIfIterationInProgress() {
        if (!this.iterationInProgress) {
            this.hasNext = this.predicate.apply(this.initialInput);
        }
    }

}
