package org.jhighfun.util.sql;

import java.sql.Connection;

import org.jhighfun.util.stream.SqlResultSetStreamIterator;

public interface SqlQueryExecutor<T> {

    public SqlResultSetStreamIterator<T> execute(Connection connection);
}
