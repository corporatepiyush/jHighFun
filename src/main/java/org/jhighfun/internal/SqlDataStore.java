package org.jhighfun.internal;


import javax.sql.DataSource;

public class SqlDataStore {

    private static DataSource dataSource;

    public static void registerDataSource(DataSource dataSource) {
        SqlDataStore.dataSource = dataSource;
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

}
