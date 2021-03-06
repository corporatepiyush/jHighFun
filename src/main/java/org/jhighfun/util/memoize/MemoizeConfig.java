package org.jhighfun.util.memoize;


import java.util.concurrent.TimeUnit;

/**
 * Memoization configuration parameter
 * <p/>
 * 1. Maximum time to live for a cache object
 * 2. Maximum size of In-Memory cache
 *
 * @author Piyush Katariya
 */

public final class MemoizeConfig {

    private final long timeValue;
    private final TimeUnit timeUnit;
    private final int size;

    public MemoizeConfig(long timeValue, TimeUnit timeUnit, int size) {
        this.timeValue = timeValue;
        this.timeUnit = timeUnit;
        this.size = size;
    }

    public long getTimeValue() {
        return timeValue;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getSize() {
        return size;
    }
}
