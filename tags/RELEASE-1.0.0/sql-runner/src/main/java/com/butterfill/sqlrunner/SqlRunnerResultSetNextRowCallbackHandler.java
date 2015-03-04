
package com.butterfill.sqlrunner;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Used by SqlRunner to process result sets.
 * @author Peter Butterfill
 */
public interface SqlRunnerResultSetNextRowCallbackHandler {

    /**
     * Called once for each row in the result set.
     * @param sqlRunner
     *   The SqlRunner that is running the statement.
     * @param statement
     *   The statement we're running.
     * @param resultSet
     *   The result set returned by running the statement (probably a SQL statement)
     * @param rowNumber
     *   Number of the row of the result set we're processing. Starts at 1.
     * @throws SQLException
     *   If the JDBC calls throw an exception.
     */
    void nextRow(
            SqlRunner sqlRunner, SqlRunnerStatement statement, ResultSet resultSet, int rowNumber)
            throws SQLException;

}
