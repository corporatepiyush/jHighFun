package org.jhighfun.util.stream;

import org.jhighfun.util.DynamicIterable;
import org.jhighfun.util.Function;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.jhighfun.util.CollectionUtil.List;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ExtractorIteratorSpec {

    @Test
    public void test() {

        Iterator<String> iterator = List("A", "B", "{", "C", "C", "}", "D", "{", "E", "E", "}").iterator();

        ExtractorIterator<String> extractorIterator = new ExtractorIterator<String>(iterator, new Function<List<String>, Boolean>() {
            public Boolean apply(List<String> list) {

                return list.get(0).equals("{") && (list.size() <= 1 || !list.get(list.size() - 1).equals("}"));
            }
        });

        List<List<String>> actual = new LinkedList<List<String>>();
        for (List<String> list : new DynamicIterable<List<String>>(extractorIterator)) {
            actual.add(list);
        }

        assertEquals(actual, List(List("{", "C", "C", "}"), List("{", "E", "E", "}")));

    }
}
