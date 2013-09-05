package org.jhighfun.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ParallelLoopExecutionContext {

    private volatile AtomicBoolean interrupted = new AtomicBoolean(false);
    private volatile AtomicInteger recordCount = new AtomicInteger(0);
    private final Lock lock = new ReentrantLock(true);

    public void endLoop() {
        this.interrupted.set(true);
    }

    public boolean isInterrupted() {
        return this.interrupted.get();
    }

    void incrementRecordExecutionCount() {
        recordCount.getAndIncrement();
    }

    void incrementRecordExecutionCountBy(int newCount) {
        recordCount.getAndSet(newCount);
    }

    public int currentRecordExecutionCount() {
        return recordCount.get();
    }

    public void executeAtomic(Block block) {
        lock.lock();
        try {
            block.execute();
        } finally {
            lock.unlock();
        }
    }

}
