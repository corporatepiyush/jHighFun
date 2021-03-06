package org.jhighfun.util.stream;

import org.jhighfun.util.Function;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.jhighfun.util.CollectionUtil.IntRange;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConditionalIteratorSpec {

    @Test
    public void test() {
        Function<Integer, Boolean> spy = spy(new Function<Integer, Boolean>() {

            public Boolean apply(Integer arg) {
                return arg % 4 == 0;
            }
        });

        ConditionalStreamIterator<Integer> conditionalIterator = new ConditionalStreamIterator<Integer>(new AbstractStreamIteratorAdapter<Integer>(IntRange(1, 100).iterator()), spy);

        while (conditionalIterator.hasNext()) {
            Integer integer = conditionalIterator.next();
            assertTrue(integer % 4 == 0);
        }

        verify(spy, times(100)).apply(any(Integer.class));
    }

}
