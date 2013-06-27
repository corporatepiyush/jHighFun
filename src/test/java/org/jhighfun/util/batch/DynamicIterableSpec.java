package org.jhighfun.util.batch;

import org.jhighfun.util.DynamicIterable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Iterator;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class DynamicIterableSpec {

    @Test
    public void test() {

        Iterator mockIterator = mock(Iterator.class);

        DynamicIterable dynamicIterable = new DynamicIterable(mockIterator);

        assertTrue(dynamicIterable.iterator() == mockIterator);

    }

}
