
package com.butterfill.sqlrunner;

import com.butterfill.sqlrunner.util.DefaultCallbackHandlerImpl;
import com.butterfill.sqlrunner.util.AttributeSettingResultSetNextRowCallbackHandlerImpl;
import java.lang.reflect.Method;
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

/**
 *
 * @author Peter Butterfill
 */
public class SqlRunnerTest {

    /**
     * Line separator on this platform.
     */
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    private SqlRunner instance;
    private DataSource dataSource;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private SqlRunnerCallbackHandler callbackHandler;
    private SqlRunnerResultSetNextRowCallbackHandler resultSetNextRowCallbackHandler;

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
        instance = new SqlRunner(dataSource, callbackHandler, resultSetNextRowCallbackHandler);
    }

    @After
    public void tearDown() {
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor() {
        new SqlRunner(null, callbackHandler, resultSetNextRowCallbackHandler);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor2() {
        new SqlRunner(dataSource, null, resultSetNextRowCallbackHandler);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor3() {
        new SqlRunner(dataSource, callbackHandler, null);
    }

    /**
     * Test of setCharsetName method, of class SqlRunner.
     */
    @Test
    public void testSetCharsetName() {
        System.out.println("setCharsetName");
        assertEquals("UTF-8", instance.getCharsetName());

        String charsetName = "couldBeAnything";

        SqlRunner result = instance.setCharsetName(charsetName);
        assertSame(result, instance);
        assertEquals(charsetName, result.getCharsetName());
        assertEquals(charsetName, instance.getCharsetName());
    }

    /**
     * Test of setFilePathPrefix method, of class SqlRunner.
     */
    @Test
    public void testSetFilePathPrefix() {
        System.out.println("setFilePathPrefix");

        assertEquals("", instance.getFilePathPrefix());

        String filePathPrefix = "/com/test/scripts/mysql/";

        SqlRunner result = instance.setFilePathPrefix(filePathPrefix);
        assertSame(result, instance);
        assertEquals(filePathPrefix, instance.getFilePathPrefix());
        assertEquals(filePathPrefix, result.getFilePathPrefix());
    }

    /**
     * Test of setAttributePrefix method, of class SqlRunner.
     */
    @Test
    public void testSetAttributePrefix() {
        System.out.println("setAttributePrefix");

        assertEquals("#{", instance.getAttributePrefix());
        assertEquals("}", instance.getAttributePostfix());

        String attributeNamePrefix = "<";
        String attributeNamePostfix = ">";

        SqlRunner result = instance.setAttributePrefixAndPostfix(
                attributeNamePrefix, attributeNamePostfix);
        assertSame(result, instance);
        assertEquals(attributeNamePrefix, result.getAttributePrefix());
        assertEquals(attributeNamePostfix, result.getAttributePostfix());

        result = instance.setAttributePrefixAndPostfix(null, null);
        assertSame(result, instance);
        assertEquals("", result.getAttributePrefix());
        assertEquals("", result.getAttributePostfix());
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

    @Test(expected = NullPointerException.class)
    public void testSetSingleLineCommentPrefix_passNull() {
        instance.setSingleLineCommentPrefix(null);
    }

    @Test
    public void testSetSingleLineCommentPrefix() {
        assertEquals("--", instance.getSingleLineCommentPrefix());
        instance.setSingleLineCommentPrefix("#");
        assertEquals("#", instance.getSingleLineCommentPrefix());
        assertEquals("#sqlrunner.name:",
                TestHelper.getFieldValue(SqlRunner.class, "nameCommentPrefix", instance));
    }

    @Test
    public void testReadFile() throws Exception {
        System.out.println("readFile");

        Method readFileMethod = TestHelper.getMethod(SqlRunner.class, "readFile", String.class);

        instance.setFilePathPrefix("/");
        List<SqlRunnerStatement> result = (List<SqlRunnerStatement>)
                readFileMethod.invoke(instance, "test.sql");

        SqlRunnerStatement statement = result.get(0);
        assertEquals(null, statement.getUpdateCount());
        assertEquals(null, statement.getName());
        String expectedSql = "update a" + LINE_SEPARATOR
                + "   set b = 2" + LINE_SEPARATOR
                + " where 1 = 2";
        assertEquals(expectedSql, statement.getSql());

        statement = result.get(1);
        assertEquals(null, statement.getUpdateCount());
        assertEquals("query1", statement.getName());
        expectedSql = "SELECT *" + LINE_SEPARATOR + "  FROM dual";
        assertEquals(expectedSql, statement.getSql());

        statement = result.get(2);
        assertEquals("query2", statement.getName());

        assertEquals(3, result.size());

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
    }

    @Test(expected = NullPointerException.class)
    public void testRunNullSql() throws Exception {
        System.out.println("run");

        instance.run(null);

    }

    @Test(expected = NullPointerException.class)
    public void testRunNullFileName() throws Exception {
        System.out.println("run");

        instance.runFile(null);

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
