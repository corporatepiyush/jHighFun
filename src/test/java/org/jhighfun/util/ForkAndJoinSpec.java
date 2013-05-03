package org.jhighfun.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class ForkAndJoinSpec {

    @Spy
    ExecutorService spyHighPriorityTaskThreadPool = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 1, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

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

        String object = "Scala";

        Task<String> mockTask1 = mock(Task.class);
        Task<String> mockTask2 = mock(Task.class);
        Task<String> mockTask3 = mock(Task.class);

        ForkAndJoin<String> objectForkAndJoin = new ForkAndJoin<String>(object);

        objectForkAndJoin.execute(mockTask1);
        objectForkAndJoin.execute(mockTask2);
        objectForkAndJoin.execute(mockTask3);

        verify(mockTask1, times(0)).execute(object);
        verify(mockTask2, times(0)).execute(object);
        verify(mockTask3, times(0)).execute(object);

    }

    @Test
    public void testThatJoinExecutesAllTask() {

        String object = "Scala";

        Task<String> mockTask1 = mock(Task.class);
        Task<String> mockTask2 = mock(Task.class);
        Task<String> mockTask3 = mock(Task.class);

        ForkAndJoin<String> objectForkAndJoin = new ForkAndJoin<String>(object);

        objectForkAndJoin.execute(mockTask1);
        objectForkAndJoin.execute(mockTask2);
        objectForkAndJoin.execute(mockTask3);

        objectForkAndJoin.join();

        verify(mockTask1, times(1)).execute(object);
        verify(mockTask2, times(1)).execute(object);
        verify(mockTask3, times(1)).execute(object);
        verify(spyHighPriorityTaskThreadPool, times(2)).submit(any(Runnable.class));

    }


}
