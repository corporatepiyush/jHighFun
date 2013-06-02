package org.jhighfun.util;

/**
 * Adapter to facilitate external cache storage
 *
 * @author Piyush Katariya
 */

public interface ManagedCache {

    void put(Object key, Object value);

    Object get(Object key);

}
