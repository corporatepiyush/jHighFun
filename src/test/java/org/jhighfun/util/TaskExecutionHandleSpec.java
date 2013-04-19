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
        list.add(spy(getFuture(1000)));
        list.add(spy(getFuture(2000)));
        list.add(spy(getFuture(3000)));

        long startTime = System.nanoTime();

        new TaskExecutionHandle(list, startTime).waitForNTasks(2);

        verify(list.get(0), times(1)).get();
        verify(list.get(1), times(1)).get();
        verify(list.get(2), times(0)).get();
    }


    @Test
    public void testTotalTaskCompleted() throws ExecutionException, InterruptedException {

        List<Future> list = new ArrayList<Future>();
        list.add(spy(getFuture(1)));
        list.add(spy(getFuture(1)));
        list.add(spy(getFuture(1)));

        long startTime = System.nanoTime();

        TaskExecutionHandle handle = new TaskExecutionHandle(list, startTime);

        assertEquals(handle.totalTasksCompleted(), 0);
        list.get(0).get();
        assertEquals(handle.totalTasksCompleted(), 1);
        list.get(1).get();
        assertEquals(handle.totalTasksCompleted(), 2);
        list.get(2).get();
        assertEquals(handle.totalTasksCompleted(), 3);

        verify(list.get(0), times(4)).isDone();
        verify(list.get(1), times(4)).isDone();
        verify(list.get(2), times(4)).isDone();
    }

    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private Future getFuture(final int millis) {
        return new Future() {
            private Boolean isDone = false;

            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            public boolean isCancelled() {
                return false;
            }

            public boolean isDone() {
                return isDone;
            }

            public Object get() throws InterruptedException, ExecutionException {
                Thread.sleep(millis);
                isDone = true;
                return null;
            }

            public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                Thread.sleep(millis);
                isDone = true;
                return null;
            }
        };
    }
}
