package org.jhighfun.util;

import java.util.Comparator;

public interface HigherOrderFunction<I> {

    <O> CollectionFunctionChain<O> map(Converter<I, O> converter);

    <O> CollectionFunctionChain<O> map(Converter<I, O> converter, Parallel parallel);

    CollectionFunctionChain<I> filter(Predicate<I> predicate);

    CollectionFunctionChain<I> filter(Predicate<I> predicate, Parallel parallel);

    CollectionFunctionChain<I> sortWith(Comparator<I> comparator);

    CollectionFunctionChain<I> sort();

    CollectionFunctionChain<I> sortBy(String memberVar, String... memberVars);

    CollectionFunctionChain<I> each(RecordProcessor<I> recordProcessor);

    CollectionFunctionChain<I> eachWithIndex(RecordWithIndexProcessor<I> recordWithIndexProcessor);

    CollectionFunctionChain<I> each(RecordProcessor<I> recordProcessor, Parallel parallel);

    <ACCUM> ObjectFunctionChain<ACCUM> foldLeft(ACCUM accum,
                                                Accumulator<ACCUM, I> accumulator);

    <ACCUM> ObjectFunctionChain<ACCUM> foldRight(ACCUM accum,
                                                 Accumulator<ACCUM, I> accumulator);

    ObjectFunctionChain<I> reduce(Accumulator<I, I> accumulator);

    ObjectFunctionChain<I> reduce(Accumulator<I, I> accumulator, Parallel parallel);

    ObjectFunctionChain<Boolean> every(Predicate<I> predicate);

    ObjectFunctionChain<Boolean> any(Predicate<I> predicate);

    ObjectFunctionChain<Integer> count(Predicate<I> predicate);
}
