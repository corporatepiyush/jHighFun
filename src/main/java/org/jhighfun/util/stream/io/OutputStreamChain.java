package org.jhighfun.util.stream.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamChain {

	private final OutputStream outputStream;

	public OutputStreamChain(OutputStream outputStream){
		this.outputStream = outputStream;
	}
	
	public void write(byte[] byteArray, int bufferArraySize) {
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

	public void write(byte[] byteArray) {
		write(byteArray, 8192);
	}
}
