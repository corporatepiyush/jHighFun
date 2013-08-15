package org.jhighfun.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelLoopExecutionContext {

    private volatile AtomicBoolean interrupted = new AtomicBoolean(false);
    private volatile AtomicInteger recordCount = new AtomicInteger(0);

    public void endLoop() {
        this.interrupted.set(true);
    }

    public boolean isInterrupted() {
        return this.interrupted.get();
    }

    void incrementRecordExecutionCount() {
        recordCount.getAndIncrement();
    }

    public int currentRecordExecutionCount() {
        return recordCount.get();
    }


}
