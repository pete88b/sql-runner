
package com.butterfill.sqlrunner.util;

import com.butterfill.sqlrunner.SqlRunner;
import com.butterfill.sqlrunner.SqlRunnerResultSetNextRowCallbackHandler;
import com.butterfill.sqlrunner.SqlRunnerStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Used to save the results of SELECT statements as attributes.
 * @author Peter Butterfill
 */
public class AttributeSettingResultSetNextRowCallbackHandlerImpl
        implements SqlRunnerResultSetNextRowCallbackHandler {

    /**
     * Reads the columns of the current row of the result set and saves values as attributes on the
     * specified SQL runner.
     * e.g. <code>sqlRunner.setAttribute(
     * [columnName].[rowNumber], resultSet.getObject(columnIndex));</code>
     * @param sqlRunner
     *   This method will set attributes on this SQL runner.
     * @param statement
     *   Not used by this method.
     * @param resultSet
     *   The result set being processed.
     * @param rowNumber
     *   Number of the row being processed.
     * @throws SQLException
     *   If working with the result set throws an exception.
     */
    public void nextRow(final SqlRunner sqlRunner, final SqlRunnerStatement statement,
            final ResultSet resultSet, final int rowNumber) throws SQLException {
        final ResultSetMetaData metaData = resultSet.getMetaData();

        final int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            sqlRunner.setAttribute(
                    metaData.getColumnName(i) + "." + rowNumber,
                    resultSet.getObject(i));

        }

    }

}
