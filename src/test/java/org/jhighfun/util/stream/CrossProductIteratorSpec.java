package org.jhighfun.util.stream;

import org.jhighfun.util.Tuple2;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CrossProductIteratorSpec {

    @Test
    public void test() {

        List<Integer> list = new LinkedList<Integer>();

        for (int i = 1; i <= 100; i++) {
            list.add(i);
        }

        AbstractStreamIteratorAdapter<Integer> iteratorAdapter1 = new AbstractStreamIteratorAdapter<Integer>(list.subList(0, list.size()));
        AbstractStreamIteratorAdapter<Integer> iteratorAdapter2 = new AbstractStreamIteratorAdapter<Integer>(list.subList(0, list.size()));

        CrossProductStreamIterator<Integer, Integer> streamIterator = new CrossProductStreamIterator<Integer, Integer>(iteratorAdapter1, iteratorAdapter2);

        for (Integer integer1 : list.subList(0, list.size())) {
            for (Integer integer2 : list.subList(0, list.size())) {
                streamIterator.hasNext();
                assertEquals(new Tuple2<Integer, Integer>(integer1, integer2), streamIterator.next());
            }
        }


    }
}
