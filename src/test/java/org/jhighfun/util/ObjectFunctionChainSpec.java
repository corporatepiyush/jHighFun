package org.jhighfun.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static org.jhighfun.util.CollectionUtil.IntRange;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ObjectFunctionChainSpec {

    @Spy
    ExecutorService spyHighPriorityTaskThreadPool = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 1, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

    @Spy
    ExecutorService spyLowPriorityAsyncTaskThreadPool = new ThreadPoolExecutor(0, 5, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());


    @Before
    public void before() {
        try {
            Field globalPool = FunctionUtil.class.getDeclaredField("highPriorityTaskThreadPool");
            globalPool.setAccessible(true);
            globalPool.set(null, spyHighPriorityTaskThreadPool);

            globalPool = FunctionUtil.class.getDeclaredField("lowPriorityAsyncTaskThreadPool");
            globalPool.setAccessible(true);
            globalPool.set(null, spyLowPriorityAsyncTaskThreadPool);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testExtract() {
        Object object = new Object();
        assertTrue(new ObjectFunctionChain<Object>(object).extract() == object);
    }

    @Test
    public void testTransform() {
        Object object = new Object();
        ObjectFunctionChain<Object> chain = new ObjectFunctionChain<Object>(object);

        Function mockConverter = mock(Function.class);
        chain.transform(mockConverter);
        verify(mockConverter, times(1)).apply(object);

    }

    @Test
    public void testAsCollection() {
        Object object = new Object();
        ObjectFunctionChain<Object> objectFunctionChain = new ObjectFunctionChain<Object>(object);

        CollectionFunctionChain<Object> collectionFunctionChain = objectFunctionChain.asCollection();

        Collection<Object> objectCollection = collectionFunctionChain.extract();

        assertTrue(objectCollection.size() == 1);
        assertTrue(objectCollection.contains(object));
    }

    @Test
    public void testToCollection() {
        Integer upper = 100;

        ObjectFunctionChain<Integer> objectFunctionChain = new ObjectFunctionChain<Integer>(upper);

        CollectionFunctionChain<Integer> collectionFunctionChain = objectFunctionChain.toCollection(new Function<Integer, List<Integer>>() {
            public List<Integer> apply(Integer arg) {
                return IntRange(1, arg);
            }
        });

        List<Integer> objectCollection = collectionFunctionChain.extract();

        assertEquals(objectCollection, IntRange(1, upper));
    }

    @Test
    public void testExecute() {

        Object object = new Object();
        ObjectFunctionChain<Object> objectFunctionChain = new ObjectFunctionChain<Object>(object);
        Task<Object> mockTask = mock(Task.class);
        objectFunctionChain.execute(mockTask);

        verify(mockTask, times(1)).execute(object);

    }

    @Test
    public void testExecuteAsync() {

        ObjectFunctionChain<String> chain = new ObjectFunctionChain<String>("Scala");
        Task<String> mockTask = mock(Task.class);

        chain.executeAsync(mockTask);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(mockTask, times(1)).execute("Scala");
        verify(spyHighPriorityTaskThreadPool, times(1)).submit(any((Runnable.class)));
    }

    @Test
    public void testExecuteLater() {

        ObjectFunctionChain<String> chain = new ObjectFunctionChain<String>("Scala");
        Task<String> mockTask = mock(Task.class);

        chain.executeLater(mockTask);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(mockTask, times(1)).execute("Scala");
        verify(spyLowPriorityAsyncTaskThreadPool, times(1)).submit(any((Runnable.class)));
    }

    public void testFork() {

        String object = "Scala";

        ObjectFunctionChain<String> chain = new ObjectFunctionChain<String>(object);
        assertTrue(chain.fork().getClass() == ObjectForkAndJoin.class);

    }

    @Test
    public void testExecuteWithGlobalLockWithMultipleThread() {

        final List<Integer> list = new LinkedList<Integer>();

        List<Integer> load = CollectionUtil.List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        for (int i = 0; i < 10000; list.add(i), i++) ;

        final Block spyBlock = spy(new Block() {
            public void execute() {
                list.add(1);
                for (Integer i : list) ;
                list.add(2);
            }
        });

        FunctionUtil.each(load, new RecordProcessor<Integer>() {
            public void process(Integer item) {
                new ObjectFunctionChain<Integer>(1).executeWithGlobalLock(new Task<Integer>() {
                    public void execute(Integer input) {
                        spyBlock.execute();
                    }
                });
            }
        }, FunctionUtil.parallel(10));

        verify(spyBlock, times(10)).execute();
    }

    private static ConcurrentHashMap<ExecutionThrottler, ExecutorService> mapSpy;
    private static ExecutionThrottler throttler;

    public static void init() throws Exception {

        Field throttlerPoolMap = FunctionUtil.class.getDeclaredField("throttlerPoolMap");
        throttlerPoolMap.setAccessible(true);
        mapSpy = spy(new ConcurrentHashMap<ExecutionThrottler, ExecutorService>(15, 0.9f, 32));
        throttlerPoolMap.set(null, mapSpy);

        String identity = "some operation";
        throttler = FunctionUtil.throttler(identity);
        FunctionUtil.registerPool(throttler, 10);

    }

    @Test
    public void testExecuteWithThrottle() throws Exception {
        init();
        Task<List<Integer>> taskMock = mock(Task.class);

        List<Integer> integerList = IntRange(1, 100);
        ObjectFunctionChain<List<Integer>> chain = new ObjectFunctionChain<List<Integer>>(integerList);

        chain.executeWithThrottle(throttler, taskMock);

        verify(mapSpy, times(1)).get(throttler);

        verify(taskMock, times(1)).execute(integerList);

    }

    @Test
    public void testExecuteAsyncWithThrottle() throws Exception {
        init();
        Task<List<Integer>> taskMock = mock(Task.class);

        List<Integer> integerList = IntRange(1, 100);
        ObjectFunctionChain<List<Integer>> chain = new ObjectFunctionChain<List<Integer>>(integerList);

        chain.executeAsyncWithThrottle(throttler, taskMock);

        verify(mapSpy, times(1)).get(throttler);
        Thread.sleep(200);

        verify(taskMock, times(1)).execute(integerList);

    }

    @Test
    public void testExecuteAsyncWithThrottle_callback() throws Exception {
        init();
        Function<List<Integer>, Object> function = mock(Function.class);
        CallbackTask<Object> callbackTask = mock(CallbackTask.class);
        Object asyncTaskResult = new Object();
        List<Integer> integerList = IntRange(1, 100);
        ObjectFunctionChain<List<Integer>> chain = new ObjectFunctionChain<List<Integer>>(integerList);


        when(function.apply(integerList)).thenReturn(asyncTaskResult);

        chain.executeAsyncWithThrottle(throttler, function, callbackTask);

        verify(mapSpy, times(1)).get(throttler);
        Thread.sleep(200);
        ArgumentCaptor<AsyncTaskHandle> argument = ArgumentCaptor.forClass(AsyncTaskHandle.class);
        verify(callbackTask, times(1)).execute(argument.capture());

        assertEquals(argument.getValue().getOutput(), asyncTaskResult);
        assertEquals(argument.getValue().getException(), null);
    }

}
