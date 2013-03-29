package org.jhighfun.internal;

import org.jhighfun.util.*;

import java.util.Comparator;

public interface HigherOrderFunction<I> {

    <O> FunctionChain<O> map(Converter<I, O> converter);

    <O> FunctionChain<O> map(Converter<I, O> converter, int threads);

    FunctionChain<I> filter(Predicate<I> predicate);

    FunctionChain<I> filter(Predicate<I> predicate, int threads);

    FunctionChain<I> sortWith(Comparator<I> comparator);

    FunctionChain<I> sort();

    FunctionChain<I> sortBy(String memberVar, String... memberVars);

    FunctionChain<I> each(RecordProcessor<I> recordProcessor);

    FunctionChain<I> eachWithIndex(RecordWithIndexProcessor<I> recordWithIndexProcessor);

    FunctionChain<I> each(RecordProcessor<I> recordProcessor, int threads);

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
