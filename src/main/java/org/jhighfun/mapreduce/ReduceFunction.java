package org.jhighfun.mapreduce;

import java.util.List;

import org.jhighfun.util.Function;
import org.jhighfun.util.Tuple2;

public abstract class ReduceFunction<K, V, O> extends
		Function<Tuple2<K, List<V>>, O> {

	@Override
	public final O apply(Tuple2<K, List<V>> tuple) {
		return apply(tuple._1, tuple._2);
	}

	public abstract O apply(K key, List<V> value);

}
