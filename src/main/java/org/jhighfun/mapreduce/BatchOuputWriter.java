package org.jhighfun.mapreduce;

import java.util.LinkedList;
import java.util.List;

public abstract class BatchOuputWriter<T> implements OutputWriter<T> {

	private final int batchSize;
	private int currentSize = 0;
	private List<T> list = new LinkedList<T>();

	public BatchOuputWriter(int batchSize) {
		if (batchSize < 1)
			throw new IllegalArgumentException(
					"Please provide batch size greater than ZERO");
		this.batchSize = batchSize;
	}

	public final void write(T t) {
		if (currentSize == batchSize) {
			write(list);
			list = new LinkedList<T>();
			currentSize = 0;
		}
		list.add(t);
		currentSize++;
	}

	public abstract void write(List<T> list);
}
