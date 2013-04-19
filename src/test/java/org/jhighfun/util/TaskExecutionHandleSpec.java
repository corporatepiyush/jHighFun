package org.jhighfun.util;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TaskExecutionHandleSpec {


    @Test
    public void testWaitForCompletion() throws ExecutionException, InterruptedException {

        List<Future> list = new ArrayList<Future>();
        list.add(mock(Future.class));
        list.add(mock(Future.class));
        list.add(mock(Future.class));

        new TaskExecutionHandle(list, System.nanoTime()).waitForAllTasks();

        for (Future future : list) {
            verify(future, times(1)).get();
        }
    }


    @Test
    public void testGetTaskOutput() throws ExecutionException, InterruptedException {

        List<Future> list = new ArrayList<Future>();
        list.add(mock(Future.class));
        list.add(mock(Future.class));
        list.add(mock(Future.class));

        List<Object> expectedOutList = new ArrayList<Object>();
        expectedOutList.add(new Object());
        expectedOutList.add(new Object());
        expectedOutList.add(new Object());

        int index = 0;
        for (Future future : list) {
            when(future.get()).thenReturn(expectedOutList.get(index++));
        }

        List<Object> outList = new TaskExecutionHandle(list, System.nanoTime()).getTaskOutput();

        for (Future future : list) {
            verify(future, times(1)).get();
        }

        assertEquals(outList, expectedOutList);
    }

    @Test
    public void testWaitForNTask() throws ExecutionException, InterruptedException {

        List<Future> list = new ArrayList<Future>();
        list.add(spy(getFuture(1)));
        list.add(spy(getFuture(2)));
        list.add(spy(getFuture(3)));

        long startTime = System.nanoTime();

        new TaskExecutionHandle(list, startTime).waitForNTasks(2);

        verify(list.get(0), times(1)).get();
        verify(list.get(1), times(1)).get();
        verify(list.get(2), times(0)).get();
    }

    private Future getFuture(final int multiple) {
        return new Future() {
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;  
            }

            public boolean isCancelled() {
                return false;  
            }

            public boolean isDone() {
                return false;  
            }

            public Object get() throws InterruptedException, ExecutionException {
                Thread.sleep(1000 * multiple);
                return null;  
            }

            public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                Thread.sleep(1000 * multiple);
                return null;  
            }
        };
    }
}
