package org.jhighfun.util;

import java.util.concurrent.Callable;

/**
 * A future handle to a AsyncTask which also holds either output or exception
 * due to execution of AsyncTask task.
 *
 * @author Piyush Katariya
 */
public final class AsyncTaskHandle<T> {

    private final Callable<T> asyncTask;
    private final Throwable exception;
    private final T output;

    public AsyncTaskHandle(Callable<T> asyncTask, T output, Throwable exception) {
        this.asyncTask = asyncTask;
        this.output = output;
        this.exception = exception;
    }

    public Callable<T> getAsyncTask() {
        return this.asyncTask;
    }

    public Throwable getException() {
        return this.exception;
    }

    public T getOutput() {
        return this.output;
    }

}
