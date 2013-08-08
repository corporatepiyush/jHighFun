package org.jhighfun.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class ExecuteWithThrottleSpec {

    private static ConcurrentHashMap<ExecutionThrottler, ExecutorService> mapSpy;
    private static ExecutionThrottler throttler;

    public static void init() throws Exception {

        Field throttlerPoolMap = FunctionUtil.class.getDeclaredField("throttlerPoolMap");
        throttlerPoolMap.setAccessible(true);
        mapSpy = spy(new ConcurrentHashMap<ExecutionThrottler, ExecutorService>(15, 0.9f, 32));
        throttlerPoolMap.set(null, mapSpy);

        String identity = "some operation";
        throttler = FunctionUtil.throttler(identity);
        FunctionUtil.registerPool(throttler, 10);

    }

    @Test
    public void testExecuteWithThrottle() throws Exception {
        init();
        Runnable codeBlockMock = mock(Runnable.class);
        FunctionUtil.executeWithThrottle(throttler, codeBlockMock);

        verify(mapSpy, times(1)).get(throttler);
        verify(codeBlockMock, times(1)).run();

    }

    @Test
    public void testExecuteAsyncWithThrottle() throws Exception {
        init();
        Runnable codeBlockMock = mock(Runnable.class);
        FunctionUtil.executeAsyncWithThrottle(throttler, codeBlockMock);

        verify(mapSpy, times(1)).get(throttler);
        Thread.sleep(200);

        verify(codeBlockMock, times(1)).run();

    }

    @Test
    public void testExecuteAsyncWithFutureAndThrottler() throws Exception {
        init();
        Callable<String> asyncTaskSpy = spy(new Callable<String>() {

            public String call() {
                return "Completed";
            }
        });

        Future<String> future = FunctionUtil.executeAsyncWithThrottle(throttler, asyncTaskSpy);
        future.get().equals("Completed");
        verify(mapSpy, times(1)).get(throttler);
        verify(asyncTaskSpy, times(1)).call();
    }

    @Test
    public void testExecuteAsyncWithThrottle_callback() throws Exception {
        init();
        Callable<Object> asyncTask = mock(Callable.class);
        CallbackTask<Object> callbackTask = mock(CallbackTask.class);


        Object asyncTaskResult = new Object();
        when(asyncTask.call()).thenReturn(asyncTaskResult);

        FunctionUtil.executeAsyncWithThrottle(throttler, asyncTask, callbackTask);

        verify(mapSpy, times(1)).get(throttler);
        Thread.sleep(200);


        verify(asyncTask, times(1)).call();
        ArgumentCaptor<AsyncTaskHandle> argument = ArgumentCaptor.forClass(AsyncTaskHandle.class);
        verify(callbackTask, times(1)).execute(argument.capture());

        assertEquals(argument.getValue().getAsyncTask(), asyncTask);
        assertEquals(argument.getValue().getOutput(), asyncTaskResult);
        assertEquals(argument.getValue().getException(), null);
    }
}
