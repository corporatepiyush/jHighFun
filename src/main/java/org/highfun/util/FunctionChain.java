package org.highfun.util;

import java.util.Collection;
import java.util.Comparator;

public class FunctionChain<I> {

    private Collection<I> collection;

    public FunctionChain(Collection<I> collection) {
        this.collection = collection;
    }

    public <O> FunctionChain<O> map(Converter<I, O> converter) {
        return new FunctionChain<O>(FunctionUtil.map(this.collection, converter));
    }

    public <O> FunctionChain<O> map(Converter<I, O> converter, int threads) {
        return new FunctionChain<O>(FunctionUtil.map(this.collection, converter, threads));
    }

    public FunctionChain<I> filter(Predicate<I> predicate) {
        this.collection = FunctionUtil.filter(this.collection, predicate);
        return this;
    }

    public FunctionChain<I> filter(Predicate<I> predicate, int threads) {
        this.collection = FunctionUtil.filter(this.collection, predicate, threads);
        return this;
    }

    public FunctionChain<I> sort(Comparator<I> comparator) {
        this.collection = FunctionUtil.sort(this.collection, comparator);
        return this;
    }

    public FunctionChain<I> each(RecordProcessor<I> recordProcessor) {
        FunctionUtil.each(this.collection, recordProcessor);
        return this;
    }

    public FunctionChain<I> each(RecordProcessor<I> recordProcessor, int threads) {
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

    public Collection<I> unchain() {
        return this.collection;
    }
}
