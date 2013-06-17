package org.jhighfun.internal;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class ExecutorServiceProxy implements ExecutorService {

    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;
    private TimeUnit unit;
    private BlockingQueue<Runnable> workQueue;

    private ExecutorService executorService;

    public ExecutorServiceProxy(int corePoolSize,
                                int maximumPoolSize,
                                long keepAliveTime,
                                TimeUnit unit,
                                BlockingQueue<Runnable> workQueue) {

        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.unit = unit;
        this.workQueue = workQueue;
    }

    private void initializeExecutorService() {
        if (executorService != null) {
            executorService = new ThreadPoolExecutor(corePoolSize,
                    maximumPoolSize,
                    keepAliveTime,
                    unit,
                    workQueue);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    executorService.shutdownNow();
                }
            });
        }
    }

    public void shutdown() {
        initializeExecutorService();
        executorService.shutdown();
    }

    public List<Runnable> shutdownNow() {
        initializeExecutorService();
        return executorService.shutdownNow();
    }

    public boolean isShutdown() {
        initializeExecutorService();
        return executorService.isShutdown();
    }

    public boolean isTerminated() {
        initializeExecutorService();
        return executorService.isTerminated();
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        initializeExecutorService();
        return executorService.awaitTermination(timeout, unit);
    }

    public <T> Future<T> submit(Callable<T> task) {
        initializeExecutorService();
        return executorService.submit(task);
    }

    public <T> Future<T> submit(Runnable task, T result) {
        initializeExecutorService();
        return executorService.submit(task, result);
    }

    public Future<?> submit(Runnable task) {
        initializeExecutorService();
        return executorService.submit(task);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        initializeExecutorService();
        return executorService.invokeAll(tasks);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        initializeExecutorService();
        return executorService.invokeAll(tasks, timeout, unit);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        initializeExecutorService();
        return executorService.invokeAny(tasks);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        initializeExecutorService();
        return executorService.invokeAny(tasks, timeout, unit);
    }

    public void execute(Runnable command) {
        initializeExecutorService();
        executorService.execute(command);
    }
}
