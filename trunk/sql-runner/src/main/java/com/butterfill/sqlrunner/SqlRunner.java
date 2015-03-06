
package com.butterfill.sqlrunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * Helper class to run SQL statements -
 * the statements can be read from a file (which can contain any number of statements) or
 * single statements can be passed as strings.
 *
 * <p>This class is not thread safe.</p>
 *
 * <p>Please see {@link com.butterfill.sqlrunner} description for usage details.</p>
 *
 * <p>Note: this class is final as it has not been designed to be extended.</p>
 *
 * @author Peter Butterfill
 */
public final class SqlRunner {

    /**
     * The name of this class.
     */
    public static final String CLASS_NAME = SqlRunner.class.getName();

    /**
     * The logger for this class.
     */
    private static final Logger logger = Logger.getLogger(CLASS_NAME);

    /**
     * Line separator on this platform.
     */
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    /**
     * The data source to use when executing scripts.
     */
    private final DataSource dataSource;

    /**
     * We'll use this handler when no handler has been specified.
     */
    private final SqlRunnerCallbackHandler defaultCallbackHandler;

    /**
     * We'll use this handler when no result set handler has been specified.
     */
    private final SqlRunnerResultSetNextRowCallbackHandler defaultResultSetNextRowCallbackHandler;

    /**
     * The file reader used by this instance.
     */
    private final SqlRunnerFileReader fileReader;

    /**
     * The attribute name prefix.
     */
    private String attributePrefix = "#{";

    /**
     * The attribute name postfix.
     */
    private String attributePostfix = "}";

    /**
     * Attributes that may be used in the SQL file.
     */
    private final Map<String, String> attributeMap = new HashMap<String, String>();

    /**
     * Map of callback handlers.
     * Key is a statement name (set via a sql-runner comment or null).
     */
    private final Map<String, SqlRunnerCallbackHandler> callbackHandlerMap =
            new HashMap<String, SqlRunnerCallbackHandler>();

    /**
     * Map of result set next row callback handlers.
     * Key is a statement name (set via a sql-runner comment or null).
     */
    private final Map<String, SqlRunnerResultSetNextRowCallbackHandler> rsnrCallbackHandlerMap =
            new HashMap<String, SqlRunnerResultSetNextRowCallbackHandler>();

    /**
     * The auto commit setting of a connection before getConnection is called.
     */
    private boolean oldAutoCommitSetting;

    /**
     * Creates a new SqlScriptRunner that will use the specified data source and default handlers.
     * @param dataSource
     *   The data source to use when executing scripts.
     * @param callbackHandler
     *   The callback handler to use when no handler has been set by statement name.
     * @param resultSetNextRowCallbackHandler
     *   The result set callback handler to use when no handler has been set by statement name.
     * @param fileReader 
     *   The file reader to use.
     */
    public SqlRunner(final DataSource dataSource, final SqlRunnerCallbackHandler callbackHandler,
            final SqlRunnerResultSetNextRowCallbackHandler resultSetNextRowCallbackHandler,
            final SqlRunnerFileReader fileReader) {
        if (dataSource == null) {
            throw new NullPointerException("dataSource must not be null");
        }
        if (callbackHandler == null) {
            throw new NullPointerException("callbackHandler must not be null");
        }
        if (resultSetNextRowCallbackHandler == null) {
            throw new NullPointerException("resultSetNextRowCallbackHandler must not be null");
        }
        if (fileReader == null) {
            throw new NullPointerException("fileReader must not be null");
        }
        this.dataSource = dataSource;
        this.defaultCallbackHandler = callbackHandler;
        this.defaultResultSetNextRowCallbackHandler = resultSetNextRowCallbackHandler;
        this.fileReader = fileReader;
    }

    /**
     * Sets the attribute name prefix and postfix.
     * @param attributePrefix
     *   The attribute name prefix.
     * @param attributePostfix
     *   The attribute name postfix.
     * @return
     *   this instance.
     */
    public SqlRunner setAttributePrefixAndPostfix(
            final String attributePrefix, final String attributePostfix) {
        this.attributePrefix = (attributePrefix == null) ? "" : attributePrefix;
        this.attributePostfix = (attributePostfix == null) ? "" : attributePostfix;
        return this;
    }

    /**
     * Sets the value of a attribute on this instance.
     * @param attribute
     *   Name of the attribute.
     * @param attributeValue
     *   Value of the attribute - which will be converted to a String.
     *   This method will use an empty string for the attribute value, if attributeValue is null.
     * @return
     *   this instance.
     */
    public SqlRunner setAttribute(final String attribute, final Object attributeValue) {
        return setAttribute(attribute, attributeValue, "");

    }

    /**
     * Sets the value of a attribute on this instance.
     * @param attribute
     *   Name of the attribute.
     * @param attributeValue
     *   Value of the attribute - which will be converted to a String.
     * @param valueIfNull
     *   Value to use if attributeValue is null.
     * @return
     *   this instance.
     */
    public SqlRunner setAttribute(
            final String attribute, final Object attributeValue, final String valueIfNull) {
        final String stringAttributeValue;
        if (attributeValue == null) {
            stringAttributeValue = valueIfNull;
        } else if (attributeValue instanceof String) {
            stringAttributeValue = (String) attributeValue;
        } else {
            stringAttributeValue = attributeValue.toString();
        }

        attributeMap.put(attribute, stringAttributeValue);

        return this;

    }

    /**
     * Sets a handler for a statement.
     * @param statementName
     *   The name of the statement.
     *   Can be null, which is useful if you want a handler when calling
     *   {@link SqlRunner#run(java.lang.String) }.
     * @param handler
     *   The handler.
     *   Pass null if you want to go back to using the default handler.
     * @return
     *   this instance.
     */
    public SqlRunner setCallbackHandler(
            final String statementName, final SqlRunnerCallbackHandler handler) {
        callbackHandlerMap.put(statementName, handler);
        return this;
    }

    /**
     * Sets a result set handler for the statement.
     * @param statementName
     *   The name of the statement.
     *   Can be null, which is useful if you want a handler when calling
     *   {@link SqlRunner#run(java.lang.String) }.
     * @param handler
     *   The handler.
     *   Pass null if you want to go back to using the default handler.
     * @return
     *   this instance.
     */
    public SqlRunner setResultSetNextRowCallbackHandler(final String statementName,
            final SqlRunnerResultSetNextRowCallbackHandler handler) {
        rsnrCallbackHandlerMap.put(statementName, handler);
        return this;
    }

    /**
     * Converts a list of SQL strings to a list of SqlRunnerStatements.
     * @param sqlList
     *   A list of SQL strings.
     * @return
     *   A list of SqlRunnerStatements.
     */
    public List<SqlRunnerStatement> toSqlRunnerStatements(final List<String> sqlList) {
        if (sqlList == null) {
            throw new NullPointerException("sqlList must not be null");
        }

        final List<SqlRunnerStatement> sqlRunnerStatements = new ArrayList<SqlRunnerStatement>();

        for (String sql : sqlList) {
            sqlRunnerStatements.add(new SqlRunnerStatement(null, sql));
        }

        return sqlRunnerStatements;

    }

    /**
     * Runs all SQL statements read from the specified file.
     * This method will;
     * <ul>
     *   <li>Read the specified file from the classpath (using filePathPrefix)</li>
     *   <li>
     *     Splits the file contents into statements - statements are terminated by a semi-colon.
     *   </li>
     *   <li>
     *     Creates a list of SqlRunnerStatements, one for each statement in the file, and calls
     *     {@link #run(java.util.List) }
     *   </li>
     * </ul>
     *
     * @param fileName
     *   The name of the file (containing any number of SQL DML statements) that you want to run.
     * @return
     *   One SqlRunnerResult for each statement executed.
     */
    public List<SqlRunnerStatement> runFile(final String fileName) {
        return run(fileReader.readFile(fileName));

    }

    /**
     * Runs a list of SqlRunnerStatements.
     * This method will;
     * <ul>
     *   <li>
     *     Replace all attributes in the SQL statements that have been set by calls to
     *     setAttributeValues(String)
     *   </li>
     *   <li>Get a connection and disable auto commit</li>
     *   <li>Run the each statement using PreparedStatement#executeUpdate()</li>
     *   <li>If running a "fail fast" statement throws an exception;</li>
     *   <ul>
     *     <li>Save the exception on the SqlRunnerStatement</li>
     *     <li>rollback the transaction</li>
     *     <li>and throw a SqlRunnerException</li>
     *   </ul>
     *   <li>If running a non-"fail fast" statement throws an exception;</li>
     *   <ul>
     *     <li>Save the exception on the SqlRunnerStatement</li>
     *     <li>Note: we don't rollback and we don't throw the exception</li>
     *   </ul>
     *   <li>If all "fail fast" statements run without error;</li>
     *   <ul>
     *     <li>Commit the transaction</li>
     *     <li>and return the result</li>
     *   </ul>
     *   <li>Return the auto commit setting of the connection to what is was before this call</li>
     *   <li>
     *     Close the connection
     *     (i.e. return it to the connection to the pool if using a pooled data source)
     *   </li>
     * </ul>
     *
     * @param sqlRunnerStatements
     *   A list of SqlRunnerStatements that you want to run.
     * @return
     *   A new list of SqlRunnerStatements, which will have been updated as they are executed.
     */
    public List<SqlRunnerStatement> run(
            final List<SqlRunnerStatement> sqlRunnerStatements) {
        if (sqlRunnerStatements == null) {
            throw new NullPointerException("sqlRunnerStatements must not be null");
        }

        final List<SqlRunnerStatement> result = new ArrayList<SqlRunnerStatement>();

        final Connection connection = getConnection();

        try {
            for (SqlRunnerStatement sqlRunnerStatement : sqlRunnerStatements) {
                final SqlRunnerStatement statementToExecute = new SqlRunnerStatement(
                        sqlRunnerStatement.getName(),
                        replaceAttributes(sqlRunnerStatement.getSql()),
                        sqlRunnerStatement.getFailFast());
                result.add(statementToExecute);
                execute(connection, statementToExecute);
            }

        } catch (SqlRunnerException ex) {
            throw rollbackOnError(connection, ex);

        } finally {
            close(connection);

        }

        return result;

    }

    /**
     * Runs a single SQL statement.
     * This method will;
     * <ul>
     *   <li>Get a connection and disable auto commit</li>
     *   <li>
     *     Replace all attributes in sql that have been set by calls to setAttributeValues(String)
     *   </li>
     *   <li>Run the statement using PreparedStatement#executeUpdate()</li>
     *   <li>If the statement runs without error;</li>
     *   <ul>
     *     <li>Commit the transaction</li>
     *     <li>and return the result</li>
     *   </ul>
     *   <li>If running the statement throws an exception;</li>
     *   <ul>
     *     <li>Rollback the transaction</li>
     *     <li>and throw a SqlRunnerException</li>
     *   </ul>
     *   <li>Return the auto commit setting of the connection to what is was before this call</li>
     *   <li>
     *     Close the connection
     *     (i.e. return it to the connection to the pool if using a pooled data source)
     *   </li>
     * </ul>
     *
     * @param sql
     *   A SQL statement to execute.
     * @return
     *   A SqlRunnerStatement for the executed statement.
     */
    public SqlRunnerStatement run(final String sql) {
        if (sql == null) {
            throw new NullPointerException("sql must not be null");
        }

        final Connection connection = getConnection();

        try {
            return execute(connection, new SqlRunnerStatement(null, replaceAttributes(sql)));

        } catch (SqlRunnerException ex) {
            throw rollbackOnError(connection, ex);

        } finally {
            close(connection);

        }

    }

    /**
     * Runs all SQL statements read from the specified file.
     * This method does the same as {@link #runFile(java.lang.String) } but uses the specified
     * connection, rather than getting a connection from the datasource and does not commit the
     * transaction.
     * <p>
     * This method might be useful if you needed to use SqlRunner and Hibernate to access a DB in
     * the same transaction. Maybe using Hibernates Session#doWork(Work) method.
     * </p>
     * @param fileName
     *   The name of the file (containing any number of SQL DML statements) that you want to run.
     * @param connection
     *   The connection this method will use to run the SQL.
     * @return
     *   One SqlRunnerResult for each statement executed.
     */
    public List<SqlRunnerStatement> runFile(final String fileName, final Connection connection) {
        return run(fileReader.readFile(fileName), connection);

    }

    /**
     * Runs a list of SqlRunnerStatements using the specified connection.
     * This method does the same as {@link #run(java.util.List) } but uses the specified
     * connection, rather than getting a connection from the datasource and does not commit the
     * transaction.
     * <p>
     * This method might be useful if you needed to use SqlRunner and Hibernate to access a DB in
     * the same transaction. Maybe using Hibernates Session#doWork(Work) method.
     * </p>
     * @param sqlRunnerStatements
     *   A list of SqlRunnerStatements that you want to run.
     * @param connection
     *   The connection this method will use to run the SQL.
     * @return
     *   A new list of SqlRunnerStatements, which will have been updated as they are executed.
     */
    public List<SqlRunnerStatement> run(
            final List<SqlRunnerStatement> sqlRunnerStatements, final Connection connection) {
        if (sqlRunnerStatements == null) {
            throw new NullPointerException("sqlRunnerStatements must not be null");
        }

        if (connection == null) {
            throw new NullPointerException("connection must not be null");
        }

        final List<SqlRunnerStatement> result = new ArrayList<SqlRunnerStatement>();

        for (SqlRunnerStatement sqlRunnerStatement : sqlRunnerStatements) {
            final SqlRunnerStatement statementToExecute = new SqlRunnerStatement(
                    sqlRunnerStatement.getName(),
                    replaceAttributes(sqlRunnerStatement.getSql()),
                    sqlRunnerStatement.getFailFast());
            result.add(statementToExecute);
            execute(connection, statementToExecute);
        }

        return result;

    }

    /**
     * Runs a single SQL statement.
     * This method does the same as {@link #run(java.lang.String) } but uses the specified
     * connection, rather than getting a connection from the datasource and does not commit the
     * transaction.
     * <p>
     * This method might be useful if you needed to use SqlRunner and Hibernate to access a DB in
     * the same transaction. Maybe using Hibernates Session#doWork(Work) method.
     * </p>
     * @param sql
     *   A SQL statement to execute.
     * @param connection
     *   The connection this method will use to run the SQL.
     * @return
     *   A SqlRunnerStatement for the executed statement.
     */
    public SqlRunnerStatement run(final String sql, final Connection connection) {
        if (sql == null) {
            throw new NullPointerException("sql must not be null");
        }

        if (connection == null) {
            throw new NullPointerException("connection must not be null");
        }

        return execute(connection, new SqlRunnerStatement(null, replaceAttributes(sql)));

    }

    /**
     * Replaces all attributes with their values.
     * @param string
     *   A string containing attribute names.
     * @return
     *   A string containing attribute values.
     */
    private String replaceAttributes(String string) {
        for (Map.Entry<String, String> entry : attributeMap.entrySet()) {
            string = string.replace(
                    attributePrefix + entry.getKey() + attributePostfix,
                    entry.getValue());
        }
        return string;
    }

    /**
     * Executes the statement.
     * @param connection
     *   The connection to use.
     * @param sqlRunnerStatement
     *   The statement to execute.
     * @return
     *   statement, after setting resultOfExecutionWasResultSet and updateCount.
     */
    private SqlRunnerStatement execute(
            final Connection connection, final SqlRunnerStatement sqlRunnerStatement) {
        final String method = "execute(Connection, SqlRunnerStatement)";

        SqlRunnerCallbackHandler handler = callbackHandlerMap.get(sqlRunnerStatement.getName());
        if (handler == null) {
            handler = defaultCallbackHandler;
        }

        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = handler.prepareStatement(connection, sqlRunnerStatement);
            if (preparedStatement.execute()) {
                sqlRunnerStatement.setResultOfExecutionWasResultSet(true);
                processResultSet(preparedStatement.getResultSet(), sqlRunnerStatement);
            } else {
                sqlRunnerStatement.setResultOfExecutionWasResultSet(false);
                sqlRunnerStatement.setUpdateCount(preparedStatement.getUpdateCount());
            }
            handler.executeComplete(preparedStatement, sqlRunnerStatement);

        } catch (SQLException ex) {
            sqlRunnerStatement.setException(ex);
            if (sqlRunnerStatement.getFailFast()) {
                throw new SqlRunnerException("failed to execute. " + sqlRunnerStatement, ex);
            }

        } finally {
            close(preparedStatement);

        }

        logger.logp(Level.FINER, CLASS_NAME, method, "{0}", sqlRunnerStatement);
        return sqlRunnerStatement;

    }

    /**
     * Processes a result set, calling {@link SqlRunnerResultSetNextRowCallbackHandler#nextRow(
     * com.butterfill.sqlrunner.SqlRunner, com.butterfill.sqlrunner.SqlRunnerStatement,
     * java.sql.ResultSet, int) } for each row.
     *
     * @param resultSet
     *   The result set to process.
     * @param sqlRunnerStatement
     *   The statement that we ran to get the result set.
     */
    private void processResultSet(
            final ResultSet resultSet, final SqlRunnerStatement sqlRunnerStatement) {
        try {
            SqlRunnerResultSetNextRowCallbackHandler handler =
                    rsnrCallbackHandlerMap.get(sqlRunnerStatement.getName());
            if (handler == null) {
                handler = defaultResultSetNextRowCallbackHandler;
            }

            int rowNumber = 1;
            while (resultSet.next()) {
                handler.nextRow(this, sqlRunnerStatement, resultSet, rowNumber++);
            }

        } catch (SQLException ex) {
            throw new SqlRunnerException("failed to process result set. " + sqlRunnerStatement, ex);

        }

    }

    /**
     * Closes a statement without letting exceptions propagate.
     * @param statement
     *   The statement to close.
     */
    private void close(final Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException ex) {
            logger.logp(Level.WARNING, CLASS_NAME, "close(java.sql.Statement)",
                    "failed to close statement", ex);
        }
    }

    /**
     * Returns a connection from the data source - after disabling auto commit.
     * @return
     *   A SQL connection.
     */
    private Connection getConnection() {
        try {
            final Connection result = dataSource.getConnection();
            oldAutoCommitSetting = result.getAutoCommit();
            if (oldAutoCommitSetting) {
                // set auto-commit off
                result.setAutoCommit(false);
            }
            return result;

        } catch (SQLException ex) {
            throw new SqlRunnerException("failed to get connection", ex);

        }

    }

    /**
     * Closes a connection after committing and putting auto commit back to how it was before
     * getConnection was called.
     * @param connection
     *   The connection to close.
     */
    private void close(final Connection connection) {
        if (connection != null) {
            try {
                try {
                    // we don't want to close a connection (i.e. put it back in the connection pool)
                    // with an open transaction - so we really have to commit here
                    connection.commit();

                } catch (SQLException ex) {
                    throw new SqlRunnerException("failed to commit", ex);

                }

                try {
                    if (oldAutoCommitSetting != connection.getAutoCommit()) {
                        // if we changed auto commit, put it back to how it was
                        connection.setAutoCommit(oldAutoCommitSetting);
                    }

                } catch (SQLException ex) {
                    throw new SqlRunnerException(
                            "failed to set auto commit back to " + oldAutoCommitSetting, ex);
                }

            } finally {
                // no matter what happens, we'll still close the connection
                try {
                    connection.close();
                }  catch (SQLException ex) {
                    logger.logp(Level.WARNING, CLASS_NAME, "close(java.sql.Connection)",
                            "failed to close connection", ex);
                }

            }

        }

    }

    /**
     * Rolls back a transaction because something went wrong - and re-throws ex.
     * @param connection
     *   The connection to rollback - which must not be null.
     * @param ex
     *   The error - which must not be null.
     * @return
     *   This method never returns, it always throws an exception - this is just to keep the
     *   compiler happy when called from a non-void method.
     */
    private SqlRunnerException rollbackOnError(
            final Connection connection, final SqlRunnerException ex) {
        try {
            connection.rollback();
        } catch (SQLException sqlEx) {
            ex.setRollbackFailedException(sqlEx);
        }
        throw ex;
    }

}
