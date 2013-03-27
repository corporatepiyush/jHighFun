package org.jhighfun.util;


import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.jhighfun.util.CollectionUtil.List;
import static org.junit.Assert.assertEquals;

public class CurriedFunctionSpec {

    CurriedFunction<Character, String> curriedFunction;

    @Before
    public void before() {
        curriedFunction = new CurriedFunction<Character, String>(new Function<Character, String>() {
            public String apply(List<Character> args) {
                StringBuilder string = new StringBuilder();
                for (Character character : args) {
                    string.append(character);
                }
                return string.toString();
            }
        },
                List('H', 'e', 'l', 'l', 'o'));

    }

    @Test
    public void testCallWithFixedInputs() {
        assertEquals(curriedFunction.call(), "Hello");
    }

    @Test
    public void testCallWithVarArg() {
        assertEquals(curriedFunction.call(' ', 'J', 'i', 'm'), "Hello Jim");
    }

    @Test
    public void testCallWithListArg() {
        assertEquals(curriedFunction.call(List(' ', 'J', 'i', 'm')), "Hello Jim");
    }

    @Test
    public void testCurryWithListArgs() {
        assertEquals(curriedFunction.curry(List(' ', 'J', 'i', 'm')).call(), "Hello Jim");
    }

    @Test
    public void testCurryWithVarArgs() {
        assertEquals(curriedFunction.curry(' ', 'J', 'i', 'm').call(), "Hello Jim");
    }
}
