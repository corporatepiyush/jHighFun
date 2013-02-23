package org.highfun;


import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class TaskInputOutputSpec {

    @Test
    public void test() {
        TaskInputOutput<String, Date> today = new TaskInputOutput<String, Date>("today");
        assertEquals(today.getInput(), "today");
        assertEquals(today.getOutput(), null);

        Date date = new Date();
        today.setOutput(date);
        assertEquals(today.getInput(), "today");
        assertEquals(today.getOutput(), date);
    }
}
