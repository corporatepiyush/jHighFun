package org.jhighfun.util.sql;

import org.jhighfun.util.Function;
import org.jhighfun.util.TaskStream;
import org.jhighfun.util.stream.SqlResultSetStreamIterator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            for (int i = 0; i < dynamicArgs.length; i++) {
                statement.setObject(i + 1, dynamicArgs[i]);
            }
            ResultSet resultSet = statement.executeQuery();
            return new TaskStream<T>(new SqlResultSetStreamIterator(resultSet, rowMapper));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while executing sql prepared statement", e);
        }
    }

    public <T> int executeUpdate() {
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
            SqlDataStore.closeStatement(statement);
            SqlDataStore.closeConnection(connection);
        }
    }

    public <T> int executeUpdate(Object[] dynamicArgs) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            for (int i = 0; i < dynamicArgs.length; i++) {
                statement.setObject(i + 1, dynamicArgs[i]);
            }
            return statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while executing sql prepared statement", e);
        } finally {
            SqlDataStore.closeStatement(statement);
            SqlDataStore.closeConnection(connection);
        }
    }


    public <T> int executeBatchUpdate(List<Object[]> dynamicArgsList) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            for (Object[] dynamicArgs : dynamicArgsList) {
                for (int i = 0; i < dynamicArgs.length; i++) {
                    statement.setObject(i + 1, dynamicArgs[i]);
                }
                statement.addBatch();
            }
            return statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while executing sql prepared statement", e);
        } finally {
            SqlDataStore.closeStatement(statement);
            SqlDataStore.closeConnection(connection);
        }
    }

}
