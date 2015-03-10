
package com.butterfill.sqlrunner.util;

import com.butterfill.sqlrunner.SqlRunnerStatement;
import java.util.List;
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
public class CachingFileReaderTest {

    public CachingFileReaderTest() {
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

    @Test(expected = NullPointerException.class)
    public void testConstructorNullArg() {
        new CachingFileReader(null);
    }

    /**
     * Test of readFile method, of class CachingFileReader.
     */
    @Test
    public void testReadFile() {
        System.out.println("readFile");

        CachingFileReader cachingFileReader = new CachingFileReader(new DefaultFileReader("/"));
        DefaultFileReader defaultFileReader = new DefaultFileReader("/");

        List<SqlRunnerStatement> result = cachingFileReader.readFile("test.sql");
        assertSame(result, cachingFileReader.readFile("test.sql"));

        List<SqlRunnerStatement> defaultResult = defaultFileReader.readFile("test.sql");

        for (int i = 0; i < result.size(); i++) {
            SqlRunnerStatement statement = result.get(i);
            SqlRunnerStatement defaultStatement = defaultResult.get(i);

            assertEquals(statement.getException(), defaultStatement.getException());
            assertEquals(statement.getFailFast(), defaultStatement.getFailFast());
            assertEquals(statement.getName(), defaultStatement.getName());
            assertEquals(statement.getResult(), defaultStatement.getResult());
            assertEquals(statement.getResultOfExecutionWasResultSet(), defaultStatement.getResultOfExecutionWasResultSet());
            assertEquals(statement.getSql(), defaultStatement.getSql());
            assertEquals(statement.getUpdateCount(), defaultStatement.getUpdateCount());

        }

    }

}
