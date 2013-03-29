package org.jhighfun.util;

import org.jhighfun.internal.HigherOrderFunction;
import org.jhighfun.internal.SetTheoryFunction;

import java.util.*;

public class CollectionFunctionChain<I> implements HigherOrderFunction<I>, SetTheoryFunction<I> {

    private Collection<I> collection;

    public CollectionFunctionChain(Collection<I> collection) {
        this.collection = collection;
    }

    public <O> CollectionFunctionChain<O> map(Converter<I, O> converter) {
        return new CollectionFunctionChain<O>(FunctionUtil.map(this.collection, converter));
    }

    public <O> CollectionFunctionChain<O> map(Converter<I, O> converter, int threads) {
        return new CollectionFunctionChain<O>(FunctionUtil.map(this.collection, converter, threads));
    }

    public CollectionFunctionChain<I> filter(Predicate<I> predicate) {
        this.collection = FunctionUtil.filter(this.collection, predicate);
        return this;
    }

    public CollectionFunctionChain<I> filter(Predicate<I> predicate, int threads) {
        this.collection = FunctionUtil.filter(this.collection, predicate, threads);
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

    public CollectionFunctionChain<I> each(RecordProcessor<I> recordProcessor, int threads) {
        FunctionUtil.each(this.collection, recordProcessor, threads);
        return this;
    }

    public <ACCUM> ACCUM foldLeft(ACCUM accum,
                                  Accumulator<ACCUM, I> accumulator) {
        return FunctionUtil.foldLeft(this.collection, accum, accumulator);
    }

    public <ACCUM> ACCUM foldRight(ACCUM accum,
                                   Accumulator<ACCUM, I> accumulator) {
        return FunctionUtil.foldRight(this.collection, accum, accumulator);
    }

    public I reduce(Accumulator<I, I> accumulator) {
        return FunctionUtil.reduce(this.collection, accumulator);
    }

    public I reduce(Accumulator<I, I> accumulator, int threads) {
        return FunctionUtil.reduce(this.collection, accumulator, threads);
    }

    public boolean every(Predicate<I> predicate) {
        return FunctionUtil.every(this.collection, predicate);
    }

    public boolean any(Predicate<I> predicate) {
        return FunctionUtil.any(this.collection, predicate);
    }

    public int count(Predicate<I> predicate) {
        return FunctionUtil.count(this.collection, predicate);
    }

    public CollectionFunctionChain<I> plus(Collection<I> collection) {
        this.collection.addAll(collection);
        return this;
    }

    public CollectionFunctionChain<I> minus(Collection<I> collection) {
        this.collection.removeAll(collection);
        return this;
    }

    public CollectionFunctionChain<I> union(Collection<I> inputCollection) {
        final Collection<I> extraElements = new LinkedList<I>();
        for (I item : inputCollection) {
            if (!this.collection.contains(item))
                extraElements.add(item);
        }
        this.collection.addAll(extraElements);
        return this;
    }


    public CollectionFunctionChain<I> intersect(Collection<I> collection) {
        final Collection<I> commonElements = getCollection();
        for (I item : this.collection) {
            if (collection.contains(item))
                commonElements.add(item);
        }
        return new CollectionFunctionChain<I>(commonElements);
    }


    public CollectionFunctionChain<I> slice(int from, int to) {
        final Collection<I> sliced = getCollection();
        int index = 0;
        for (I item : this.collection) {
            if (index >= from && index <= to) {
                sliced.add(item);
            }
            index++;
        }
        return new CollectionFunctionChain<I>(sliced);
    }

    public ForkAndJoin<I> fork() {
        return new ForkAndJoin<I>(this);
    }

    public CollectionFunctionChain<I> execute(Task<Collection<I>> task) {
        task.execute(this.collection);
        return this;
    }

    public CollectionFunctionChain<I> executeAsync(final Task<Collection<I>> task) {
        final Collection<I> collectionCopy = getCollection();
        collectionCopy.addAll(this.collection);
        FunctionUtil.executeAsync(new Block() {
            public void execute() {
                task.execute(collectionCopy);
            }
        });
        return this;
    }

    public CollectionFunctionChain<I> executeLater(final Task<Collection<I>> task) {
        final Collection<I> collectionCopy = getCollection();
        collectionCopy.addAll(this.collection);
        FunctionUtil.executeLater(new Block() {
            public void execute() {
                task.execute(collectionCopy);
            }
        });
        return this;
    }

    public CollectionFunctionChain<I> executeWithGlobalLock(final Task<Collection<I>> task) {
        FunctionUtil.executeWithGlobalLock(new Block() {
            public void execute() {
                task.execute(collection);
            }
        });
        return this;
    }

    private Collection<I> getCollection() {
        if (this.collection instanceof Set) {
            return new LinkedHashSet<I>();
        } else {
            return new LinkedList<I>();
        }
    }

    public Collection<I> extract() {
        return this.collection;
    }

}
