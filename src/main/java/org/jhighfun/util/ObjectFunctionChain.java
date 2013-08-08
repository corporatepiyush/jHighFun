package org.jhighfun.util;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Cascading interface which enables availability of utility methods which cam be invoked on
 * any object to compose/write clean business logic flow of any kind
 *
 * @author Piyush Katariya
 */

public final class ObjectFunctionChain<I> {

    private I object;

    public ObjectFunctionChain(I object) {
        this.object = object;
    }

    public <O> ObjectFunctionChain<O> transform(Function<I, O> converter) {
        return new ObjectFunctionChain<O>(converter.apply(object));
    }

    public CollectionFunctionChain<I> asCollection() {
        final List<I> collection = new LinkedList<I>();
        collection.add(object);
        return new CollectionFunctionChain<I>(collection);
    }

    public <O> CollectionFunctionChain<O> toCollection(Function<I, List<O>> converter) {
        return new CollectionFunctionChain<O>(converter.apply(object));
    }

    public ObjectForkAndJoin<I> fork() {
        return new ObjectForkAndJoin<I>(this);
    }

    public ObjectFunctionChain<I> execute(final Task<I> task) {
        task.execute(object);
        return this;
    }

    public ObjectFunctionChain<I> executeAsync(final Task<I> task) {
        FunctionUtil.executeAsync(new Runnable() {
            public void run() {
                task.execute(object);
            }
        });
        return this;
    }

    public ObjectFunctionChain<I> executeLater(final Task<I> task) {
        FunctionUtil.executeLater(new Runnable() {
            public void run() {
                task.execute(object);
            }
        });
        return this;
    }

    public ObjectFunctionChain<I> executeWithGlobalLock(final Task<I> task) {
        FunctionUtil.executeWithGlobalLock(new Block() {
            public void execute() {
                task.execute(object);
            }
        });
        return this;
    }

    public ObjectFunctionChain<I> executeWithLock(Operation operation, final Task<I> task) {
        FunctionUtil.executeWithLock(operation, new Block() {
            public void execute() {
                task.execute(object);
            }
        });
        return this;
    }

    public ObjectFunctionChain<I> executeWithThrottle(ExecutionThrottler executionThrottler, final Task<I> task) {
        FunctionUtil.executeWithThrottle(executionThrottler, new Runnable() {
            public void run() {
                task.execute(object);
            }
        });
        return this;
    }

    public ObjectFunctionChain<I> executeAwait(final Task<I> task, Integer time, TimeUnit timeUnit) {
        FunctionUtil.executeAwait(new Runnable() {
            public void run() {
                task.execute(object);
            }
        }, time, timeUnit);
        return this;
    }

    public ObjectFunctionChain<I> executeAsyncWithThrottle(ExecutionThrottler executionThrottler, final Task<I> task) {
        FunctionUtil.executeAsyncWithThrottle(executionThrottler, new Runnable() {
            public void run() {
                task.execute(object);
            }
        });
        return this;
    }

    public <O> ObjectFunctionChain<I> executeAsyncWithThrottle(ExecutionThrottler executionThrottler, final Function<I, O> asyncTask, final CallbackTask<O> callbackTask) {
        FunctionUtil.executeAsyncWithThrottle(executionThrottler, new Callable<O>() {
            public O call() {
                return asyncTask.apply(object);
            }
        }, callbackTask);

        return this;
    }

    public I extract() {
        return object;
    }

    public <O> O extract(Function<I, O> extractor) {
        return extractor.apply(this.object);
    }

    @Override
    public String toString() {
        return object.toString();
    }
}
