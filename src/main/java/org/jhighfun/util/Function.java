package org.jhighfun.util;

import java.util.List;

public interface Function<I, O> {

    public O apply(List<I> args);

}
