package org.jhighfun.util;


import org.jhighfun.util.memoize.MemoizeConfig;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MemoizationSpec {

    @Test
    public void testMemoizeForCondition() {

        final List<String> spyInjection = new LinkedList<String>();
        final String inputCheckValue = "today";

        Function<String, Boolean> memoizedFunction = FunctionUtil.memoize(new Function<String, Boolean>() {

            public Boolean apply(String input) {
                spyInjection.add(input);
                return input.equals("today") ? true : false;
            }
        });

        assertEquals(spyInjection.size(), 0);
        assertEquals(memoizedFunction.apply(inputCheckValue), true);
        assertEquals(spyInjection.size(), 1);

        for (int i = 0; i < 100; i++) {
            assertEquals(memoizedFunction.apply(inputCheckValue), true);
            assertEquals(spyInjection.size(), 1);
        }
    }

    @Test
    public void testMemoizeForFunction() {

        final List<String> spyInjection = new LinkedList<String>();

        Function<List<String>, String> memoizedFunction = FunctionUtil.memoize(new Function<List<String>, String>() {

            public String apply(List<String> args) {
                spyInjection.add(args.toString());
                StringBuilder builder = new StringBuilder();
                for (String string : args) {
                    builder.append(string);
                }
                return builder.toString();
            }
        });

        assertEquals(spyInjection.size(), 0);
        assertEquals(memoizedFunction.apply(CollectionUtil.List("I", "am", "the", "Almighty")), "IamtheAlmighty");
        assertEquals(spyInjection.size(), 1);

        for (int i = 0; i < 100; i++) {
            assertEquals(memoizedFunction.apply(CollectionUtil.List("I", "am", "the", "Almighty")), "IamtheAlmighty");
            assertEquals(spyInjection.size(), 1);
        }
    }


    @Test
    public void testMemoizeForFunctionWithConfigForMaxPersistenceTime() throws InterruptedException {

        final List<String> spyInjection = new LinkedList<String>();

        Function<List<String>, String> memoizedFunction = FunctionUtil.memoize(new Function<List<String>, String>() {

            public String apply(List<String> args) {
                spyInjection.add(args.toString());
                StringBuilder builder = new StringBuilder();
                for (String string : args) {
                    builder.append(string);
                }
                return builder.toString();
            }
        }, new MemoizeConfig(100, TimeUnit.MILLISECONDS, 10));

        assertEquals(spyInjection.size(), 0);
        assertEquals(memoizedFunction.apply(CollectionUtil.List("I", "am", "the", "Almighty")), "IamtheAlmighty");
        assertEquals(spyInjection.size(), 1);

        final Long initialCachingTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            assertEquals(memoizedFunction.apply(CollectionUtil.List("I", "am", "the", "Almighty")), "IamtheAlmighty");
            assertEquals(spyInjection.size(), 1);
        }

        Thread.sleep(100 - (System.currentTimeMillis() - initialCachingTime));

        assertEquals(memoizedFunction.apply(CollectionUtil.List("I", "am", "the", "King")), "IamtheKing");
        assertEquals(spyInjection.size(), 2);
    }

    @Test
    public void testMemoizeForFunctionWithConfigForCacheSize() throws InterruptedException {

        Function<String, String> mockFunction = mock(Function.class);

        final int cacheSize = 100;

        Function<String, String> memoizedFunction = FunctionUtil.memoize(mockFunction, new MemoizeConfig(100, TimeUnit.MILLISECONDS, cacheSize));

        //set expectation
        for (int i = 1; i <= cacheSize; i++) {
            when(mockFunction.apply(String.valueOf(i))).thenReturn(String.valueOf(i));
        }

        when(mockFunction.apply(String.valueOf(1000))).thenReturn(String.valueOf(1000));

        //call explicitly
        for (int i = 1; i <= cacheSize; i++) {
            memoizedFunction.apply(String.valueOf(i));
        }

        verify(mockFunction, times(1)).apply(String.valueOf(1));

        memoizedFunction.apply(String.valueOf(1000));
        // wait for LRU to operate so that entry for "1" is removed
        Thread.sleep(100);

        //call again for "1"
        memoizedFunction.apply(String.valueOf(1));
        verify(mockFunction, times(2)).apply(String.valueOf(1));

    }


    @Test
    public void testMemoizeForFunctionWithManagedCache() throws InterruptedException {

        ManagedCache managedCache = mock(ManagedCache.class);

        Function<String, String> function = mock(Function.class);

        Function<String, String> memoizedFunction = FunctionUtil.memoize(function, managedCache);

        when(function.apply("input")).thenReturn("output");

        memoizedFunction.apply("input");
        verify(managedCache).put("input", "output");

        when(managedCache.get("input")).thenReturn("output");
        memoizedFunction.apply("input");
    }

    @Test
    public void testMemoizeForFunctionUnderHighLoadWhenInitialCallInProgress() {

        final List<String> spyInjection = new CopyOnWriteArrayList<String>();

        final int load = 1000;

        Function<List<String>, String> spyFunction = spy(new Function<List<String>, String>() {

            public String apply(List<String> args) {

                if (spyInjection.isEmpty()) {
                    spyInjection.add(args.toString());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                spyInjection.add(args.toString());
                StringBuilder builder = new StringBuilder();
                for (String string : args) {
                    builder.append(string);
                }
                return builder.toString();
            }
        });

        final Function<List<String>, String> memoizedFunction = FunctionUtil.memoize(spyFunction);

        List<Integer> loadList = new LinkedList<Integer>();

        for (int i = 0; i < load; i++) {
            loadList.add(i);
        }

        FunctionUtil.executeAsync(new Runnable() {
            public void run() {
                memoizedFunction.apply(CollectionUtil.List("I", "am", "the", "Almighty"));
            }
        });

        FunctionUtil.each(loadList, new RecordProcessor<Integer>() {
            public void process(Integer item) {
                assertEquals(memoizedFunction.apply(CollectionUtil.List("I", "am", "the", "Almighty")), "IamtheAlmighty");
            }
        }, FunctionUtil.parallel(load));


        assertEquals(spyInjection.size(), 2);
    }

    @Test
    public void testMemoizeForAccumulator() {

        final List<String> spyInjection = new LinkedList<String>();

        Accumulator<String, String> memoizedFunction = FunctionUtil.memoize(new Accumulator<String, String>() {

            public String accumulate(String accum, String element) {
                spyInjection.add(element);
                StringBuilder builder = new StringBuilder();
                builder.append(accum).append(element);
                return builder.toString();
            }
        });

        assertEquals(spyInjection.size(), 0);
        assertEquals(memoizedFunction.accumulate("Java", "Rocks!"), "JavaRocks!");
        assertEquals(spyInjection.size(), 1);

        for (int i = 0; i < 100; i++) {
            assertEquals(memoizedFunction.accumulate("Java", "Rocks!"), "JavaRocks!");
            assertEquals(spyInjection.size(), 1);
        }
    }
}
