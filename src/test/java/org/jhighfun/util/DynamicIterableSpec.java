package org.jhighfun.util;


import org.junit.Test;

import java.util.List;

import static org.jhighfun.util.CollectionUtil.List;
import static org.junit.Assert.assertEquals;

public class DynamicIterableSpec {

    @Test
    public void test() {

        List<String> lists = new DynamicIterable<String>(List("India", "UK", "US", "Singapore"))
                .filter(new Function<String, Boolean>() {
                    public Boolean apply(String arg) {
                        return arg.contains("a");
                    }
                })
                .process()
                .batch(2)
                .expand(new Function<List<String>, Iterable<String>>() {
                    public Iterable<String> apply(List<String> arg) {
                        return arg;
                    }
                })
                .processExclusively()
                .batch(2)
                .expand(new Function<List<String>, Iterable<String>>() {
                    public Iterable<String> apply(List<String> arg) {
                        return arg;
                    }
                })
                .extract();

        assertEquals(List("India", "Singapore"), lists);

    }
}
