package org.jhighfun.util;


import java.util.concurrent.TimeUnit;

public class MemoizeConfig {

    private final long unitValue;
    private final TimeUnit timeUnit;

    public MemoizeConfig(long unitValue, TimeUnit timeUnit) {
        this.unitValue = unitValue;
        this.timeUnit = timeUnit;
    }

    public long getUnitValue() {
        return unitValue;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
