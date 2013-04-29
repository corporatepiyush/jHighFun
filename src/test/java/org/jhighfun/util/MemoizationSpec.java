package org.jhighfun.util;


import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

public class MemoizationSpec {

    @Test
    public void testMemoizeForCondition() {

        final List<String> spyInjection = new LinkedList<String>();
        final String inputCheckValue = "today";

        Predicate<String> memoizedFunction = FunctionUtil.memoize(new Predicate<String>() {

            public boolean evaluate(String input) {
                spyInjection.add(input);
                return input.equals("today") ? true : false;
            }
        });

        assertEquals(spyInjection.size(), 0);
        assertEquals(memoizedFunction.evaluate(inputCheckValue), true);
        assertEquals(spyInjection.size(), 1);

        for (int i = 0; i < 100; i++) {
            assertEquals(memoizedFunction.evaluate(inputCheckValue), true);
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
    public void testMemoizeForFunctionUnderLoad() {

        final List<String> spyInjection = new LinkedList<String>();

        final int load = 10000;

        Function<List<String>, String> spyFunction = spy(new Function<List<String>, String>() {

            public String apply(List<String> args) {
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

        FunctionUtil.each(loadList, new RecordProcessor<Integer>() {
            public void process(Integer item) {
                assertEquals(memoizedFunction.apply(CollectionUtil.List("I", "am", "the", "Almighty")), "IamtheAlmighty");
            }
        }, FunctionUtil.parallel(load));


        assertEquals(spyInjection.size(), 1);
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
