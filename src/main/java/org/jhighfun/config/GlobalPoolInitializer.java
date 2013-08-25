package org.jhighfun.config;

import org.jhighfun.internal.Constants;
import org.jhighfun.util.FunctionUtil;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;

public class GlobalPoolInitializer {

    public GlobalPoolInitializer (ExecutorService highPriorityTaskThreadPool, ExecutorService lowPriorityAsyncTaskThreadPool) throws NoSuchFieldException, IllegalAccessException {
        initialize(highPriorityTaskThreadPool, lowPriorityAsyncTaskThreadPool);
    }

    private void initialize(ExecutorService highPriorityTaskThreadPool, ExecutorService lowPriorityAsyncTaskThreadPool) throws NoSuchFieldException, IllegalAccessException {
        Field globalPool = FunctionUtil.class.getDeclaredField(Constants.HIGH_PRIORITY_TASK_THREAD_POOL);
        globalPool.setAccessible(true);
        globalPool.set(null, highPriorityTaskThreadPool);

        globalPool = FunctionUtil.class.getDeclaredField(Constants.LOW_PRIORITY_ASYNC_TASK_THREAD_POOL);
        globalPool.setAccessible(true);
        globalPool.set(null, lowPriorityAsyncTaskThreadPool);
    }
}
