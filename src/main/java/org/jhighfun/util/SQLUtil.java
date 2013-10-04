package org.jhighfun.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.jhighfun.internal.SqlDataStore;
import org.jhighfun.util.sql.SQLQuery;

public class SQLUtil {

    public static SQLQuery SQLQuery(String query) {
        return new SQLQuery(query, SqlDataStore.getDataSource());
    }

    public static SQLQuery SQLQuery(String query, DataSource dataSource) {
        return new SQLQuery(query, dataSource);
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
