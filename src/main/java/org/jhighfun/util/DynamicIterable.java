package org.jhighfun.util;

import org.jhighfun.util.batch.*;

import java.util.Iterator;
import java.util.LinkedList;
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

    public DynamicIterable<IN> _ensureThreadSafety() {
        return new DynamicIterable<IN>(new ConcurrentIterator<IN>(iterator));
    }


    public DynamicIterable<IN> processExclusively() {
        final List<IN> list = new LinkedList<IN>();
        final Tuple2<String, Throwable> exception = new Tuple2<String, Throwable>("Exception", null);
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (iterator.hasNext()) {
                        list.add(iterator().next());
                    }
                } catch (Throwable e) {
                    exception._2 = e;
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (exception._2 != null) {
            throw new RuntimeException(exception._2);
        }

        return new DynamicIterable<IN>(list);
    }

    public DynamicIterable<IN> process() {
        final List<IN> list = new LinkedList<IN>();
        while (iterator.hasNext()) {
            list.add(iterator().next());
        }
        return new DynamicIterable<IN>(list);
    }

    public CollectionFunctionChain<IN> processAndChain() {
        return new CollectionFunctionChain<IN>(extract());
    }

    public List<IN> extract() {
        final List<IN> list = new LinkedList<IN>();
        while (iterator.hasNext()) {
            list.add(iterator().next());
        }
        return list;
    }

    public <O> O extract(Function<Iterable<IN>, O> extractor) {
        return extractor.apply(this);
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
