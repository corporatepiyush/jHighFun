package org.jhighfun.internal;

import java.lang.ref.SoftReference;

public class CacheObject<T> {

    private SoftReference<T> softCacheObject;
    private T cacheObject;

    public CacheObject(SoftReference<T> softCacheObject) {
        if (softCacheObject == null)
            throw new NullPointerException("Please provide the object.");
        this.softCacheObject = softCacheObject;
    }

    public CacheObject(T cacheObject) {
        if (cacheObject == null)
            throw new NullPointerException("Please provide the object.");
        this.cacheObject = cacheObject;
    }

    public T get() {
        return this.cacheObject == null ? this.softCacheObject.get()
                : this.cacheObject;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CacheObject))
            return false;
        if (this == o || this.get() == ((CacheObject) o).get()
                || this.get().equals(((CacheObject) o).get()))
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        T t = softCacheObject != null ? softCacheObject.get() : null;
        int result = t != null ? t.hashCode() : 0;
        result = 31 * result
                + (cacheObject != null ? cacheObject.hashCode() : 0);
        return result;
    }
}
