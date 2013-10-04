package org.jhighfun.util;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.jhighfun.util.stream.AbstractStreamIterator;
import org.jhighfun.util.stream.InputStreamIterator;
import org.jhighfun.util.stream.ReaderStreamIterator;
import org.jhighfun.util.stream.io.FileChain;

public class StreamUtil {

	public static <INIT, IN> TaskStream<IN> dynamicTaskStream(INIT initialInput, Function<INIT, Tuple2<INIT, IN>> function,
			Function<INIT, Boolean> predicate) {
		return new TaskStream<IN>(initialInput, function, predicate);
	}

	public static TaskStream<Character> characterTaskStream(InputStream inputStream) {
		return new TaskStream<Character>(new ReaderStreamIterator(inputStream));
	}

	public static TaskStream<Character> characterTaskStream(Reader reader) {
		return new TaskStream<Character>(new ReaderStreamIterator(reader));
	}

	public static TaskStream<Byte> byteTaskStream(InputStream inputStream) {
		return new TaskStream<Byte>(new InputStreamIterator(inputStream));
	}

	public static <I> TaskStream<I> taskStream(I[] arr) {
		return new TaskStream<I>(CollectionUtil.Iterify(arr));
	}

	public static <I> TaskStream<I> taskStream(AbstractStreamIterator<I> iterator) {
		return new TaskStream<I>(iterator);
	}

	public static <I> TaskStream<I> taskStream(Iterator<I> iterator) {
		return new TaskStream<I>(iterator);
	}

	public static <I> TaskStream<I> taskStream(Iterable<I> iterable) {
		return new TaskStream<I>(iterable);
	}

	public static FileChain fileChain(File file) {
		return new FileChain(file);
	}

}
