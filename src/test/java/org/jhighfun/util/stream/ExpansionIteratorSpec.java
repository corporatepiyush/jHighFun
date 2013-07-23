package org.jhighfun.util.stream;

import org.jhighfun.util.CollectionUtil;
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

        ExpansionStreamer<Integer, Integer> iterator = new ExpansionStreamer<Integer, Integer>(new AbstractStreamerAdapter<Integer>(CollectionUtil.IntRange(10, 100, 10).iterator()), new Function<Integer, Iterable<Integer>>() {
            public Iterable<Integer> apply(Integer arg) {
                return IntRange(arg, arg + 10);
            }
        });

        int index = 10;
        while (iterator.hasNext()) {
            int integer = iterator.next();
            assertEquals(integer, index++);
        }

    }

}
