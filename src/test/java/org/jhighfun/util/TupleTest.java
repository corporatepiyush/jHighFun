package org.jhighfun.util;

import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class TupleTest {

    @Test
    public void test() {

        Date date = new Date();
        Tuple tuple = new Tuple3<String, Character, Date>("Today", '$', date);

        assertEquals("Today", tuple.get(Tuple.Index._1, String.class));
        assertEquals(new Character('$'), tuple.get(Tuple.Index._2, Character.class));
        assertEquals(date, tuple.get(Tuple.Index._3, Date.class));
    }


}
