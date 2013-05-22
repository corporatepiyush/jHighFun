package org.jhighfun.internal;

import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.concurrent.*;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ThreadPoolFactorySpec {

    static InitialContext context = null;

    static {

        System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES,
                "org.apache.naming");

        try {
            context = new InitialContext();
            context.createSubcontext("java:");
            context.createSubcontext("java:/comp");
            context.createSubcontext("java:/comp/env");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJNDILookUpForGlobalPool() {

        ExecutorService service = new ThreadPoolExecutor(0, 10, 5, TimeUnit.MINUTES, new SynchronousQueue<Runnable>());

        System.setProperty("org.jhighfun.hpthreadpool", "globalThreadPool");

        try {
            context.rebind("java:/comp/env/globalThreadPool", service);
        } catch (NamingException e) {
            e.printStackTrace();
        }

        assertTrue(ThreadPoolFactory.getHighPriorityTaskThreadPool() == service);

        try {
            context.unbind("java:/comp/env/globalThreadPool");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ThreadPoolExecutor defaultPool = (ThreadPoolExecutor) ThreadPoolFactory.getHighPriorityTaskThreadPool();

        assertEquals(defaultPool.getCorePoolSize(), 0);
        assertEquals(defaultPool.getMaximumPoolSize(), Integer.MAX_VALUE);
        assertEquals(defaultPool.getKeepAliveTime(TimeUnit.MINUTES), 5);
        assertEquals(defaultPool.getQueue().getClass(), new SynchronousQueue<Runnable>().getClass());
    }

    @Test
    public void testJNDILookUpForGlobalAsyncPool() {

        ExecutorService service = new ThreadPoolExecutor(0, 100, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());

        System.setProperty("org.jhighfun.mpthreadpool", "asyncGlobalThreadPool");

        try {
            context.rebind("java:/comp/env/asyncGlobalThreadPool", service);
        } catch (NamingException e) {
            e.printStackTrace();
        }

        assertTrue(ThreadPoolFactory.getMediumPriorityAsyncTaskThreadPool() == service);

        try {
            context.unbind("java:/comp/env/asyncGlobalThreadPool");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ThreadPoolExecutor defaultPool = (ThreadPoolExecutor) ThreadPoolFactory.getMediumPriorityAsyncTaskThreadPool();

        assertEquals(defaultPool.getCorePoolSize(), 20);
        assertEquals(defaultPool.getMaximumPoolSize(), 20);
        assertEquals(defaultPool.getKeepAliveTime(TimeUnit.MINUTES), 5);
        assertEquals(defaultPool.getQueue().getClass(), new LinkedBlockingQueue<Runnable>().getClass());
    }


    @Test
    public void testJNDILookUpForGlobalAsyncLowPriorityPool() {

        ExecutorService service = new ThreadPoolExecutor(0, 5, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());

        System.setProperty("org.jhighfun.lpthreadpool", "asyncLowPriorityGlobalThreadPool");

        try {
            context.rebind("java:/comp/env/asyncLowPriorityGlobalThreadPool", service);
        } catch (NamingException e) {
            e.printStackTrace();
        }

        assertTrue(ThreadPoolFactory.getLowPriorityAsyncTaskThreadPool() == service);

        try {
            context.unbind("java:/comp/env/asyncLowPriorityGlobalThreadPool");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ThreadPoolExecutor defaultPool = (ThreadPoolExecutor) ThreadPoolFactory.getLowPriorityAsyncTaskThreadPool();

        assertEquals(defaultPool.getCorePoolSize(), 5);
        assertEquals(defaultPool.getMaximumPoolSize(), 5);
        assertEquals(defaultPool.getKeepAliveTime(TimeUnit.MINUTES), 5);
        assertEquals(defaultPool.getQueue().getClass(), new LinkedBlockingQueue<Runnable>().getClass());
    }

}