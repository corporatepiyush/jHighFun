package org.highfun.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class MultiThreadedFunctionSpec {

    @Spy
    ExecutorService testThreadPool = new ThreadPoolExecutor(1,100,1, TimeUnit.SECONDS,new SynchronousQueue<Runnable>());

    @Before
    public void before(){
        try {
            Field globalPool = FunctionUtil.class.getDeclaredField("globalPool");
            globalPool.setAccessible(true);
            globalPool.set(null, testThreadPool);
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
                }, 3);

        int i = 0;

        for (Character character : list1) {
            assertTrue(character.charValue() == list.get(i++).charAt(0));
        }

        verify(testThreadPool, times(2)).submit(any(Runnable.class));

    }

    @Test
    public void testFilterFunctionForListWithThreads() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 1000; i++) {
            list.add("Scala");
            list.add("Ruby");
        }

        List<String> list1 = FunctionUtil.filter(list,
                new Condition<String>() {

                    public boolean evaluate(String t) {
                        return t.contains("y");
                    }

                }, 3);

        assertTrue(list1.size() == (list.size() / 2));

        for (String string : list1) {
            assertTrue(string.equals("Ruby"));
        }

        verify(testThreadPool, times(2)).submit(any(Runnable.class));
    }

    @Test
    public void testFilterFunctionForSetWithThreads() {

        Set<String> set = new HashSet<String>();
        for (int i = 1; i <= 1000; i++) {
            set.add("Scala");
            set.add("Ruby");
        }

        Set<String> set1 = FunctionUtil.filter(set,
                new Condition<String>() {

                    public boolean evaluate(String t) {
                        return t.contains("y");
                    }

                }, 3);

        assertEquals(set1.size(), (set.size() / 2));

        for (String string : set1) {
            assertTrue(string.equals("Ruby"));
        }

        verify(testThreadPool, times(1)).submit(any(Runnable.class));
    }

    @Test
    public void testEachFunctionWithThreads() {

        List<Integer> list = new LinkedList<Integer>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }

        final List<Integer> temp = new CopyOnWriteArrayList<Integer>();

        FunctionUtil.each(list, new ItemRecord<Integer>() {
            public void process(Integer item) {
                temp.add(item);
            }
        }, 5);

        for (int i = 0; i < 1000; i++) {
            assertTrue(temp.contains(i));
        }

        verify(testThreadPool, times(4)).submit(any(Runnable.class));
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

        }, 3);

        assertTrue(reduceOutput == 10);
        verify(testThreadPool, times(2)).submit(any(Runnable.class));

    }
}
