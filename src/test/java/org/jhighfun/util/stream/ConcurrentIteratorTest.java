package org.jhighfun.util.stream;

import org.jhighfun.util.DynamicIterable;
import org.jhighfun.util.FunctionUtil;
import org.jhighfun.util.RecordProcessor;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class ConcurrentIteratorTest {

    @Test
    public void testForMultipleThreads(){

        List<Integer> list = new LinkedList<Integer>();

        for(int i = 0; i < 10000; i++){
             list.add(i);
        }

        final DynamicIterable<Integer> dynamicIterable = new DynamicIterable<Integer>(new ConcurrentIterator<Integer>(list.iterator()));

        FunctionUtil.each(list, new RecordProcessor<Integer>() {
            public void process(Integer record) {
                for(Integer integer : dynamicIterable);
            }
        }, FunctionUtil.parallel(100));

    }
}
