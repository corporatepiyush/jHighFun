package org.jhighfun.util.stream;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class ReaderStreamIterator extends AbstractStreamIterator<Character> {

    private final Reader reader;
    private Character currentCharacter;

    public ReaderStreamIterator(Reader reader) {
        this.reader = reader;
    }

    public ReaderStreamIterator(InputStream inputStream) {
        this.reader = new InputStreamReader(inputStream);
    }

    @Override
    public boolean hasNext() {
        try {
            int characterRead = reader.read();
            if (characterRead == -1) {
                closeResources();
                return false;
            } else {
                currentCharacter = (char) characterRead;
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return false;
    }

    @Override
    public Character next() {
        return currentCharacter;
    }

    @Override
    public void closeResources() {
        try {
            reader.close();
        } catch (IOException e) {
            System.err.println("Exception while closing reader.");
            e.printStackTrace();
        }
    }
}
