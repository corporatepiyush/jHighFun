package org.jhighfun.util;

import java.io.*;
import java.util.Iterator;

import org.jhighfun.util.stream.AbstractStreamIterator;
import org.jhighfun.util.stream.InputStreamIterator;
import org.jhighfun.util.stream.ReaderStreamIterator;

public class StreamUtil {

	public void forEachLineInFile(File file, RecordProcessor<String> lineProcessor) {
		if (!file.exists()) {
			throw new IllegalArgumentException("Please provide available file resource.");
		}
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				lineProcessor.process(line);
			}
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static <INIT, IN> TaskStream<IN> dynamicTaskStream(INIT initialInput, Function<INIT, Tuple2<INIT, IN>> function, Function<INIT, Boolean> predicate) {
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

	public static byte[] readBytesAndClose(InputStream inputStream) {
		return readBytesAndClose(inputStream, 8192);
	}

	public static byte[] readBytesAndClose(InputStream inputStream, int byteBufferSize) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[byteBufferSize];
		int bytesRead;
		try {
			while ((bytesRead = inputStream.read(buffer)) > 0) {
				byteArrayOutputStream.write(buffer, 0, bytesRead);
			}
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Exception while reading data from byte input stream", e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return byteArrayOutputStream.toByteArray();
	}

	public static char[] readCharsAndClose(Reader reader) {
		return readCharsAndClose(reader, 8192);
	}

	public static char[] readCharsAndClose(Reader reader, int charArraySize) {
		try {
			StringBuilder charStore = new StringBuilder();
			char[] buffer = new char[charArraySize];
			int charsRead;
			while ((charsRead = reader.read(buffer)) > 0) {
				charStore.append(buffer, 0, charsRead);
			}
			reader.close();
			char[] extract = new char[charStore.length()];
			charStore.getChars(0, charStore.length(), extract, 0);
			return extract;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Exception while reading data from character input stream", e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void writeBytesAndClose(OutputStream outputStream, byte[] byteArray, int bufferArraySize) {
		BufferedOutputStream bufferedOutputStream = null;
		try {
			bufferedOutputStream = new BufferedOutputStream(outputStream, bufferArraySize);
			bufferedOutputStream.write(byteArray);
			bufferedOutputStream.flush();
			bufferedOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Exception while writing byte data to output stream", e);
		} finally {
			try {
				bufferedOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void writeBytesAndClose(OutputStream outputStream, byte[] byteArray) {
		writeBytesAndClose(outputStream, byteArray, 8192);
	}

	public static void writeCharsAndClose(Writer writer, char[] charArray, int charArraySize) {
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(writer, charArraySize);
			bufferedWriter.write(charArray);
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Exception while writing char data to output writer", e);
		} finally {
			try {
				bufferedWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void writeCharsAndClose(Writer writer, char[] charArray) {
		writeCharsAndClose(writer, charArray, 8192);
	}

	public static void transferAndClose(InputStream inputStream, OutputStream outputStream, int bufferArraySize) {
		byte[] buffer = new byte[bufferArraySize];
		int byteRead = 0;
		try {
			while ((byteRead = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, byteRead);
				outputStream.flush();
			}
			inputStream.close();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Exception while tranferring byte data from input to output stream", e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void transferAndClose(InputStream inputStream, OutputStream outputStream) {
		transferAndClose(inputStream, outputStream, 8192);
	}

	public static void transferAndClose(Reader reader, Writer writer, int bufferArraySize) {
		char[] buffer = new char[bufferArraySize];
		int byteRead = 0;
		try {
			while ((byteRead = reader.read(buffer)) > 0) {
				writer.write(buffer, 0, byteRead);
				writer.flush();
			}
			reader.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Exception while tranferring char data from input to output stream", e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void transferAndClose(Reader reader, Writer writer) {
		transferAndClose(reader, writer, 8192);
	}

	public static StringBuffer readFileContent(File file) {
		BufferedReader reader = null;
		StringBuffer content = null;
		try {
			content = new StringBuffer();
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			char[] buff = new char[8192];
			int charRead = 0;
			while ((charRead = reader.read(buff)) > 0) {
				content.append(buff, 0, charRead);
			}
			reader.close();
			return content;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Exception while reading ",e);
		} finally {
			content = null;
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
