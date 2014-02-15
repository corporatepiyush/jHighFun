package org.jhighfun.mapreduce;

import java.util.List;

import org.jhighfun.util.Tuple2;
import org.jhighfun.util.stream.AbstractStreamIterator;

public final class MapReduceJob<I, K, V, O> {

	private final AbstractStreamIterator<I> mapInputReader;
	private final MapFunction<I, K, V> mapFunction;
	private final ReduceFunction<K, V, O> reduceFunction;
	private final AbstractStreamIterator<Tuple2<K, List<V>>> reduceInputReader;
	private final OutputWriter<O> reduceOutputWriter;
	private final OutputWriter<Tuple2<K, V>> mapOutputWriter;

	public MapReduceJob(AbstractStreamIterator<I> mapInputReader,
			MapFunction<I, K, V> mapFunction,
			OutputWriter<Tuple2<K, V>> mapOutputWriter,
			AbstractStreamIterator<Tuple2<K, List<V>>> reduceInputReader,
			ReduceFunction<K, V, O> reduceFunction,
			OutputWriter<O> reduceOutputWriter
			) {
		this.mapInputReader = mapInputReader;
		this.mapFunction = mapFunction;
		this.mapOutputWriter = mapOutputWriter;
		this.reduceInputReader = reduceInputReader;
		this.reduceFunction = reduceFunction;
		this.reduceOutputWriter = reduceOutputWriter;
	}

	public AbstractStreamIterator<I> getMapInputReader() {
		return mapInputReader;
	}

	public MapFunction<I, K, V> getMapFunction() {
		return mapFunction;
	}

	public ReduceFunction<K, V, O> getReduceFunction() {
		return reduceFunction;
	}

	public AbstractStreamIterator<Tuple2<K, List<V>>> getReduceInputReader() {
		return reduceInputReader;
	}

	public OutputWriter<O> getReduceOutputWriter() {
		return reduceOutputWriter;
	}

	public OutputWriter<Tuple2<K, V>> getMapOutputWriter() {
		return mapOutputWriter;
	}
	
}
