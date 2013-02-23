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

    public FunctionChain<I> filter(Condition<I> condition) {
        this.collection = FunctionUtil.filter(this.collection, condition);
        return this;
    }

    public FunctionChain<I> filter(Condition<I> condition, int threads) {
        this.collection = FunctionUtil.filter(this.collection, condition, threads);
        return this;
    }

    public FunctionChain<I> sort(Comparator<I> comparator) {
        this.collection = FunctionUtil.sort(this.collection, comparator);
        return this;
    }

    public FunctionChain<I> each(ItemRecord<I> itemRecord) {
        FunctionUtil.each(this.collection, itemRecord);
        return this;
    }

    public FunctionChain<I> each(ItemRecord<I> itemRecord, int threads) {
        FunctionUtil.each(this.collection, itemRecord, threads);
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

    public <ACCUM> ACCUM reduce(ACCUM accum,
                                Accumulator<ACCUM, I> accumulator) {
        return FunctionUtil.reduce(this.collection, accum, accumulator);
    }

    public boolean every(Condition<I> condition) {
        return FunctionUtil.every(this.collection, condition);
    }

    public boolean some(Condition<I> condition) {
        return FunctionUtil.any(this.collection, condition);
    }

    public int count(Condition<I> condition) {
        return FunctionUtil.count(this.collection, condition);
    }

    public Collection<I> unchain() {
        return this.collection;
    }
}
