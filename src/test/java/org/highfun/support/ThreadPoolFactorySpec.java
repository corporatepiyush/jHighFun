package org.highfun.support;

import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ThreadPoolFactorySpec {

    @Test
    public void testForJNDILookUp() {

        ExecutorService service = new ThreadPoolExecutor(1, 10, 5, TimeUnit.MINUTES, new SynchronousQueue<Runnable>());
        InitialContext context = null;

        System.setProperty("org.highfun.threadpool", "globalThreadPool");
        // Create initial context
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES,
                "org.apache.naming");

        try {
            context = new InitialContext();
            context.createSubcontext("java:");
            context.createSubcontext("java:/comp");
            context.createSubcontext("java:/comp/env");
            context.rebind("java:/comp/env/globalThreadPool", service);
        } catch (NamingException e) {
            e.printStackTrace();
        }

        assertTrue(ThreadPoolFactory.getThreadPool() == service);

        try {
            context.unbind("java:/comp/env/globalThreadPool");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ThreadPoolExecutor defaultPool = (ThreadPoolExecutor) ThreadPoolFactory.getThreadPool();

        assertEquals(defaultPool.getCorePoolSize(), 1);
        assertEquals(defaultPool.getMaximumPoolSize(), Integer.MAX_VALUE);
        assertEquals(defaultPool.getKeepAliveTime(TimeUnit.MINUTES), 5);
        assertEquals(defaultPool.getQueue().getClass(), new SynchronousQueue<Runnable>().getClass());
    }

}