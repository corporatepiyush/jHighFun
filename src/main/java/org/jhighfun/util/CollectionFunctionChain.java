package org.jhighfun.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Cascading interface which enables availability of utility methods which cam be invoked on
 * List data structure to compose/write clean business logic flow of any kind, more suitable for batch
 * programming.
 *
 * @author Piyush Katariya
 */

public final class CollectionFunctionChain<I> {

    private List<I> collection;

    public CollectionFunctionChain(List<I> collection) {
        this.collection = collection;
    }

    public <O> ObjectFunctionChain<O> transformToObject(Function<List<I>, O> converter) {
        return new ObjectFunctionChain<O>(converter.apply(this.collection));
    }

    public ObjectFunctionChain<List<I>> asObject() {
        return new ObjectFunctionChain<List<I>>(this.collection);
    }

    public <O> CollectionFunctionChain<O> transform(Function<List<I>, List<O>> converter) {
        return new CollectionFunctionChain<O>(converter.apply(this.collection));
    }

    public CollectionFunctionChain<I> flatten() {
        if (this.collection.get(0) instanceof Iterable) {
            List<I> newList = new LinkedList<I>();
            List<? extends Iterable<I>> collection = (List<? extends Iterable<I>>) this.collection;
            for (Iterable<I> inner : collection) {
                for (I i : inner) {
                    newList.add(i);
                }
            }
            return new CollectionFunctionChain<I>(newList);
        } else {
            throw new RuntimeException("Cannot perform flatten operation");
        }
    }

    public <O> CollectionFunctionChain<O> map(Function<I, O> converter) {
        return new CollectionFunctionChain<O>(FunctionUtil.map(this.collection, converter));
    }

    public <O> CollectionFunctionChain<O> map(Function<I, O> converter, WorkDivisionStrategy workDivisionStrategy) {
        return new CollectionFunctionChain<O>(FunctionUtil.map(this.collection, converter, workDivisionStrategy));
    }

    public CollectionFunctionChain<I> filter(Function<I, Boolean> predicate) {
        this.collection = FunctionUtil.filter(this.collection, predicate);
        return this;
    }

    public CollectionFunctionChain<I> filter(Function<I, Boolean> predicate, WorkDivisionStrategy workDivisionStrategy) {
        this.collection = FunctionUtil.filter(this.collection, predicate, workDivisionStrategy);
        return this;
    }

    public CollectionFunctionChain<I> sortWith(Comparator<I> comparator) {
        this.collection = FunctionUtil.sortWith(this.collection, comparator);
        return this;
    }

    public CollectionFunctionChain<I> sort() {
        this.collection = FunctionUtil.sort(this.collection);
        return this;
    }

    public CollectionFunctionChain<I> sortBy(String memberVar, String... memberVars) {
        this.collection = FunctionUtil.sortBy(this.collection, memberVar, memberVars);
        return this;
    }

    public CollectionFunctionChain<I> each(RecordProcessor<I> recordProcessor) {
        FunctionUtil.each(this.collection, recordProcessor);
        return this;
    }

    public CollectionFunctionChain<I> eachWithIndex(RecordWithIndexProcessor<I> recordWithIndexProcessor) {
        FunctionUtil.eachWithIndex(this.collection, recordWithIndexProcessor);
        return this;
    }

    public CollectionFunctionChain<I> each(RecordProcessor<I> recordProcessor, WorkDivisionStrategy workDivisionStrategy) {
        FunctionUtil.each(this.collection, recordProcessor, workDivisionStrategy);
        return this;
    }

    public <ACCUM> ObjectFunctionChain<ACCUM> foldLeft(ACCUM accum,
                                                       Accumulator<ACCUM, I> accumulator) {
        return new ObjectFunctionChain<ACCUM>(FunctionUtil.foldLeft(this.collection, accum, accumulator));
    }

    public <ACCUM> ObjectFunctionChain<ACCUM> foldRight(ACCUM accum,
                                                        Accumulator<ACCUM, I> accumulator) {
        return new ObjectFunctionChain<ACCUM>(FunctionUtil.foldRight(this.collection, accum, accumulator));
    }

    public ObjectFunctionChain<I> reduce(Accumulator<I, I> accumulator) {
        return new ObjectFunctionChain<I>(FunctionUtil.reduce(this.collection, accumulator));
    }

    public ObjectFunctionChain<I> reduce(Accumulator<I, I> accumulator, WorkDivisionStrategy workDivisionStrategy) {
        return new ObjectFunctionChain<I>(FunctionUtil.reduce(this.collection, accumulator, workDivisionStrategy));
    }

    public ObjectFunctionChain<Boolean> every(Function<I, Boolean> predicate) {
        return new ObjectFunctionChain<Boolean>(FunctionUtil.every(this.collection, predicate));
    }

    public ObjectFunctionChain<Boolean> any(Function<I, Boolean> predicate) {
        return new ObjectFunctionChain<Boolean>(FunctionUtil.any(this.collection, predicate));
    }

    public ObjectFunctionChain<Integer> count(Function<I, Boolean> predicate) {
        return new ObjectFunctionChain<Integer>(FunctionUtil.count(this.collection, predicate));
    }

    public CollectionFunctionChain<I> extractWithIndex(Function<Integer, Boolean> predicate) {
        this.collection = FunctionUtil.extractWithIndex(this.collection, predicate);
        return this;
    }

    public CollectionFunctionChain<I> plus(Iterable<I> iterable) {
        if (iterable instanceof Collection) {
            this.collection.addAll((Collection<I>) iterable);
        } else {
            for (I i : iterable) {
                this.collection.add(i);
            }
        }
        return this;
    }

    public CollectionFunctionChain<I> minus(Iterable<I> iterable) {
        if (iterable instanceof Collection) {
            this.collection.removeAll((Collection<I>) iterable);
        } else {
            for (I i : iterable) {
                this.collection.remove(i);
            }
        }
        return this;
    }

    public CollectionFunctionChain<I> union(Iterable<I> iterable) {
        for (I item : iterable) {
            if (!this.collection.contains(item))
                this.collection.add(item);
        }
        return this;
    }


    public CollectionFunctionChain<I> intersect(Iterable<I> iterable) {
        final List<I> commonElements = getCollection();
        for (I item : iterable) {
            if (this.collection.contains(item))
                commonElements.add(item);
        }
        return new CollectionFunctionChain<I>(commonElements);
    }


    public CollectionFunctionChain<I> slice(int from, int to) {
        final List<I> sliced = getCollection();
        int index = 0;
        for (I item : this.collection) {
            if (index >= from && index <= to) {
                sliced.add(item);
            }
            index++;
        }
        return new CollectionFunctionChain<I>(sliced);
    }

    public CollectionFunctionChain<I> limit(int to) {
        return slice(0, to);
    }

    public CollectionFunctionChain<I> reverse() {
        final LinkedList<I> reverseList = new LinkedList<I>();
        for (I element : collection) {
            reverseList.addFirst(element);
        }
        collection = reverseList;
        return this;
    }


    public CollectionFunctionChain<I> removeDuplicates() {
        final LinkedList<I> newList = new LinkedList<I>();
        for (I element : collection) {
            if (!newList.contains(element))
                newList.addFirst(element);
        }
        collection = newList;
        return this;
    }

    public <I, T> CollectionFunctionChain<Tuple2<I, T>> zip(Collection<T> second) {
        List<Tuple2<I, T>> tuple2List = (List) FunctionUtil.zip(collection, second);
        return new CollectionFunctionChain<Tuple2<I, T>>(tuple2List);
    }

    public CollectionForkAndJoin<I> fork() {
        return new CollectionForkAndJoin<I>(this);
    }

    public CollectionFunctionChain<I> divideAndConquer(Task<Collection<I>> task, WorkDivisionStrategy partition) {
        FunctionUtil.divideAndConquer(this.collection, task, partition);
        return this;
    }

    public CollectionFunctionChain<I> execute(Task<List<I>> task) {
        task.execute(this.collection);
        return this;
    }

    public CollectionFunctionChain<I> executeAsync(final Task<List<I>> task) {
        final LinkedList<I> list = copyCollection();
        FunctionUtil.executeAsync(new Block() {
            public void execute() {
                task.execute(list);
            }
        });
        return this;
    }

    private LinkedList<I> copyCollection() {
        return new LinkedList<I>(collection);
    }

    public CollectionFunctionChain<I> executeLater(final Task<List<I>> task) {
        final LinkedList<I> list = copyCollection();
        FunctionUtil.executeLater(new Block() {
            public void execute() {
                task.execute(list);
            }
        });
        return this;
    }

    public CollectionFunctionChain<I> executeWithGlobalLock(final Task<List<I>> task) {
        FunctionUtil.executeWithGlobalLock(new Block() {
            public void execute() {
                task.execute(collection);
            }
        });
        return this;
    }

    public CollectionFunctionChain<I> executeWithLock(Operation operation, final Task<List<I>> task) {
        FunctionUtil.executeWithLock(operation, new Block() {
            public void execute() {
                task.execute(collection);
            }
        });
        return this;
    }

    public CollectionFunctionChain<I> executeWithThrottle(ExecutionThrottler executionThrottler, final Task<List<I>> task) {
        FunctionUtil.executeWithThrottle(executionThrottler, new Block() {
            public void execute() {
                task.execute(collection);
            }
        });
        return this;
    }

    public CollectionFunctionChain<I> executeAwait(final Task<List<I>> task, Integer time, TimeUnit timeUnit) {
        FunctionUtil.executeAwait(new Block() {
            public void execute() {
                task.execute(collection);
            }
        }, time, timeUnit);
        return this;
    }

    public CollectionFunctionChain<I> executeAsyncWithThrottle(ExecutionThrottler executionThrottler, final Task<List<I>> task) {
        final LinkedList<I> list = copyCollection();
        FunctionUtil.executeAsyncWithThrottle(executionThrottler, new Block() {
            public void execute() {
                task.execute(list);
            }
        });
        return this;
    }

    public <O> CollectionFunctionChain<I> executeAsyncWithThrottle(ExecutionThrottler executionThrottler, final Function<List<I>, O> asyncTask, final CallbackTask<O> callbackTask) {
        final LinkedList<I> list = copyCollection();
        FunctionUtil.executeAsyncWithThrottle(executionThrottler, new AsyncTask<O>() {
            public O execute() {
                return asyncTask.apply(list);
            }
        }, callbackTask);

        return this;
    }

    private List<I> getCollection() {
        return new LinkedList<I>();
    }

    public List<I> extract() {
        return this.collection;
    }

    @Override
    public String toString() {
        return collection.toString();
    }
}
