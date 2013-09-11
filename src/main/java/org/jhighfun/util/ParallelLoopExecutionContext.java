package org.jhighfun.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ParallelLoopExecutionContext {

    private AtomicBoolean interrupted = new AtomicBoolean(false);
    private volatile Map<Long, Integer> recordCountMap = new HashMap<Long, Integer>();
    private final Lock lock = new ReentrantLock(true);

    public void endLoop() {
        this.interrupted.set(true);
    }

    public boolean isInterrupted() {
        return this.interrupted.get();
    }

    void incrementRecordExecutionCount() {
        incrementRecordExecutionCountBy(1);
    }

    void incrementRecordExecutionCountBy(int newCount) {
        long threadId = Thread.currentThread().getId();
        Integer currentCount = this.recordCountMap.get(threadId);
        this.recordCountMap.put(threadId, currentCount == null ? 1 : currentCount + newCount);
    }

    public int currentRecordExecutionCount() {
        int count = 0;
        for (Map.Entry<Long, Integer> entry : this.recordCountMap.entrySet()) {
            count += entry.getValue();
        }
        return count;
    }

    public void executeAtomic(Block block) {
        this.lock.lock();
        try {
            block.execute();
        } finally {
            this.lock.unlock();
        }
    }

}
