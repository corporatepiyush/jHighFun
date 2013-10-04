package org.jhighfun.util.stream.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class WriterChain {

	private final Writer writer;

	public WriterChain(Writer writer) {
		this.writer = writer;
	}

	public void write(char[] charArray, int charArraySize) {
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

	public void write(char[] charArray) {
		write(charArray, 8192);
	}
	
	public void write(String string) {
		write(string.toCharArray(), 8192);
	}

}
