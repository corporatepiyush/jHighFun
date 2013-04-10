package org.jhighfun.util;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CollectionForkAndJoinSpec {

    @Spy
    ExecutorService spyHighPriorityTaskThreadPool = new ThreadPoolExecutor(1, 100, 1, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

    @Before
    public void before() {
        try {
            Field globalPool = FunctionUtil.class.getDeclaredField("highPriorityTaskThreadPool");
            globalPool.setAccessible(true);
            globalPool.set(null, spyHighPriorityTaskThreadPool);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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

        CollectionForkAndJoin<String> collectionForkAndJoin = new CollectionForkAndJoin<String>(new CollectionFunctionChain<String>(list));

        collectionForkAndJoin.execute(mockTask1);
        collectionForkAndJoin.execute(mockTask2);
        collectionForkAndJoin.execute(mockTask3);

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

        CollectionForkAndJoin<String> collectionForkAndJoin = new CollectionForkAndJoin<String>(new CollectionFunctionChain<String>(list));

        collectionForkAndJoin.execute(mockTask1);
        collectionForkAndJoin.execute(mockTask2);
        collectionForkAndJoin.execute(mockTask3);

        collectionForkAndJoin.join();

        verify(mockTask1, times(1)).execute(list);
        verify(mockTask2, times(1)).execute(list);
        verify(mockTask3, times(1)).execute(list);
        verify(spyHighPriorityTaskThreadPool, times(2)).submit(any(Runnable.class));

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

        CollectionForkAndJoin<String> collectionForkAndJoin = new CollectionForkAndJoin<String>(new CollectionFunctionChain<String>(list));

        collectionForkAndJoin.execute(mockTask1);
        collectionForkAndJoin.execute(mockTask2);
        collectionForkAndJoin.execute(mockTask3);

        assertEquals(collectionForkAndJoin.join().extract(), list);

    }

}
