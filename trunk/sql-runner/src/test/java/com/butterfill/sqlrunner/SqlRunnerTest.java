
package com.butterfill.sqlrunner;

import com.butterfill.sqlrunner.util.DefaultCallbackHandlerImpl;
import com.butterfill.sqlrunner.util.AttributeSettingResultSetNextRowCallbackHandlerImpl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
import static org.mockito.Mockito.*;
import static com.butterfill.sqlrunner.TestHelper.*;
import com.butterfill.sqlrunner.util.DefaultFileReader;
import java.util.ArrayList;

/**
 *
 * @author Peter Butterfill
 */
public class SqlRunnerTest {

    private SqlRunner instance;
    private DataSource dataSource;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private SqlRunnerCallbackHandler callbackHandler;
    private SqlRunnerResultSetNextRowCallbackHandler resultSetNextRowCallbackHandler;
    private SqlRunnerFileReader fileReader;

    public SqlRunnerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        preparedStatement = mock(PreparedStatement.class);
        connection = mock(Connection.class);
        dataSource = mock(DataSource.class);
        callbackHandler = new DefaultCallbackHandlerImpl();
        resultSetNextRowCallbackHandler = new AttributeSettingResultSetNextRowCallbackHandlerImpl();
        fileReader = new DefaultFileReader();
        instance = new SqlRunner(
                dataSource, callbackHandler, resultSetNextRowCallbackHandler, fileReader);
    }

    @After
    public void tearDown() {
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor() {
        new SqlRunner(null, callbackHandler, resultSetNextRowCallbackHandler, fileReader);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor2() {
        new SqlRunner(dataSource, null, resultSetNextRowCallbackHandler, fileReader);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor3() {
        new SqlRunner(dataSource, callbackHandler, null, fileReader);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor4() {
        new SqlRunner(dataSource, callbackHandler, resultSetNextRowCallbackHandler, null);
    }

    @Test(expected = NullPointerException.class)
    public void testToSqlRunnerStatemets_null() {
        instance.toSqlRunnerStatements(null);
    }

    @Test
    public void testToSqlRunnerStatemets() {
        List<String> sqlList = new ArrayList<String>();
        assertTrue(instance.toSqlRunnerStatements(sqlList).isEmpty());
        sqlList.add("a");
        sqlList.add("b");
        List<SqlRunnerStatement> result = instance.toSqlRunnerStatements(sqlList);
        assertEquals(2, result.size());
        SqlRunnerStatement statement = result.get(1);
        assertEquals("b", statement.getSql());
        assertEquals(null, statement.getException());
        assertEquals(true, statement.getFailFast());
        assertEquals(null, statement.getName());
        assertEquals(null, statement.getResult());
        assertEquals(null, statement.getResultOfExecutionWasResultSet());
        assertEquals(null, statement.getUpdateCount());
    }

    /**
     * Test of setAttributePrefix method, of class SqlRunner.
     */
    @Test
    public void testSetAttributePrefixAndPostifx() {
        System.out.println("setAttributePrefix");

        assertEquals("#{", getFieldValue(SqlRunner.class, "attributePrefix", instance));
        assertEquals("}", getFieldValue(SqlRunner.class, "attributePostfix", instance));

        String attributeNamePrefix = "<";
        String attributeNamePostfix = ">";

        SqlRunner result = instance.setAttributePrefixAndPostfix(
                attributeNamePrefix, attributeNamePostfix);
        assertSame(result, instance);
        assertEquals(attributeNamePrefix, getFieldValue(SqlRunner.class, "attributePrefix", result));
        assertEquals(attributeNamePostfix, getFieldValue(SqlRunner.class, "attributePostfix", result));

        result = instance.setAttributePrefixAndPostfix(null, null);
        assertSame(result, instance);
        assertEquals("", getFieldValue(SqlRunner.class, "attributePrefix", result));
        assertEquals("", getFieldValue(SqlRunner.class, "attributePostfix", result));
    }

    /**
     * Test of setAttribute method, of class SqlRunner.
     */
    @Test
    public void testSetAttribute() {
        System.out.println("setAttribute");

        Map attributeMap = (Map) TestHelper.getFieldValue(
                SqlRunner.class, "attributeMap", instance);
        assertTrue(attributeMap.isEmpty());

        String attributeName = "key";
        Object attributeValue = "value";

        SqlRunner result = instance.setAttribute(attributeName, attributeValue);
        assertSame(result, instance);
        assertEquals(attributeValue, attributeMap.get(attributeName));

        attributeValue = null;
        instance.setAttribute(attributeName, attributeValue);
        assertEquals("", attributeMap.get(attributeName));

        attributeValue = 9L;
        instance.setAttribute(attributeName, attributeValue);
        assertEquals("9", attributeMap.get(attributeName));

    }

    /**
     * Test of run method, of class SqlRunner.
     */
    @Test
    public void testRun() throws Exception {
        System.out.println("run");

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(false);
        when(connection.prepareStatement("update emp set a = 'a'")).thenReturn(preparedStatement);
        when(preparedStatement.execute()).thenReturn(false);
        when(preparedStatement.getUpdateCount()).thenReturn(-3);

        String sql = "update #{table} set a = 'a'";

        instance.setAttribute("table", "emp");

        SqlRunnerStatement result = instance.run(sql);
        assertEquals(Integer.valueOf("-3"), result.getUpdateCount());
        assertEquals("update emp set a = 'a'", result.getSql());
        assertEquals(Boolean.FALSE, result.getResultOfExecutionWasResultSet());
        assertEquals(null, result.getException());
    }

    @Test(expected = NullPointerException.class)
    public void testRunNullSql() throws Exception {
        System.out.println("run");
        String sql = null;
        instance.run(sql);

    }

    @Test(expected = NullPointerException.class)
    public void testRunNullSql2() throws Exception {
        System.out.println("run");
        String sql = null;
        instance.run(sql, null);

    }

    @Test(expected = NullPointerException.class)
    public void testRunNullSql3() throws Exception {
        System.out.println("run");
        String sql = "some sql";
        instance.run(sql, null);

    }

    @Test(expected = NullPointerException.class)
    public void testRunNullSql4() throws Exception {
        System.out.println("run");
        List<SqlRunnerStatement> sqlRunnerStatements = null;
        instance.run(sqlRunnerStatements);

    }

    @Test(expected = NullPointerException.class)
    public void testRunNullSql5() throws Exception {
        System.out.println("run");
        List<SqlRunnerStatement> sqlRunnerStatements = null;
        instance.run(sqlRunnerStatements, null);

    }

    @Test(expected = NullPointerException.class)
    public void testRunNullFileName() throws Exception {
        System.out.println("run");
        instance.runFile(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRunNullFileName2() throws Exception {
        System.out.println("run");
        instance.runFile(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testRunNullConnection3() throws Exception {
        System.out.println("run");
        instance.runFile("/test.sql", null);
    }

    @Test(expected = NullPointerException.class)
    public void testRunNullSqlList() throws Exception {
        System.out.println("run");
        List<SqlRunnerStatement> sqlList = null;
        instance.run(sqlList, null);
    }

    @Test(expected = SqlRunnerException.class)
    public void testRunInvalidFileName() throws Exception {
        System.out.println("run");

        instance.runFile("this file does not exist");

    }

    @Test
    public void testFailedToGetConnection() throws Exception {
        when(dataSource.getConnection()).thenThrow(new SQLException("test SQL ex"));
        try {
            instance.run("select * from dual");
            fail();
        } catch (SqlRunnerException ex) {
            assertEquals("failed to get connection", ex.getMessage());
        }
    }

    // TODO: add more exception condition tests

}
