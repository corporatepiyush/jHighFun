package org.jhighfun.util.sql;

import org.jhighfun.util.Function;
import org.jhighfun.util.SQLUtil;
import org.jhighfun.util.TaskStream;
import org.jhighfun.util.stream.SqlResultSetStreamIterator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public final class SQLQuery {

    private final String sql;
    private final DataSource dataSource;

    public SQLQuery(String sql, DataSource dataSource) {
        this.sql = sql;
        this.dataSource = dataSource;
    }

    public <T> TaskStream<T> executeQuery(Function<ResultSetRow, T> rowMapper) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            return new TaskStream<T>(new SqlResultSetStreamIterator(resultSet, rowMapper));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while executing sql statement", e);
        }
    }

    public <T> TaskStream<T> executeDynamicQuery(Object[] dynamicArgs, Function<ResultSetRow, T> rowMapper) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < dynamicArgs.length; i++) {
                preparedStatement.setObject(i + 1, dynamicArgs[i]);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            return new TaskStream<T>(new SqlResultSetStreamIterator(resultSet, rowMapper));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while executing sql prepared statement", e);
        }
    }

    public int executeUpdate() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while executing sql prepared statement", e);
        } finally {
            SQLUtil.closeStatement(statement);
            SQLUtil.closeConnection(connection);
        }
    }

    public int executeUpdate(Object[] dynamicArgs) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < dynamicArgs.length; i++) {
                preparedStatement.setObject(i + 1, dynamicArgs[i]);
            }
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while executing sql prepared statement", e);
        } finally {
            SQLUtil.closeStatement(preparedStatement);
            SQLUtil.closeConnection(connection);
        }
    }


    public int[] executeBatchUpdate(List<Object[]> dynamicArgsList) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            connection.setAutoCommit(false);
            for (Object[] dynamicArgs : dynamicArgsList) {
                for (int i = 0; i < dynamicArgs.length; i++) {
                    preparedStatement.setObject(i + 1, dynamicArgs[i]);
                }
                preparedStatement.addBatch();
            }
            int[] rowsUpdated = preparedStatement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
            return rowsUpdated;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while executing sql prepared statement", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            SQLUtil.closeStatement(preparedStatement);
            SQLUtil.closeConnection(connection);
        }
    }

}
