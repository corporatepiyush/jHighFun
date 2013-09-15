package org.jhighfun.util.sql;

import org.jhighfun.util.stream.SqlResultSetStreamIterator;

import java.sql.Connection;

public interface SqlQueryExecutor<T> {

    public SqlResultSetStreamIterator<T> execute(Connection connection);
}
