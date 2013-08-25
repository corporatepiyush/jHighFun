package org.jhighfun.util;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import support.Person;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;

import static org.jhighfun.util.CollectionUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class FunctionUtilSpec {

    @Spy
    ExecutorService spyHighPriorityAsyncTaskThreadPool = new ThreadPoolExecutor(0, 100, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @Spy
    ExecutorService spyLowPriorityAsyncTaskThreadPool = new ThreadPoolExecutor(0, 5, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @Before
    public void before() {
        try {

            Field globalPool = FunctionUtil.class.getDeclaredField("highPriorityTaskThreadPool");
            globalPool.setAccessible(true);
            globalPool.set(null, spyHighPriorityAsyncTaskThreadPool);

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

        CollectionFunctionChain<String> chain = FunctionUtil.chain(CollectionUtil.FlattenList(set));

        Collection<String> expected = chain.extract();

        assertEquals(CollectionUtil.FlattenList(set), expected);
    }

    @Test
    public void testCurry() {

        CurriedFunction<Integer, Integer> addToFive = FunctionUtil.curry(new Function<List<Integer>, Integer>() {
            public Integer apply(List<Integer> integers) {
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

        CurriedFunction<Integer, Integer> addToZero = FunctionUtil.curry(new Function<List<Integer>, Integer>() {
            public Integer apply(List<Integer> integers) {
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
    public void testExecuteAsync() throws ExecutionException, InterruptedException {

        Runnable mockBlock = mock(Runnable.class);

        Future future = FunctionUtil.executeAsync(mockBlock);

        future.get();

        verify(mockBlock, times(1)).run();
        verify(spyHighPriorityAsyncTaskThreadPool, times(1)).submit(any(Runnable.class));
    }


    @Test
    public void testExecuteAsyncWithFuture() throws Exception {

        Callable<String> asyncTaskSpy = spy(new Callable<String>() {

            public String call() {
                return "Completed";
            }
        });

        Future<String> future = FunctionUtil.executeAsync(asyncTaskSpy);
        future.get().equals("Completed");

        verify(asyncTaskSpy, times(1)).call();
        verify(spyHighPriorityAsyncTaskThreadPool, times(1)).submit(any(Callable.class));
    }


    @Test
    public void testExecuteAsyncWithCallback() throws Exception {

        Callable<String> asyncTaskSpy = spy(new Callable<String>() {
            public String call() {
                return "output";
            }
        });
        CallbackTask callbackTaskMock = mock(CallbackTask.class);

        FunctionUtil.executeAsync(asyncTaskSpy, callbackTaskMock);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArgumentCaptor<AsyncTaskHandle> argument = ArgumentCaptor.forClass(AsyncTaskHandle.class);

        verify(asyncTaskSpy, times(1)).call();
        verify(callbackTaskMock, times(1)).execute(argument.capture());
        assertEquals(argument.getValue().getAsyncTask(), asyncTaskSpy);
        assertEquals(argument.getValue().getOutput(), "output");
        assertEquals(argument.getValue().getException(), null);
        verify(spyHighPriorityAsyncTaskThreadPool, times(1)).submit(any(Runnable.class));

    }


    @Test
    public void testExecuteAsyncWithCallbackWithException() throws Exception {

        Callable<String> asyncTaskSpy = spy(new Callable<String>() {
            public String call() {
                if (1 < 2) throw new RuntimeException();
                return "output";
            }
        });
        CallbackTask callbackTaskMock = mock(CallbackTask.class);

        FunctionUtil.executeAsync(asyncTaskSpy, callbackTaskMock);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArgumentCaptor<AsyncTaskHandle> argument = ArgumentCaptor.forClass(AsyncTaskHandle.class);

        verify(asyncTaskSpy, times(1)).call();
        verify(callbackTaskMock, times(1)).execute(argument.capture());
        assertEquals(argument.getValue().getAsyncTask(), asyncTaskSpy);
        assertEquals(argument.getValue().getOutput(), null);
        assertEquals(argument.getValue().getException().getClass(), RuntimeException.class);
        verify(spyHighPriorityAsyncTaskThreadPool, times(1)).submit(any(Runnable.class));

    }

    @Test
    public void testExecuteLater() {

        Runnable mockBlock = mock(Runnable.class);

        FunctionUtil.executeLater(mockBlock);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(mockBlock, times(1)).run();
        verify(spyLowPriorityAsyncTaskThreadPool, times(1)).submit(any(Runnable.class));
    }

    @Test
    public void testExecuteLaterWithCallback() throws Exception {

        Callable<String> asyncTaskSpy = spy(new Callable<String>() {
            public String call() {
                return "output";
            }
        });
        CallbackTask callbackTaskMock = mock(CallbackTask.class);

        FunctionUtil.executeLater(asyncTaskSpy, callbackTaskMock);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArgumentCaptor<AsyncTaskHandle> argument = ArgumentCaptor.forClass(AsyncTaskHandle.class);

        verify(asyncTaskSpy, times(1)).call();
        verify(callbackTaskMock, times(1)).execute(argument.capture());
        assertEquals(argument.getValue().getAsyncTask(), asyncTaskSpy);
        assertEquals(argument.getValue().getOutput(), "output");
        assertEquals(argument.getValue().getException(), null);
        verify(spyLowPriorityAsyncTaskThreadPool, times(1)).submit(any(Runnable.class));

    }


    @Test
    public void testExecuteLaterWithCallbackWithException() throws Exception {

        Callable<String> asyncTaskSpy = spy(new Callable<String>() {
            public String call() {
                if (1 < 2) throw new RuntimeException();
                return "output";
            }
        });
        CallbackTask callbackTaskMock = mock(CallbackTask.class);

        FunctionUtil.executeLater(asyncTaskSpy, callbackTaskMock);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArgumentCaptor<AsyncTaskHandle> argument = ArgumentCaptor.forClass(AsyncTaskHandle.class);

        verify(asyncTaskSpy, times(1)).call();
        verify(callbackTaskMock, times(1)).execute(argument.capture());
        assertEquals(argument.getValue().getAsyncTask(), asyncTaskSpy);
        assertEquals(argument.getValue().getOutput(), null);
        assertEquals(argument.getValue().getException().getClass(), RuntimeException.class);
        verify(spyLowPriorityAsyncTaskThreadPool, times(1)).submit(any(Runnable.class));

    }


    @Test
    public void testExecuteWithLockWithSingleThread() {

        Block mockBlock = mock(Block.class);

        FunctionUtil.executeWithLock(FunctionUtil.operation("Operation"), mockBlock);

        verify(mockBlock, times(1)).execute();
    }


    @Test
    public void testExecuteWithLockWithMultipleThreads() {

        final List<Block> blockList = new LinkedList<Block>();

        for (int i = 0; i < 1000; i++) {
            blockList.add(spy(new Block() {
                public void execute() {
                    for (Block block : blockList) ;
                }
            }));
        }

        // should not throw concurrent modification exception
        FunctionUtil.each(blockList, new RecordProcessor<Block>() {
            public void process(Block block) {
                FunctionUtil.executeWithLock(FunctionUtil.operation("Operation"), block);
            }
        }, FunctionUtil.parallel(blockList.size()));

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
                new Function<String, Character>() {

                    public Character apply(String input) {
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
    public void testMapFunctionForIterable() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 100; i++) {
            list.add("India");
            list.add("ndia");
            list.add("dia");
            list.add("ia");
            list.add("a");
        }

        Iterable<Character> list1 = FunctionUtil.map((Iterable) list,
                new Function<String, Character>() {

                    public Character apply(String input) {
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
                new Function<String, Boolean>() {

                    public Boolean apply(String t) {
                        return t.contains("y");
                    }

                });

        assertTrue(list1.size() == (list.size() / 2));

        for (String string : list1) {
            assertTrue(string.equals("Ruby"));
        }

    }


    @Test
    public void testFilterFunctionForIterable() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 1000; i++) {
            list.add("Scala");
            list.add("Ruby");
        }

        Iterable<String> list1 = FunctionUtil.filter((Iterable) list,
                new Function<String, Boolean>() {

                    public Boolean apply(String t) {
                        return t.contains("y");
                    }

                });


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
                new Function<String, Boolean>() {

                    public Boolean apply(String t) {
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

        boolean bool = FunctionUtil.every(list, new Function<String, Boolean>() {

            public Boolean apply(String string) {
                return string.contains("v");
            }
        });

        assertTrue(!bool);

        bool = FunctionUtil.every(list, new Function<String, Boolean>() {

            public Boolean apply(String string) {
                return string.contains("a");
            }
        });

        assertTrue(bool);
    }

    @Test
    public void testAnyFunction() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 10; i++) {
            list.add("Scala");
            list.add("Java");
        }

        boolean bool = FunctionUtil.any(list, new Function<String, Boolean>() {

            public Boolean apply(String string) {
                return string.contains("R");
            }
        });

        assertTrue(!bool);

        bool = FunctionUtil.any(list, new Function<String, Boolean>() {

            public Boolean apply(String string) {
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

        int count = FunctionUtil.count(set, new Function<String, Boolean>() {
            public Boolean apply(String s) {
                return s.contains("Scala");
            }
        });

        assertEquals(count, 1);
    }

    @Test
    public void testPartition() {

        Set<String> set = new HashSet<String>();
        set.add("Scala");
        set.add("Java");


        Tuple2<List<String>, List<String>> tuple2 = FunctionUtil.partition(set, new Function<String, Boolean>() {
            public Boolean apply(String s) {
                return s.contains("Scala");
            }
        });
        assertEquals(tuple2._1.toString(), "[Scala]");
        assertEquals(tuple2._2.toString(), "[Java]");
    }

    @Test
    public void testCrossProduct() {

        Iterable<Integer> integers = LazyIntRange(1, 100);

        Iterable<Long> longs = LazyLongRange(101, 200);

        Iterable<String> product = FunctionUtil.crossProduct(integers, longs, new Function<Tuple2<Integer, Long>, String>() {

            public String apply(Tuple2<Integer, Long> tuple) {
                return tuple.toString();
            }
        });

        Iterator<String> iterator = product.iterator();

        for (Integer integer : integers) {
            for (Long lon : longs) {
                assertEquals(iterator.next(), new Tuple2<Integer, Long>(integer, lon).toString());
            }
        }

    }

    @Test
    public void testMerge() {

        List<String> lang = new LinkedList<String>();
        lang.add("Scala");
        lang.add("Java");
        lang.add("C++");


        List<String> author = new LinkedList<String>();
        author.add("Martin");
        author.add("James");
        author.add("Dennis");

        Collection<Tuple2<String, String>> mergeOutput = FunctionUtil.zip(lang, author);

        Collection<Tuple2<String, String>> expected = List(tuple("Scala", "Martin"), tuple("Java", "James"), tuple("C++", "Dennis"));

        assertEquals(mergeOutput.toString(), expected.toString());

    }

    @Test
    public void testFork() {
        String object = "someObject";

        assertEquals(FunctionUtil.fork(object).getClass(), ForkAndJoin.class);
    }

    @Test
    public void testWithIndex() {

        List<String> list = List("hello", "Mr.", "FirstName", "LastName");

        List<String> outList = FunctionUtil.filterWithIndex(list, new Function<Integer, Boolean>() {
            public Boolean apply(Integer index) {
                return index % 2 == 0;
            }
        });

        assertEquals(outList, List("hello", "FirstName"));

    }


    @Test
    public void testRegisterPool() throws NoSuchFieldException, IllegalAccessException {

        Field throttlerPoolMap = FunctionUtil.class.getDeclaredField("throttlerPoolMap");
        throttlerPoolMap.setAccessible(true);
        ConcurrentHashMap<ExecutionThrottler, ExecutorService> map = new ConcurrentHashMap<ExecutionThrottler, ExecutorService>(15, 0.9f, 32);
        throttlerPoolMap.set(null, map);

        String identity = "some operation";
        FunctionUtil.registerPool(FunctionUtil.throttler(identity), 10);

        assertTrue(map.size() == 1);
        assertTrue(map.containsKey(FunctionUtil.throttler(identity)));
        assertTrue(((ThreadPoolExecutor) map.get(FunctionUtil.throttler(identity))).getMaximumPoolSize() == 10);

    }

    @Test
    public void testExecuteAwait() throws InterruptedException {

        // what if task executes later

        Runnable codeBlockSpy = spy(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        long startTime = System.currentTimeMillis();

        FunctionUtil.executeAwait(codeBlockSpy, 100, TimeUnit.MILLISECONDS);

        System.out.print((System.currentTimeMillis() - startTime));
        assertTrue((System.currentTimeMillis() - startTime) < 200);
        Thread.sleep(100);
        verify(codeBlockSpy).run();

        // what if task executes earlier

        codeBlockSpy = spy(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        startTime = System.currentTimeMillis();

        FunctionUtil.executeAwait(codeBlockSpy, 200, TimeUnit.MILLISECONDS);

        assertTrue((System.currentTimeMillis() - startTime) < 200);
        verify(codeBlockSpy).run();

    }

    @Test(expected = TimeoutException.class)
    public void testExecuteWithTimeoutForMoreExecutionTime() throws TimeoutException {

        Runnable codeBlockSpy = spy(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        FunctionUtil.executeWithTimeout(codeBlockSpy, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testExecuteWithTimeoutForLessExecutionTime() throws TimeoutException {

        Runnable codeBlockSpy = spy(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        FunctionUtil.executeWithTimeout(codeBlockSpy, 200, TimeUnit.MILLISECONDS);
        assertTrue(true);
    }

    @Test
    public void testGroupBy() {

        List<String> list = List("a", "b", "a", "c", "c", "d");

        Map<Character, List<String>> results = FunctionUtil.groupBy(list, new Function<String, Character>() {

            @Override
            public Character apply(String arg) {
                return arg.charAt(0);
            }
        });


        assertEquals(results, Map(Entry('a', List("a", "a")), Entry('b', List("b")), Entry('c', List("c", "c")), Entry('d', List("d"))));
    }

}
