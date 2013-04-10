package org.jhighfun.util;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import support.Person;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.jhighfun.util.CollectionUtil.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class FunctionUtilSpec {

    @Spy
    ExecutorService spyMediumPriorityAsyncTaskThreadPool = new ThreadPoolExecutor(0, 100, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @Spy
    ExecutorService spyLowPriorityAsyncTaskThreadPool = new ThreadPoolExecutor(0, 5, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @Before
    public void before() {
        try {

            Field globalPool = FunctionUtil.class.getDeclaredField("mediumPriorityAsyncTaskThreadPool");
            globalPool.setAccessible(true);
            globalPool.set(null, spyMediumPriorityAsyncTaskThreadPool);

            globalPool = FunctionUtil.class.getDeclaredField("lowPriorityAsyncTaskThreadPool");
            globalPool.setAccessible(true);
            globalPool.set(null, spyLowPriorityAsyncTaskThreadPool);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testChain() {

        Set<String> set = new HashSet<String>();
        set.add("Scala");
        set.add("Java");

        CollectionFunctionChain<String> chain = FunctionUtil.chain(set);

        Collection<String> expected = chain.extract();

        assertEquals(set, expected);
    }

    @Test
    public void testCurry() {

        CurriedFunction<Integer, Integer> addToFive = FunctionUtil.curry(new Function<Integer, Integer>() {
            public Integer execute(Collection<Integer> integers) {
                int sum = 0;
                for (Integer i : integers) {
                    sum = sum + i;
                }
                return sum;
            }
        }, List(5));

        assertTrue(addToFive.call(List(10)) == 15);
        assertTrue(addToFive.call(List(10, 15)) == 30);
        assertTrue(addToFive.call(10) == 15);
        assertTrue(addToFive.call(10, 15) == 30);

        CurriedFunction<Integer, Integer> addToZero = FunctionUtil.curry(new Function<Integer, Integer>() {
            public Integer execute(Collection<Integer> integers) {
                int sum = 0;
                for (Integer i : integers) {
                    sum = sum + i;
                }
                return sum;
            }
        }, 0, 0);

        assertTrue(addToZero.call(List(10)) == 10);
        assertTrue(addToZero.call(List(15)) == 15);
        assertTrue(addToZero.call(List(0)) == 0);
        assertTrue(addToZero.call(5) == 5);
        assertTrue(addToZero.call(5, 10) == 15);
    }

    @Test
    public void testMemoizeForConverter() {

        final List<String> spyInjection = new LinkedList<String>();
        final String inputCheckValue = "today";
        final Date outputCheckValue = new Date();

        Converter<String, Date> memoizedFunction = FunctionUtil.memoize(new Converter<String, Date>() {

            public Date convert(String input) {
                spyInjection.add(input);
                return input.equals("today") ? outputCheckValue : null;
            }
        });

        assertEquals(spyInjection.size(), 0);
        assertEquals(memoizedFunction.convert(inputCheckValue), outputCheckValue);
        assertEquals(spyInjection.size(), 1);

        assertEquals(memoizedFunction.convert(inputCheckValue), outputCheckValue);
        assertEquals(spyInjection.size(), 1);
    }

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

        assertEquals(memoizedFunction.evaluate(inputCheckValue), true);
        assertEquals(spyInjection.size(), 1);
    }

    @Test
    public void testMemoizeForFunction() {

        final List<String> spyInjection = new LinkedList<String>();

        Function<String, String> memoizedFunction = FunctionUtil.memoize(new Function<String, String>() {

            public String execute(Collection<String> args) {
                spyInjection.add(args.toString());
                StringBuilder builder = new StringBuilder();
                for (String string : args) {
                    builder.append(string);
                }
                return builder.toString();
            }
        });

        assertEquals(spyInjection.size(), 0);
        assertEquals(memoizedFunction.execute(List("I", "am", "the", "Almighty")), "IamtheAlmighty");
        assertEquals(spyInjection.size(), 1);

        assertEquals(memoizedFunction.execute(List("I", "am", "the", "Almighty")), "IamtheAlmighty");
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

        assertEquals(memoizedFunction.accumulate("Java", "Rocks!"), "JavaRocks!");
        assertEquals(spyInjection.size(), 1);
    }

    @Test
    public void testExecuteAsync() {

        Block mockBlock = mock(Block.class);

        FunctionUtil.executeAsync(mockBlock);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(mockBlock, times(1)).execute();
        verify(spyMediumPriorityAsyncTaskThreadPool, times(1)).submit(any(Runnable.class));
    }

    @Test
    public void testExecuteLater() {

        Block mockBlock = mock(Block.class);

        FunctionUtil.executeLater(mockBlock);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(mockBlock, times(1)).execute();
        verify(spyLowPriorityAsyncTaskThreadPool, times(1)).submit(any(Runnable.class));
    }


    @Test
    public void testExecuteWithGlobalLockWithSingleThread() {

        Block mockBlock = mock(Block.class);

        FunctionUtil.executeWithGlobalLock(mockBlock);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(mockBlock, times(1)).execute();
    }

    @Test
    public void testExecuteWithGlobalLockWithMultipleThread() {

        final List<Integer> list = new LinkedList<Integer>();

        List<Integer> load = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        for (int i = 0; i < 10000; list.add(i), i++) ;

        final Block spyBlock = spy(new Block() {
            public void execute() {
                list.add(1);
                for (Integer i : list) ;
                list.add(2);
            }
        });

        FunctionUtil.each(load, new RecordProcessor<Integer>() {
            public void process(Integer item) {
                FunctionUtil.executeWithGlobalLock(spyBlock);
            }
        }, FunctionUtil.parallel(10));

        verify(spyBlock, times(10)).execute();

    }


    @Test
    public void testMapFunctionForList() {

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
                });

        int i = 0;
        for (Character character : list1) {
            assertTrue(character.charValue() == list.get(i++).charAt(0));
        }
    }

    @Test
    public void testFilterFunctionForList() {

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

                });

        assertTrue(list1.size() == (list.size() / 2));

        for (String string : list1) {
            assertTrue(string.equals("Ruby"));
        }

    }

    @Test
    public void testFilterFunctionForSet() {

        Set<String> set = new HashSet<String>();
        for (int i = 1; i <= 1000; i++) {
            set.add("Scala");
            set.add("Ruby");
        }

        Set<String> list1 = FunctionUtil.filter(set,
                new Predicate<String>() {

                    public boolean evaluate(String t) {
                        return t.contains("y");
                    }

                });

        assertTrue(list1.size() == (set.size() / 2));

        for (String string : list1) {
            assertTrue(string.equals("Ruby"));
        }

    }

    @Test
    public void testFoldLeftForStringAppend() {

        List<String> list = new LinkedList<String>();
        list.add("Java");
        list.add(" ");
        list.add("Rocks");
        list.add("!");

        StringBuilder stringBuilder = new StringBuilder();

        StringBuilder foldLeft = FunctionUtil.foldLeft(list, stringBuilder,
                new Accumulator<StringBuilder, String>() {

                    public StringBuilder accumulate(StringBuilder accumulator,
                                                    String element) {
                        return accumulator.append(element);
                    }

                });

        assertTrue(foldLeft.toString().equals("Java Rocks!"));

    }

    @Test
    public void testFoldLeftForAdditionOFIntegers() {

        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        Integer foldLeft = FunctionUtil.foldLeft(list, 0,
                new Accumulator<Integer, Integer>() {

                    public Integer accumulate(Integer accumulator,
                                              Integer element) {
                        return accumulator + element;
                    }

                });

        assertTrue(foldLeft == 10);

    }

    @Test
    public void testFoldRightForStringAppend() {

        List<String> list = new LinkedList<String>();
        list.add("Java");
        list.add(" ");
        list.add("Rocks");
        list.add("!");

        StringBuilder stringBuilder = new StringBuilder();

        StringBuilder foldRight = FunctionUtil.foldRight(list, stringBuilder,
                new Accumulator<StringBuilder, String>() {

                    public StringBuilder accumulate(StringBuilder accumulator,
                                                    String element) {
                        return accumulator.append(element);
                    }

                });

        System.out.println(foldRight);

        assertTrue(foldRight.toString().equals("!Rocks Java"));

    }

    @Test
    public void testFoldRightForAdditionOFIntegers() {

        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        Integer foldRight = FunctionUtil.foldRight(list, 0,
                new Accumulator<Integer, Integer>() {

                    public Integer accumulate(Integer accumulator,
                                              Integer element) {
                        return accumulator + element;
                    }

                });

        assertTrue(foldRight == 10);

    }

    @Test
    public void testForSortWithForList() {
        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(4);
        list.add(2);
        list.add(3);

        assertTrue(FunctionUtil.sortWith(list, new Comparator<Integer>() {

            public int compare(Integer t1, Integer t2) {
                return t1 - t2;
            }
        }).toString().equals("[1, 2, 3, 4]"));

    }

    @Test
    public void testForSortWithForSet() {
        Set<Integer> set = new HashSet<Integer>();
        set.add(1);
        set.add(4);
        set.add(2);
        set.add(3);

        assertTrue(FunctionUtil.sortWith(set, new Comparator<Integer>() {

            public int compare(Integer t1, Integer t2) {
                return t1 - t2;
            }
        }).toString().equals("[1, 2, 3, 4]"));

    }

    @Test
    public void testForSort() {
        Set<Integer> set = new HashSet<Integer>();
        set.add(1);
        set.add(4);
        set.add(2);
        set.add(3);

        assertTrue(FunctionUtil.sort(set).toString().equals("[1, 2, 3, 4]"));

    }

    @Test
    public void testForSortBy() {
        Person joe = new Person("Joe", 10000, 34);
        Person amanda = new Person("Amanda", 70000, 24);
        Person chloe = new Person("Chloe", 10000, 30);

        List<Person> inputList = new LinkedList<Person>();
        inputList.add(joe);
        inputList.add(amanda);
        inputList.add(chloe);

        //---sort by age

        List<Person> expectedList = new LinkedList<Person>();
        expectedList.add(amanda);
        expectedList.add(chloe);
        expectedList.add(joe);

        assertEquals(FunctionUtil.sortBy(inputList, "age"), expectedList);

        //---sort by salary, name

        expectedList = new LinkedList<Person>();
        expectedList.add(chloe);
        expectedList.add(joe);
        expectedList.add(amanda);

        assertEquals(FunctionUtil.sortBy(inputList, "salary", "firstName"), expectedList);

    }


    @Test
    public void testEveryFunction() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 10; i++) {
            list.add("Scala");
            list.add("Java");
        }

        boolean bool = FunctionUtil.every(list, new Predicate<String>() {

            public boolean evaluate(String string) {
                return string.contains("v");
            }
        });

        assertTrue(!bool);

        bool = FunctionUtil.every(list, new Predicate<String>() {

            public boolean evaluate(String string) {
                return string.contains("a");
            }
        });

        assertTrue(bool);
    }

    @Test
    public void testSomeFunction() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 10; i++) {
            list.add("Scala");
            list.add("Java");
        }

        boolean bool = FunctionUtil.any(list, new Predicate<String>() {

            public boolean evaluate(String string) {
                return string.contains("R");
            }
        });

        assertTrue(!bool);

        bool = FunctionUtil.any(list, new Predicate<String>() {

            public boolean evaluate(String string) {
                return string.contains("a");
            }
        });

        assertTrue(bool);
    }

    @Test
    public void testReduce() {

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

        });

        assertTrue(reduceOutput == 10);

    }

    @Test
    public void testEachFunction() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");

        final List<String> temp = new LinkedList<String>();

        FunctionUtil.each(list, new RecordProcessor<String>() {
            public void process(String item) {
                temp.add(item);
            }
        });

        assertEquals(list, temp);
    }

    @Test
    public void testEachWithIndexFunction() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");

        final Map<Integer, String> temp = new HashMap<Integer, String>();

        FunctionUtil.eachWithIndex(list, new RecordWithIndexProcessor<String>() {
            public void process(String item, int index) {
                temp.put(index, item);
            }
        });

        final Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(0, "Scala");
        expected.put(1, "Java");

        assertEquals(expected, temp);
    }

    @Test
    public void testEachFunctionForMap() {

        Map<String, String> map = new HashMap<String, String>();
        map.put("IN", "India");
        map.put("US", "United States");

        final Map<String, String> temp = new HashMap<String, String>();

        FunctionUtil.each(map, new KeyValueRecordProcessor<String, String>() {
            public void process(String key, String value) {
                temp.put(key, value);
            }
        });

        assertEquals(map, temp);
    }

    @Test
    public void testCount() {

        Set<String> set = new HashSet<String>();
        set.add("Scala");
        set.add("Java");

        final Set<String> temp = new HashSet<String>();

        int count = FunctionUtil.count(set, new Predicate<String>() {
            public boolean evaluate(String s) {
                return s.contains("Scala");
            }
        });

        assertEquals(count, 1);
    }

    @Test
    public void testSplit() {

        Set<String> set = new HashSet<String>();
        set.add("Scala");
        set.add("Java");

        final Set<String> temp = new HashSet<String>();

        Collection<Collection<String>> splits = FunctionUtil.split(set, new Predicate<String>() {
            public boolean evaluate(String s) {
                return s.contains("Scala");
            }
        });

        int i = 0;
        for (Collection<String> split : splits) {
            if (i == 0) {
                assertEquals(split.toString(), "[Scala]");
            } else {
                assertEquals(split.toString(), "[Java]");
            }
        }
    }

    @Test
    public void testFork(){
        String object = "someObject" ;

        assertEquals(FunctionUtil.fork(object).getClass(), ForkAndJoin.class);
    }

}
