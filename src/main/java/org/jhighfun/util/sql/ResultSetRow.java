package org.jhighfun.util.sql;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ResultSetRow {

    private final ResultSet resultSet;

    public ResultSetRow(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public <T> T getColumnValueByName(String columnName, Class<T> type) {
        try {
            return (T) this.resultSet.getObject(columnName);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while accessing result row set invalid column name", e);
        }
    }

    public byte[] getBlob(String columnName) {
        InputStream binaryStream = null;
        try {
            Blob blob = this.resultSet.getBlob(columnName);
            binaryStream = blob.getBinaryStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = binaryStream.read()) > 0) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            binaryStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while accessing result row set invalid column index", e);
        } finally {
            try {
                binaryStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Exception while closing blob input stream", e);
            }
        }
    }

    public char[] getClob(String columnName) {
        Reader charStream = null;
        try {
            Clob clob = this.resultSet.getClob(columnName);
            charStream = clob.getCharacterStream();

            StringBuilder charStore = new StringBuilder();
            char[] buffer = new char[1024];
            int charsRead;
            while ((charsRead = charStream.read()) > 0) {
                charStore.append(buffer, 0, charsRead);
            }
            charStream.close();
            char[] extract = new char[charStore.length()];
            charStore.getChars(0, charStore.length(), extract, 0);
            return extract;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while accessing result row set invalid column index", e);
        } finally {
            try {
                charStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Exception while closing clob input stream", e);
            }
        }
    }
}
