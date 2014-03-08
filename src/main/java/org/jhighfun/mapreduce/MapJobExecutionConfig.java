package org.jhighfun.mapreduce;

import java.util.concurrent.ExecutorService;

public class MapJobExecutionConfig<I, K, V> {

	private final ExecutorService executor;
	private final MapJobExceptionHandler<I, K, V> exceptionHandler;
	
	public MapJobExecutionConfig(ExecutorService executor,
			MapJobExceptionHandler<I, K, V> exceptionHandler) {
		super();
		this.executor = executor;
		this.exceptionHandler = exceptionHandler;
	}

	public ExecutorService getExecutor() {
		return executor;
	}
	
	public MapJobExceptionHandler<I, K, V> getExceptionHandler() {
		return exceptionHandler;
	}
	
}
