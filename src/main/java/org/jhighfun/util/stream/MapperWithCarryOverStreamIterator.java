package org.jhighfun.util.stream;

import org.jhighfun.util.Function;
import org.jhighfun.util.Tuple2;

public class MapperWithCarryOverStreamIterator<IN, OUT, CARRY> extends AbstractStreamIterator<OUT> {

    private final AbstractStreamIterator<IN> mapperWithCarryOverIterator;
    private Function<Tuple2<CARRY, IN>, Tuple2<CARRY, OUT>> function;
    private CARRY carryOver;
    private Tuple2<CARRY, IN> input = new Tuple2<CARRY, IN>(null, null);

    public MapperWithCarryOverStreamIterator(AbstractStreamIterator<IN> mapperWithCarryOverIterator, CARRY initialValue, Function<Tuple2<CARRY, IN>, Tuple2<CARRY, OUT>> function) {
        this.mapperWithCarryOverIterator = mapperWithCarryOverIterator;
        this.carryOver = initialValue;
        this.function = function;
    }

    public boolean hasNext() {
        return this.mapperWithCarryOverIterator.hasNext();
    }

    public OUT next() {
        this.input._1 = this.carryOver;
        this.input._2 = this.mapperWithCarryOverIterator.next();
        Tuple2<CARRY, OUT> result = this.function.apply(this.input);
        this.carryOver = result._1;
        return result._2;
    }

    @Override
    public void closeResources() {
        this.input = null;
        this.carryOver = null;
        this.function = null;
        this.mapperWithCarryOverIterator.closeResources();
    }
}
