
package com.butterfill.sqlrunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleTypes;
import oracle.jdbc.pool.OracleDataSource;
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
@ContextConfiguration(locations = "classpath:test-context.xml")
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
//        try {
//            sqlRunnerFactory.newSqlRunner().run("drop table a");
//        } catch (RuntimeException ex) {
//            // ignore it
//        }
    }

    @After
    public void tearDown() {
    }

    private boolean isOracleDb() {
        return dataSource instanceof OracleDataSource;
    }

    /**
     * Test of run method, of class SqlRunner.
     */
    @Test
    public void testRun_withCallbackHandler() throws Exception {
        if (!isOracleDb()) {
            return;
        }

        final List<Object> results = new ArrayList<Object>();

        SqlRunner instance = sqlRunnerFactory.newSqlRunner();

        SqlRunnerCallbackHandler callbackHandler = new SqlRunnerCallbackHandler() {

            @Override
            public PreparedStatement prepareStatement(
                    Connection connection, SqlRunnerStatement sqlRunnerStatement)
                    throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        sqlRunnerStatement.getSql(), PreparedStatement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, 9);
                return preparedStatement;
            }

            @Override
            public void executeComplete(PreparedStatement preparedStatement, SqlRunnerStatement sqlRunnerStatement) throws SQLException {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
                // resultSet.getRowId would be good but Oracle does not support this method
                // neither do MySQL or Java DB
                // calling getObject will give is a ROWID
                results.add(resultSet.getObject(1));
                assertFalse("expect just one row in the result set", resultSet.next());
                resultSet.close();
            }
        };

        SqlRunnerCallbackHandler callbackHandler2 = new SqlRunnerCallbackHandler() {

            @Override
            public PreparedStatement prepareStatement(
                    Connection connection, SqlRunnerStatement sqlRunnerStatement)
                    throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        sqlRunnerStatement.getSql());
                preparedStatement.setObject(1, results.get(0));
                return preparedStatement;
            }

            @Override
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

            sql = "select * from a where rowid = ?";
            instance.setCallbackHandler(null, callbackHandler2);
            result = instance.run(sql);
            instance.setCallbackHandler(null, null);
            assertEquals(null, result.getUpdateCount());
            assertTrue(result.getResultOfExecutionWasResultSet());

            Map<String, String> attributeMap = (Map<String, String>)
                    TestHelper.getFieldValue(SqlRunner.class, "attributeMap", instance);
            assertEquals("9", attributeMap.get("NUMBER_COL.1"));
            assertEquals("test text", attributeMap.get("TEXT_COL.1"));

        } finally {
            instance.run("drop table a");

        }

    }

    @Test
    public void testRun_withCallbackHandler_returning() throws Exception {
        if (!isOracleDb()) {
            return;
        }

        final List<Object> results = new ArrayList<Object>();

        SqlRunner instance = sqlRunnerFactory.newSqlRunner();

        SqlRunnerCallbackHandler callbackHandler = new SqlRunnerCallbackHandler() {

            @Override
            public PreparedStatement prepareStatement(
                    Connection connection, SqlRunnerStatement sqlRunnerStatement)
                    throws SQLException {
                OraclePreparedStatement oraclePreparedStatement = (OraclePreparedStatement)
                        connection.prepareStatement(sqlRunnerStatement.getSql());
                oraclePreparedStatement.setInt(1, 9);
                oraclePreparedStatement.registerReturnParameter(2, OracleTypes.VARCHAR);
                return oraclePreparedStatement;
            }

            @Override
            public void executeComplete(PreparedStatement preparedStatement, SqlRunnerStatement sqlRunnerStatement) throws SQLException {
                OraclePreparedStatement oraclePreparedStatement =
                        (OraclePreparedStatement) preparedStatement;
                ResultSet resultSet = oraclePreparedStatement.getReturnResultSet();
                resultSet.next();
                results.add(resultSet.getObject(1));
                assertFalse("expect just one row in the result set", resultSet.next());
                resultSet.close();
            }
        };

        try {
            String sql =
                    "create table a(number_col number, text_col varchar2(20) default 'test text')";
            SqlRunnerStatement result = instance.run(sql);
            assertEquals(Integer.valueOf("0"), result.getUpdateCount());
            assertFalse(result.getResultOfExecutionWasResultSet());

            sql = "insert into a(number_col) values(?) returning text_col into ?";
            instance.setCallbackHandler(null, callbackHandler);
            result = instance.run(sql);
            instance.setCallbackHandler(null, null);
            assertEquals((Object) 1, result.getUpdateCount());
            assertFalse(result.getResultOfExecutionWasResultSet());

            assertEquals("test text", results.get(0));

        } finally {
            instance.run("drop table a");

        }

    }

}
