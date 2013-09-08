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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class MultithreadedFunctionWithEmptyInputSpec {

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
    public void testMapFunctionForListWithThreads() {

        List<String> list = new LinkedList<String>();

        List<Character> list1 = FunctionUtil.map(list,
                new Function<String, Character>() {

                    public Character apply(String input) {
                        try {
                            Thread.currentThread().sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return input.charAt(0);
                    }
                }, FunctionUtil.parallel(3));


    }

    @Test
    public void testFilterFunctionForListWithThreads() {

        List<String> list = new LinkedList<String>();

        List<String> list1 = FunctionUtil.filter(list,
                new Function<String, Boolean>() {

                    public Boolean apply(String t) {
                        return t.contains("y");
                    }

                }, FunctionUtil.parallel(3));

    }

    @Test
    public void testFilterFunctionForSetWithThreads() {

        Set<String> set = new HashSet<String>();

        Set<String> set1 = FunctionUtil.filter(set,
                new Function<String, Boolean>() {

                    public Boolean apply(String t) {
                        return t.contains("y");
                    }

                }, FunctionUtil.parallel(3));

    }

    @Test
    public void testEachFunctionWithThreads() {

        List<Integer> list = new LinkedList<Integer>();

        final List<Integer> temp = new CopyOnWriteArrayList<Integer>();

        FunctionUtil.each(list, new RecordProcessor<Integer>() {
            public void process(Integer item) {
                temp.add(item);
            }
        }, FunctionUtil.parallel(5));

    }

    @Test
    public void testEachWithContextFunctionWithThreads() {

        List<Integer> list = new LinkedList<Integer>();

        final List<Integer> temp = new CopyOnWriteArrayList<Integer>();

        FunctionUtil.each(list, new RecordWithContextProcessor<Integer>() {
            public void process(Integer item, ParallelLoopExecutionContext context) {
                temp.add(item);
            }
        }, FunctionUtil.parallel(5));

    }

    @Test
    public void testReduceWithNoOfThreads() {

        List<Integer> list = new LinkedList<Integer>();

        Integer reduceOutput = FunctionUtil.reduce(list, new Accumulator<Integer, Integer>() {

            public Integer accumulate(Integer accumulator,
                                      Integer element) {
                return accumulator + element;
            }

        }, FunctionUtil.parallel(3));

    }


    @Test
    public void testDivideAndConquerWithBatch() {
        List<Integer> list = new LinkedList<Integer>();

        Task<Collection<Integer>> mockTask = mock(Task.class);

        FunctionUtil.divideAndConquer(list, mockTask, FunctionUtil.batch(2));

    }

    @Test
    public void testDivideAndConquerWithPartitions() {
        List<Integer> list = new LinkedList<Integer>();

        Task<Collection<Integer>> mockTask = mock(Task.class);

        FunctionUtil.divideAndConquer(list, mockTask, FunctionUtil.parallel(3));

    }


    @Test
    public void testEveryFunction() {

        List<String> list = new LinkedList<String>();

        boolean bool = FunctionUtil.every(list, new Function<String, Boolean>() {

            public Boolean apply(String string) {
                return string.contains("v");
            }
        }, FunctionUtil.parallel(3));

        assertFalse(bool);

    }

    @Test
    public void testAnyFunction() {

        List<String> list = new LinkedList<String>();

        boolean bool = FunctionUtil.any(list, new Function<String, Boolean>() {

            public Boolean apply(String string) {
                return string.contains("v");
            }
        }, FunctionUtil.parallel(3));

        assertFalse(bool);

    }


    @Test
    public void testCountFunction() {

        List<String> list = new LinkedList<String>();

        int count = FunctionUtil.count(list, new Function<String, Boolean>() {

            public Boolean apply(String string) {
                return string.contains("v");
            }
        }, FunctionUtil.parallel(3));

        assertEquals(count, 0);
    }
}
