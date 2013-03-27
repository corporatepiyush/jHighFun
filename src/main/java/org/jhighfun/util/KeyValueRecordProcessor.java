package org.jhighfun.util;


public interface KeyValueRecordProcessor<K, V> {
    public void process(K key, V value);
}
