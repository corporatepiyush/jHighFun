package org.jhighfun.util;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ForkAndJoinSpec {

    @Test
    public void testThatExecuteJustAcceptTask() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");
        list.add("Groovy");
        list.add("Ruby");

        Task<Collection<String>> mockTask1 = mock(Task.class);
        Task<Collection<String>> mockTask2 = mock(Task.class);
        Task<Collection<String>> mockTask3 = mock(Task.class);

        ForkAndJoin<String> forkAndJoin = new ForkAndJoin<String>(new FunctionChain<String>(list));

        forkAndJoin.execute(mockTask1);
        forkAndJoin.execute(mockTask2);
        forkAndJoin.execute(mockTask3);

        verify(mockTask1, times(0)).execute(list);
        verify(mockTask2, times(0)).execute(list);
        verify(mockTask3, times(0)).execute(list);

    }

    @Test
    public void testThatJoinExecutesAllTask() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");
        list.add("Groovy");
        list.add("Ruby");

        Task<Collection<String>> mockTask1 = mock(Task.class);
        Task<Collection<String>> mockTask2 = mock(Task.class);
        Task<Collection<String>> mockTask3 = mock(Task.class);

        ForkAndJoin<String> forkAndJoin = new ForkAndJoin<String>(new FunctionChain<String>(list));

        forkAndJoin.execute(mockTask1);
        forkAndJoin.execute(mockTask2);
        forkAndJoin.execute(mockTask3);

        forkAndJoin.join();

        verify(mockTask1, times(1)).execute(list);
        verify(mockTask2, times(1)).execute(list);
        verify(mockTask3, times(1)).execute(list);

    }

    @Test
    public void testThatJoinShouldRestoreChain() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");
        list.add("Groovy");
        list.add("Ruby");

        Task<Collection<String>> mockTask1 = mock(Task.class);
        Task<Collection<String>> mockTask2 = mock(Task.class);
        Task<Collection<String>> mockTask3 = mock(Task.class);

        ForkAndJoin<String> forkAndJoin = new ForkAndJoin<String>(new FunctionChain<String>(list));

        forkAndJoin.execute(mockTask1);
        forkAndJoin.execute(mockTask2);
        forkAndJoin.execute(mockTask3);

        assertEquals(forkAndJoin.join().extract(), list);

    }

}
