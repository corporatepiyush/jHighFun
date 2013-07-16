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
                .when((String) null).thenReturn("bad")
                .whenMatchesWith("@copyright").thenReturn("ok")
                .whenMatchesWith("@COPYRIGHT").thenReturn("bad")
                .otherwiseReturn("noneMatch");

        assertEquals(result, "ok");
    }

    @Test
    public void testCheckerWithOutputFunctions() {

        String result = new WhenChecker<String>("@copyright")
                .when((String) null).thenReturn(new Function<String, String>() {
                    public String apply(String arg) {
                        return "bad";
                    }
                })
                .whenMatchesWith("@copyrighT").thenReturn(new Function<String, String>() {
                    public String apply(String arg) {
                        return "ok";
                    }
                })
                .whenMatchesWith("@COPYRIGHT").thenReturn("bad")
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
                .when("@copyright").thenExecute(new Task<String>() {
            public void execute(String input) {
                tuple._2 = "ok";
            }
        })
                .whenMatchesWith((String) null).thenExecute(new Task<String>() {
            public void execute(String input) {
                tuple._2 = "null";
            }
        })
                .whenMatchesWith(new Function<String, Boolean>() {
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
                .when("@copyrighT").thenExecute(new Task<String>() {
            public void execute(String input) {
                tuple._2 = "ok";
            }
        })
                .whenMatchesWith((String) null).thenExecute(new Task<String>() {
            public void execute(String input) {
                tuple._2 = "null";
            }
        })
                .whenMatchesWith(new Function<String, Boolean>() {
                    public Boolean apply(String arg) { return arg.equals("@COPYRIGHT");  }
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
