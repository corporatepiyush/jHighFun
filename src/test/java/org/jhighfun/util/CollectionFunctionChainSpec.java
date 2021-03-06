package org.jhighfun.util;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import support.Language;
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
    ExecutorService spyLowPriorityAsyncTaskThreadPool = new ThreadPoolExecutor(0, 5, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());


    @Before
    public void before() {
        try {
            Field globalPool = FunctionUtil.class.getDeclaredField("highPriorityTaskThreadPool");
            globalPool.setAccessible(true);
            globalPool.set(null, spyHighPriorityTaskThreadPool);

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
    public void testBatch() {
        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 1000; i++) {
            list.add("Scala");
            list.add("Ruby");
        }

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        List<List<String>> batchedOutput = chain.batch(100).extract();

        assertEquals(batchedOutput.size(), 20);

        for (List<String> batchedList : batchedOutput) {
            assertEquals(batchedList.size(), 100);
        }

    }


    @Test
    public void testExpand() {
        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Ruby");

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        List<String> expandedOutput = chain.flatMap(new Function<String, Iterable<String>>() {
            public Iterable<String> apply(String arg) {
                return List(arg, arg);
            }
        }).extract();

        assertEquals(expandedOutput.size(), 4);
        assertEquals(expandedOutput.get(0), "Scala");
        assertEquals(expandedOutput.get(1), "Scala");
        assertEquals(expandedOutput.get(2), "Ruby");
        assertEquals(expandedOutput.get(3), "Ruby");

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
    public void testForSortByWithSetterGetter() {
        Language java = new Language(false, "Java");
        Language javascript = new Language(true, "Javascript");
        Language ruby = new Language(true, "Ruby");
        Language scala = new Language(true, "Scala");

        List<Language> inputList = new LinkedList<Language>();
        inputList.add(java);
        inputList.add(ruby);
        inputList.add(javascript);
        inputList.add(scala);

        //---sort by age

        List<Language> expectedList = new LinkedList<Language>();
        expectedList.add(java);
        expectedList.add(ruby);
        expectedList.add(javascript);
        expectedList.add(scala);


        assertEquals(new CollectionFunctionChain<Language>(inputList).sortBy("functional").extract(), expectedList);

        //---sort by salary, name

        expectedList = new LinkedList<Language>();
        expectedList.add(java);
        expectedList.add(javascript);
        expectedList.add(ruby);
        expectedList.add(scala);

        assertEquals(new CollectionFunctionChain<Language>(inputList).sortBy("name").extract(), expectedList);

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

        TaskStream<Integer> integers = chain.asTaskStream();

        assertEquals(list, integers.extract());
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

        verify(spyHighPriorityTaskThreadPool, times(1)).submit(any(Callable.class));

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
    public void testEveryFunctionWithThreads() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 10000; i++) {
            list.add("Scala");
            list.add("Java");
        }

        boolean bool = new CollectionFunctionChain<String>(list).every(new Function<String, Boolean>() {

            public Boolean apply(String string) {
                return string.contains("v");
            }
        }, FunctionUtil.parallel(3)).extract();

        assertTrue(!bool);

        verify(spyHighPriorityTaskThreadPool, times(2)).submit(any(Runnable.class));

        bool = new CollectionFunctionChain<String>(list).every(new Function<String, Boolean>() {

            public Boolean apply(String string) {
                return string.contains("a");
            }
        }, FunctionUtil.parallel(5)).extract();

        assertTrue(bool);
        verify(spyHighPriorityTaskThreadPool, times(2 + 4)).submit(any(Runnable.class));
    }

    @Test
    public void testAnyFunctionWithThreads() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 10000; i++) {
            list.add("Scala");
            list.add("Java");
        }

        boolean bool = new CollectionFunctionChain<String>(list).any(new Function<String, Boolean>() {

            public Boolean apply(String string) {
                return string.contains("v");
            }
        }, FunctionUtil.parallel(3)).extract();

        assertTrue(bool);

        verify(spyHighPriorityTaskThreadPool, times(2)).submit(any(Runnable.class));

        bool = new CollectionFunctionChain<String>(list).any(new Function<String, Boolean>() {

            public Boolean apply(String string) {
                return string.contains("a");
            }
        }, FunctionUtil.parallel(5)).extract();

        assertTrue(bool);
        verify(spyHighPriorityTaskThreadPool, times(2 + 4)).submit(any(Runnable.class));
    }


    @Test
    public void testCountFunctionWithThreads() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 10000; i++) {
            list.add("Scala");
            list.add("Java");
        }

        int count = new CollectionFunctionChain<String>(list).count(new Function<String, Boolean>() {

            public Boolean apply(String string) {
                return string.contains("v");
            }
        }, FunctionUtil.parallel(3)).extract();

        assertEquals(count, 10000);
        verify(spyHighPriorityTaskThreadPool, times(2)).submit(any(Callable.class));
    }

    @Test
    public void testWithIndex() {

        List<String> list = List("hello", "Mr.", "FirstName", "LastName");

        List<String> outList = new CollectionFunctionChain(list).filterWithIndex(new Function<Integer, Boolean>() {
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

        Collection<String> combinedList = chain.union(FlattenSet(list1)).extract();

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

        Collection<String> combinedList = chain.intersect(FlattenSet(list1)).extract();

        List<String> expectedList = new LinkedList<String>();
        expectedList.add("Java");

        assertEquals(expectedList, combinedList);
    }


    @Test
    public void testCrossProduct() {

        Iterable<Integer> integers = LazyIntRange(1, 100);

        Iterable<Long> longs = LazyLongRange(101, 200);

        Iterable<String> product = new CollectionFunctionChain<Integer>(integers).crossProduct(longs, new Function<Tuple2<Integer, Long>, String>() {

            public String apply(Tuple2<Integer, Long> tuple) {
                return tuple.toString();
            }
        }).extract();

        Iterator<String> iterator = product.iterator();

        for (Integer integer : integers) {
            for (Long lon : longs) {
                assertEquals(iterator.next(), new Tuple2<Integer, Long>(integer, lon).toString());
            }
        }

    }

    @Test
    public void testSelfProduct() {

        Iterable<Integer> integers = LazyIntRange(1, 100);

        Iterable<Integer> integers1 = LazyIntRange(1, 100);

        Iterable<String> product = new CollectionFunctionChain<Integer>(integers).selfProduct(new Function<Tuple2<Integer, Integer>, String>() {

            public String apply(Tuple2<Integer, Integer> tuple) {
                return tuple.toString();
            }
        }).extract();

        Iterator<String> iterator = product.iterator();

        for (Integer integer : integers) {
            for (Integer integer1 : integers1) {
                assertEquals(iterator.next(), new Tuple2<Integer, Integer>(integer, integer1).toString());
            }
        }

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
    public void testLimit() {
        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");
        list.add("Groovy");
        list.add("Ruby");

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        assertEquals(chain.limit(10).extract(), List("Scala", "Java", "Groovy", "Ruby"));
        assertEquals(chain.limit(3).extract(), List("Scala", "Java", "Groovy"));
        assertEquals(chain.limit(1).extract(), List("Scala"));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testLimitWithNegativeValue() {
        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");
        list.add("Groovy");
        list.add("Ruby");

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        assertEquals(chain.limit(-1).extract(), List());
    }

    @Test
    public void testReverse() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");
        list.add("Groovy");
        list.add("Ruby");

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        assertEquals(chain.reverse().extract(), List("Ruby", "Groovy", "Java", "Scala"));
    }

    @Test
    public void testRemoveAlikes() {

        List<String> list = new LinkedList<String>();
        list.add("Scala - JVM");
        list.add("Java  - JVM");
        list.add("Groovy  - JVM");
        list.add("Ruby - Custom");

        CollectionFunctionChain<String> chain = new CollectionFunctionChain<String>(list);

        assertEquals(chain.removeDuplicates(new Function<Tuple2<String, String>, Boolean>() {
            public Boolean apply(Tuple2<String, String> tuple) {
                return tuple._1.endsWith("JVM") && tuple._2.endsWith("JVM");
            }
        }).extract(), List("Scala - JVM", "Ruby - Custom"));
    }

    @Test
    public void testDivideAndConquerWithChunks() {
        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        Task<List<Integer>> mockTask = mock(Task.class);

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

        Task<List<Integer>> mockTask = mock(Task.class);

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
        verify(spyHighPriorityTaskThreadPool, times(1)).submit(any((Runnable.class)));
    }

    @Test
    public void testGroupBy() {

        List<String> list = List("a", "b", "a", "c", "c", "d");

        Map<Character, List<String>> results = new CollectionFunctionChain<String>(list).groupBy(new Function<String, Character>() {

            @Override
            public Character apply(String arg) {
                return arg.charAt(0);
            }
        }).extract();


        assertEquals(results, Map(Entry('a', List("a", "a")), Entry('b', List("b")), Entry('c', List("c", "c")), Entry('d', List("d"))));
    }

    @Test
    public void testPartition() {

        List<String> set = new LinkedList<String>();
        set.add("Scala");
        set.add("Java");


        Tuple2<List<String>, List<String>> tuple2 = new CollectionFunctionChain<String>(set).partition(new Function<String, Boolean>() {
            public Boolean apply(String s) {
                return s.contains("Scala");
            }
        }).extract();

        assertEquals(tuple2._1.toString(), "[Scala]");
        assertEquals(tuple2._2.toString(), "[Java]");
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
