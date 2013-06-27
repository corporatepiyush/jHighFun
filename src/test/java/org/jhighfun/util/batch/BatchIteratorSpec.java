package org.jhighfun.util.batch;

import org.jhighfun.util.DynamicIterable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.jhighfun.util.CollectionUtil.NumberRange;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class BatchIteratorSpec {

    @Test
    public void test() {

        List<Integer> integerList = NumberRange(1, 100);
        int batchSize = 10;
        BatchIterator<Integer> integerBatchIterator = new BatchIterator<Integer>(integerList.iterator(), batchSize);

        int first = 1;
        int last = batchSize;
        for (List<Integer> batch : new DynamicIterable<List<Integer>>(integerBatchIterator)) {
            assertEquals(batch, NumberRange(first, last));
            first = last + 1;
            last = last + batchSize;
        }

    }
}
