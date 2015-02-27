
package com.butterfill.sqlrunner.util;

import com.butterfill.sqlrunner.SqlRunnerCallbackHandler;
import com.butterfill.sqlrunner.SqlRunnerStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Used by {@link com.butterfill.sqlrunner.SqlRunnerFactory} when no callback handler has been
 * specified.
 *
 * @author Peter Butterfill
 */
public class DefaultCallbackHandlerImpl implements SqlRunnerCallbackHandler {

    /**
     * Returns a statement created from the SQL of the SqlRunnerStatement.
     * @param connection
     *   The connection we use to prepare the statement.
     * @param sqlRunnerStatement
     *   We use the SQL from this statement.
     * @return
     *   A PreparedStatement created from the SQL of the SqlRunnerStatement.
     * @throws SQLException
     *   If working with the connection throws an exception.
     */
    public PreparedStatement prepareStatement(
            final Connection connection, final SqlRunnerStatement sqlRunnerStatement)
            throws SQLException {
        return connection.prepareStatement(sqlRunnerStatement.getSql());
    }

    /**
     * Does nothing.
     * @param preparedStatement
     *   Is ignored.
     * @param sqlRunnerStatement
     *   Is ignored.
     */
    public void executeComplete(
            final PreparedStatement preparedStatement,
            final SqlRunnerStatement sqlRunnerStatement) {
        // do nothing
    }

}
