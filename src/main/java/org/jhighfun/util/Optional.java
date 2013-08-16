package org.jhighfun.util;

import java.util.NoSuchElementException;

public abstract class Optional<T> {
    protected T object;

    protected void set(T object) {
        this.object = object;
    }

    public T get() {
        if(this.object == null)
            throw new NoSuchElementException();
        return this.object;
    }

    public T getOrElse(T alternateObject) {
        return this.object == null ? alternateObject : this.object;
    }

    public boolean isDefined() {
        return this.object != null;
    }
}
