package org.jhighfun.util;


import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.jhighfun.util.CollectionUtil.List;
import static org.junit.Assert.assertEquals;

public class DynamicIterableSpec {

    @Test
    public void test() {

        List<String> lists = new TaskStream<String>(List("India", "UK", "US", "Singapore"))
                .filter(new Function<String, Boolean>() {
                    public Boolean apply(String arg) {
                        return arg.contains("a");
                    }
                })
                ._process()
                ._batch(2)
                .flatMap(new Function<ArrayList<String>, Iterable<String>>() {
                    public Iterable<String> apply(ArrayList<String> arg) {
                        return arg;
                    }
                })
                ._processExclusively()
                ._batch(2)
                .flatMap(new Function<ArrayList<String>, Iterable<String>>() {
                    public Iterable<String> apply(ArrayList<String> arg) {
                        return arg;
                    }
                })
                .extract();

        assertEquals(List("India", "Singapore"), lists);

    }

    @Test
    public void testChain() {

        List<String> lists = new TaskStream<String>(List("India", "UK", "US", "Singapore"))
                ._processAndChain()
                .extract();

        assertEquals(lists, List("India", "UK", "US", "Singapore"));
    }


    @Test
    public void testExtractSequences() {

        Iterator<String> iterator = List("A", "B", "{", "C", "C", "}", "D", "{", "E", "E", "}").iterator();

        List<List<String>> actual = new TaskStream<String>(iterator).filterSequences(
                new Function<List<String>, Boolean>() {
                    public Boolean apply(List<String> list) {
                        return list.get(0).equals("{") && (list.size() <= 1 || !list.get(list.size() - 1).equals("}"));
                    }
                }).extract();

        assertEquals(actual, List(List("{", "C", "C", "}"), List("{", "E", "E", "}")));

    }
}
