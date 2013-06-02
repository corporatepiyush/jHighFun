package org.jhighfun.internal;

import java.lang.ref.SoftReference;

/**
 * A garbage aware(SoftReferenced) wrapper around a an object
 *
 * @author Piyush Katariya
 */

public class CacheObject<T> {

    private SoftReference<T> softCacheObject;

    public CacheObject(T cacheObject) {
        if (cacheObject == null)
            throw new NullPointerException("Please provide the object.");
        this.softCacheObject = new SoftReference<T>(cacheObject);
    }

    public T get() {
        return this.softCacheObject.get();
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
        return softCacheObject.get() != null ? softCacheObject.get().hashCode() : 0;
    }
}
