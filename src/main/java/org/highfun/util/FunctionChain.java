package org.highfun.util;

import java.util.*;

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

    public FunctionChain<I> eachWithIndex(RecordWithIndexProcessor<I> recordWithIndexProcessor) {
        FunctionUtil.eachWithIndex(this.collection, recordWithIndexProcessor);
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

    public FunctionChain<I> plus(Collection<I> collection) {
        this.collection.addAll(collection);
        return this;
    }

    public FunctionChain<I> minus(Collection<I> collection) {
        this.collection.removeAll(collection);
        return this;
    }

    public FunctionChain<I> union(Collection<I> inputCollection) {

        Collection<I> extraElements =  new LinkedList<I>();

        for(I item : inputCollection){
            if(!this.collection.contains(item))
                extraElements.add(item);
        }

        this.collection.addAll(extraElements);
        return this;
    }


    public FunctionChain<I> intersect(Collection<I> collection) {

        Collection<I> commonElements = null;

        commonElements = getCollection();

        for(I item : this.collection){
             if(collection.contains(item))
                 commonElements.add(item);
        }

        return new FunctionChain<I>(commonElements);
    }


    public FunctionChain<I> slice(int from, int to) {

        Collection<I> sliced = null;

        sliced = getCollection();

        int index = 0;

        for (I item : this.collection){
            if(index >= from  && index <=to){
                sliced.add(item);
            }
            index++;
        }

        return new FunctionChain<I>(sliced);
    }

    private Collection<I> getCollection() {
        Collection<I> is = null ;
        if(this.collection instanceof Set){
            is = new LinkedHashSet<I>();
        }else {
            is = new LinkedList<I>();
        }
        return is;
    }
}
