package org.jhighfun.util.batch;

import org.jhighfun.util.FunctionUtil;
import org.jhighfun.util.RecordProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.jhighfun.util.CollectionUtil.NumberRange;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SafeIteratorSpec {


    @Test
    public void testWithSingleThread() {

        List<Integer> integerList = NumberRange(1, 10000);
        SafeIterator<Integer> integerSafeIterator = new SafeIterator<Integer>(integerList.iterator());

        final DynamicIterable<Integer> dynamicIterable = new DynamicIterable<Integer>(integerSafeIterator);

        int index = 0;
        for (Integer integer : dynamicIterable) {
            assertEquals(integerList.get(index++), integer);
        }

    }


    @Test
    public void testWithMultipleThreads() {

        SafeIterator<Integer> integerSafeIterator = new SafeIterator<Integer>(NumberRange(1, 1000).iterator());

        final DynamicIterable<Integer> dynamicIterable = new DynamicIterable<Integer>(integerSafeIterator);

        FunctionUtil.each(NumberRange(1, 5), new RecordProcessor<Integer>() {
            public void process(Integer record) {
                for (Integer integer : dynamicIterable) {
                    System.out.println(Thread.currentThread().getId() + " :: " + integer);
                }
            }
        }, FunctionUtil.parallel());

        System.out.println("finished");

    }
}
