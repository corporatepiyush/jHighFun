package org.jhighfun.util;

import java.io.Serializable;

public abstract class Tuple implements Serializable, Cloneable {

    public enum Index {
        _1, _2, _3, _4, _5, _6
    }

    public <T> T get(Index index, Class<T> type) throws NoSuchFieldException, IllegalAccessException {
        return (T) getClass().getDeclaredField(index.toString()).get(this);
    }

}
