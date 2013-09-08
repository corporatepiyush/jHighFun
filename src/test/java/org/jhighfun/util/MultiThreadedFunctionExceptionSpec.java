package org.jhighfun.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;


@RunWith(MockitoJUnitRunner.class)
public class MultiThreadedFunctionExceptionSpec {

    @Test(expected = RuntimeException.class)
    public void testMapFunctionForListWithThreads() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 100; i++) {
            list.add("India");
            list.add("ndia");
            list.add("dia");
            list.add("ia");
            list.add("a");
        }

        FunctionUtil.map(list,
                new Function<String, Character>() {

                    public Character apply(String input) {
                        try {
                            Thread.currentThread().sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (input.equals("a"))
                            throw new RuntimeException();

                        return input.charAt(0);
                    }
                }, FunctionUtil.parallel(3));

    }

    @Test(expected = RuntimeException.class)
    public void testFilterFunctionForListWithThreads() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 1000; i++) {
            list.add("Scala");
            list.add("Ruby");
        }

        FunctionUtil.filter(list,
                new Function<String, Boolean>() {

                    public Boolean apply(String t) {
                        if (t.equals("Ruby")) throw new RuntimeException();
                        return t.contains("y");
                    }

                }, FunctionUtil.parallel(3));

    }

    @Test(expected = RuntimeException.class)
    public void testFilterFunctionForSetWithThreads() {

        Set<String> set = new HashSet<String>();
        set.add("Scala");
        set.add("Ruby");

        FunctionUtil.filter(set,
                new Function<String, Boolean>() {

                    public Boolean apply(String t) {
                        if (t.equals("Ruby")) throw new RuntimeException();
                        return t.contains("y");
                    }

                }, FunctionUtil.parallel(3));

    }

    @Test(expected = RuntimeException.class)
    public void testEachFunctionWithThreads() {

        List<Integer> list = new LinkedList<Integer>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }

        final List<Integer> temp = new CopyOnWriteArrayList<Integer>();

        FunctionUtil.each(list, new RecordProcessor<Integer>() {
            public void process(Integer item) {
                if (item.equals(50)) throw new RuntimeException();
                temp.add(item);
            }
        }, FunctionUtil.parallel(5));

    }

    @Test(expected = RuntimeException.class)
    public void testEachWithContextFunctionWithThreads() {

        List<Integer> list = new LinkedList<Integer>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }

        final List<Integer> temp = new CopyOnWriteArrayList<Integer>();

        FunctionUtil.each(list, new RecordWithContextProcessor<Integer>() {
            public void process(Integer item, ParallelLoopExecutionContext context) {
                if (item.equals(50)) throw new RuntimeException();
                temp.add(item);
            }
        }, FunctionUtil.parallel(5));

    }


    @Test(expected = RuntimeException.class)
    public void testReduceWithNoOfThreads() {

        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        FunctionUtil.reduce(list, new Accumulator<Integer, Integer>() {

            public Integer accumulate(Integer accumulator,
                                      Integer element) {
                if (element.equals(5)) throw new RuntimeException();
                return accumulator + element;
            }

        }, FunctionUtil.parallel(3));

    }

    @Test(expected = RuntimeException.class)
    public void testEveryFunction() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 10000; i++) {
            list.add("Scala");
            list.add("Java");
        }

        boolean bool = FunctionUtil.every(list, new Function<String, Boolean>() {

            public Boolean apply(String string) {
                if (1 == 1)
                    throw new RuntimeException();
                return string.contains("v");
            }
        }, FunctionUtil.parallel(3));

    }

    @Test(expected = RuntimeException.class)
    public void testAnyFunction() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 10000; i++) {
            list.add("Scala");
            list.add("Java");
        }

        boolean bool = FunctionUtil.any(list, new Function<String, Boolean>() {

            public Boolean apply(String string) {
                if (1 == 1)
                    throw new RuntimeException();
                return string.contains("v");
            }
        }, FunctionUtil.parallel(3));

    }

    @Test(expected = RuntimeException.class)
    public void testCountFunction() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 10000; i++) {
            list.add("Scala");
            list.add("Java");
        }

        int count = FunctionUtil.count(list, new Function<String, Boolean>() {

            public Boolean apply(String string) {
                if (1 == 1)
                    throw new RuntimeException();
                return string.contains("v");
            }
        }, FunctionUtil.parallel(3));

    }

}
