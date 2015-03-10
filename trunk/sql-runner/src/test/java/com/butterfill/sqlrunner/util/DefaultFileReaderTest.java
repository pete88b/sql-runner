
package com.butterfill.sqlrunner.util;

import com.butterfill.sqlrunner.SqlRunnerStatement;
import static com.butterfill.sqlrunner.TestHelper.getFieldValue;
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
public class DefaultFileReaderTest {

    /**
     * Line separator on this platform.
     */
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    public DefaultFileReaderTest() {
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
     * Test of setCharsetName method, of class DefaultFileReader.
     */
    @Test
    public void testSetCharsetName() {
        DefaultFileReader instance = new DefaultFileReader();
        assertEquals("UTF-8", getFieldValue(DefaultFileReader.class, "charsetName", instance));

        String charsetName = "couldBeAnything";
        DefaultFileReader result = new DefaultFileReader("", charsetName, "#");
        assertEquals(charsetName, getFieldValue(DefaultFileReader.class, "charsetName", result));
    }

    /**
     * Test of setFilePathPrefix method, of class DefaultFileReader.
     */
    @Test
    public void testSetFilePathPrefix() {
        System.out.println("setFilePathPrefix");

        DefaultFileReader instance = new DefaultFileReader();
        assertEquals("", getFieldValue(DefaultFileReader.class, "filePathPrefix", instance));

        String filePathPrefix = "/com/test/scripts/mysql/";
        DefaultFileReader result = new DefaultFileReader(filePathPrefix);
        assertEquals(filePathPrefix, getFieldValue(DefaultFileReader.class, "filePathPrefix", result));
    }


    @Test(expected = NullPointerException.class)
    public void testSetSingleLineCommentPrefix_passNull() {
        DefaultFileReader instance = new DefaultFileReader("", "UFT-16", null);
    }

    /**
     * Test of setSingleLineCommentPrefix method, of class DefaultFileReader.
     */
    @Test
    public void testSetSingleLineCommentPrefix() {
        System.out.println("setSingleLineCommentPrefix");
        DefaultFileReader instance = new DefaultFileReader();
        assertEquals("--", getFieldValue(DefaultFileReader.class, "singleLineCommentPrefix", instance));
        instance = new DefaultFileReader("", "", "#");
        assertEquals("#", getFieldValue(DefaultFileReader.class, "singleLineCommentPrefix", instance));
        assertEquals("#sqlrunner.name:",
                getFieldValue(DefaultFileReader.class, "nameCommentPrefix", instance));
    }

    /**
     * Test of readFile method, of class DefaultFileReader.
     */
    @Test
    public void testReadFile() {
        System.out.println("readFile");
        DefaultFileReader instance = new DefaultFileReader("/");

        List<SqlRunnerStatement> result = instance.readFile("test.sql");

        SqlRunnerStatement statement = result.get(0);
        assertEquals(null, statement.getUpdateCount());
        assertEquals(null, statement.getName());
        assertEquals(null, statement.getException());
        String expectedSql = "update a" + LINE_SEPARATOR
                + "   set b = 2" + LINE_SEPARATOR
                + " where 1 = 2";
        assertEquals(expectedSql, statement.getSql());

        statement = result.get(1);
        assertEquals(null, statement.getUpdateCount());
        assertEquals("query1", statement.getName());
        assertEquals(null, statement.getException());
        expectedSql = "SELECT *" + LINE_SEPARATOR + "  FROM dual";
        assertEquals(expectedSql, statement.getSql());

        statement = result.get(2);
        assertEquals("query2", statement.getName());

        assertEquals(3, result.size());
    }

}
