package org.jhighfun.util.stream.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.jhighfun.util.TaskStream;
import org.jhighfun.util.stream.ReaderStreamIterator;

public class ReaderChain {

	private final Reader reader;

	public ReaderChain(Reader reader) {
		this.reader = reader;
	}

	public char[] read() {
		return read(8192);
	}

	public char[] read(int charArraySize) {
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

	public void transfer(Writer writer, int bufferArraySize) {
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

	public void transfer(Writer writer) {
		transfer(writer, 8192);
	}

	public TaskStream<Character> asTaskStream() {
		return new TaskStream<Character>(new ReaderStreamIterator(reader));
	}

}
