package org.jhighfun.util.stream;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamIterator extends AbstractStreamIterator<Byte> {

    private final InputStream inputStream;
    private Byte currentByte;

    public InputStreamIterator(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public boolean hasNext() {
        try {
            int byteRead = inputStream.read();
            if (byteRead == -1) {
                closeResources();
                return false;
            } else {
                currentByte = (byte) byteRead;
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while reading input stream");
        } finally {
            closeResources();
        }
    }

    @Override
    public Byte next() {
        return currentByte;
    }

    @Override
    public void closeResources() {
        try {
            inputStream.close();
        } catch (IOException e) {
            System.err.println("Exception while closing input stream.");
            e.printStackTrace();
        }
    }
}
