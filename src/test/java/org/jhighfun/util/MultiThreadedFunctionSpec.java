package org.jhighfun.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class MultiThreadedFunctionSpec {

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
    public void testMapFunctionForListWithThreads() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 100; i++) {
            list.add("India");
            list.add("ndia");
            list.add("dia");
            list.add("ia");
            list.add("a");
        }

        List<Character> list1 = FunctionUtil.map(list,
                new Converter<String, Character>() {

                    public Character convert(String input) {
                        try {
                            Thread.currentThread().sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return input.charAt(0);
                    }
                }, FunctionUtil.parallel(3));

        int i = 0;

        for (Character character : list1) {
            assertTrue(character.charValue() == list.get(i++).charAt(0));
        }

        verify(spyHighPriorityTaskThreadPool, times(2)).submit(any(Runnable.class));

    }

    @Test
    public void testFilterFunctionForListWithThreads() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 1000; i++) {
            list.add("Scala");
            list.add("Ruby");
        }

        List<String> list1 = FunctionUtil.filter(list,
                new Predicate<String>() {

                    public boolean evaluate(String t) {
                        return t.contains("y");
                    }

                }, FunctionUtil.parallel(3));

        assertTrue(list1.size() == (list.size() / 2));

        for (String string : list1) {
            assertTrue(string.equals("Ruby"));
        }

        verify(spyHighPriorityTaskThreadPool, times(2)).submit(any(Runnable.class));
    }

    @Test
    public void testFilterFunctionForSetWithThreads() {

        Set<String> set = new HashSet<String>();
        for (int i = 1; i <= 1000; i++) {
            set.add("Scala");
            set.add("Ruby");
        }

        Set<String> set1 = FunctionUtil.filter(set,
                new Predicate<String>() {

                    public boolean evaluate(String t) {
                        return t.contains("y");
                    }

                }, FunctionUtil.parallel(3));

        assertEquals(set1.size(), (set.size() / 2));

        for (String string : set1) {
            assertTrue(string.equals("Ruby"));
        }

        verify(spyHighPriorityTaskThreadPool, times(1)).submit(any(Runnable.class));
    }

    @Test
    public void testEachFunctionWithThreads() {

        List<Integer> list = new LinkedList<Integer>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }

        final List<Integer> temp = new CopyOnWriteArrayList<Integer>();

        FunctionUtil.each(list, new RecordProcessor<Integer>() {
            public void process(Integer item) {
                temp.add(item);
            }
        }, FunctionUtil.parallel(5));

        for (int i = 0; i < 1000; i++) {
            assertTrue(temp.contains(i));
        }

        verify(spyHighPriorityTaskThreadPool, times(4)).submit(any(Runnable.class));
    }

    @Test
    public void testReduceWithNoOfThreads() {

        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        Integer reduceOutput = FunctionUtil.reduce(list, new Accumulator<Integer, Integer>() {

            public Integer accumulate(Integer accumulator,
                                      Integer element) {
                return accumulator + element;
            }

        }, FunctionUtil.parallel(3));

        assertTrue(reduceOutput == 10);
        verify(spyHighPriorityTaskThreadPool, times(2)).submit(any(Runnable.class));

    }


    @Test
    public void testDivideAndConquerWithBatch() {
        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        Task<Collection<Integer>> mockTask = mock(Task.class);

        FunctionUtil.divideAndConquer(list, FunctionUtil.batch(2), mockTask);

        Collection<Integer> chunk1 = new LinkedList<Integer>();
        chunk1.add(1);
        chunk1.add(2);

        verify(mockTask, times(1)).execute(chunk1);

        Collection<Integer> chunk2 = new LinkedList<Integer>();
        chunk2.add(3);
        chunk2.add(4);

        verify(mockTask, times(1)).execute(chunk2);
        verify(spyHighPriorityTaskThreadPool, times(1)).submit(any(Runnable.class));

    }

    @Test
    public void testDivideAndConquerWithPartitions() {
        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        Task<Collection<Integer>> mockTask = mock(Task.class);

        FunctionUtil.divideAndConquer(list, FunctionUtil.parallel(3), mockTask);

        Collection<Integer> partition1 = new LinkedList<Integer>();
        partition1.add(1);
        partition1.add(4);

        verify(mockTask, times(1)).execute(partition1);

        Collection<Integer> partition2 = new LinkedList<Integer>();
        partition2.add(2);
        partition2.add(5);

        verify(mockTask, times(1)).execute(partition2);

        Collection<Integer> partitions3 = new LinkedList<Integer>();
        partitions3.add(3);

        verify(mockTask, times(1)).execute(partitions3);

        verify(spyHighPriorityTaskThreadPool, times(2)).submit(any(Runnable.class));

    }

}
