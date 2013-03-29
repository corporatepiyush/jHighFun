package org.jhighfun.internal;

import org.jhighfun.util.CollectionFunctionChain;

import java.util.Collection;

public interface SetTheoryFunction<I> {

    CollectionFunctionChain<I> plus(Collection<I> collection);

    CollectionFunctionChain<I> minus(Collection<I> collection);

    CollectionFunctionChain<I> union(Collection<I> inputCollection);

    CollectionFunctionChain<I> intersect(Collection<I> collection);

    CollectionFunctionChain<I> slice(int from, int to);
}
