package org.highfun;


import org.highfun.util.FunctionUtil;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FunctionChainSpec {

     @Test
     public void testProduce(){

         List<String> list = new LinkedList<String>();
         for (int i = 1; i <= 100; i++) {
             list.add("India");
             list.add("ndia");
             list.add("dia");
             list.add("ia");
             list.add("a");
         }
         FunctionChain<String> chain = new FunctionChain<String>(list);

         assertEquals(chain.unchain(), list);
     }

    @Test
    public void testMap(){

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
        }).unchain();

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
    public void testMapWithThreads(){

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
        }, 3).unchain();

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
    public void testFilter() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 1000; i++) {
            list.add("Scala");
            list.add("Ruby");
        }


        FunctionChain<String> chain = new FunctionChain<String>(list);

        Collection<String> filterList = chain.filter(new Condition<String>() {

            public boolean evaluate(String t) {
                return t.contains("y");
            }

        }).unchain();

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

        Collection<String> filterList = chain.filter(new Condition<String>() {

            public boolean evaluate(String t) {
                return t.contains("y");
            }

        }, 3).unchain();

        assertEquals(filterList.size(), (list.size() / 2));

        for (String string : filterList) {
            assertTrue(string.equals("Ruby"));
        }

    }

    @Test
    public void testSort() {
        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(4);
        list.add(2);
        list.add(3);

        FunctionChain<Integer> chain = new FunctionChain<Integer>(list);

        Collection<Integer> filterList = chain.sort(new Comparator<Integer>() {

            public int compare(Integer t1, Integer t2) {
                return t1 - t2;
            }
        }).unchain();

        assertTrue(filterList.toString().equals("[1, 2, 3, 4]"));

    }

    @Test
    public void testEach() {

        List<String> list = new LinkedList<String>();
        list.add("Scala");
        list.add("Java");

        final List<String> temp = new LinkedList<String>();

        FunctionChain<String> chain = new FunctionChain<String>(list);

        chain.each(new ItemRecord<String>() {
            public void process(String item) {
                temp.add(item);
            }
        }).unchain();

        assertEquals(temp, list);

    }

    @Test
    public void testEachFunctionWithThreads() {

        List<Integer> list = new LinkedList<Integer>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }

        final List<Integer> temp = new CopyOnWriteArrayList<Integer>();

        FunctionChain<Integer> chain = new FunctionChain<Integer>(list);

        chain.each(new ItemRecord<Integer>() {
            public void process(Integer item) {
                temp.add(item);
            }
        }, 5).unchain();

        for (int i = 0; i < 1000; i++) {
            assertTrue(temp.contains(i));
        }

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

        StringBuilder foldLeft = chain.foldLeft(stringBuilder,new Accumulator<StringBuilder, String>() {

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

        Integer foldLeft = chain.reduce(0,
                new Accumulator<Integer, Integer>() {

                    public Integer accumulate(Integer accumulator,
                                              Integer element) {
                        return accumulator + element;
                    }

                });

        assertTrue(foldLeft == 10);

    }

    @Test
    public void testEvery(){

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 10; i++) {
            list.add("Scala");
            list.add("Java");
        }

        FunctionChain<String> chain = new FunctionChain<String>(list);

        boolean bool = chain.every( new Condition<String>() {

            public boolean evaluate(String string) {
                return string.contains("v");
            }
        });

        assertTrue(!bool);

        bool = chain.every( new Condition<String>() {

            public boolean evaluate(String string) {
                return string.contains("a");
            }
        });

        assertTrue(bool);
    }

    @Test
    public void testSomeFunction(){

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 10; i++) {
            list.add("Scala");
            list.add("Java");
        }

        FunctionChain<String> chain = new FunctionChain<String>(list);

        boolean bool = chain.some( new Condition<String>() {

            public boolean evaluate(String string) {
                return string.contains("R");
            }
        });

        assertTrue(!bool);

        bool = chain.some( new Condition<String>() {

            public boolean evaluate(String string) {
                return string.contains("a");
            }
        });

        assertTrue(bool);
    }

    @Test
    public void testCount(){

        Set<String> set = new HashSet<String>();
        set.add("Scala");
        set.add("Java");

        FunctionChain<String> chain = new FunctionChain<String>(set);

        int count = chain.count(new Condition<String>() {
            public boolean evaluate(String s) {
                return s.contains("Scala");
            }
        });

        assertEquals(count, 1);
    }
}
