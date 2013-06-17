package org.jhighfun.internal;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
                    return managedThreadPool;
                else
                    return getDefaultThreadPool(HIGH_PRIORITY);
            }

        } else {
            return getDefaultThreadPool(HIGH_PRIORITY);
        }

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
                    return managedThreadPool;
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
                    return managedThreadPool;
                else
                    return getDefaultThreadPool(LOW_PRIORITY);
            }

        } else {
            return getDefaultThreadPool(LOW_PRIORITY);
        }

    }

    private static ExecutorService getDefaultThreadPool(String priority) {

        ExecutorService executor = null;

        if (priority.equals(HIGH_PRIORITY)) {
            executor = getThreadPoolExecutor(0, Integer.MAX_VALUE, 5, TimeUnit.MINUTES, new SynchronousQueue<Runnable>());
        } else if (priority.equals(MEDIUM_PRIORITY)) {
            executor = getThreadPoolExecutor(20, 20, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        } else if (priority.equals(LOW_PRIORITY)) {
            executor = getThreadPoolExecutor(5, 5, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        }

        return executor;
    }

    private static ExecutorService getThreadPoolExecutor(int corePoolSize,
                                                         int maximumPoolSize,
                                                         long keepAliveTime,
                                                         TimeUnit unit,
                                                         BlockingQueue<Runnable> workQueue) {
        return new ExecutorServiceProxy(corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue);
    }

}

