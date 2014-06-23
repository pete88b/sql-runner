package com.butterfill.sqlrunner;

import com.butterfill.sqlrunner.util.DynamicResultSetNextRowCallbackHandlerImpl;
import java.util.logging.Logger;

/**
 * Wraps a SQL statement and holds the results of running the statement.
 * This class also gives the statement a name
 * - you can tell SqlRunner use callback handlers by statement name.
 * By Default, all statements are "fail fast" - this means that if an exception is thrown when
 * the statement is run, the exception will propagate and subsequent statements will not be run.
 * @author Peter Butterfill
 */
public class SqlRunnerStatement {

    /**
     * The name of this class.
     */
    public static final String CLASS_NAME = SqlRunnerStatement.class.getName();

    /**
     * The logger for this class.
     */
    private static final Logger logger = Logger.getLogger(CLASS_NAME);

    /**
     * The "name" of the statement.
     */
    private final String name;

    /**
     * The SQL of this statement.
     */
    private String sql;

    /**
     * Option to control fail fast behaviour.
     */
    private final boolean failFast;

    /**
     * The result of running the statement.
     */
    private Integer updateCount;

    /**
     * Will be set to true if the result of executing the statement was a result set,
     * false if the result of executing the statement was not a result set or
     * null if the statement has not been executed.
     */
    private Boolean resultOfExecutionWasResultSet;

    /**
     * The result of running this statement (set by clients of SqlRunner).
     */
    private Object result;

    /**
     * The exception thrown by running this statement.
     */
    private Exception exception;

    /**
     * Creates a new SqlRunnerStatement giving a name to the specified SQL.
     *
     * @param name
     *   The name of this statement.
     * @param sql
     *   The SQL statement that we will run.
     */
    public SqlRunnerStatement(final String name, final String sql) {
        this.name = name;
        this.sql = sql;
        this.failFast = true;
    }

    /**
     * Creates a new SqlRunnerStatement giving a name to the specified SQL.
     *
     * @param name
     *   The name of this statement.
     * @param sql
     *   The SQL statement that we will run.
     * @param failFast
     *   Pass true if this is a "fail fast" statement, false otherwise.
     */
    public SqlRunnerStatement(final String name, final String sql, final boolean failFast) {
        this.name = name;
        this.sql = sql;
        this.failFast = failFast;
    }

    /**
     * Returns the name of this statement.
     * @return
     *   The name of this statement.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the SQL wrapped by this SqlRunnerStatement.
     * @return
     *   The statement that we will/have run.
     */
    public String getSql() {
        return sql;
    }

    /**
     * Sets the SQL for this statement, replacing the SQL set by the constructor.
     * @param sql
     *   he SQL statement that we will run.
     */
    public void setSql(final String sql) {
        this.sql = sql;
    }

    /**
     * Returns true if this is a "fail fast" statement, false otherwise.
     * @return
     *   true if this is a "fail fast" statement.
     */
    public boolean getFailFast() {
        return failFast;
    }

    /**
     * After statement execution; this method returns true if the result of execution was a
     * ResultSet, false otherwise.
     * Returns null if this statement has not yet been run.
     * @return
     *   true if the result of executing this statement was a java.sql.ResultSet.
     */
    public Boolean getResultOfExecutionWasResultSet() {
        return resultOfExecutionWasResultSet;
    }

    /**
     * Pass true if the result of execution was a ResultSet, false otherwise.
     * @param resultOfExecutionWasResultSet
     *   Pass true if the result of executing this statement was a java.sql.ResultSet.
     */
    public void setResultOfExecutionWasResultSet(final Boolean resultOfExecutionWasResultSet) {
        this.resultOfExecutionWasResultSet = resultOfExecutionWasResultSet;
    }

    /**
     * Returns the result of running the statement - which will be either the row count for
     * SQL DML statements or 0 for SQL statements that return nothing.
     * If the statement has not yet been run, this method will return null.
     * @return
     *   The update count returned by executing the statement.
     */
    public Integer getUpdateCount() {
        return updateCount;
    }

    /**
     * Sets the update count of running the statement.
     * @param updateCount
     *   The update count of running the statement.
     */
    public void setUpdateCount(final Integer updateCount) {
        this.updateCount = updateCount;
    }

    /**
     * Returns the "result" of running this statement
     * - which was probably set by a callback handler after processing a ResultSet.
     * @see DynamicResultSetNextRowCallbackHandlerImpl
     * @return
     *   The "result" of running this statement.
     */
    public Object getResult() {
        return result;
    }

    /**
     * Sets the "result" of running this statement.
     * The result property is provided to make SqlRunnerStatement a result holder - i.e. make it
     * easy to save a result when working with callback handlers.
     * @param result
     *   The "result" of running this statement
     */
    public void setResult(final Object result) {
        this.result = result;
    }

    /**
     * Returns the exception raised by running this statement,
     * returns null if the statement ran successfully or has not yet been run.
     * @return
     *   The exception raised by running this statement,
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Sets the exception raised by running this statement,
     * @param exception
     *   The exception raised by running this statement,
     */
    public void setException(final Exception exception) {
        this.exception = exception;
    }

    /**
     * Returns a string representation of this instance.
     * @return
     *   A string representation of this instance.
     */
    @Override
    public String toString() {
        return new StringBuilder()
                .append("SqlRunnerStatement[\n  name=")
                .append(name)
                .append("\n  statement=")
                .append(sql)
                .append("\n  failFast=")
                .append(failFast)
                .append("\n  resultOfExecutionWasResultSet=")
                .append(resultOfExecutionWasResultSet)
                .append("\n  updateCount=")
                .append(updateCount)
                .append("\n  result=")
                .append(result)
                .append("\n  exception=")
                .append(exception)
                .append("]")
                .toString();
    }

}
