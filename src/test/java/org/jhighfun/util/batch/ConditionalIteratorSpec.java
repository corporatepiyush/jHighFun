package org.jhighfun.util.batch;

import org.jhighfun.util.Function;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.jhighfun.util.CollectionUtil.NumberRange;
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

        ConditionalIterator<Integer> conditionalIterator = new ConditionalIterator<Integer>(NumberRange(1, 100).iterator(), spy);

        for (Integer integer : new DynamicIterable<Integer>(conditionalIterator)) {
            assertTrue(integer % 4 == 0);
        }

        verify(spy, times(100)).apply(any(Integer.class));
    }

}
