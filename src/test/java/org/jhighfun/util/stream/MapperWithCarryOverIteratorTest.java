package org.jhighfun.util.stream;

import org.jhighfun.util.Function;
import org.jhighfun.util.Tuple2;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MapperWithCarryOverIteratorTest {

    @Test
    public void test() {

        List<Integer> list = new LinkedList<Integer>();

        for (int i = 1; i <= 100; i++) {
            list.add(i);
        }

        MapperWithCarryOverStreamIterator<Integer, Integer, Integer> iterator = new MapperWithCarryOverStreamIterator<Integer, Integer, Integer>(new AbstractStreamIteratorAdapter<Integer>(list.iterator()), 1, new Function<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>>() {
            @Override
            public Tuple2<Integer, Integer> apply(Tuple2<Integer, Integer> tuple2) {
                return new Tuple2<Integer, Integer>(tuple2._1 + 1, tuple2._2 + tuple2._1);
            }
        });

        for (int integer : list.subList(0, list.size())) {
            iterator.hasNext();
            assertEquals(integer + integer, iterator.next().intValue());
        }

    }
}
