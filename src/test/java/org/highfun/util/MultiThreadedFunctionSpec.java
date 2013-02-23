package org.highfun.util;

import org.highfun.Condition;
import org.highfun.Converter;
import org.highfun.ItemRecord;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class MultiThreadedFunctionSpec {

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

    }
}
