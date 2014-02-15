package org.jhighfun.mapreduce;

public interface OutputWriter<W> {

	public void write(W w);
	
}
