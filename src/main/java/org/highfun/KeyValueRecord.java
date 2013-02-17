package org.highfun;


public interface KeyValueRecord<K,V> {
    public void process(K key, V value);
}
