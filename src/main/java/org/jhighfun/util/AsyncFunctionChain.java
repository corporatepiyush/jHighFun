package org.jhighfun.util;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class AsyncFunctionChain<I, O> {

	private final Function<I, O> function;
	private final ExecutionThrottler initiator;

	public AsyncFunctionChain(final ExecutionThrottler throttler, Function<I, O> function) {
		this.initiator = throttler;
		this.function = function;
	}

	public <CO> AsyncFunctionChain<I, CO> execute(final ExecutionThrottler throttler, final Function<O, CO> composerFunction) {
		final Tuple1<Future<CO>> tuple1 = new Tuple1<Future<CO>>(null);
		Function<I, O> proxy = function.proxy(new FunctionInvocationHandler<I, O>() {
			public O invoke(final Function<I, O> function1, final I input) {
				final O output = function1.apply(input);
				tuple1._1 = FunctionUtil.executeAsyncWithThrottle(throttler, new Callable<CO>() {
					public CO call() throws Exception {
						return composerFunction.apply(output);
					}
				});
				return output;
			}
		});

		Function<O, CO> dummy = new Function<O, CO>() {
			public CO apply(O arg) {
				try {
					return tuple1._1.get();
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		};

		return new AsyncFunctionChain<I, CO>(this.initiator, proxy.compose(dummy));
	}

	public void execute(final I input) {
		FunctionUtil.executeAsyncWithThrottle(initiator, new Runnable() {
			public void run() {
				function.apply(input);
			}
		});
	}
}
