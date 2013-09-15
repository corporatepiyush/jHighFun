package org.jhighfun.util.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ResultSetRow {

    private final ResultSet resultSet;

    public ResultSetRow(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public<T> T getColumnValueByName(String columnName, Class<T> type) {
        try {
            return (T) this.resultSet.getObject(columnName);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while accessing result row set invalid column name", e);
        }
    }

    public<T> T getColumnValueByIndex(int columnIndex, Class<T> type) {
        try {
            return (T) this.resultSet.getObject(columnIndex);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while accessing result row set invalid column index", e);
        }
    }
}
