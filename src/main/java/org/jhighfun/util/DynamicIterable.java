package org.jhighfun.util;

import org.jhighfun.util.batch.*;

import java.util.Iterator;
import java.util.List;

/**
 * @author Piyush Katariya
 */
public class DynamicIterable<IN> implements Iterable<IN> {

    private final Iterator<IN> iterator;

    public DynamicIterable(Iterator<IN> iterator) {
        this.iterator = iterator;
    }

    public DynamicIterable(Iterable<IN> iterable) {
        this.iterator = iterable.iterator();
    }

    public <INIT> DynamicIterable(INIT initialInput, Function<INIT, Tuple2<INIT, IN>> function, Function<INIT, Boolean> predicate) {
        this.iterator = new LazyIterator<INIT, IN>(initialInput, function, predicate);
    }

    public <OUT> DynamicIterable<OUT> expand(Function<IN, Iterable<OUT>> function) {
        return new DynamicIterable<OUT>(new ExpansionIterator<IN, OUT>(iterator, function));
    }

    public DynamicIterable<List<IN>> batch(int batchSize) {
        return new DynamicIterable<List<IN>>(new BatchIterator<IN>(iterator, batchSize));
    }

    public <OUT> DynamicIterable<OUT> map(Function<IN, OUT> function) {
        return new DynamicIterable<OUT>(new MapperIterator<IN, OUT>(iterator, function));
    }

    public DynamicIterable<IN> filter(Function<IN, Boolean> function) {
        return new DynamicIterable<IN>(new ConditionalIterator<IN>(iterator, function));
    }

    public DynamicIterable<IN> filter(Function<IN, Boolean> function, Task<IN> task) {
        return new DynamicIterable<IN>(new ConditionalIterator<IN>(iterator, function, task));
    }

    public DynamicIterable<IN> execute(Task<IN> task) {
        return new DynamicIterable<IN>(new ExecutorIterator<IN>(iterator, task));
    }

    public DynamicIterable<IN> executeAsync(final Task<IN> task) {
        return new DynamicIterable<IN>(new ExecutorIterator<IN>(iterator, new Task<IN>() {
            public void execute(final IN input) {
                FunctionUtil.executeAsync(new Block() {
                    public void execute() {
                        task.execute(input);
                    }
                });
            }
        }));
    }

    public DynamicIterable<IN> executeLater(final Task<IN> task) {
        return new DynamicIterable<IN>(new ExecutorIterator<IN>(iterator, new Task<IN>() {
            public void execute(final IN input) {
                FunctionUtil.executeLater(new Block() {
                    public void execute() {
                        task.execute(input);
                    }
                });
            }
        }));
    }


    public DynamicIterable<IN> executeWithThrottle(final ExecutionThrottler executionThrottler, final Task<IN> task) {
        return new DynamicIterable<IN>(new ExecutorIterator<IN>(iterator, new Task<IN>() {
            public void execute(final IN input) {
                FunctionUtil.executeWithThrottle(executionThrottler, new Block() {
                    public void execute() {
                        task.execute(input);
                    }
                });
            }
        }));
    }

    public DynamicIterable<IN> executeAsyncWithThrottle(final ExecutionThrottler executionThrottler, final AsyncTask<IN> asyncTask, final CallbackTask<IN> callbackTask) {
        return new DynamicIterable<IN>(new ExecutorIterator<IN>(iterator, new Task<IN>() {
            public void execute(final IN input) {
                FunctionUtil.executeAsyncWithThrottle(executionThrottler, asyncTask, callbackTask);
            }
        }));
    }

    public DynamicIterable<IN> executeAsyncWithThrottle(final ExecutionThrottler executionThrottler, final Task<IN> task) {
        return new DynamicIterable<IN>(new ExecutorIterator<IN>(iterator, new Task<IN>() {
            public void execute(final IN input) {
                FunctionUtil.executeAsyncWithThrottle(executionThrottler, new Block() {
                    public void execute() {
                        task.execute(input);
                    }
                });
            }
        }));
    }

    public DynamicIterable<IN> executeWithGlobalLock(final Task<IN> task) {
        return new DynamicIterable<IN>(new ExecutorIterator<IN>(iterator, new Task<IN>() {
            public void execute(final IN input) {
                FunctionUtil.executeWithGlobalLock(new Block() {
                    public void execute() {
                        task.execute(input);
                    }
                });
            }
        }));
    }

    public DynamicIterable<IN> executeWithLock(final Operation operation, final Task<IN> task) {
        return new DynamicIterable<IN>(new ExecutorIterator<IN>(iterator, new Task<IN>() {
            public void execute(final IN input) {
                FunctionUtil.executeWithLock(operation, new Block() {
                    public void execute() {
                        task.execute(input);
                    }
                });
            }
        }));
    }

    public DynamicIterable<IN> ensureThreadSafety() {
        return new DynamicIterable<IN>(new ConcurrentIterator<IN>(iterator));
    }

    public Iterator<IN> iterator() {
        return iterator;
    }

    @Override
    public String toString() {

        StringBuilder string = new StringBuilder("[");
        if (iterator.hasNext()) {
            string.append(iterator.next().toString());
        }
        while (iterator.hasNext()) {
            string.append(", ");
            string.append(iterator.next().toString());
        }
        string.append("]");

        return string.toString();
    }
}
