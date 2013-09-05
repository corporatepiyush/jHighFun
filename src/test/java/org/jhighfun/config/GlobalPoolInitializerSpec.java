package org.jhighfun.config;

import org.jhighfun.internal.Constants;
import org.jhighfun.util.FunctionUtil;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertTrue;

public class GlobalPoolInitializerSpec {

    @Test
    public void shouldInitializeGlobalPool() throws NoSuchFieldException, IllegalAccessException {

        ExecutorService HPPool = Executors.newFixedThreadPool(1);
        ExecutorService LPPool = Executors.newFixedThreadPool(1);
        new GlobalPoolInitializer(HPPool, LPPool);

        Field globalPool = FunctionUtil.class.getDeclaredField(Constants.HIGH_PRIORITY_TASK_THREAD_POOL);
        globalPool.setAccessible(true);
        assertTrue(globalPool.get(null) == HPPool);

        globalPool = FunctionUtil.class.getDeclaredField(Constants.LOW_PRIORITY_ASYNC_TASK_THREAD_POOL);
        globalPool.setAccessible(true);
        assertTrue(globalPool.get(null) == LPPool);

    }
}
