
package com.butterfill.sqlrunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Callback handler used by SqlRunner to prepare statements and optionally do some work after the
 * statement has been run.
 * @author Peter Butterfill
 */
public interface SqlRunnerCallbackHandler {

    /**
     * Returns a prepared statement - Must not return null.
     * @param connection
     *   Use this connection to prepare the statement.
     * @param sqlRunnerStatement
     *   The statement we want to run.
     *   Use {@link com.butterfill.sqlrunner.SqlRunnerStatement#getSql() } to get the SQL.
     * @return
     *   A prepared statement
     * @throws SQLException
     *   If the JDBC calls throw an exception.
     */
    PreparedStatement prepareStatement(Connection connection, SqlRunnerStatement sqlRunnerStatement)
            throws SQLException;

    /**
     * Optionally do some work after the statement has been executed.
     * This method gets called by SqlRunner before the statement is closed.
     * @param preparedStatement
     *   The prepared statement that has just be run.
     * @param sqlRunnerStatement
     *   The SqlRunnerStatement.
     * @throws SQLException
     *   If the JDBC calls throw an exception.
     */
    void executeComplete(PreparedStatement preparedStatement, SqlRunnerStatement sqlRunnerStatement)
            throws SQLException;

}
