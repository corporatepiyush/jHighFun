package org.jhighfun.util.stream;

import org.jhighfun.util.Function;
import org.jhighfun.util.SQLUtil;
import org.jhighfun.util.sql.ResultSetRow;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlResultSetStreamIterator<T> extends AbstractStreamIterator<T> {

    private final ResultSet resultSet;
    private final Connection connection;
    private final Function<ResultSetRow, T> rowMapper;
    private final Statement statement;
    private final ResultSetRow resultSetRow;

    public SqlResultSetStreamIterator(ResultSet resultSet, Function<ResultSetRow, T> rowMapper) {
        this.resultSet = resultSet;
        Connection connection = null;
        Statement statement = null;
        try {
            statement = resultSet.getStatement();
            connection = statement.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.connection = connection;
        this.statement = statement;
        this.rowMapper = rowMapper;
        this.resultSetRow = new ResultSetRow(resultSet);
    }

    @Override
    public boolean hasNext() {
        try {
            boolean next = this.resultSet.next();
            if (!next) {
                closeResources();
            }
            return next;
        } catch (Exception e) {
            e.printStackTrace();
            closeResources();
            throw new RuntimeException("Exception while fetching Sql ResultSet", e);
        }
    }

    @Override
    public T next() {
        try {
            return this.rowMapper.apply(this.resultSetRow);
        } catch (Exception e) {
            e.printStackTrace();
            closeResources();
            throw new RuntimeException("Exception while converting sql result row.", e);
        }

    }

    public void closeResources() {
        SQLUtil.closeStatement(this.statement);
        SQLUtil.closeConnection(this.connection);
    }


}
