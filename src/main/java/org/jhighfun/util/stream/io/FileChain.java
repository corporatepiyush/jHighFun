package org.jhighfun.util.stream.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.jhighfun.util.RecordProcessor;

public class FileChain {

	private final File file;

	public FileChain(File file) {
		this.file = file;
	}

	public InputStreamChain asInputStream() {
		InputStreamChain chain = null;
		try {
			chain = new InputStreamChain(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return chain;
	}

	public OutputStreamChain asOutputStream() {
		OutputStreamChain chain = null;
		try {
			chain = new OutputStreamChain(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return chain;
	}

	public FileChain createFileIfDoesNotExists() {
		if (file.exists()) {
			return this;
		}
		String absolutePath = file.getAbsolutePath();
		String directoryName = absolutePath.substring(0, absolutePath.lastIndexOf(System.getProperty("file.separator")));
		new File(directoryName).mkdirs();
		try {
			new File(file.getAbsolutePath()).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Exception while trying to create new File.");
		}
		return this;
	}

	public FileChain createDirectory() {
		file.mkdirs();
		return this;
	}

	public FileChain forEachLine(RecordProcessor<String> lineProcessor) {
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
		return this;
	}

	public FileChain write(String content) {
		this.asOutputStream().write(content.getBytes());
		return this;
	}

	public String read() {
		return new String(this.asInputStream().readBytes());
	}
}
