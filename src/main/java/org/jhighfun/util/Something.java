package org.jhighfun.util;

public class Something<T> extends Optional<T> {

    public Something(T object) {
        if (object == null) {
            throw new IllegalArgumentException();
        }
        set(object);
    }

}
