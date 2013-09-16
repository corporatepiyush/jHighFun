package org.jhighfun.util.stream;

import org.jhighfun.util.Function;
import org.jhighfun.util.Tuple2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.jhighfun.util.CollectionUtil.tuple;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class LazyIteratorSpec {

    @Test
    public void test() {

        final int load = 10000;
        DynamicStreamIterator<Integer, Integer> lazyIterator = new DynamicStreamIterator<Integer, Integer>(1,
                new Function<Integer, Tuple2<Integer, Integer>>() {
                    public Tuple2<Integer, Integer> apply(Integer arg) {
                        return tuple(arg + 1, arg);
                    }
                },
                new Function<Integer, Boolean>() {
                    public Boolean apply(Integer arg) {
                        return arg != load;
                    }
                }
        );

        int index = 1;
        while (lazyIterator.hasNext()) {
            int integer = lazyIterator.next();
            assertEquals(integer, index++);
        }
        assertEquals(index, load);
    }
}
