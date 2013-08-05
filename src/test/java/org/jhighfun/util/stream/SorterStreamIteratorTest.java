package org.jhighfun.util.stream;

import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class SorterStreamIteratorTest {

    @Test
    public void test(){

        List<Integer> list = new LinkedList<Integer>();

        for (int i = 1; i <= 100; i++) {
            list.add(i);
        }

        List<Integer> integers = list.subList(0, list.size());
        Collections.shuffle(integers);
        SorterStreamIterator<Integer> iterator = new SorterStreamIterator<Integer>(new AbstractStreamIteratorAdapter<Integer>(integers), new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });

        List<Integer> expected = new LinkedList<Integer>();

        for (int i = 1; i <= 100; i++) {
            expected.add(i);
        }

        for(Integer integer : expected) {
            iterator.hasNext();
            Integer next = iterator.next();
            assertEquals(integer, next);
        }

    }
}


