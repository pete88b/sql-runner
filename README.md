# sql-runner
Automatically exported from code.google.com/p/sql-runner

#Provides an easy way to run SQL from Java.
The initial idea behind this project was to make it easy to run any number of SQL statements, read from a file, in a single transaction. You could see this as an alternative to using simple stored procedures - but wouldn't want to use sql-runner in place of ORM.

Use ;
* SqlRunner#runFile(java.lang.String) to run any number of statements read from a file.
* SqlRunner#run(java.lang.String) to run a single statement.
* SqlRunner#run(java.util.List) to run a multiple statements.
 
The methods;
* SqlRunner#runFile(java.lang.String, java.sql.Connection),
* SqlRunner#run(java.lang.String, java.sql.Connection) and
* SqlRunner#run(java.util.List, java.sql.Connection)

do the same as;
* SqlRunner#runFile(java.lang.String),
* SqlRunner#run(java.lang.String) and
* SqlRunner#run(java.util.List)
but use the specified connection, rather than getting a connection from the datasource. This might be useful if you needed to use SqlRunner and Hibernate to access a DB in the same transaction. Maybe using Hibernates Session#doWork(Work) method.

## Access via maven
sql-runner is available via maven central;
```
<dependency>
    <groupId>com.butterfill</groupId>
    <artifactId>sql-runner</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick example of use
The following example shows how you can run an update statement and then use the update count;
```
    SqlRunnerStatement result = sqlRunnerFactory.newSqlRunner()
            .setAttribute("empno", 12345L)
            .run("UPDATE emp SET sal = sal * 2 WHERE empno = #{empno};");

    System.out.println("updated " + result.getUpdateCount() + " rows in emp");
```
It is expected that the SQL runner factory will be configured via dependency injection. 
Please take a look at the unit tests if you'd like to see more examples.

## Using attributes in your SQL statements.
Using attributes could make your code vulnerable to SQL injection attacks. 
The default attributePrefix and attributePostfix are "#{" and "}" so if you call setAttribute("empno", 12345) you should 
use #{empno} in your SQL. So if you had the following SQL;
```
UPDATE emp SET sal = sal * 2 WHERE empno = #{empno};
```
After replacing attributes, your SQL will be;
```
UPDATE emp SET sal = sal * 2 WHERE empno = 12345;
```
SqlRunner will not throw an exception if;
* you set attributes that are not used in the statement (or file)
* you have not set attributes that are used in the statement (or file) - as long as your SQL is still valid.

## Running SELECT statements.
By default, the results of a SELECT statement are added as attributes - so they can be used in subsequent statements. 
This is achieved by default using AttributeSettingResultSetNextRowCallbackHandlerImpl.
DynamicResultSetNextRowCallbackHandlerImpl could be useful if you would like the results as a collection of maps. 
If you need custom behaviour, use a SqlRunnerResultSetNextRowCallbackHandler.

## Using files to execute multiple statements.
You can have any number of statements in the file. These can be any kind of SQL statement. 
No effort is made to support running procedural code - but if your code will run via PreparedStatement.execute() SqlRunner 
will not try to block it. The file is read from the class path using filePathPrefix + fileName. 
Comments are removed before running a statement. Statements are terminated by semi-columns.

## Running lists of statements.
You can build a list of SqlRunnerStatement. 
If you have a list of SQL strings, you can easily convert them to SqlRunnerStatements using 
SqlRunner.toSqlRunnerStatements(java.util.List). e.g.
```
     List sqlList = new ArrayList();
     ... add any number of SQL statement strings to the list ...
     SqlRunner sqlRunner = sqlRunnerFactory.newSqlRunner();
     List results = sqlRunner.run(sqlRunner.toSqlRunnerStatements(sqlList));
```
## Using parameters in your SQL statements.
Retrieving generated key values can be done with a SqlRunnerCallbackHandler. See unit tests for examples. 

Binding "out" parameters and retrieving their values can be done with a SqlRunnerCallbackHandler. See unit tests for examples. 

Binding values to "in" parameters can be done with a SqlRunnerCallbackHandler.
```
   public void demoWithBindingParamter() {

        // create a new callback handler that will set value of the binding parameter
        final SqlRunnerCallbackHandler callbackHandler = new SqlRunnerCallbackHandler() {

            public PreparedStatement prepareStatement(
                    Connection connection, SqlRunnerStatement sqlRunnerStatement)
                    throws SQLException {
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sqlRunnerStatement.getSql());
                // set value of parameter (in position 1) to 3
                preparedStatement.setInt(1, 3);
                return preparedStatement;
            }

            public void executeComplete(
                    PreparedStatement preparedStatement, SqlRunnerStatement sqlRunnerStatement)
                    throws SQLException {
                // don't need to do anything here
            }

        };

        // create a new SqlRunner, set the callback handler and run the file
        List<SqlRunnerStatement> result = sqlRunnerFactory.newSqlRunner()
                .setCallbackHandler("select-from-table-a", callbackHandler)
                .runFile("demo.sql");

    }
```
demo.sql
```
   --sqlrunner.name: create-table-a
   create table a as
   select rownum as number_col, 'a' as text_col
     from all_objects
    where rownum <= 20;

   --sqlrunner.name: select-from-table-a
   select * from a where number_col < ?;

   drop table a;
```
