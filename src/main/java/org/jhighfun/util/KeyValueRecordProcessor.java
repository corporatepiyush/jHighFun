package org.jhighfun.util;


/**
 * A monoid which gets called as result of iteration over Map data structure
 *
 * @author Piyush Katariya
 */

public interface KeyValueRecordProcessor<K, V> {
    public void process(K key, V value);
}
