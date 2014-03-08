package org.jhighfun.mapreduce;

import java.util.List;

import org.jhighfun.util.Tuple2;
import org.jhighfun.util.stream.AbstractStreamIterator;

public final class MapJob<I, K, V> {

	private final AbstractStreamIterator<I> inputReader;
	private final MapFunction<I, K, V> mapFunction;
	private final OutputWriter<Tuple2<K, List<V>>> outputWriter;

	public MapJob(AbstractStreamIterator<I> inputReader,
			MapFunction<I, K, V> mapFunction,
			OutputWriter<Tuple2<K, List<V>>> outputWriter) {
				this.inputReader = inputReader;
				this.mapFunction = mapFunction;
				this.outputWriter = outputWriter;
	}
	
	

}
