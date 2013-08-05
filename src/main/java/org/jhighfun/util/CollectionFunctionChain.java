package org.jhighfun.util;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Cascading interface which enables availability of utility methods which cam be invoked on
 * List data structure to compose/write clean business logic flow of any kind, more suitable for batch
 * programming and data computation.
 *
 * @author Piyush Katariya
 */

public final class CollectionFunctionChain<I> {

    private List<I> collection;

    public CollectionFunctionChain(List<I> collection) {
        this.collection = collection;
    }

    public CollectionFunctionChain(Iterable<I> iterable) {
        if (iterable instanceof List) {
            this.collection = (List<I>) iterable;
        } else {
            List<I> list = new LinkedList<I>();
            for (I i : iterable) {
                list.add(i);
            }
            this.collection = list;
        }
    }


    public <O> ObjectFunctionChain<O> toObject(Function<List<I>, O> converter) {
        return new ObjectFunctionChain<O>(converter.apply(this.collection));
    }

    public ObjectFunctionChain<List<I>> asObject() {
        return new ObjectFunctionChain<List<I>>(this.collection);
    }

    public <O> CollectionFunctionChain<O> transform(Function<List<I>, List<O>> converter) {
        return new CollectionFunctionChain<O>(converter.apply(this.collection));
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

    public <O> CollectionFunctionChain<O> foldLeft(Collection<O> accum,
                                                   Accumulator<Collection<O>, I> accumulator) {
        return new CollectionFunctionChain<O>(FunctionUtil.foldLeft(this.collection, accum, accumulator));
    }

    public <ACCUM> ObjectFunctionChain<ACCUM> foldRight(ACCUM accum,
                                                        Accumulator<ACCUM, I> accumulator) {
        return new ObjectFunctionChain<ACCUM>(FunctionUtil.foldRight(this.collection, accum, accumulator));
    }

    public <O> CollectionFunctionChain<O> foldRight(Collection<O> accum,
                                                    Accumulator<Collection<O>, I> accumulator) {
        return new CollectionFunctionChain<O>(FunctionUtil.foldRight(this.collection, accum, accumulator));
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

    public CollectionFunctionChain<I> filterWithIndex(Function<Integer, Boolean> predicate) {
        this.collection = FunctionUtil.filterWithIndex(this.collection, predicate);
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

    public CollectionFunctionChain<I> union(Set<I> set) {
        for (I item : set) {
            if (!this.collection.contains(item))
                this.collection.add(item);
        }
        return this;
    }


    public CollectionFunctionChain<I> intersect(Set<I> set) {
        final List<I> commonElements = new LinkedList<I>();
        for (I item : set) {
            if (this.collection.contains(item))
                commonElements.add(item);
        }
        return new CollectionFunctionChain<I>(commonElements);
    }


    public CollectionFunctionChain<I> slice(int from, int to) {

        if (from < 0 || to < 0) {
            throw new IllegalArgumentException("Please provide positive values for 'from' and 'to' to carry out slicing of collection.");
        }

        final List<I> sliced = new LinkedList<I>();
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
        if (to < 1) {
            throw new IllegalArgumentException("Please provide 'limit' value greater than ZERO.");
        }

        return slice(0, to - 1);
    }

    public CollectionFunctionChain<I> reverse() {
        final LinkedList<I> reverseList = new LinkedList<I>();
        for (I element : this.collection) {
            reverseList.addFirst(element);
        }
        this.collection = reverseList;
        return this;
    }

    public CollectionFunctionChain<I> removeDuplicates(Function<Tuple2<I, I>, Boolean> likenessEvaluator) {
        final LinkedList<I> newList = new LinkedList<I>();
        Tuple2<I, I> tuple = new Tuple2<I, I>(null, null);
        while (this.collection.size() > 0) {
            I first = this.collection.remove(0);
            newList.add(first);
            Iterator<I> iterator = this.collection.iterator();
            while (iterator.hasNext()) {
                tuple._1 = first;
                tuple._2 = iterator.next();
                if (likenessEvaluator.apply(tuple)) {
                    iterator.remove();
                }
            }
        }
        this.collection = newList;
        return this;
    }

    public <I, T> CollectionFunctionChain<Tuple2<I, T>> zip(Collection<T> second) {
        List<Tuple2<I, T>> tuple2List = (List) FunctionUtil.zip(this.collection, second);
        return new CollectionFunctionChain<Tuple2<I, T>>(tuple2List);
    }

    public CollectionForkAndJoin<I> fork() {
        return new CollectionForkAndJoin<I>(this);
    }

    public CollectionFunctionChain<I> divideAndConquer(final Task<List<I>> task, WorkDivisionStrategy partition) {
        FunctionUtil.divideAndConquer(this.collection, new Task<Collection<I>>() {
            public void execute(Collection<I> input) {
                task.execute((List<I>) input);
            }
        }, partition);
        return this;
    }

    public <O> CollectionFunctionChain<O> divideAndConquer(final Function<List<I>, List<O>> function, WorkDivisionStrategy partition) {
        return new CollectionFunctionChain<O>((List<O>) FunctionUtil.divideAndConquer(this.collection, new Function<Collection<I>, Collection<O>>() {
            public Collection<O> apply(Collection<I> collection1) {
                return function.apply((List<I>) collection1);
            }
        }, partition));
    }

    public <J> ObjectFunctionChain<Map<J, List<I>>> groupBy(Function<I, J> function) {
        return new ObjectFunctionChain<Map<J, List<I>>>(FunctionUtil.groupBy(this.collection, function));
    }

    public CollectionFunctionChain<I> execute(Task<List<I>> task) {
        task.execute(new LinkedList<I>(this.collection));
        return this;
    }

    public CollectionFunctionChain<I> executeAsync(final Task<List<I>> task) {
        final LinkedList<I> list = new LinkedList<I>(this.collection);
        FunctionUtil.executeAsync(new Block() {
            public void execute() {
                task.execute(list);
            }
        });
        return this;
    }

    public CollectionFunctionChain<I> executeLater(final Task<List<I>> task) {
        final LinkedList<I> list = new LinkedList<I>(this.collection);
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
        final LinkedList<I> list = new LinkedList<I>(this.collection);
        FunctionUtil.executeAsyncWithThrottle(executionThrottler, new Block() {
            public void execute() {
                task.execute(list);
            }
        });
        return this;
    }

    public <O> CollectionFunctionChain<I> executeAsyncWithThrottle(ExecutionThrottler executionThrottler, final Function<List<I>, O> asyncTask, final CallbackTask<O> callbackTask) {
        final LinkedList<I> list = new LinkedList<I>(this.collection);
        FunctionUtil.executeAsyncWithThrottle(executionThrottler, new AsyncTask<O>() {
            public O execute() {
                return asyncTask.apply(list);
            }
        }, callbackTask);

        return this;
    }

    public TaskStream<I> asTaskStream() {
        return new TaskStream<I>(this.collection);
    }

    public List<I> extract() {
        return this.collection;
    }

    public <O> O extract(Function<List<I>, O> extractor) {
        return extractor.apply(this.collection);
    }

    public CollectionFunctionChain<List<I>> batch(int batchSize) {

        if (batchSize < 1) {
            throw new IllegalArgumentException("Please provide 'batch' size greater than ZERO.");
        }

        List<List<I>> batchCollection = new LinkedList<List<I>>();

        int batchCount = (this.collection.size() / batchSize) + ((this.collection.size() % batchSize) > 0 ? 1 : 0);

        for (int i = 0; i < batchCount; i++) {
            batchCollection.add(new LinkedList<I>());
        }

        int index = 0;
        List<I> currentBatchedList = batchCollection.get(0);
        for (I element : this.collection) {
            if (currentBatchedList.size() < batchSize) {
                currentBatchedList.add(element);
            } else {
                index++;
                currentBatchedList = batchCollection.get(index);
                currentBatchedList.add(element);
            }
        }

        return new CollectionFunctionChain<List<I>>(batchCollection);
    }


    public <O> CollectionFunctionChain<O> flatMap(Function<I, Iterable<O>> function) {
        List<O> expandedList = new LinkedList<O>();

        for (I element : this.collection) {
            Iterable<O> iterable = function.apply(element);
            for (O input : iterable) {
                expandedList.add(input);
            }
        }

        return new CollectionFunctionChain<O>(expandedList);
    }

    public ObjectFunctionChain<Tuple2<List<I>, List<I>>> partition(Function<I, Boolean> function) {
        return new ObjectFunctionChain<Tuple2<List<I>, List<I>>>(FunctionUtil.partition(this.collection, function));
    }

    public <J, K> CollectionFunctionChain<K> crossProduct(Iterable<J> ys, Function<Tuple2<I, J>, K> function) {
        return new CollectionFunctionChain<K>(FunctionUtil.crossProduct(this.collection, ys, function));
    }

    public <O> CollectionFunctionChain<O> selfProduct(Function<Tuple2<I, I>, O> function) {
        return new CollectionFunctionChain<O>(FunctionUtil.crossProduct(this.collection, this.collection, function));
    }

    @Override
    public String toString() {
        return collection.toString();
    }
}
