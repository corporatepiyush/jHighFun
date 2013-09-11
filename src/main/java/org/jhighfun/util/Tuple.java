package org.jhighfun.util;

import java.io.Serializable;
import java.lang.reflect.Field;

public abstract class Tuple implements Serializable, Cloneable {

    public enum Index {
        _1, _2, _3, _4, _5, _6
    }

    public <T> T get(Index index, Class<T> type) {
        try {
            Field field = getClass().getDeclaredField(index.toString());
            return (T) field.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
