package org.jhighfun.util;


public interface ManagedCache {

    void put(Object key, Object value);

    Object get(Object key);

}
