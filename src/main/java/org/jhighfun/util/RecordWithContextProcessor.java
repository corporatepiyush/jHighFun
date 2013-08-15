package org.jhighfun.util;

public interface RecordWithContextProcessor<T> {
    void process(T item, ParallelLoopExecutionContext parallelLoopExecutionContext);
}
