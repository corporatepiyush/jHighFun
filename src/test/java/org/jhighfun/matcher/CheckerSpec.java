package org.jhighfun.matcher;

import org.jhighfun.util.Function;
import org.jhighfun.util.Task;
import org.jhighfun.util.Tuple2;
import org.jhighfun.util.matcher.WhenChecker;
import org.junit.Test;

import static org.jhighfun.util.CollectionUtil.tuple;
import static org.junit.Assert.assertEquals;

public class CheckerSpec {

    @Test
    public void testCheckerWithOutput() {

        String result = new WhenChecker<String>("@copyright")
                .ifEquals((String) null).thenReturn("bad")
                .ifEquals("@copyright").thenReturn("ok")
                .ifEquals("@COPYRIGHT").thenReturn("bad")
                .otherwiseReturn("noneMatch");

        assertEquals(result, "ok");
    }

    @Test
    public void testCheckerWithOutputFunctions() {

        String result = new WhenChecker<String>("@copyright")
                .ifEquals((String) null).thenReturn(new Function<String, String>() {
                    public String apply(String arg) {
                        return "bad";
                    }
                })
                .ifEquals("@copyrighT").thenReturn(new Function<String, String>() {
                    public String apply(String arg) {
                        return "ok";
                    }
                })
                .ifEquals("@COPYRIGHT").thenReturn("bad")
                .otherwiseReturn(new Function<String, String>() {
                    public String apply(String arg) {
                        return "noneMatch";
                    }
                });

        assertEquals(result, "noneMatch");
    }

    @Test
    public void testCheckerWithTask() {

        final Tuple2<String, Object> tuple = tuple("key", null);

        new WhenChecker<String>("@copyright")
                .ifEquals(new Function<String, Boolean>() {
                    public Boolean apply(String arg) {
                        return arg.equals("@copyright");
                    }
                }).thenExecute(new Task<String>() {
            public void execute(String input) {
                tuple._2 = "ok";
            }
        })
                .ifEquals((String) null).thenExecute(new Task<String>() {
            public void execute(String input) {
                tuple._2 = "null";
            }
        })
                .ifEquals(new Function<String, Boolean>() {
                    public Boolean apply(String arg) {
                        return arg.equals("@COPYRIGHT");
                    }
                }).thenExecute(new Task<String>() {
            public void execute(String input) {
                tuple._2 = "bad";
            }
        })
                .otherwiseExecute(new Task<String>() {
                    public void execute(String input) {
                        tuple._2 = "NONE";
                    }
                });

        assertEquals(tuple._2, "ok");
    }

    @Test
    public void testCheckerWithTask_otherwise() {

        final Tuple2<String, Object> tuple = tuple("key", null);

        new WhenChecker<String>("@copyright")
                .ifEquals("@copyrighT").thenExecute(new Task<String>() {
            public void execute(String input) {
                tuple._2 = "ok";
            }
        })
                .ifEquals((String) null).thenExecute(new Task<String>() {
            public void execute(String input) {
                tuple._2 = "null";
            }
        })
                .ifEquals(new Function<String, Boolean>() {
                    public Boolean apply(String arg) {
                        return arg.equals("@COPYRIGHT");
                    }
                }).thenExecute(new Task<String>() {
            public void execute(String input) {
                tuple._2 = "bad";
            }
        })
                .otherwiseExecute(new Task<String>() {
                    public void execute(String input) {
                        tuple._2 = "NONE";
                    }
                });

        assertEquals(tuple._2, "NONE");
    }
}
