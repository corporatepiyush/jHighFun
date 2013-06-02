package org.jhighfun.util;

/**
 * A task which produces some outcome and to be executed in asynchronous fashion
 *
 * @author Piyush Katariya
 */

public interface AsyncTask<T> {
    public T execute();
}
