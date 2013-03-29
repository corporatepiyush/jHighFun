package org.jhighfun.util;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import support.Person;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FunctionChainSpec {

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
    public void testUnchain() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 100; i++) {
            list.add("India");
            list.add("ndia");
            list.add("dia");
            list.add("ia");
            list.add("a");
        }
        FunctionChain<String> chain = new FunctionChain<String>(list);

        assertEquals(chain.extract(), list);
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
        FunctionChain<String> chain = new FunctionChain<String>(list);

        Collection<Character> mapList = chain.map(new Converter<String, Character>() {
            public Character convert(String input) {
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
        FunctionChain<String> chain = new FunctionChain<String>(list);

        Collection<Character> mapList = chain.map(new Converter<String, Character>() {
            public Character convert(String input) {
                return input.charAt(0);
            }
        }, 3).extract();

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


        FunctionChain<String> chain = new FunctionChain<String>(list);

        Collection<String> filterList = chain.filter(new Predicate<String>() {

            public boolean evaluate(String t) {
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

        FunctionChain<String> chain = new FunctionChain<String>(list);

        Collection<String> filterList = chain.filter(new Predicate<String>() {

            public boolean evaluate(String t) {
                return t.contains("y");
            }

        }, 3).extract();

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

        FunctionChain<Integer> chain = new FunctionChain<Integer>(list);

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

        assertTrue(new FunctionChain<Integer>(set).sort().extract().toString().equals("[1, 2, 3, 4]"));

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

        assertEquals(new FunctionChain<Person>(inputList).sortBy("age").extract(), expectedList);

        //---sort by salary, name

        expectedList = new LinkedList<Person>();
        expectedList.add(chloe);
        expectedList.add(joe);
        expectedList.add(amanda);

        assertEquals(new FunctionChain<Person>(inputList).sortBy("salary", "firstName").extract(), expectedList);

    }


    @Test
    public void testEach() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");

        final List<String> temp = new LinkedList<String>();

        FunctionChain<String> chain = new FunctionChain<String>(list);

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

        new FunctionChain<String>(list).eachWithIndex(new RecordWithIndexProcessor<String>() {
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

        FunctionChain<Integer> chain = new FunctionChain<Integer>(list);

        chain.each(new RecordProcessor<Integer>() {
            public void process(Integer item) {
                temp.add(item);
            }
        }, 5).extract();

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

        FunctionChain<String> chain = new FunctionChain<String>(list);

        StringBuilder foldLeft = chain.foldLeft(stringBuilder, new Accumulator<StringBuilder, String>() {

            public StringBuilder accumulate(StringBuilder accumulator,
                                            String element) {
                return accumulator.append(element);
            }

        });

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

        FunctionChain<String> chain = new FunctionChain<String>(list);

        StringBuilder foldRight = chain.foldRight(stringBuilder, new Accumulator<StringBuilder, String>() {

            public StringBuilder accumulate(StringBuilder accumulator,
                                            String element) {
                return accumulator.append(element);
            }

        });

        assertTrue(foldRight.toString().equals("!Rocks Java"));

    }

    @Test
    public void testReduce() {

        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        FunctionChain<Integer> chain = new FunctionChain<Integer>(list);

        Integer foldLeft = chain.reduce(new Accumulator<Integer, Integer>() {

            public Integer accumulate(Integer accumulator,
                                      Integer element) {
                return accumulator + element;
            }

        });

        assertTrue(foldLeft == 10);

    }


    @Test
    public void testReduceWithNoOfThreads() {

        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        FunctionChain<Integer> chain = new FunctionChain<Integer>(list);

        Integer foldLeft = chain.reduce(new Accumulator<Integer, Integer>() {

            public Integer accumulate(Integer accumulator,
                                      Integer element) {
                return accumulator + element;
            }

        }, 2);

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

        FunctionChain<String> chain = new FunctionChain<String>(list);

        boolean bool = chain.every(new Predicate<String>() {

            public boolean evaluate(String string) {
                return string.contains("v");
            }
        });

        assertTrue(!bool);

        bool = chain.every(new Predicate<String>() {

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

        FunctionChain<String> chain = new FunctionChain<String>(list);

        boolean bool = chain.any(new Predicate<String>() {

            public boolean evaluate(String string) {
                return string.contains("R");
            }
        });

        assertTrue(!bool);

        bool = chain.any(new Predicate<String>() {

            public boolean evaluate(String string) {
                return string.contains("a");
            }
        });

        assertTrue(bool);
    }

    @Test
    public void testCount() {

        Set<String> set = new HashSet<String>();
        set.add("Scala");
        set.add("Java");

        FunctionChain<String> chain = new FunctionChain<String>(set);

        int count = chain.count(new Predicate<String>() {
            public boolean evaluate(String s) {
                return s.contains("Scala");
            }
        });

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

        FunctionChain<String> chain = new FunctionChain<String>(list);

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

        FunctionChain<String> chain = new FunctionChain<String>(list);

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

        FunctionChain<String> chain = new FunctionChain<String>(list);

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

        FunctionChain<String> chain = new FunctionChain<String>(list);

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

        Collection<String> combinedList = new FunctionChain<String>(list).slice(1, 2).extract();

        List<String> expectedList = new LinkedList<String>();
        expectedList.add("Java");
        expectedList.add("Groovy");

        assertEquals(expectedList, combinedList);

        //--------------------------------

        combinedList = new FunctionChain<String>(list).slice(1, 3).extract();

        expectedList = new LinkedList<String>();
        expectedList.add("Java");
        expectedList.add("Groovy");
        expectedList.add("Ruby");

        assertEquals(expectedList, combinedList);

        //--------------------------------

        combinedList = new FunctionChain<String>(list).slice(0, 0).extract();

        expectedList = new LinkedList<String>();
        expectedList.add("Scala");

        assertEquals(expectedList, combinedList);

        //--------------------------------

        combinedList = new FunctionChain<String>(list).slice(-2, -1).extract();

        expectedList = new LinkedList<String>();

        assertEquals(expectedList, combinedList);

        //--------------------------------

        combinedList = new FunctionChain<String>(list).slice(5, 6).extract();

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

        FunctionChain<String> chain = new FunctionChain<String>(list);
        assertTrue(chain.fork().getClass() == ForkAndJoin.class);

    }

    @Test
    public void testExecute() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");
        list.add("Groovy");
        list.add("Ruby");

        FunctionChain<String> chain = new FunctionChain<String>(list);
        Task<Collection<String>> mockTask = mock(Task.class);

        chain.execute(mockTask);

        verify(mockTask, times(1)).execute(list);


    }
}
