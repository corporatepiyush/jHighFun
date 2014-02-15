package org.jhighfun.mapreduce;

import org.jhighfun.util.Function;
import org.jhighfun.util.Tuple2;

public abstract class MapFunction<I, K, V> extends Function<I, Tuple2<K, V>> {

	public Tuple2<K, V> emit(K key, V value) {
		return new Tuple2<K, V>(key, value);
	}

}
