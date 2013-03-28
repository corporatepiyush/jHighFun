package org.jhighfun.util;

import java.util.Collection;

public interface SetTheoryFunction<I> {

    FunctionChain<I> plus(Collection<I> collection);

    FunctionChain<I> minus(Collection<I> collection);

    FunctionChain<I> union(Collection<I> inputCollection);

    FunctionChain<I> intersect(Collection<I> collection);

    FunctionChain<I> slice(int from, int to);
}
