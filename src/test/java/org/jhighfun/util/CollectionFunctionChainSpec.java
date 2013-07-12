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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CollectionFunctionChainSpec {

    @Spy
    ExecutorService spyHighPriorityTaskThreadPool = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 1, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

    @Spy
    ExecutorService spyMediumPriorityAsyncTaskThreadPool = new ThreadPoolExecutor(0, 100, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @Spy
    ExecutorService spyLowPriorityAsyncTaskThreadPool = new ThreadPoolExecutor(0, 5, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());


    @Before
    public void before() {
        try {
            Field globalPool = FunctionUtil.class.getDeclaredField("highPriorityTaskThreadPool");
            globalPool.setAccessible(true);
            globalPool.set(null, spyHighPriorityTaskThreadPool);

            globalPool = FunctionUtil.class.getDeclaredField("mediumPriorityAsyncTaskThreadPool");
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
    public void testUnchain() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 100; i++) {
            list.add("India");
            list.add("ndia");
            list.add("dia");
            list.add("ia");
            list.add("a");
        }
        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        assertEquals(chain.extract(), list);
    }


    @Test
    public void testToObject() {
        Object object = new Object();
        CollectionFunctionChain<Object> chain = new CollectionFunctionChain<Object>(List(object));

        Function<List<Object>, Object> mockConverter = spy(new Function<List<Object>, Object>() {
            public Object apply(List<Object> input) {
                return input.get(0);
            }
        });
        assertEquals(chain.toObject(mockConverter).extract(), object);
        verify(mockConverter, times(1)).apply(List(object));

    }

    @Test
    public void testMap() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 100; i++) {
            list.add("India");
            list.add("ndia");
            list.add("dia");
            list.add("ia");
            list.add("a");
        }
        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        Collection<Character> mapList = chain.map(new Function<String, Character>() {
            public Character apply(String input) {
                return input.charAt(0);
            }
        }).extract();

        List<Character> expectedList = new LinkedList<Character>();
        for (int i = 1; i <= 100; i++) {
            expectedList.add('I');
            expectedList.add('n');
            expectedList.add('d');
            expectedList.add('i');
            expectedList.add('a');
        }

        assertEquals(mapList, expectedList);
    }

    @Test
    public void testMapWithThreads() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 100; i++) {
            list.add("India");
            list.add("ndia");
            list.add("dia");
            list.add("ia");
            list.add("a");
        }
        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        Collection<Character> mapList = chain.map(new Function<String, Character>() {
            public Character apply(String input) {
                return input.charAt(0);
            }
        }, FunctionUtil.parallel(3)).extract();

        List<Character> expectedList = new LinkedList<Character>();
        for (int i = 1; i <= 100; i++) {
            expectedList.add('I');
            expectedList.add('n');
            expectedList.add('d');
            expectedList.add('i');
            expectedList.add('a');
        }

        assertEquals(mapList, expectedList);
        verify(spyHighPriorityTaskThreadPool, times(2)).submit(any(Runnable.class));
    }

    @Test
    public void testFilter() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 1000; i++) {
            list.add("Scala");
            list.add("Ruby");
        }


        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        Collection<String> filterList = chain.filter(new Function<String, Boolean>() {

            public Boolean apply(String t) {
                return t.contains("y");
            }

        }).extract();

        assertTrue(filterList.size() == (list.size() / 2));

        for (String string : filterList) {
            assertTrue(string.equals("Ruby"));
        }

    }

    @Test
    public void testFilterWithThreads() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 1000; i++) {
            list.add("Scala");
            list.add("Ruby");
        }

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        Collection<String> filterList = chain.filter(new Function<String, Boolean>() {

            public Boolean apply(String t) {
                return t.contains("y");
            }

        }, FunctionUtil.parallel(3)).extract();

        assertEquals(filterList.size(), (list.size() / 2));

        for (String string : filterList) {
            assertTrue(string.equals("Ruby"));
        }

        verify(spyHighPriorityTaskThreadPool, times(2)).submit(any(Runnable.class));
    }

    @Test
    public void testSortWith() {
        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(4);
        list.add(2);
        list.add(3);

        CollectionFunctionChain<Integer> chain = new CollectionFunctionChain<Integer>(list);

        Collection<Integer> filterList = chain.sortWith(new Comparator<Integer>() {

            public int compare(Integer t1, Integer t2) {
                return t1 - t2;
            }
        }).extract();

        assertTrue(filterList.toString().equals("[1, 2, 3, 4]"));

    }

    @Test
    public void testForSort() {
        Set<Integer> set = new HashSet<Integer>();
        set.add(1);
        set.add(4);
        set.add(2);
        set.add(3);

        assertTrue(new CollectionFunctionChain<Integer>(CollectionUtil.FlattenList(set)).sort().extract().toString().equals("[1, 2, 3, 4]"));

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

        assertEquals(new CollectionFunctionChain<Person>(inputList).sortBy("age").extract(), expectedList);

        //---sort by salary, name

        expectedList = new LinkedList<Person>();
        expectedList.add(chloe);
        expectedList.add(joe);
        expectedList.add(amanda);

        assertEquals(new CollectionFunctionChain<Person>(inputList).sortBy("salary", "firstName").extract(), expectedList);

    }


    @Test
    public void testEach() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");

        final List<String> temp = new LinkedList<String>();

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        chain.each(new RecordProcessor<String>() {
            public void process(String item) {
                temp.add(item);
            }
        }).extract();

        assertEquals(temp, list);

    }

    @Test
    public void testEachWithIndexFunction() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");

        final Map<Integer, String> temp = new HashMap<Integer, String>();

        new CollectionFunctionChain<String>(list).eachWithIndex(new RecordWithIndexProcessor<String>() {
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
    public void testEachFunctionWithThreads() {

        List<Integer> list = new LinkedList<Integer>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }

        final List<Integer> temp = new CopyOnWriteArrayList<Integer>();

        CollectionFunctionChain<Integer> chain = new CollectionFunctionChain<Integer>(list);

        chain.each(new RecordProcessor<Integer>() {
            public void process(Integer item) {
                temp.add(item);
            }
        }, FunctionUtil.parallel(5)).extract();

        for (int i = 0; i < 1000; i++) {
            assertTrue(temp.contains(i));
        }

        verify(spyHighPriorityTaskThreadPool, times(4)).submit(any(Runnable.class));
    }

    @Test
    public void testFoldLeft() {

        List<String> list = new LinkedList<String>();
        list.add("Java");
        list.add(" ");
        list.add("Rocks");
        list.add("!");

        StringBuilder stringBuilder = new StringBuilder();

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        StringBuilder foldLeft = chain.foldLeft(stringBuilder, new Accumulator<StringBuilder, String>() {

            public StringBuilder accumulate(StringBuilder accumulator,
                                            String element) {
                return accumulator.append(element);
            }

        }).extract();

        assertTrue(foldLeft.toString().equals("Java Rocks!"));

    }

    @Test
    public void testFoldRight() {

        List<String> list = new LinkedList<String>();
        list.add("Java");
        list.add(" ");
        list.add("Rocks");
        list.add("!");

        StringBuilder stringBuilder = new StringBuilder();

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        StringBuilder foldRight = chain.foldRight(stringBuilder, new Accumulator<StringBuilder, String>() {

            public StringBuilder accumulate(StringBuilder accumulator,
                                            String element) {
                return accumulator.append(element);
            }

        }).extract();

        assertTrue(foldRight.toString().equals("!Rocks Java"));

    }

    @Test
    public void testTransform() {

        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        CollectionFunctionChain<Integer> chain = new CollectionFunctionChain<Integer>(list);

        List<String> stringList = chain.transform(new Function<List<Integer>, List<String>>() {
            public List<String> apply(List<Integer> arg) {
                return List("One", "Two", "Three");
            }
        }).extract();

        assertEquals(stringList, List("One", "Two", "Three"));
    }


    @Test
    public void testAsObject() {

        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        CollectionFunctionChain<Integer> chain = new CollectionFunctionChain<Integer>(list);

        List<Integer> integerList = chain.asObject().extract();

        assertEquals(integerList, List(1, 2, 3, 4));
    }


    @Test
    public void testExtract() {

        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        CollectionFunctionChain<Integer> chain = new CollectionFunctionChain<Integer>(list);

        Integer extract = chain.extract(new Function<List<Integer>, Integer>() {
            public Integer apply(List<Integer> arg) {
                return arg.get(0) + arg.get(1);
            }
        });

        assertTrue(extract == 3);
    }

    @Test
    public void testAsStream() {

        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        CollectionFunctionChain<Integer> chain = new CollectionFunctionChain<Integer>(list);

        DynamicIterable<Integer> integers = chain.asStream();

        assertEquals(list, FlattenList(integers));
    }

    @Test
    public void testReduce() {

        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        CollectionFunctionChain<Integer> chain = new CollectionFunctionChain<Integer>(list);

        Integer foldLeft = chain.reduce(new Accumulator<Integer, Integer>() {

            public Integer accumulate(Integer accumulator,
                                      Integer element) {
                return accumulator + element;
            }

        }).extract();

        assertTrue(foldLeft == 10);

    }


    @Test
    public void testReduceWithNoOfThreads() {

        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        CollectionFunctionChain<Integer> chain = new CollectionFunctionChain<Integer>(list);

        Integer foldLeft = chain.reduce(new Accumulator<Integer, Integer>() {

            public Integer accumulate(Integer accumulator,
                                      Integer element) {
                return accumulator + element;
            }

        }, FunctionUtil.parallel(2)).extract();

        assertTrue(foldLeft == 10);

        verify(spyHighPriorityTaskThreadPool, times(1)).submit(any(Runnable.class));

    }

    @Test
    public void testEvery() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 10; i++) {
            list.add("Scala");
            list.add("Java");
        }

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        boolean bool = chain.every(new Function<String, Boolean>() {

            public Boolean apply(String string) {
                return string.contains("v");
            }
        }).extract();

        assertTrue(!bool);

        bool = chain.every(new Function<String, Boolean>() {

            public Boolean apply(String string) {
                return string.contains("a");
            }
        }).extract();

        assertTrue(bool);
    }

    @Test
    public void testWithIndex() {

        List<String> list = List("hello", "Mr.", "FirstName", "LastName");

        List<String> outList = new CollectionFunctionChain(list).extractWithIndex(new Function<Integer, Boolean>() {
            public Boolean apply(Integer index) {
                return index % 2 == 0;
            }
        }).extract();

        assertEquals(outList, List("hello", "FirstName"));

    }

    @Test
    public void testSomeFunction() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 10; i++) {
            list.add("Scala");
            list.add("Java");
        }

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        boolean bool = chain.any(new Function<String, Boolean>() {

            public Boolean apply(String string) {
                return string.contains("R");
            }
        }).extract();

        assertTrue(!bool);

        bool = chain.any(new Function<String, Boolean>() {

            public Boolean apply(String string) {
                return string.contains("a");
            }
        }).extract();

        assertTrue(bool);
    }

    @Test
    public void testCount() {

        Set<String> set = new HashSet<String>();
        set.add("Scala");
        set.add("Java");

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(CollectionUtil.FlattenList(set));

        int count = chain.count(new Function<String, Boolean>() {
            public Boolean apply(String s) {
                return s.contains("Scala");
            }
        }).extract();

        assertEquals(count, 1);
    }


    @Test
    public void testPlus() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");

        List<String> list1 = new LinkedList<String>();
        list1.add("Groovy");
        list1.add("Ruby");

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        Collection<String> combinedList = chain.plus(list1).extract();

        List<String> expectedList = new LinkedList<String>();
        expectedList.add("Scala");
        expectedList.add("Java");
        expectedList.add("Groovy");
        expectedList.add("Ruby");

        assertEquals(expectedList, combinedList);
    }

    @Test
    public void testMinus() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");

        List<String> list1 = new LinkedList<String>();
        list1.add("Java");

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        Collection<String> combinedList = chain.minus(list1).extract();

        List<String> expectedList = new LinkedList<String>();
        expectedList.add("Scala");

        assertEquals(expectedList, combinedList);
    }


    @Test
    public void testUnion() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");

        List<String> list1 = new LinkedList<String>();
        list1.add("Java");
        list1.add("Groovy");

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        Collection<String> combinedList = chain.union(list1).extract();

        List<String> expectedList = new LinkedList<String>();
        expectedList.add("Scala");
        expectedList.add("Java");
        expectedList.add("Groovy");

        assertEquals(expectedList, combinedList);
    }

    @Test
    public void testIntersect() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");

        List<String> list1 = new LinkedList<String>();
        list1.add("Java");

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        Collection<String> combinedList = chain.intersect(list1).extract();

        List<String> expectedList = new LinkedList<String>();
        expectedList.add("Java");

        assertEquals(expectedList, combinedList);
    }


    @Test
    public void testSlice() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");
        list.add("Groovy");
        list.add("Ruby");

        Collection<String> combinedList = new CollectionFunctionChain<String>(list).slice(1, 2).extract();

        List<String> expectedList = new LinkedList<String>();
        expectedList.add("Java");
        expectedList.add("Groovy");

        assertEquals(expectedList, combinedList);

        //--------------------------------

        combinedList = new CollectionFunctionChain<String>(list).slice(1, 3).extract();

        expectedList = new LinkedList<String>();
        expectedList.add("Java");
        expectedList.add("Groovy");
        expectedList.add("Ruby");

        assertEquals(expectedList, combinedList);

        //--------------------------------

        combinedList = new CollectionFunctionChain<String>(list).slice(0, 0).extract();

        expectedList = new LinkedList<String>();
        expectedList.add("Scala");

        assertEquals(expectedList, combinedList);

        //--------------------------------

        combinedList = new CollectionFunctionChain<String>(list).slice(-2, -1).extract();

        expectedList = new LinkedList<String>();

        assertEquals(expectedList, combinedList);

        //--------------------------------

        combinedList = new CollectionFunctionChain<String>(list).slice(5, 6).extract();

        expectedList = new LinkedList<String>();

        assertEquals(expectedList, combinedList);

    }

    @Test
    public void testFork() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");
        list.add("Groovy");
        list.add("Ruby");

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);
        assertTrue(chain.fork().getClass() == CollectionForkAndJoin.class);

    }

    @Test
    public void testDivideAndConquerWithChunks() {
        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        Task<Collection<Integer>> mockTask = mock(Task.class);

        new CollectionFunctionChain<Integer>(list).divideAndConquer(mockTask, FunctionUtil.batch(2));

        List<Integer> chunk1 = new LinkedList<Integer>();
        chunk1.add(1);
        chunk1.add(2);

        verify(mockTask, times(1)).execute(chunk1);

        List<Integer> chunk2 = new LinkedList<Integer>();
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

        new CollectionFunctionChain<Integer>(list).divideAndConquer(mockTask, FunctionUtil.parallel(3));

        List<Integer> partition1 = new LinkedList<Integer>();
        partition1.add(1);
        partition1.add(4);

        verify(mockTask, times(1)).execute(partition1);

        List<Integer> partition2 = new LinkedList<Integer>();
        partition2.add(2);
        partition2.add(5);

        verify(mockTask, times(1)).execute(partition2);

        List<Integer> partitions3 = new LinkedList<Integer>();
        partitions3.add(3);

        verify(mockTask, times(1)).execute(partitions3);

        verify(spyHighPriorityTaskThreadPool, times(2)).submit(any(Runnable.class));

    }


    @Test
    public void testExecute() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");
        list.add("Groovy");
        list.add("Ruby");

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);
        Task<List<String>> mockTask = mock(Task.class);

        chain.execute(mockTask);

        verify(mockTask, times(1)).execute(list);
    }

    @Test
    public void testExecuteAsync() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");
        list.add("Groovy");
        list.add("Ruby");

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);
        Task<List<String>> mockTask = mock(Task.class);

        chain.executeAsync(mockTask);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(mockTask, times(1)).execute(list);
        verify(spyMediumPriorityAsyncTaskThreadPool, times(1)).submit(any((Runnable.class)));
    }

    @Test
    public void testExecuteLater() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");
        list.add("Groovy");
        list.add("Ruby");

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);
        Task<List<String>> mockTask = mock(Task.class);

        chain.executeLater(mockTask);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(mockTask, times(1)).execute(list);
        verify(spyLowPriorityAsyncTaskThreadPool, times(1)).submit(any((Runnable.class)));
    }

    @Test
    public void testExecuteWithGlobalLockWithMultipleThread() {

        final List<Integer> list = IntRange(1, 10000);

        final Block spyBlock = spy(new Block() {
            public void execute() {
                list.add(1);
                for (Integer i : list) ;
                list.add(2);
            }
        });

        FunctionUtil.each(IntRange(1, 100), new RecordProcessor<Integer>() {
            public void process(Integer item) {
                new CollectionFunctionChain<Integer>(IntRange(1, 10)).executeWithGlobalLock(new Task<List<Integer>>() {
                    public void execute(List<Integer> input) {
                        spyBlock.execute();
                    }
                });
            }
        }, FunctionUtil.parallel(100));

        verify(spyBlock, times(100)).execute();
    }


    private static ConcurrentHashMap<ExecutionThrottler, ExecutorService> mapSpy;
    private static ExecutionThrottler throttler;

    public static void init() throws Exception {

        Field throttlerPoolMap = FunctionUtil.class.getDeclaredField("throttlerPoolMap");
        throttlerPoolMap.setAccessible(true);
        mapSpy = spy(new ConcurrentHashMap<ExecutionThrottler, ExecutorService>(15, 0.9f, 32));
        throttlerPoolMap.set(null, mapSpy);

        String identity = "some operation";
        throttler = FunctionUtil.throttler(identity);
        FunctionUtil.registerPool(throttler, 10);

    }

    @Test
    public void testExecuteWithThrottle() throws Exception {
        init();
        Task<List<Integer>> taskMock = mock(Task.class);

        List<Integer> integerList = IntRange(1, 100);
        CollectionFunctionChain<Integer> chain = new CollectionFunctionChain<Integer>(integerList);

        chain.executeWithThrottle(throttler, taskMock);

        verify(mapSpy, times(1)).get(throttler);

        verify(taskMock, times(1)).execute(integerList);

    }

    @Test
    public void testExecuteAsyncWithThrottle() throws Exception {
        init();
        Task<List<Integer>> taskMock = mock(Task.class);

        List<Integer> integerList = IntRange(1, 100);
        CollectionFunctionChain<Integer> chain = new CollectionFunctionChain<Integer>(integerList);

        chain.executeAsyncWithThrottle(throttler, taskMock);

        verify(mapSpy, times(1)).get(throttler);
        Thread.sleep(200);

        verify(taskMock, times(1)).execute(integerList);

    }

    @Test
    public void testExecuteAsyncWithThrottle_callback() throws Exception {
        init();
        Function<List<Integer>, Object> function = mock(Function.class);
        CallbackTask<Object> callbackTask = mock(CallbackTask.class);
        Object asyncTaskResult = new Object();
        List<Integer> integerList = IntRange(1, 100);
        CollectionFunctionChain<Integer> chain = new CollectionFunctionChain<Integer>(integerList);


        when(function.apply(integerList)).thenReturn(asyncTaskResult);

        chain.executeAsyncWithThrottle(throttler, function, callbackTask);

        verify(mapSpy, times(1)).get(throttler);
        Thread.sleep(200);
        ArgumentCaptor<AsyncTaskHandle> argument = ArgumentCaptor.forClass(AsyncTaskHandle.class);
        verify(callbackTask, times(1)).execute(argument.capture());

        assertEquals(argument.getValue().getOutput(), asyncTaskResult);
        assertEquals(argument.getValue().getException(), null);
    }

}
