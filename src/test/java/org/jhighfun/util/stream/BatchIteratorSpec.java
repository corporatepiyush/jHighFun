package org.jhighfun.util.stream;

import org.jhighfun.util.TaskStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.jhighfun.util.CollectionUtil.IntRange;
import static org.jhighfun.util.CollectionUtil.List;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class BatchIteratorSpec {

    @Test
    public void test() {

        List<Integer> integerList = IntRange(1, 100);
        int batchSize = 10;
        BatchStreamer<Integer> integerBatchIterator = new BatchStreamer<Integer>(new AbstractStreamerAdapter<Integer>(integerList.iterator()), batchSize);

        int first = 1;
        int last = batchSize;
        while (integerBatchIterator.hasNext()) {
            List<Integer> batch  = integerBatchIterator.next();
            assertEquals(batch, IntRange(first, last));
            first = last + 1;
            last = last + batchSize;
        }

    }

    @Test
    public void test1() {

        int batchSize = 2;
        List<String> stringList = List("India", "Singapore");
        BatchStreamer<String> integerBatchIterator = new BatchStreamer<String>(new AbstractStreamerAdapter<String>(stringList.iterator()), batchSize);

        List<List<String>> list = new TaskStream<List<String>>(integerBatchIterator)
                .extract();

        assertEquals(List(stringList), list);

    }
}
