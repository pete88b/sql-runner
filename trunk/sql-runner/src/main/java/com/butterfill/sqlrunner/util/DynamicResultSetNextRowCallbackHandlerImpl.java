/*

  File name: DynamicResultSetNextRowCallbackHandlerImpl.java

  Modification history:

  ==============================================================================
  Version Date           Developer             Code Reviewer          Jira
  ==============================================================================
  1.0     31-Mar-2014    Peter Butterfill      Kristofer Pelchat      TRIG-000
  Additional Comments: This is the initial version of this file.

*/

package com.butterfill.sqlrunner.util;

import com.butterfill.sqlrunner.SqlRunner;
import com.butterfill.sqlrunner.SqlRunnerResultSetNextRowCallbackHandler;
import com.butterfill.sqlrunner.SqlRunnerStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Peter Butterfill
 */
public class DynamicResultSetNextRowCallbackHandlerImpl
        implements SqlRunnerResultSetNextRowCallbackHandler {

    private static final SqlNameToJavaNameHelper SQL_NAME_TO_JAVA_NAME_HELPER =
            new SqlNameToJavaNameHelper();

    @Override
    public void nextRow(final SqlRunner sqlRunner, final SqlRunnerStatement sqlRunnerStatement,
            final ResultSet resultSet, final int rowNumber) throws SQLException {
        // get metadata for every row to keep this method stateless - hope the JDBC drivers
        // cache metadata or this might hurt performance
        final ResultSetMetaData metadata = resultSet.getMetaData();

        final Map<String, Object> row = new HashMap<String, Object>();

        // for every column in the result set, save the column name
        // (converted to java name) and it's value in the row map
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            row.put(SQL_NAME_TO_JAVA_NAME_HELPER.sqlNameToJavaName(metadata.getColumnName(i)),
                    resultSet.getObject(i));
        }

        final List<Map<String, Object>> result;

        if (rowNumber == 1) {
            // create the result and set it on the sqlRunnerStatement
            result = new ArrayList<Map<String, Object>>();
            sqlRunnerStatement.setResult(result);
        } else {
            // if we're past row 1, result should already have been set on the sqlRunnerStatement
            result = (List<Map<String, Object>>) sqlRunnerStatement.getResult();
        }

        // add this row to the result
        result.add(row);

    }

}
