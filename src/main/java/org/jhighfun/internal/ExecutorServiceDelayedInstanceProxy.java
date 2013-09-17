package org.jhighfun.internal;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class ExecutorServiceDelayedInstanceProxy implements ExecutorService {

    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;
    private TimeUnit unit;
    private BlockingQueue<Runnable> workQueue;

    private ExecutorService executorService;

    public ExecutorServiceDelayedInstanceProxy(int corePoolSize,
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

    private final ExecutorService getExecutorService() {
        if (this.executorService == null) {
            this.executorService = new ThreadPoolExecutor(this.corePoolSize,
                    this.maximumPoolSize,
                    this.keepAliveTime,
                    this.unit,
                    this.workQueue);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    executorService.shutdownNow();
                }
            });
        }
        return executorService;
    }

    public void shutdown() {
        getExecutorService().shutdown();
    }

    public List<Runnable> shutdownNow() {
        return getExecutorService().shutdownNow();
    }

    public boolean isShutdown() {
        return getExecutorService().isShutdown();
    }

    public boolean isTerminated() {
        return getExecutorService().isTerminated();
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return getExecutorService().awaitTermination(timeout, unit);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return getExecutorService().submit(task);
    }

    public <T> Future<T> submit(Runnable task, T result) {
        return getExecutorService().submit(task, result);
    }

    public Future<?> submit(Runnable task) {
        return getExecutorService().submit(task);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return getExecutorService().invokeAll(tasks);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return getExecutorService().invokeAll(tasks, timeout, unit);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return getExecutorService().invokeAny(tasks);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return getExecutorService().invokeAny(tasks, timeout, unit);
    }

    public void execute(Runnable command) {
        getExecutorService().execute(command);
    }
}
