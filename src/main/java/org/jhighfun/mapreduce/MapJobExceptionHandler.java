package org.jhighfun.mapreduce;

public interface MapJobExceptionHandler<I, K, V> {
	
	void handleException(I input, K key, V value, Throwable exception, MapJobPhase phase);

}
