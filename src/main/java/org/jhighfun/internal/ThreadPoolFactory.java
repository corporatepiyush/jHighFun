package org.jhighfun.internal;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * jHighFun library uses three global ThreadPoolExecutors
 * <p/>
 * 1. HighPriorityTask thread pool - used for all multi-threaded/concurrent operations supported by framework
 * 2. MediumPriorityTask thread pool - used for executing medium priority business task execution in async fashion
 * which does not help generate the output which is necessary to be sent out as response
 * 3. LowPriorityTask thread pool - used for low priority tasks such as event logging etc. which are not part of business logic and
 * hence not important to be executed in synchronous fashion.
 *
 * @author Piyush Katariya
 */

public class ThreadPoolFactory {

    private static String HIGH_PRIORITY = "hp";
    private static String MEDIUM_PRIORITY = "mp";
    private static String LOW_PRIORITY = "lp";

    public static ExecutorService getHighPriorityTaskThreadPool() {
        Context context = null;
        try {
            context = new InitialContext();
        } catch (NamingException e) {
            System.err.println("Error while looking up for 'org.jhighfun.hpthreadpool' system property, falling back to default AsyncHighPriorityThreadPool.");
        }

        if (context != null) {
            ExecutorService managedThreadPool = null;
            try {
                managedThreadPool = (ExecutorService) context.lookup("java:/comp/env/" + System.getProperty("org.jhighfun.hpthreadpool"));
            } catch (Exception e) {
                System.err.println("Error while looking up for 'org.jhighfun.hpthreadpool' system property, falling back to default AsyncHighPriorityThreadPool.");
            } finally {
                if (managedThreadPool != null)
                    return embrace(managedThreadPool);
                else
                    return getDefaultThreadPool(HIGH_PRIORITY);
            }

        } else {
            return getDefaultThreadPool(HIGH_PRIORITY);
        }

    }

    private static ExecutorService embrace(final ExecutorService executorService) {
        return new ExecutorService() {
            public void shutdown() {
                executorService.shutdown();
            }

            public List<Runnable> shutdownNow() {
                return executorService.shutdownNow();
            }

            public boolean isShutdown() {
                return executorService.isShutdown();
            }

            public boolean isTerminated() {
                return executorService.isTerminated();
            }

            public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
                return executorService.awaitTermination(timeout, unit);
            }

            public <T> Future<T> submit(Callable<T> task) {
                return executorService.submit(embrace(task));
            }

            public <T> Future<T> submit(Runnable task, T result) {
                return executorService.submit(embrace(task), result);
            }

            public Future<?> submit(Runnable task) {
                return executorService.submit(embrace(task));
            }

            public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
                return executorService.invokeAll(tasks);
            }

            public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
                return executorService.invokeAll(tasks, timeout, unit);
            }

            public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
                return executorService.invokeAny(tasks);
            }

            public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return executorService.invokeAny(tasks, timeout, unit);
            }

            public void execute(Runnable command) {
                executorService.execute(embrace(command));
            }
        };
    }

    public static ExecutorService getMediumPriorityAsyncTaskThreadPool() {
        Context context = null;
        try {
            context = new InitialContext();
        } catch (NamingException e) {
            System.err.println("Error while looking up for 'org.jhighfun.mpthreadpool' system property, falling back to default AsyncMediumPriorityThreadPool.");
        }

        if (context != null) {
            ExecutorService managedThreadPool = null;
            try {
                managedThreadPool = (ExecutorService) context.lookup("java:/comp/env/" + System.getProperty("org.jhighfun.mpthreadpool"));
            } catch (Exception e) {
                System.err.println("Error while looking up for 'org.jhighfun.mpthreadpool' system property, falling back to default AsyncMediumPriorityThreadPool.");
            } finally {
                if (managedThreadPool != null)
                    return embrace(managedThreadPool);
                else
                    return getDefaultThreadPool(MEDIUM_PRIORITY);
            }

        } else {
            return getDefaultThreadPool(MEDIUM_PRIORITY);
        }

    }

    public static ExecutorService getLowPriorityAsyncTaskThreadPool() {
        Context context = null;
        try {
            context = new InitialContext();
        } catch (NamingException e) {
            System.err.println("Error while looking up for 'org.jhighfun.lpthreadpool' system property, falling back to default AsyncLowPriorityThreadPool.");
        }

        if (context != null) {
            ExecutorService managedThreadPool = null;
            try {
                managedThreadPool = (ExecutorService) context.lookup("java:/comp/env/" + System.getProperty("org.jhighfun.lpthreadpool"));
            } catch (Exception e) {
                System.err.println("Error while looking up for 'org.jhighfun.lpthreadpool' system property, falling back to default AsyncLowPriorityThreadPool.");
            } finally {
                if (managedThreadPool != null)
                    return embrace(managedThreadPool);
                else
                    return getDefaultThreadPool(LOW_PRIORITY);
            }

        } else {
            return getDefaultThreadPool(LOW_PRIORITY);
        }

    }

    private static ExecutorService getDefaultThreadPool(String priority) {

        ThreadPoolExecutor executor = null;

        if (priority.equals(HIGH_PRIORITY)) {
            executor = getThreadPoolExecutor(0, Integer.MAX_VALUE, 5, TimeUnit.MINUTES, new SynchronousQueue<Runnable>());
        } else if (priority.equals(MEDIUM_PRIORITY)) {
            executor = getThreadPoolExecutor(20, 20, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        } else if (priority.equals(LOW_PRIORITY)) {
            executor = getThreadPoolExecutor(5, 5, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        }

        final ThreadPoolExecutor executorDummy = executor;

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                executorDummy.shutdownNow();
            }
        });

        return executor;
    }

    private static ThreadPoolExecutor getThreadPoolExecutor(int corePoolSize,
                                                            int maximumPoolSize,
                                                            long keepAliveTime,
                                                            TimeUnit unit,
                                                            BlockingQueue<Runnable> workQueue) {
        return new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue) {
            public <T> Future<T> submit(Callable<T> task) {
                return super.submit(embrace(task));
            }

            public <T> Future<T> submit(Runnable task, T result) {
                return super.submit(embrace(task), result);
            }

            public Future<?> submit(Runnable task) {
                return super.submit(embrace(task));
            }
        };
    }


    private static Runnable embrace(final Runnable runnable) {

        final Object threadVariable = new ThreadLocal().get();

        return new Runnable() {
            public void run() {
                ThreadLocal local = new ThreadLocal();
                local.remove();
                local.set(threadVariable);
                runnable.run();
            }
        };
    }


    private static Callable embrace(final Callable callable) {

        final Object threadVariable = new ThreadLocal().get();

        return new Callable() {
            public Object call() throws Exception {
                ThreadLocal local = new ThreadLocal();
                local.remove();
                local.set(threadVariable);
                return callable.call();
            }
        };
    }

}

