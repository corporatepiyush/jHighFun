package org.jhighfun.internal;

import org.jhighfun.util.*;

import java.util.Comparator;

public interface HigherOrderFunction<I> {

    <O> CollectionFunctionChain<O> map(Converter<I, O> converter);

    <O> CollectionFunctionChain<O> map(Converter<I, O> converter, int threads);

    CollectionFunctionChain<I> filter(Predicate<I> predicate);

    CollectionFunctionChain<I> filter(Predicate<I> predicate, int threads);

    CollectionFunctionChain<I> sortWith(Comparator<I> comparator);

    CollectionFunctionChain<I> sort();

    CollectionFunctionChain<I> sortBy(String memberVar, String... memberVars);

    CollectionFunctionChain<I> each(RecordProcessor<I> recordProcessor);

    CollectionFunctionChain<I> eachWithIndex(RecordWithIndexProcessor<I> recordWithIndexProcessor);

    CollectionFunctionChain<I> each(RecordProcessor<I> recordProcessor, int threads);

    <ACCUM> ACCUM foldLeft(ACCUM accum,
                           Accumulator<ACCUM, I> accumulator);

    <ACCUM> ACCUM foldRight(ACCUM accum,
                            Accumulator<ACCUM, I> accumulator);

    I reduce(Accumulator<I, I> accumulator);

    I reduce(Accumulator<I, I> accumulator, int threads);

    boolean every(Predicate<I> predicate);

    boolean any(Predicate<I> predicate);

    int count(Predicate<I> predicate);
}
