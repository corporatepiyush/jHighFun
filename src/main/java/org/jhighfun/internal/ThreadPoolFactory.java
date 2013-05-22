package org.jhighfun.internal;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.concurrent.*;


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

        ThreadPoolExecutor executor = null;

        if (priority.equals(HIGH_PRIORITY)) {
            executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 5, TimeUnit.MINUTES, new SynchronousQueue<Runnable>());
        } else if (priority.equals(MEDIUM_PRIORITY)) {
            executor = new ThreadPoolExecutor(20, 20, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        } else if (priority.equals(LOW_PRIORITY)) {
            executor = new ThreadPoolExecutor(5, 5, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        }

        final ThreadPoolExecutor executorDummy = executor;

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                executorDummy.shutdownNow();
            }
        });

        return executor;
    }
}
