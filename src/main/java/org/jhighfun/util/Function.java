package org.jhighfun.util;

import java.util.Collection;

public interface Function<I, O> {

    public O execute(Collection<I> args);

}
