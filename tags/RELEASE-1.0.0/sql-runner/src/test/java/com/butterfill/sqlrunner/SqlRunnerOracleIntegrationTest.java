
package com.butterfill.sqlrunner;

import com.butterfill.sqlrunner.util.DynamicResultSetNextRowCallbackHandlerImpl;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Peter Butterfill
 */
@ContextConfiguration(locations = "classpath:oracle/test-context-oracle.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SqlRunnerOracleIntegrationTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    SqlRunnerFactory sqlRunnerFactory;

    @Autowired
    DataSource dataSource;

    public SqlRunnerOracleIntegrationTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setAttribute method, of class SqlRunner.
     */
    @Test
    public void testSetAttribute() {
        System.out.println("setAttribute");
        SqlRunner instance = sqlRunnerFactory.newSqlRunner();
        SqlRunner expResult = instance;
        SqlRunner result = instance.setAttribute("a", "A");
        assertEquals(expResult, result);
        // TODO: xxx

    }

    /**
     * Test of setStatementPreparedCallbackHandler method, of class SqlRunner.
     */
    @Test
    public void testSetStatementPreparedCallbackHandler() {
        System.out.println("setStatementPreparedCallbackHandler");
        String statementName = "statement1";
        SqlRunnerCallbackHandler handler = new SqlRunnerCallbackHandler() {

            public PreparedStatement prepareStatement(Connection c, SqlRunnerStatement s) {
                return null;
            }

            public void executeComplete(PreparedStatement p, SqlRunnerStatement s) {
            }
        };
        SqlRunner instance = sqlRunnerFactory.newSqlRunner();
        SqlRunner expResult = instance;
        SqlRunner result = instance.setCallbackHandler(statementName, handler);
        assertEquals(expResult, result);
    }

    /**
     * Test of setResultSetNextRowCallbackHandler method, of class SqlRunner.
     */
    @Test
    public void testSetResultSetNextRowCallbackHandler() {
        System.out.println("setResultSetNextRowCallbackHandler");
        String statementName = "s2";
        SqlRunnerResultSetNextRowCallbackHandler handler =
                new SqlRunnerResultSetNextRowCallbackHandler() {

                    public void nextRow(SqlRunner r, SqlRunnerStatement s, ResultSet rs, int rowNumber) {
                // do nothing
            }
        };
        SqlRunner instance = sqlRunnerFactory.newSqlRunner();
        SqlRunner expResult = instance;
        SqlRunner result = instance.setResultSetNextRowCallbackHandler(statementName, handler);
        assertEquals(expResult, result);

    }

    /**
     * Test of runFile method, of class SqlRunner.
     */
    @Test
    public void testRunFile_emptySql() {
        if (!TestHelper.isOracleDb(dataSource)) {
            return;
        }
        System.out.println("runFile");
        String fileName = "empty.sql";
        SqlRunner instance = sqlRunnerFactory.newSqlRunner();
        List<SqlRunnerStatement> result = instance.runFile(fileName);
        assertEquals(0, result.size());

    }

    /**
     * Test of runFile method, of class SqlRunner.
     */
    @Test
    public void testRunFile() {
        if (!TestHelper.isOracleDb(dataSource)) {
            return;
        }
        System.out.println("runFile");
        String fileName = "test_1.sql";
        SqlRunner instance = sqlRunnerFactory.newSqlRunner();
        List<SqlRunnerStatement> result = instance.runFile(fileName);
        assertEquals(3, result.size());

        assertEquals(Integer.valueOf("1"), result.get(0).getUpdateCount());
        assertEquals(null, result.get(1).getUpdateCount());
        assertEquals(Integer.valueOf("0"), result.get(2).getUpdateCount());

        Map<String, String> attributeMap = (Map<String, String>)
                TestHelper.getFieldValue(SqlRunner.class, "attributeMap", instance);
        assertEquals("1", attributeMap.get("NUMBER_COL.1"));
        assertEquals("a", attributeMap.get("TEXT_COL.1"));

    }

    /**
     * same as the test above but uses in-line SQL instead of test_1.sql.
     */
    @Test
    public void testRunListOfStatements() {
        if (!TestHelper.isOracleDb(dataSource)) {
            return;
        }
        System.out.println("");

        List<String> sqlList = new ArrayList<String>();
        sqlList.add("create table a as\n" +
                "select rownum as number_col, 'a' as text_col\n" +
                "  from all_objects\n" +
                " where rownum < 2");

        sqlList.add("select * from a");

        sqlList.add("drop table a");

        SqlRunner instance = sqlRunnerFactory.newSqlRunner();
        List<SqlRunnerStatement> result = instance.run(instance.toSqlRunnerStatements(sqlList));
        assertEquals(3, result.size());

        assertEquals(Integer.valueOf("1"), result.get(0).getUpdateCount());
        assertEquals(null, result.get(1).getUpdateCount());
        assertEquals(Integer.valueOf("0"), result.get(2).getUpdateCount());

        Map<String, String> attributeMap = (Map<String, String>)
                TestHelper.getFieldValue(SqlRunner.class, "attributeMap", instance);
        assertEquals("1", attributeMap.get("NUMBER_COL.1"));
        assertEquals("a", attributeMap.get("TEXT_COL.1"));

    }

    /**
     * Test of runFile method, of class SqlRunner.
     */
    @Test
    public void testRunFile_withConnection() throws Exception {
        if (!TestHelper.isOracleDb(dataSource)) {
            return;
        }
        System.out.println("runFile");
        String fileName = "test_1.sql";

        Connection connection = dataSource.getConnection();

        SqlRunner instance = sqlRunnerFactory.newSqlRunner();
        List<SqlRunnerStatement> result = instance.runFile(fileName, connection);
        assertEquals(3, result.size());

        assertEquals(Integer.valueOf("1"), result.get(0).getUpdateCount());
        assertEquals(null, result.get(1).getUpdateCount());
        assertEquals(Integer.valueOf("0"), result.get(2).getUpdateCount());

        Map<String, String> attributeMap = (Map<String, String>)
                TestHelper.getFieldValue(SqlRunner.class, "attributeMap", instance);
        assertEquals("1", attributeMap.get("NUMBER_COL.1"));
        assertEquals("a", attributeMap.get("TEXT_COL.1"));

    }

    /**
     * Test of runFile method, of class SqlRunner.
     */
    @Test
    public void testRunFile_withBindingParamter_notSet() {
        if (!TestHelper.isOracleDb(dataSource)) {
            return;
        }
        System.out.println("runFile");
        String fileName = "test_2.sql";
        SqlRunner instance = sqlRunnerFactory.newSqlRunner();

        try {
            instance.runFile(fileName);
            fail("expected an exception becuase the bind parameter was not set");

        } catch (SqlRunnerException ex) {
            assertTrue(ex.getMessage().startsWith("failed to execute"));

        } finally {
            instance.run("drop table a");

        }

    }

    @Test
    public void testRunFile_withBindingParamter() {
        if (!TestHelper.isOracleDb(dataSource)) {
            return;
        }

        System.out.println("testRunFile_withBindingParamter");

        String fileName = "test_2.sql";
        SqlRunner instance = sqlRunnerFactory.newSqlRunner();
        instance.setCallbackHandler("select-from-table-a", new SqlRunnerCallbackHandler() {

            public PreparedStatement prepareStatement(
                    Connection connection, SqlRunnerStatement sqlRunnerStatement)
                    throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(sqlRunnerStatement.getSql());
                preparedStatement.setInt(1, 3);
                return preparedStatement;
            }

            public void executeComplete(PreparedStatement preparedStatement, SqlRunnerStatement sqlRunnerStatement) throws SQLException {

            }
        });
        List<SqlRunnerStatement> result = instance.runFile(fileName);
        assertEquals(3, result.size());

        assertEquals(Integer.valueOf("20"), result.get(0).getUpdateCount());
        assertEquals(null, result.get(1).getUpdateCount());
        assertEquals(Integer.valueOf("0"), result.get(2).getUpdateCount());

        Map<String, String> attributeMap = (Map<String, String>)
                TestHelper.getFieldValue(SqlRunner.class, "attributeMap", instance);
        assertEquals("1", attributeMap.get("NUMBER_COL.1"));
        assertEquals("a", attributeMap.get("TEXT_COL.1"));

        assertEquals("2", attributeMap.get("NUMBER_COL.2"));
        assertEquals("a", attributeMap.get("TEXT_COL.2"));

        assertEquals(4, attributeMap.size());

    }

    /**
     * Test of runFile method, of class SqlRunner.
     */
    @Test
    public void testRunFile_withResultSetNextRowCallbackHandler() {
        if (!TestHelper.isOracleDb(dataSource)) {
            return;
        }

        System.out.println("runFile_withResultSetNextRowCallbackHandler");
        final List<Object> results = new ArrayList<Object>();
        SqlRunner instance = sqlRunnerFactory.newSqlRunner();
        instance.setResultSetNextRowCallbackHandler(null, new SqlRunnerResultSetNextRowCallbackHandler() {

            public void nextRow(SqlRunner sqlRunner, SqlRunnerStatement statement,
                    ResultSet resultSet, int rowNumber)
                    throws SQLException {
                results.add(resultSet.getObject(1));
                results.add(resultSet.getObject(2));
                // expect only one row
                assertEquals(1, rowNumber);
            }
        });
        String fileName = "test_1.sql";

        List<SqlRunnerStatement> result = instance.runFile(fileName);
        assertEquals(3, result.size());

        assertEquals(Integer.valueOf("1"), result.get(0).getUpdateCount());
        assertEquals(null, result.get(1).getUpdateCount());
        assertEquals(Integer.valueOf("0"), result.get(2).getUpdateCount());

        assertEquals(BigDecimal.ONE, results.get(0));
        assertEquals("a", results.get(1));
        assertEquals(2, results.size());

    }

    /**
     * Test of runFile method, of class SqlRunner.
     */
    @Test
    public void testRunFile_withDynamicResultSetNextRowCallbackHandler() {
        if (!TestHelper.isOracleDb(dataSource)) {
            return;
        }

        System.out.println("runFile_withDynamicResultSetNextRowCallbackHandler");

        SqlRunner instance = sqlRunnerFactory.newSqlRunner();
        instance.setResultSetNextRowCallbackHandler(
                null, new DynamicResultSetNextRowCallbackHandlerImpl());
        String fileName = "test_3.sql";

        List<SqlRunnerStatement> result = instance.runFile(fileName);
        assertEquals(3, result.size());

        assertEquals(Integer.valueOf("3"), result.get(0).getUpdateCount());
        assertEquals(null, result.get(1).getUpdateCount());
        assertEquals(Integer.valueOf("0"), result.get(2).getUpdateCount());

        List<Map<String, Object>> results = (List<Map<String, Object>>) result.get(1).getResult();
        assertEquals(3, results.size());
        Map<String, Object> row = results.get(0);
        assertEquals(BigDecimal.ONE, row.get("numberCol"));
        assertEquals("A", row.get("textCol"));

    }

    /**
     * Test of run method, of class SqlRunner.
     */
    @Test
    public void testRun() {
        if (!TestHelper.isOracleDb(dataSource)) {
            return;
        }

        System.out.println("run");

        SqlRunner instance = sqlRunnerFactory.newSqlRunner();

        try {
            String sql = "create table a as select 1 as number_col, 'a' as text_col "
                    + "from all_objects where rownum < 2";
            SqlRunnerStatement result = instance.run(sql);
            assertEquals(Integer.valueOf("1"), result.getUpdateCount());
            assertFalse(result.getResultOfExecutionWasResultSet());

            sql = "select * from a";
            result = instance.run(sql);
            assertEquals(null, result.getUpdateCount());
            assertTrue(result.getResultOfExecutionWasResultSet());

            Map<String, String> attributeMap = (Map<String, String>)
                    TestHelper.getFieldValue(SqlRunner.class, "attributeMap", instance);
            assertEquals("1", attributeMap.get("NUMBER_COL.1"));
            assertEquals("a", attributeMap.get("TEXT_COL.1"));

        } finally {
            instance.run("drop table a");

        }

    }

    /**
     * Test of run method, of class SqlRunner.
     */
    @Test
    public void testRun_withConnection() throws Exception {
        if (!TestHelper.isOracleDb(dataSource)) {
            return;
        }

        System.out.println("run");

        SqlRunner instance = sqlRunnerFactory.newSqlRunner();

        try {
            Connection connection = dataSource.getConnection();

            String sql = "create table a as select 1 as number_col, 'a' as text_col "
                    + "from all_objects where rownum < 2";
            SqlRunnerStatement result = instance.run(sql, connection);
            assertEquals(Integer.valueOf("1"), result.getUpdateCount());
            assertFalse(result.getResultOfExecutionWasResultSet());

            sql = "select * from a";
            result = instance.run(sql, connection);
            assertEquals(null, result.getUpdateCount());
            assertTrue(result.getResultOfExecutionWasResultSet());

            Map<String, String> attributeMap = (Map<String, String>)
                    TestHelper.getFieldValue(SqlRunner.class, "attributeMap", instance);
            assertEquals("1", attributeMap.get("NUMBER_COL.1"));
            assertEquals("a", attributeMap.get("TEXT_COL.1"));

            connection.close();

        } finally {
            instance.run("drop table a");

        }

    }

    /**
     * Test of run method, of class SqlRunner.
     */
    @Test
    public void testRun_withCallbackHandler() {
        if (!TestHelper.isOracleDb(dataSource)) {
            return;
        }

        System.out.println("run_withCallbackHandler");

        SqlRunner instance = sqlRunnerFactory.newSqlRunner();

        SqlRunnerCallbackHandler callbackHandler = new SqlRunnerCallbackHandler() {

            public PreparedStatement prepareStatement(
                    Connection connection, SqlRunnerStatement sqlRunnerStatement)
                    throws SQLException {
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sqlRunnerStatement.getSql());
                preparedStatement.setInt(1, 9);
                return preparedStatement;
            }

            public void executeComplete(PreparedStatement preparedStatement, SqlRunnerStatement sqlRunnerStatement) throws SQLException {

            }
        };

        try {
            String sql =
                    "create table a(number_col number, text_col varchar2(20) default 'test text')";
            SqlRunnerStatement result = instance.run(sql);
            assertEquals(Integer.valueOf("0"), result.getUpdateCount());
            assertFalse(result.getResultOfExecutionWasResultSet());

            sql = "insert into a(number_col) values(?)";
            instance.setCallbackHandler(null, callbackHandler);
            result = instance.run(sql);
            instance.setCallbackHandler(null, null);
            assertEquals((Object) 1, result.getUpdateCount());
            assertFalse(result.getResultOfExecutionWasResultSet());

            sql = "select * from a";
            instance.run(sql);

            Map<String, String> attributeMap = (Map<String, String>)
                    TestHelper.getFieldValue(SqlRunner.class, "attributeMap", instance);
            assertEquals("9", attributeMap.get("NUMBER_COL.1"));
            assertEquals("test text", attributeMap.get("TEXT_COL.1"));

        } finally {
            instance.run("drop table a");

        }

    }

    /**
     * Test of run method, of class SqlRunner.
     */
    @Test
    public void testRun_badSql() {
        if (!TestHelper.isOracleDb(dataSource)) {
            return;
        }

        System.out.println("run_badSql");

        SqlRunner instance = sqlRunnerFactory.newSqlRunner();
        try {
            instance.run("bad sql");
            fail();
        } catch (SqlRunnerException ex) {

        }
    }

    /**
     * Test of run method, of class SqlRunner.
     */
    @Test
    public void testRun_withResultSetNextRowCallbackHandler() {
        if (!TestHelper.isOracleDb(dataSource)) {
            return;
        }

        System.out.println("testRun_withResultSetNextRowCallbackHandler");

        final List<Object> results = new ArrayList<Object>();

        SqlRunner instance = sqlRunnerFactory.newSqlRunner();

        instance.setResultSetNextRowCallbackHandler(null, new SqlRunnerResultSetNextRowCallbackHandler() {

            public void nextRow(SqlRunner sqlRunner, SqlRunnerStatement statement,
                    ResultSet resultSet, int rowNumber)
                    throws SQLException {
                results.add(resultSet.getObject(1));
                results.add(resultSet.getObject(2));
                // expect only one row
                assertEquals(1, rowNumber);
            }
        });

        try {
            String sql = "create table a as select 1 as number_col, 'a' as text_col "
                    + "from all_objects where rownum < 2";
            SqlRunnerStatement result = instance.run(sql);
            assertEquals(Integer.valueOf("1"), result.getUpdateCount());
            assertFalse(result.getResultOfExecutionWasResultSet());

            sql = "select * from a";
            result = instance.run(sql);
            assertEquals(null, result.getUpdateCount());
            assertTrue(result.getResultOfExecutionWasResultSet());
            assertEquals(BigDecimal.ONE, results.get(0));
            assertEquals("a", results.get(1));
            assertEquals(2, results.size());

        } finally {
            instance.run("drop table a");

        }

    }

}
