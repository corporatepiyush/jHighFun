package org.jhighfun.config;

import org.jhighfun.internal.Constants;
import org.jhighfun.util.FunctionUtil;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class ThrottlerPoolInitializer {

    public ThrottlerPoolInitializer(Map<String, ExecutorService> throttlers) throws NoSuchFieldException, IllegalAccessException {
        initialize(throttlers);
    }

    private void initialize(Map<String, ExecutorService> throttlers) throws NoSuchFieldException, IllegalAccessException {
        Field throttlerPoolMapField = FunctionUtil.class.getDeclaredField(Constants.THROTTLER_POOL_MAP);
        throttlerPoolMapField.setAccessible(true);

        Map throttlerPoolMap = (Map) throttlerPoolMapField.get(null);
        Set<Map.Entry<String, ExecutorService>> entrySet = throttlers.entrySet();
        for (Map.Entry<String, ExecutorService> entry : entrySet) {
            throttlerPoolMap.put(FunctionUtil.throttler(entry.getKey()), entry.getValue());
        }
    }
}
