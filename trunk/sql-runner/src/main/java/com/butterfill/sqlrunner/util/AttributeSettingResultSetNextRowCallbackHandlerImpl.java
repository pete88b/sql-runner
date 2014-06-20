
package com.butterfill.sqlrunner.util;

import com.butterfill.sqlrunner.SqlRunner;
import com.butterfill.sqlrunner.SqlRunnerResultSetNextRowCallbackHandler;
import com.butterfill.sqlrunner.SqlRunnerStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 *
 * @author Peter Butterfill
 */
public class AttributeSettingResultSetNextRowCallbackHandlerImpl
        implements SqlRunnerResultSetNextRowCallbackHandler {

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
