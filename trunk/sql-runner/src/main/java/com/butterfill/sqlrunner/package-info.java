/**
 * Provides an easy way to run SQL from Java.
 *
 * <p>
 * {@link com.butterfill.sqlrunner.SqlRunner#runFile(java.lang.String)}
 * runs any number of statements read from a file.<br/>
 * {@link com.butterfill.sqlrunner.SqlRunner#run(java.lang.String)}
 * runs a single statement (passed in as a string).
 * </p>
 *
 * <p>
 * The {@link com.butterfill.sqlrunner.SqlRunner#runFile(java.lang.String, java.sql.Connection)} and
 * {@link com.butterfill.sqlrunner.SqlRunner#run(java.lang.String, java.sql.Connection)}
 * methods do the same as
 * {@link com.butterfill.sqlrunner.SqlRunner#runFile(java.lang.String)} and
 * {@link com.butterfill.sqlrunner.SqlRunner#run(java.lang.String)}
 * but use the specified connection, rather than getting a connection from the datasource.
 * <br/>
 * This might be useful if you needed to use SqlRunner and Hibernate to access a DB in the same
 * transaction. Maybe using Hibernates Session#doWork(Work) method.
 * </p>
 *
 * <p>
 * Using attributes in your SQL statements.
 * <br/>
 * <strong>Using attributes could make your code vulnerable to SQL injection attacks.</strong>
 * <br/>
 * The default attributePrefix and attributePostfix are "#{" and "}" so if you call
 * <code>setAttribute("empno", 12345)</code> you should use <code>#{empno}</code> in your SQL.
 * </p>
 * So if you had the following SQL;<br/>
 * <code>UPDATE emp SET sal = sal * 2 WHERE empno = #{empno};</code><br/>
 * After replacing attributes, your SQL will be;<br/>
 * <code>UPDATE emp SET sal = sal * 2 WHERE empno = 12345;</code><br/>
 * <p>
 * SqlRunner will not throw an exception if;
 * <ul>
 *   <li>you set attributes that are not used in the statement (or file)</li>
 *   <li>
 *     you have not set attributes that are used in the statement (or file) -
 *     as long as your SQL is still valid.
 *   </li>
 * </ul>
 * </p>
 *
 * <h3>Running SELECT statements.</h3>
 * <p>
 * By default, the results of a SELECT statement are added as attributes - so they can be used in
 * subsequent statements.
 * This is achieved by default using
 * {@link com.butterfill.sqlrunner.util.AttributeSettingResultSetNextRowCallbackHandlerImpl}.<br/>
 * {@link com.butterfill.sqlrunner.util.DynamicResultSetNextRowCallbackHandlerImpl} could be useful
 * if you would like the results as a collection of maps.
 * <br/>
 * If you need custom behaviour, use a
 * {@link com.butterfill.sqlrunner.SqlRunnerResultSetNextRowCallbackHandler}.
 * </p>
 *
 *
 * <h3>Using files to execute multiple statements.</h3>
 * <p>
 * You can have any number of statements in the file.
 * These can be any kind of SQL statement.<br/>
 * No effort is made to support running procedural code - but if your code will run via
 * {@link java.sql.PreparedStatement#execute()} SqlRunner will not try to block it.<br/>
 * The file is read from the class path using filePathPrefix + fileName.<br/>
 * Comments are removed before running a statement.<br/>
 * Statements are terminated by semi-columns.<br/>
 * </p>
 *
 * <h3>Using parameters in your SQL statements.</h3>
 *
 * Retrieving generated key values can be done with a
 * {@link com.butterfill.sqlrunner.SqlRunnerCallbackHandler}.
 * See unit tests for examples.
 * <br/>
 * Binding "out" parameters and retrieving their values can be done with a
 * {@link com.butterfill.sqlrunner.SqlRunnerCallbackHandler}.
 * See unit tests for examples.
 * <br/>
 * Binding values to "in" parameters can be done with a {@link com.butterfill.sqlrunner.SqlRunnerCallbackHandler}.
 *
 * <pre>
 *    public void demoWithBindingParamter() {
 *
 *        // create a new callback handler that will set value of the binding parameter
 *        final SqlRunnerCallbackHandler callbackHandler = new SqlRunnerCallbackHandler() {
 *
 *            public PreparedStatement prepareStatement(
 *                    Connection connection, SqlRunnerStatement sqlRunnerStatement)
 *                    throws SQLException {
 *                PreparedStatement preparedStatement =
 *                        connection.prepareStatement(sqlRunnerStatement.getSql());
 *                // set value of parameter (in position 1) to 3
 *                preparedStatement.setInt(1, 3);
 *                return preparedStatement;
 *            }
 *
 *            public void executeComplete(
 *                    PreparedStatement preparedStatement, SqlRunnerStatement sqlRunnerStatement)
 *                    throws SQLException {
 *                // don't need to do anything here
 *            }
 *
 *        };
 *
 *        // create a new SqlRunner, set the callback handler and run the file
 *        List&lt;SqlRunnerStatement> result = sqlRunnerFactory.newSqlRunner()
 *                .setCallbackHandler("select-from-table-a", callbackHandler)
 *                .runFile("demo.sql");
 *
 *    }
 *
 * </pre>
 *
 * demo.sql
 * <pre>
 *   --sqlrunner.name: create-table-a
 *   create table a as
 *   select rownum as number_col, 'a' as text_col
 *     from all_objects
 *    where rownum &lt;= 20;
 *
 *   --sqlrunner.name: select-from-table-a
 *   select * from a where number_col &lt; ?;
 *
 *   drop table a;
 * </pre>
 *
 */

package com.butterfill.sqlrunner;
