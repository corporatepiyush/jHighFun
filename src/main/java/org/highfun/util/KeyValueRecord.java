package org.highfun.util;


public interface KeyValueRecord<K, V> {
    public void process(K key, V value);
}
