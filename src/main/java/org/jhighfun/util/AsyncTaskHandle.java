package org.jhighfun.util;

public final class AsyncTaskHandle<T> {

    private final AsyncTask<T> asyncTask;
    private final Throwable exception;
    private final T output;

    public AsyncTaskHandle(AsyncTask<T> asyncTask, T output, Throwable exception) {
        this.asyncTask = asyncTask;
        this.output = output;
        this.exception = exception;
    }

    public AsyncTask<T> getAsyncTask() {
        return asyncTask;
    }

    public Throwable getException() {
        return exception;
    }

    public T getOutput() {
        return output;
    }

}
