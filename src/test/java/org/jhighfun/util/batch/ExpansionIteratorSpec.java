package org.jhighfun.util.batch;

import org.jhighfun.util.CollectionUtil;
import org.jhighfun.util.DynamicIterable;
import org.jhighfun.util.Function;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.jhighfun.util.CollectionUtil.IntRange;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ExpansionIteratorSpec {

    @Test
    public void test() {

        ExpansionIterator<Integer, Integer> iterator = new ExpansionIterator<Integer, Integer>(CollectionUtil.IntRange(10, 100, 10).iterator(), new Function<Integer, Iterable<Integer>>() {
            public Iterable<Integer> apply(Integer arg) {
                return IntRange(arg, arg + 10);
            }
        });

        int index = 10;
        for (int integer : new DynamicIterable<Integer>(iterator)) {
            assertEquals(integer, index++);
        }

    }

}
