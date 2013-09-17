package org.jhighfun.util;

import java.io.*;

public class FileIOUtil {

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

    public static byte[] getBytesAndClose(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        try {
            while ((bytesRead = inputStream.read()) > 0) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteArrayOutputStream.toByteArray();
    }


    public static char[] getCharsAndClose(Reader reader) {
        try {
            StringBuilder charStore = new StringBuilder();
            char[] buffer = new char[8192];
            int charsRead;
            while ((charsRead = reader.read()) > 0) {
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
                System.err.println("Exception while closing reader input stream." + e.getMessage());
            }
        }
    }

}
