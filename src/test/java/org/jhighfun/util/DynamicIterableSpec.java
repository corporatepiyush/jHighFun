package org.jhighfun.util;


import org.junit.Test;

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
                .batch(2)
                .expand(new Function<List<String>, Iterable<String>>() {
                    public Iterable<String> apply(List<String> arg) {
                        return arg;
                    }
                })
                ._processExclusively()
                .batch(2)
                .expand(new Function<List<String>, Iterable<String>>() {
                    public Iterable<String> apply(List<String> arg) {
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

        List<List<String>> actual = new TaskStream<String>(iterator).extractSequences(
                new Function<List<String>, Boolean>() {
                    public Boolean apply(List<String> list) {
                        return list.get(0).equals("{") && (list.size() <= 1 || !list.get(list.size() - 1).equals("}"));
                    }
                }).extract();

        assertEquals(actual, List(List("{", "C", "C", "}"), List("{", "E", "E", "}")));

    }
}
