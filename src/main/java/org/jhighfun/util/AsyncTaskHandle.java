package org.jhighfun.util;

public class AsyncTaskHandle<T> {

    private final AsyncTask<T> asyncTask;
    private final Throwable exception;

    public AsyncTaskHandle(AsyncTask<T> asyncTask, Throwable exception) {
        this.asyncTask = asyncTask;
        this.exception = exception;
    }

    public AsyncTask<T> getAsyncTask() {
        return asyncTask;
    }

    public Throwable getException() {
        return exception;
    }
}
