package org.jhighfun.util;

public class Nothing<T> extends Optional<T> {
    public Nothing(Class<T> aClass) {
        set(null);
    }
}
