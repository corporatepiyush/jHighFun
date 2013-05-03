package org.jhighfun.internal;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.concurrent.*;


public class ThreadPoolFactory {

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
                    return getDefaultThreadPool("hp");
            }

        } else {
            return getDefaultThreadPool("hp");
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
                    return getDefaultThreadPool("mp");
            }

        } else {
            return getDefaultThreadPool("mp");
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
                    return getDefaultThreadPool("lp");
            }

        } else {
            return getDefaultThreadPool("lp");
        }

    }

    private static ExecutorService getDefaultThreadPool(String priority) {

        ThreadPoolExecutor executor = null;

        if (priority.equals("hp")) {
            executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 5, TimeUnit.MINUTES, new SynchronousQueue<Runnable>());
        } else if (priority.equals("mp")) {
            executor = new ThreadPoolExecutor(0, 100, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        } else if (priority.equals("lp")) {
            executor = new ThreadPoolExecutor(0, 5, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
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
