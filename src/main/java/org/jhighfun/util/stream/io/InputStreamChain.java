package org.jhighfun.util.stream.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jhighfun.util.TaskStream;
import org.jhighfun.util.stream.InputStreamIterator;

public class InputStreamChain {

	private final InputStream inputStream;

	public InputStreamChain(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public byte[] readBytes() {
		return readBytes(8192);
	}

	public byte[] readBytes(int byteBufferSize) {
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

	public void transfer(OutputStream outputStream, int bufferArraySize) {
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

	public void transfer(OutputStream outputStream) {
		transfer(outputStream, 8192);
	}

	public TaskStream<Byte> asTaskStream() {
		return new TaskStream<Byte>(new InputStreamIterator(inputStream));
	}

}
