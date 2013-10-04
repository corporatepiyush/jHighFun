package org.jhighfun.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class AsyncFunctionChainSpec {

	@SuppressWarnings("unchecked")
	@Test
	public void testAsynchonicityForSingleTask() {

		FunctionUtil.registerPool(FunctionUtil.throttler("t1"), 1);

		final List threadIdCheck = new LinkedList();
		final List argsCheck = new LinkedList();

		FunctionUtil.asyncFunctionChain(FunctionUtil.throttler("t1"), new Function<Integer, String>() {
			public String apply(Integer arg) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				threadIdCheck.add(Thread.currentThread().getId());
				argsCheck.add(arg);
				return null;
			}
		}).execute(5);

		threadIdCheck.add(Thread.currentThread().getId());

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// assure initial async execution
		assertEquals(threadIdCheck.get(0), Thread.currentThread().getId());
		// ThreadId is should be different
		assertFalse(threadIdCheck.get(1).equals(Thread.currentThread().getId()));
		// ensure correct args passed across
		assertEquals(argsCheck, CollectionUtil.List(5));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAsynchonicityForTwoTask() {

		FunctionUtil.registerPool(FunctionUtil.throttler("t1"), 1);
		FunctionUtil.registerPool(FunctionUtil.throttler("t2"), 1);

		final List threadIdCheck = new LinkedList();
		final List argsCheck = new LinkedList();

		FunctionUtil.asyncFunctionChain(FunctionUtil.throttler("t1"), new Function<Integer, String>() {
			public String apply(Integer arg) {
				threadIdCheck.add(Thread.currentThread().getId());
				argsCheck.add(arg);
				return arg.equals(5) ? "five" : "Nan";
			}
		}).execute(FunctionUtil.throttler("t2"), new Function<String, Character>() {
			public Character apply(String arg) {
				threadIdCheck.add(Thread.currentThread().getId());
				argsCheck.add(arg);
				return null;
			}
		}).execute(5);
		threadIdCheck.add(Thread.currentThread().getId());

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertEquals(threadIdCheck.get(0), Thread.currentThread().getId());
		assertFalse(threadIdCheck.get(1).equals(Thread.currentThread().getId()));
		assertFalse(threadIdCheck.get(2).equals(Thread.currentThread().getId()));
		assertFalse(threadIdCheck.get(1).equals(threadIdCheck.get(2)));
		assertEquals(CollectionUtil.List(5, "five"), argsCheck);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAsynchonicityForMoreThanTwoTask() {

		FunctionUtil.registerPool(FunctionUtil.throttler("t1"), 1);
		FunctionUtil.registerPool(FunctionUtil.throttler("t2"), 1);

		final List threadIdCheck = new LinkedList();
		final List argsCheck = new LinkedList();

		FunctionUtil.asyncFunctionChain(FunctionUtil.throttler("t1"), new Function<Integer, String>() {
			public String apply(Integer arg) {
				threadIdCheck.add(Thread.currentThread().getId());
				argsCheck.add(arg);
				return arg.equals(5) ? "five" : "Nan";
			}
		}).execute(FunctionUtil.throttler("t2"), new Function<String, Character>() {
			public Character apply(String arg) {
				threadIdCheck.add(Thread.currentThread().getId());
				argsCheck.add(arg);
				return arg.charAt(0);
			}
		}).execute(FunctionUtil.throttler("t1"), new Function<Character, Byte>() {
			public Byte apply(Character arg) {
				threadIdCheck.add(Thread.currentThread().getId());
				argsCheck.add(arg);
				return null;
			}
		}).execute(5);
		threadIdCheck.add(Thread.currentThread().getId());

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertEquals(threadIdCheck.get(0), Thread.currentThread().getId());
		assertFalse(threadIdCheck.get(1).equals(Thread.currentThread().getId()));
		assertFalse(threadIdCheck.get(2).equals(Thread.currentThread().getId()));
		assertFalse(threadIdCheck.get(1).equals(threadIdCheck.get(2)));
		assertEquals(CollectionUtil.List(5, "five", 'f'), argsCheck);
	}
}
