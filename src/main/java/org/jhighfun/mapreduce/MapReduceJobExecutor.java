package org.jhighfun.mapreduce;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jhighfun.util.Tuple2;
import org.jhighfun.util.stream.AbstractStreamIterator;

public final class MapReduceJobExecutor<I, K, V, O> {

	private final MapReduceJob<I, K, V, O> mapReduceJob;
	private final int mapJobThreads;
	private final int reduceJobThreads;

	public MapReduceJobExecutor(MapReduceJob<I, K, V, O> mapReduceJob,
			int mapJobThreads, int reduceJobThreads) {
		this.mapReduceJob = mapReduceJob;
		this.mapJobThreads = mapJobThreads;
		this.reduceJobThreads = reduceJobThreads;
	}

	public void execute() {

		ExecutorService mapJobExecutor = Executors
				.newFixedThreadPool(mapJobThreads);
		runMapJob(mapJobExecutor);
		mapJobExecutor.shutdown();
		try {
			mapJobExecutor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ExecutorService reduceJobExecutor = Executors
				.newFixedThreadPool(reduceJobThreads);
		runReduceJob(reduceJobExecutor);
		reduceJobExecutor.shutdown();
		try {
			reduceJobExecutor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void runReduceJob(ExecutorService reduceJobExecutor) {
		AbstractStreamIterator<Tuple2<K, List<V>>> reduceInputReader = mapReduceJob
				.getReduceInputReader();
		final ReduceFunction<K, V, O> reduceFunction = mapReduceJob
				.getReduceFunction();
		final OutputWriter<O> reduceOutputWriter = mapReduceJob
				.getReduceOutputWriter();
		try {
			while (reduceInputReader.hasNext()) {
				final Tuple2<K, List<V>> current = reduceInputReader.next();
				reduceJobExecutor.execute(new Runnable() {
					public void run() {
						O output = reduceFunction.apply(current);
						reduceOutputWriter.write(output);
					}
				});
			}
		} finally {
			reduceInputReader.closeResources();
		}
	}

	private void runMapJob(ExecutorService mapJobExecutor) {

		AbstractStreamIterator<I> mapInputReader = mapReduceJob
				.getMapInputReader();
		final MapFunction<I, K, V> mapFunction = mapReduceJob.getMapFunction();
		final OutputWriter<Tuple2<K, V>> mapOutputWriter = mapReduceJob
				.getMapOutputWriter();
		try {
			while (mapInputReader.hasNext()) {
				final I currentInput = mapInputReader.next();
				mapJobExecutor.execute(new Runnable() {
					public void run() {
						mapOutputWriter.write(mapFunction.apply(currentInput));
					}
				});
			}
		} finally {
			mapInputReader.closeResources();
		}
	}
}