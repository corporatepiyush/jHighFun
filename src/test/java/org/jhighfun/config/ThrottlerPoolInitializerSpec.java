package org.jhighfun.config;


import org.jhighfun.internal.Constants;
import org.jhighfun.util.FunctionUtil;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertTrue;

public class ThrottlerPoolInitializerSpec {

    @Test
    public void shouldAbleToInitializeThrottlerPools() throws NoSuchFieldException, IllegalAccessException {

        ExecutorService pool1 = Executors.newFixedThreadPool(1);
        ExecutorService pool2 = Executors.newFixedThreadPool(1);

        Map<String, ExecutorService> throttlers = new LinkedHashMap<String, ExecutorService>();
        throttlers.put("t1", pool1);
        throttlers.put("t2", pool2);
        //when
        new ThrottlerPoolInitializer(throttlers);

        // then

        Field throttlerPoolMapField = FunctionUtil.class.getDeclaredField(Constants.THROTTLER_POOL_MAP);
        throttlerPoolMapField.setAccessible(true);

        Map throttlersExpected = new ConcurrentHashMap();
        throttlersExpected.put(FunctionUtil.throttler("t1"), pool1);
        throttlersExpected.put(FunctionUtil.throttler("t2"), pool2);

        Map poolMap = (Map) throttlerPoolMapField.get(null);

        for(Map.Entry entry  : (Set<Map.Entry>) poolMap.entrySet()) {
             assertTrue(throttlersExpected.get(entry.getKey()) != null);
            assertTrue(throttlersExpected.get(entry.getKey()) == entry.getValue());
        }
    }
}
