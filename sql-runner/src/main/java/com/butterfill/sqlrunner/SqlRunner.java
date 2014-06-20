
package com.butterfill.sqlrunner;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * Please see {@link com.butterfill.sqlrunner} description for usage details.
 *
 * @author Peter Butterfill
 */
public class SqlRunner {

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
     * The name of the character set of the SQL script file.
     */
    private String charsetName = "UTF-8";

    /**
     * The file path prefix.
     */
    private String filePathPrefix = "";

    /**
     * The attribute name prefix.
     */
    private String attributePrefix = "#{";

    /**
     * The attribute name postfix.
     */
    private String attributePostfix = "}";

    /**
     * Lines with this prefix will be treated as single line comments.
     */
    private String singleLineCommentPrefix = "--";

    /**
     * The sql-runner comment prefix -
     * changing singleLineCommentPrefix will also change sqlRunnerCommentPrefix.
     */
    private String sqlRunnerCommentPrefix = "--sqlrunner:";

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
     */
    public SqlRunner(final DataSource dataSource, final SqlRunnerCallbackHandler callbackHandler,
            final SqlRunnerResultSetNextRowCallbackHandler resultSetNextRowCallbackHandler) {
        if (dataSource == null) {
            throw new NullPointerException("dataSource must not be null");
        }
        if (callbackHandler == null) {
            throw new NullPointerException("callbackHandler must not be null");
        }
        if (resultSetNextRowCallbackHandler == null) {
            throw new NullPointerException("resultSetNextRowCallbackHandler must not be null");
        }
        this.dataSource = dataSource;
        this.defaultCallbackHandler = callbackHandler;
        this.defaultResultSetNextRowCallbackHandler = resultSetNextRowCallbackHandler;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public String getFilePathPrefix() {
        return filePathPrefix;
    }

    public String getAttributePrefix() {
        return attributePrefix;
    }

    public String getAttributePostfix() {
        return attributePostfix;
    }

    public String getSingleLineCommentPrefix() {
        return singleLineCommentPrefix;
    }

    /**
     * Sets the name of the character set of the SQL script file - UTF-8 by default.
     * @param charsetName
     *   The name of the character set of the SQL script file.
     * @return
     *   this instance.
     */
    public SqlRunner setCharsetName(final String charsetName) {
        this.charsetName = charsetName;
        return this;
    }

    /**
     * Sets the file path prefix.
     * The intended use of the file path prefix is to allow developers to work with file names
     * without having to know where the files are located.
     * e.g. To support different DBs we could have the following files
     * <ul>
     *   <li>/oracle/do-some-work.sql</li>
     *   <li>/mysql/do-some-work.sql</li>
     * </ul>
     * Developers could write
     * <p><code>new SqlScriptRunner(dataSource).setFileName("do-some-work.sql")</code></p>
     * and filePathPrefix ("/oracle/" or "/mysql/") could be set on the SqlScriptRunner via
     * dependency injection.
     * <p>See also {@link SqlRunnerFactory}.</p>
     *
     * @param filePathPrefix
     *   The file path prefix.
     * @return
     *   this instance.
     */
    public SqlRunner setFilePathPrefix(final String filePathPrefix) {
        this.filePathPrefix = filePathPrefix;
        return this;
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
     * Sets the single line comment prefix. Double hyphen is the default.
     * Note: MySQL allows # as well as --.
     * @param singleLineCommentPrefix
     *   The single line comment prefix, which must not be null.
     * @return
     *   this instance.
     */
    public SqlRunner setSingleLineCommentPrefix(final String singleLineCommentPrefix) {
        if (singleLineCommentPrefix == null) {
            throw new NullPointerException("singleLineCommentPrefix must not be null");
        }
        this.singleLineCommentPrefix = singleLineCommentPrefix;
        this.sqlRunnerCommentPrefix = singleLineCommentPrefix + "sqlrunner:";
        return this;
    }

    /**
     * Sets the value of a attribute on this instance.
     * @param attribute
     *   Name of the attribute.
     * @param attributeValue
     *   Value of the attribute - which will be converted to a String.
     * @return
     *   this instance.
     */
    public SqlRunner setAttribute(final String attribute, final Object attributeValue) {
        final String stringAttributeValue;
        if (attributeValue == null) {
            stringAttributeValue = "";
        } else if (attributeValue instanceof String) {
            stringAttributeValue = (String) attributeValue;
        } else {
            stringAttributeValue = attributeValue.toString();
        }

        attributeMap.put(attribute, stringAttributeValue);

        return this;

    }

    /**
     *
     * @param statementName
     * @param handler
     * @return
     *   this instance.
     */
    public SqlRunner setCallbackHandler(
            final String statementName, final SqlRunnerCallbackHandler handler) {
        callbackHandlerMap.put(statementName, handler);
        return this;
    }

    /**
     *
     * @param statementName
     *   Can be null, which is useful if you want a handler when calling
     *   {@link SqlRunner#run(java.lang.String) }.
     * @param handler
     * @return
     *   this instance.
     */
    public SqlRunner setResultSetNextRowCallbackHandler(final String statementName,
            final SqlRunnerResultSetNextRowCallbackHandler handler) {
        rsnrCallbackHandlerMap.put(statementName, handler);
        return this;
    }

    /**
     * Runs all SQL statements read from the specified file.
     * This method will;
     * <ul>
     *   <li>read the specified file from the classpath (using filePathPrefix)</li>
     *   <li>
     *     Replace all attributes in the file contents that have been set by calls to
     *     setAttributeValues(String)
     *   </li>
     *   <li>
     *     Splits the file contents into statements - statements are terminated by a semi-colon.
     *   </li>
     *   <li>Get a connection and disable auto commit</li>
     *   <li>Run the each statement using PreparedStatement#executeUpdate()</li>
     *   <li>If running a statement throws an exception;</li>
     *   <ul>
     *     <li>Rollback the transaction</li>
     *     <li>and throw a SqlRunnerException</li>
     *   </ul>
     *   <li>If all statements run without error;</li>
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
     * @param fileName
     *   The name of the file (containing any number of SQL DML statements) that you want to run.
     * @return
     *   One SqlRunnerResult for each statement executed.
     */
    public List<SqlRunnerStatement> runFile(final String fileName) {
        if (fileName == null) {
            throw new NullPointerException("fileName must not be null");
        }

        // read file into string and set property values
        final List<SqlRunnerStatement> sqlRunnerStatements = readFile(fileName);
        final Connection connection = getConnection();

        try {
            for (SqlRunnerStatement sqlRunnerStatement : sqlRunnerStatements) {
                sqlRunnerStatement.setSql(replaceAttributes(sqlRunnerStatement.getSql()));
                execute(connection, sqlRunnerStatement);
            }

        } catch (SqlRunnerException ex) {
            throw rollbackOnError(connection, ex);

        } finally {
            close(connection);

        }

        return sqlRunnerStatements;

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
     * connection, rather than getting a connection from the datasource.
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
        if (fileName == null) {
            throw new NullPointerException("fileName must not be null");
        }

        if (connection == null) {
            throw new NullPointerException("connection must not be null");
        }

        // read file into string and set property values
        final List<SqlRunnerStatement> sqlRunnerStatements = readFile(fileName);

        for (SqlRunnerStatement sqlRunnerStatement : sqlRunnerStatements) {
            sqlRunnerStatement.setSql(replaceAttributes(sqlRunnerStatement.getSql()));
            execute(connection, sqlRunnerStatement);
        }

        return sqlRunnerStatements;

    }

    /**
     * Runs a single SQL statement.
     * This method does the same as {@link #run(java.lang.String) } but uses the specified
     * connection, rather than getting a connection from the datasource.
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
            logger.logp(Level.FINER, CLASS_NAME, method, "{0}", sqlRunnerStatement);
            handler.executeComplete(preparedStatement, sqlRunnerStatement);
            return sqlRunnerStatement;

        } catch (SQLException ex) {
            throw new SqlRunnerException("failed to execute. " + sqlRunnerStatement, ex);

        } finally {
            close(preparedStatement);

        }

    }

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
     * Closes a closeable without letting exceptions propagate.
     * @param closeable
     *   The closeable to close.
     */
    private void close(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ex) {
            logger.logp(Level.WARNING, CLASS_NAME, "close(java.io.Closeable)",
                    "failed to close closeable", ex);
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
     * Closes a connection after commiting and putting auto commit back to how it was before
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

    /**
     * Reads a file.
     * @param fileName
     *   Name of the file to read.
     * @return
     *   The statements from the file.
     */
    private List<SqlRunnerStatement> readFile(final String fileName) {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                    this.getClass().getResourceAsStream(filePathPrefix + fileName), charsetName));

            final List<SqlRunnerStatement> sqlRunnerStatements =
                    new ArrayList<SqlRunnerStatement>();

            final StringBuilder sqlBuilder = new StringBuilder();
            String statementName = null;
            boolean inMultiLineComment = false;

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                final String trimmedLine = line.trim();

                if (trimmedLine.startsWith(sqlRunnerCommentPrefix)) {
                    // we've found a sql runner comment - this gives us the statement name
                    statementName = trimmedLine.substring(sqlRunnerCommentPrefix.length()).trim();
                    continue;
                }

                if ("".equals(trimmedLine)
                        || trimmedLine.startsWith(singleLineCommentPrefix)) {
                    // skip single line comments and empty lines
                    continue;
                }

                // skip multi-line comments
                if (trimmedLine.startsWith("/*")) {
                    // we're only in a multi-line comment if it is not ended on this line
                    inMultiLineComment = !trimmedLine.endsWith("*/");
                    continue;
                } else if (trimmedLine.endsWith("*/")) {
                    inMultiLineComment = false;
                    continue;
                } else if (inMultiLineComment) {
                    continue;
                }

                // add the line to the statement builder
                sqlBuilder.append(line);

                if (trimmedLine.endsWith(";")) {
                    // statements are terminated with a semi-colon
                    final String sql = sqlBuilder.substring(0, sqlBuilder.length() - 1);
                    sqlRunnerStatements.add(new SqlRunnerStatement(statementName, sql));
                    statementName = null;
                    sqlBuilder.setLength(0);

                } else {
                    sqlBuilder.append(LINE_SEPARATOR);

                }

            }

            return sqlRunnerStatements;

        } catch (Exception ex) {
            // we want to catch IOException and runtime exceptions such as NullPointerException
            // thrown by InputStreamReader when the resource is not found
            throw new SqlRunnerException(
                    "failed to read file [" + fileName
                    + "]. using filePathPrefix [" + filePathPrefix + "]",
                    ex);

        } finally {
            close(reader);

        }

    }

}
