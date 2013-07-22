package org.jhighfun.util.stream;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertTrue;

public class BufferIteratorSpec {

    @Test
    public void test() {

        List<Integer> list = new LinkedList<Integer>();
        for (int i = 1; i < 1000; i++) {
            list.add(i);
        }

        BufferIterator<Integer> iterator = new BufferIterator<Integer>(list.iterator(), 512);

        Integer i = 0;
        while (iterator.hasNext()) {
            assertTrue(i.equals(iterator.next()));
            i++;
        }

    }


}
