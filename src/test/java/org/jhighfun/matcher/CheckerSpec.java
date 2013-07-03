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
                .when((String) null).then("bad")
                .when("@copyright").then("ok")
                .when("@COPYRIGHT").then("bad")
                .otherwise("noneMatch");

        assertEquals(result, "ok");
    }

    @Test
    public void testCheckerWithOutputFunctions() {

        String result = new WhenChecker<String>("@copyright")
                .when((String) null).then(new Function<String, String>() {
                    public String apply(String arg) {
                        return "bad";
                    }
                })
                .when("@copyrighT").then(new Function<String, String>() {
                    public String apply(String arg) {
                        return "ok";
                    }
                })
                .when("@COPYRIGHT").then("bad")
                .otherwise(new Function<String, String>() {
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
                .when("@copyright").then(new Task<String>() {
            public void execute(String input) {
                tuple._2 = "ok";
            }
        })
                .when((String) null).then(new Task<String>() {
            public void execute(String input) {
                tuple._2 = "null";
            }
        })
                .when("@COPYRIGHT").then(new Task<String>() {
            public void execute(String input) {
                tuple._2 = "bad";
            }
        })
                .otherwise(new Task<String>() {
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
                .when("@copyrighT").then(new Task<String>() {
            public void execute(String input) {
                tuple._2 = "ok";
            }
        })
                .when((String) null).then(new Task<String>() {
            public void execute(String input) {
                tuple._2 = "null";
            }
        })
                .when("@COPYRIGHT").then(new Task<String>() {
            public void execute(String input) {
                tuple._2 = "bad";
            }
        })
                .otherwise(new Task<String>() {
                    public void execute(String input) {
                        tuple._2 = "NONE";
                    }
                });

        assertEquals(tuple._2, "NONE");
    }
}
