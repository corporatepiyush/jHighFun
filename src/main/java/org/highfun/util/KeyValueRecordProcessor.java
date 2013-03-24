package org.highfun.util;


public interface KeyValueRecordProcessor<K, V> {
    public void process(K key, V value);
}
