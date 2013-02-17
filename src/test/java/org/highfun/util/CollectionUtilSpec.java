package org.highfun.util;

import org.highfun.Entry;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.highfun.util.CollectionUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class CollectionUtilSpec {

    @Test
    public void testList(){

        List<Integer> integerList = List(1, 2);
        List<String> stringList = List("One", "Two");

       assertEquals(List(1, 2, 1).size(), 3);
       assertEquals(List().size(), 0);
    }


    @Test
    public void testListCompose(){
       assertEquals(List(List(), List()), List());
       assertEquals(List(List(1, 2, 3), List(4, 5, 6)), List(1, 2, 3, 4, 5, 6));
       assertEquals(List(List(1, 2, 3), List()), List(1, 2, 3));
    }

    @Test
    public void testEntry(){

        Entry<Integer, Integer> entry = Entry(1, 2);
       assertEquals(entry.getKey(), new Integer(1));
       assertEquals(entry.getValue(), new Integer(2));

    }

    @Test
    public void testMap(){
        Map<Integer,Integer> map = Map(Entry(1, 2), Entry(2, 3), Entry(1, 2));
       assertEquals(map.size(), 2);
       assertEquals(map.get(1), new Integer(2));
       assertEquals(map.get(2), new Integer(3));

        Map emptyMap = Map();
       assertEquals(emptyMap.size(), 0);
    }

    @Test
    public void testMapCompose(){
       assertEquals(Map(Map(Entry(1, 2)), Map(Entry(1, 2))), Map(Entry(1, 2)));
       assertEquals(Map(Map(Entry(1, 2)), Map(Entry(3, 4))), Map(Entry(1, 2), Entry(3, 4)));
       assertEquals(Map(Map(), Map()), Map());
    }

    @Test
    public void testSet(){
        Set<Integer> integerSet = Set(1, 2, 1);
       assertEquals(integerSet.size(), 2);
       assertEquals(Set().size(), 0);
    }

    @Test
    public void testSetCompose(){
       assertEquals(Set(Set(), Set()), Set());
       assertEquals(Set(Set(1, 2, 3), Set(4, 5, 6)), Set(1, 2, 3, 4, 5, 6));
       assertEquals(Set(Set(1, 2, 3), Set()), Set(1, 2, 3));
       assertEquals(Set(Set(1, 2, 3), Set(1, 2, 3)), Set(1, 2, 3));
    }
}
