
package com.butterfill.sqlrunner;

import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Butterfill
 */
public class SqlRunnerExceptionTest {

    public SqlRunnerExceptionTest() {
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
     * Test of getRollbackFailedException method, of class SqlRunnerException.
     */
    @Test
    public void testGetRollbackFailedException() {
        System.out.println("getRollbackFailedException");
        SqlRunnerException instance = new SqlRunnerException("t", new Exception());
        SQLException expResult = null;
        SQLException result = instance.getRollbackFailedException();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRollbackFailedException method, of class SqlRunnerException.
     */
    @Test
    public void testSetRollbackFailedException() {
        System.out.println("setRollbackFailedException");
        SQLException rollbackFailedException = new SQLException("test SQL ex");
        SqlRunnerException instance = new SqlRunnerException("t", new Exception());
        instance.setRollbackFailedException(rollbackFailedException);
        assertSame(rollbackFailedException, instance.getRollbackFailedException());
    }

}
