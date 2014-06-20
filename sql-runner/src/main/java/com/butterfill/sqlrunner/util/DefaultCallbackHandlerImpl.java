
package com.butterfill.sqlrunner.util;

import com.butterfill.sqlrunner.SqlRunnerCallbackHandler;
import com.butterfill.sqlrunner.SqlRunnerStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Peter Butterfill
 */
public class DefaultCallbackHandlerImpl implements SqlRunnerCallbackHandler {

    public PreparedStatement prepareStatement(
            final Connection connection, final SqlRunnerStatement sqlRunnerStatement)
            throws SQLException {
        return connection.prepareStatement(sqlRunnerStatement.getSql());
    }

    public void executeComplete(
            final PreparedStatement preparedStatement, final SqlRunnerStatement sqlRunnerStatement)
            throws SQLException {
        // do nothing
    }

}
