package org.jhighfun.util;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.jhighfun.util.CollectionUtil.*;
import static org.junit.Assert.assertEquals;


public class CollectionUtilSpec {

    @Test
    public void testList() {

        List<Integer> integerList = List(1, 2);
        List<String> stringList = List("One", "Two");

        assertEquals(List(1, 2, 1).size(), 3);
        assertEquals(List().size(), 0);
    }

    @Test
    public void testSafeList() {

        List<Integer> integerList = SafeList(1, 2);
        List<String> stringList = SafeList("One", "Two");

        assertEquals(SafeList(1, 2, 1).size(), 3);
        assertEquals(SafeList().size(), 0);
        assertEquals(SafeList().getClass(), CopyOnWriteArrayList.class);

    }


    @Test
    public void testListCompose() {
        assertEquals(List(List(), List()), List());
        assertEquals(List(List(1, 2, 3), Set(4, 5, 6)), List(1, 2, 3, 4, 5, 6));
        assertEquals(List(List(1, 2, 3), Set()), List(1, 2, 3));
    }

    @Test
    public void testEntry() {

        Pair<Integer, Integer> pair = Entry(1, 2);
        assertEquals(pair.getKey(), new Integer(1));
        assertEquals(pair.getValue(), new Integer(2));

    }

    @Test
    public void testMap() {
        Map<Integer, Integer> map = Map(Entry(1, 2), Entry(2, 3), Entry(1, 2));
        assertEquals(map.size(), 2);
        assertEquals(map.get(1), new Integer(2));
        assertEquals(map.get(2), new Integer(3));

        Map emptyMap = Map();
        assertEquals(emptyMap.size(), 0);
    }


    @Test
    public void testSafeMap() {
        Map<Integer, Integer> map = SafeMap(Entry(1, 2), Entry(2, 3), Entry(1, 2));
        assertEquals(map.size(), 2);
        assertEquals(map.get(1), new Integer(2));
        assertEquals(map.get(2), new Integer(3));

        Map emptyMap = SafeMap();
        assertEquals(emptyMap.size(), 0);
        assertEquals(emptyMap.getClass(), ConcurrentHashMap.class);
    }

    @Test
    public void testMapCompose() {
        assertEquals(Map(Map(Entry(1, 2)), Map(Entry(1, 2))), Map(Entry(1, 2)));
        assertEquals(Map(Map(Entry(1, 2)), Map(Entry(3, 4))), Map(Entry(1, 2), Entry(3, 4)));
        assertEquals(Map(Map(), Map()), Map());
    }

    @Test
    public void testSet() {
        Set<Integer> integerSet = Set(1, 2, 1);
        assertEquals(integerSet.size(), 2);
        assertEquals(Set().size(), 0);
    }

    @Test
    public void testSetCompose() {
        assertEquals(Set(Set(), Set()), Set());
        assertEquals(Set(Set(1, 2, 3), List(4, 5, 6)), Set(1, 2, 3, 4, 5, 6));
        assertEquals(Set(Set(1, 2, 3), List()), Set(1, 2, 3));
        assertEquals(Set(Set(1, 2, 3), Set(1, 2, 3)), Set(1, 2, 3));
    }

    @Test
    public void testTuples() {

        String first = "first";
        Integer second = 2;
        Double third = 3.0;
        Object fourth = new Object();
        Float fifth = 5.5f;

        Tuple2<String, Integer> tuple2 = tuple(first, second);
        assertEquals(tuple2._1, "first");
        assertEquals(tuple2._2, new Integer(2));

        Tuple3<String, Integer, Double> triplet = tuple(first, second, third);
        assertEquals(triplet._1, "first");
        assertEquals(triplet._2, new Integer(2));
        assertEquals(triplet._3, new Double(3));

        Tuple4<String, Integer, Double, Object> quadruplet = tuple(first, second, third, fourth);
        assertEquals(quadruplet._1, "first");
        assertEquals(quadruplet._2, new Integer(2));
        assertEquals(quadruplet._3, new Double(3));
        assertEquals(quadruplet._4, fourth);


        Tuple5<String, Integer, Double, Object, Float> quintuplet = tuple(first, second, third, fourth, fifth);
        assertEquals(quintuplet._1, "first");
        assertEquals(quintuplet._2, new Integer(2));
        assertEquals(quintuplet._3, new Double(3));
        assertEquals(quintuplet._4, fourth);
        assertEquals(quintuplet._5, fifth);

    }

}
