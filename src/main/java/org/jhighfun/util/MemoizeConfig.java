package org.jhighfun.util;


import java.util.concurrent.TimeUnit;

public class MemoizeConfig {

    private final long unitValue;
    private final TimeUnit timeUnit;
    private final int size;

    public MemoizeConfig(long unitValue, TimeUnit timeUnit, int size) {
        this.unitValue = unitValue;
        this.timeUnit = timeUnit;
        this.size = size;
    }

    public long getUnitValue() {
        return unitValue;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getSize() {
        return size;
    }
}
