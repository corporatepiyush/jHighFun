package org.jhighfun.util.sql;

import org.jhighfun.util.FileIOUtil;

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
        try {
            Blob blob = this.resultSet.getBlob(columnName);
            return FileIOUtil.getBytesAndClose(blob.getBinaryStream());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while reading Blob from database.", e);
        }
    }

    public char[] getClob(String columnName) {
        try {
            Clob clob = this.resultSet.getClob(columnName);
            return FileIOUtil.getCharsAndClose(clob.getCharacterStream());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while reading Clob from database.", e);
        }
    }
}
