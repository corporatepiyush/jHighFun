package org.jhighfun.util.sql;


import org.jhighfun.util.Function;
import org.jhighfun.util.TaskStream;
import org.jhighfun.util.stream.SqlResultSetStreamIterator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import support.ReflectionUtil;

import javax.sql.DataSource;
import java.sql.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SQLQuerySpec {

    @Test
    public void shouldConstructSqlQueryObj() {

        DataSource dataSourceMock = mock(DataSource.class);
        String sql = "Select salary from Employee";
        SQLQuery sqlQuery = new SQLQuery(sql, dataSourceMock);

        assertEquals(sql, ReflectionUtil.getField(sqlQuery, "sql", String.class));
        assertEquals(dataSourceMock, ReflectionUtil.getField(sqlQuery, "dataSource", DataSource.class));

    }

    @Test
    public void shouldReturnLazyResultSetWrappedInsideTaskStreamOnExecuteQuery() throws SQLException {

        DataSource dataSourceMock = mock(DataSource.class);
        Connection mockConnection = mock(Connection.class);
        Statement mockStatement = mock(Statement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        Function<ResultSetRow, String> mockRowMapper = mock(Function.class);

        String sql = "Select salary from Employee";
        SQLQuery sqlQuery = new SQLQuery(sql, dataSourceMock);

        when(dataSourceMock.getConnection()).thenReturn(mockConnection);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(sql)).thenReturn(mockResultSet);
        when(mockStatement.getConnection()).thenReturn(mockConnection);
        when(mockResultSet.getStatement()).thenReturn(mockStatement);

        TaskStream<String> taskStream = sqlQuery.executeQuery(mockRowMapper);

        SqlResultSetStreamIterator iterator = ReflectionUtil.getField(taskStream, "iterator", SqlResultSetStreamIterator.class);
        assertEquals(mockConnection, ReflectionUtil.getField(iterator, "connection", Connection.class));
        assertEquals(mockResultSet, ReflectionUtil.getField(iterator, "resultSet", ResultSet.class));
        assertEquals(mockRowMapper, ReflectionUtil.getField(iterator, "rowMapper", Function.class));
    }

    @Test
    public void shouldReturnLazyResultSetWrappedInsideTaskStreamOnExecuteDynamicQueryWithOutArguments() throws SQLException {

        DataSource dataSourceMock = mock(DataSource.class);
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        Function<ResultSetRow, String> mockRowMapper = mock(Function.class);

        String sql = "Select salary from Employee";
        SQLQuery sqlQuery = new SQLQuery(sql, dataSourceMock);

        when(dataSourceMock.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(sql)).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockStatement.getConnection()).thenReturn(mockConnection);
        when(mockResultSet.getStatement()).thenReturn(mockStatement);

        TaskStream<String> taskStream = sqlQuery.executeDynamicQuery(new Object[]{}, mockRowMapper);

        SqlResultSetStreamIterator iterator = ReflectionUtil.getField(taskStream, "iterator", SqlResultSetStreamIterator.class);
        assertEquals(mockConnection, ReflectionUtil.getField(iterator, "connection", Connection.class));
        assertEquals(mockResultSet, ReflectionUtil.getField(iterator, "resultSet", ResultSet.class));
        assertEquals(mockRowMapper, ReflectionUtil.getField(iterator, "rowMapper", Function.class));
    }


    @Test
    public void shouldReturnLazyResultSetWrappedInsideTaskStreamOnExecuteDynamicQueryWithArguments() throws SQLException {

        DataSource dataSourceMock = mock(DataSource.class);
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        Function<ResultSetRow, String> mockRowMapper = mock(Function.class);

        String sql = "Select salary from Employee where age = ? and gender = ?";
        SQLQuery sqlQuery = new SQLQuery(sql, dataSourceMock);

        when(dataSourceMock.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(sql)).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockStatement.getConnection()).thenReturn(mockConnection);
        when(mockResultSet.getStatement()).thenReturn(mockStatement);

        TaskStream<String> taskStream = sqlQuery.executeDynamicQuery(new Object[]{20, 'M'},mockRowMapper);

        verify(mockStatement, times(1)).setObject(1, 20);
        verify(mockStatement, times(1)).setObject(2, 'M');

        SqlResultSetStreamIterator iterator = ReflectionUtil.getField(taskStream, "iterator", SqlResultSetStreamIterator.class);
        assertEquals(mockConnection, ReflectionUtil.getField(iterator, "connection", Connection.class));
        assertEquals(mockResultSet, ReflectionUtil.getField(iterator, "resultSet", ResultSet.class));
        assertEquals(mockRowMapper, ReflectionUtil.getField(iterator, "rowMapper", Function.class));
    }

}
