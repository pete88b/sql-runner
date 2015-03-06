package com.butterfill.sqlrunner;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static com.butterfill.sqlrunner.TestHelper.*;
import com.butterfill.sqlrunner.util.DefaultFileReader;
import java.util.List;

/**
 *
 * @author Peter Butterfill
 */
public class SqlRunnerFactoryTest {

    public SqlRunnerFactoryTest() {
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
    
    @Test
    public void testGetAndSetFileReader() {
        System.out.println("GetAndSetFileReader");
        SqlRunnerFactory instance = new SqlRunnerFactory();
        assertTrue(instance.getFileReader() instanceof DefaultFileReader);
        SqlRunnerFileReader fileReader = new SqlRunnerFileReader() {
            public List<SqlRunnerStatement> readFile(String fileName) {
                return null;
            }
        };
        instance.setFileReader(fileReader);
        assertSame(fileReader, instance.getFileReader());
    }

    /**
     * Test of getDataSource method, of class SqlRunnerFactory.
     */
    @Test
    public void testGetDataSource() {
        System.out.println("getDataSource");
        SqlRunnerFactory instance = new SqlRunnerFactory();
        DataSource expResult = null;
        DataSource result = instance.getDataSource();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDataSource method, of class SqlRunnerFactory.
     */
    @Test
    public void testSetDataSource() {
        System.out.println("setDataSource");
        DataSource dataSource = mock(DataSource.class);
        SqlRunnerFactory instance = new SqlRunnerFactory();
        instance.setDataSource(dataSource);
        assertSame(dataSource, instance.getDataSource());
    }

    /**
     * Test of getDefaultCallbackHandler method, of class SqlRunnerFactory.
     */
    @Test
    public void testGetDefaultCallbackHandler() {
        System.out.println("getDefaultCallbackHandler");
        SqlRunnerFactory instance = new SqlRunnerFactory();
        SqlRunnerCallbackHandler result = instance.getDefaultCallbackHandler();
        assertNotNull(result);
    }

    /**
     * Test of setDefaultCallbackHandler method, of class SqlRunnerFactory.
     */
    @Test
    public void testSetDefaultCallbackHandler() {
        System.out.println("setDefaultCallbackHandler");
        SqlRunnerCallbackHandler defaultCallbackHandler = mock(SqlRunnerCallbackHandler.class);
        SqlRunnerFactory instance = new SqlRunnerFactory();
        instance.setDefaultCallbackHandler(defaultCallbackHandler);
        assertSame(defaultCallbackHandler, instance.getDefaultCallbackHandler());
    }

    /**
     * Test of getDefaultResultSetNextRowCallbackHandler method, of class SqlRunnerFactory.
     */
    @Test
    public void testGetDefaultResultSetNextRowCallbackHandler() {
        System.out.println("getDefaultResultSetNextRowCallbackHandler");
        SqlRunnerFactory instance = new SqlRunnerFactory();
        SqlRunnerResultSetNextRowCallbackHandler result
                = instance.getDefaultResultSetNextRowCallbackHandler();
        assertNotNull(result);

    }

    /**
     * Test of setDefaultResultSetNextRowCallbackHandler method, of class SqlRunnerFactory.
     */
    @Test
    public void testSetDefaultResultSetNextRowCallbackHandler() {
        System.out.println("setDefaultResultSetNextRowCallbackHandler");
        SqlRunnerResultSetNextRowCallbackHandler defaultResultSetNextRowCallbackHandler
                = mock(SqlRunnerResultSetNextRowCallbackHandler.class);
        SqlRunnerFactory instance = new SqlRunnerFactory();
        instance.setDefaultResultSetNextRowCallbackHandler(defaultResultSetNextRowCallbackHandler);
        assertSame(defaultResultSetNextRowCallbackHandler,
                instance.getDefaultResultSetNextRowCallbackHandler());
    }

    /**
     * Test of getAttributeNamePrefix method, of class SqlRunnerFactory.
     */
    @Test
    public void testGetAttributeNamePrefix() {
        System.out.println("getAttributeNamePrefix");
        SqlRunnerFactory instance = new SqlRunnerFactory();
        String expResult = "#{";
        String result = instance.getAttributeNamePrefix();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAttributeNamePrefix method, of class SqlRunnerFactory.
     */
    @Test
    public void testSetAttributeNamePrefix() {
        System.out.println("setAttributeNamePrefix");
        String attributeNamePrefix = "eggs";
        SqlRunnerFactory instance = new SqlRunnerFactory();
        instance.setAttributeNamePrefix(attributeNamePrefix);
        assertEquals(attributeNamePrefix, instance.getAttributeNamePrefix());
    }

    /**
     * Test of getAttributeNamePostfix method, of class SqlRunnerFactory.
     */
    @Test
    public void testGetAttributeNamePostfix() {
        System.out.println("getAttributeNamePostfix");
        SqlRunnerFactory instance = new SqlRunnerFactory();
        String expResult = "}";
        String result = instance.getAttributeNamePostfix();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAttributeNamePostfix method, of class SqlRunnerFactory.
     */
    @Test
    public void testSetAttributeNamePostfix() {
        System.out.println("setAttributeNamePostfix");
        String attributeNamePostfix = "eggs";
        SqlRunnerFactory instance = new SqlRunnerFactory();
        instance.setAttributeNamePostfix(attributeNamePostfix);
        assertEquals(attributeNamePostfix, instance.getAttributeNamePostfix());
    }

    /**
     * Test of getAttributeMap method, of class SqlRunnerFactory.
     */
    @Test
    public void testGetAttributeMap() {
        System.out.println("getAttributeMap");
        SqlRunnerFactory instance = new SqlRunnerFactory();
        Map<String, String> expResult = null;
        Map<String, String> result = instance.getAttributeMap();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAttributeMap method, of class SqlRunnerFactory.
     */
    @Test
    public void testSetAttributeMap() {
        System.out.println("setAttributeMap");
        Map<String, String> attributeMap = new HashMap<String, String>();
        SqlRunnerFactory instance = new SqlRunnerFactory();
        instance.setAttributeMap(attributeMap);
        assertSame(attributeMap, instance.getAttributeMap());
    }

    /**
     * Test of getCallbackHandlerMap method, of class SqlRunnerFactory.
     */
    @Test
    public void testGetCallbackHandlerMap() {
        System.out.println("getCallbackHandlerMap");
        SqlRunnerFactory instance = new SqlRunnerFactory();
        Map<String, SqlRunnerCallbackHandler> expResult = null;
        Map<String, SqlRunnerCallbackHandler> result = instance.getCallbackHandlerMap();
        assertEquals(expResult, result);
    }

    /**
     * Test of setCallbackHandlerMap method, of class SqlRunnerFactory.
     */
    @Test
    public void testSetCallbackHandlerMap() {
        System.out.println("setCallbackHandlerMap");
        Map<String, SqlRunnerCallbackHandler> callbackHandlerMap
                = new HashMap<String, SqlRunnerCallbackHandler>();
        SqlRunnerFactory instance = new SqlRunnerFactory();
        instance.setCallbackHandlerMap(callbackHandlerMap);
        assertSame(callbackHandlerMap, instance.getCallbackHandlerMap());
    }

    /**
     * Test of getRsnrCallbackHandlerMap method, of class SqlRunnerFactory.
     */
    @Test
    public void testGetRsnrCallbackHandlerMap() {
        System.out.println("getRsnrCallbackHandlerMap");
        SqlRunnerFactory instance = new SqlRunnerFactory();
        Map<String, SqlRunnerResultSetNextRowCallbackHandler> expResult = null;
        Map<String, SqlRunnerResultSetNextRowCallbackHandler> result
                = instance.getRunnerResultSetNextRowCallbackHandlerMap();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRsnrCallbackHandlerMap method, of class SqlRunnerFactory.
     */
    @Test
    public void testSetRsnrCallbackHandlerMap() {
        System.out.println("setRsnrCallbackHandlerMap");
        Map<String, SqlRunnerResultSetNextRowCallbackHandler> rsnrCallbackHandlerMap
                = new HashMap<String, SqlRunnerResultSetNextRowCallbackHandler>();
        SqlRunnerFactory instance = new SqlRunnerFactory();
        instance.setRunnerResultSetNextRowCallbackHandlerMap(rsnrCallbackHandlerMap);
        assertSame(rsnrCallbackHandlerMap, instance.getRunnerResultSetNextRowCallbackHandlerMap());
    }

    /**
     * Test of newSqlRunner method, of class SqlRunnerFactory.
     */
    @Test
    public void testNewSqlRunner() throws Exception {
        System.out.println("newSqlRunner");

        SqlRunnerFactory instance = new SqlRunnerFactory();

        DataSource dataSource = mock(DataSource.class);
        instance.setDataSource(dataSource);

        SqlRunner result = instance.newSqlRunner();

        assertEquals(dataSource,
                getFieldValue(SqlRunner.class, "dataSource", result));
        assertEquals(instance.getDefaultCallbackHandler(),
                getFieldValue(SqlRunner.class, "defaultCallbackHandler", result));
        assertEquals(instance.getDefaultResultSetNextRowCallbackHandler(),
                getFieldValue(SqlRunner.class, "defaultResultSetNextRowCallbackHandler", result));
        assertNotNull(
                getFieldValue(SqlRunner.class, "fileReader", result));
        assertEquals("#{",
                getFieldValue(SqlRunner.class, "attributePrefix", result));
        assertEquals("}",
                getFieldValue(SqlRunner.class, "attributePostfix", result));

        Map<String, String> resultAttributeMap =
                (Map<String, String>) getFieldValue(SqlRunner.class, "attributeMap", result);
        assertTrue(resultAttributeMap.isEmpty());

        Map<String, SqlRunnerCallbackHandler> resultCallbackHandlerMap
                = (Map<String, SqlRunnerCallbackHandler>)
                getFieldValue(SqlRunner.class, "callbackHandlerMap", result);
        assertTrue(resultCallbackHandlerMap.isEmpty());

        Map<String, SqlRunnerResultSetNextRowCallbackHandler> resultRsnrCallbackHandlerMap
                = (Map<String, SqlRunnerResultSetNextRowCallbackHandler>)
                getFieldValue(SqlRunner.class, "rsnrCallbackHandlerMap", result);
        assertTrue(resultRsnrCallbackHandlerMap.isEmpty());

    }

    /**
     * Test of newSqlRunner method, of class SqlRunnerFactory.
     */
    @Test
    public void testNewSqlRunner2() throws Exception {
        System.out.println("newSqlRunner2");

        SqlRunnerFactory instance = new SqlRunnerFactory();

        DataSource dataSource = mock(DataSource.class);
        instance.setDataSource(dataSource);

        Map<String, String> attributeMap = new HashMap<String, String>();
        attributeMap.put("a", "A");
        attributeMap.put("b", "B");
        instance.setAttributeMap(attributeMap);

        Map<String, SqlRunnerCallbackHandler> callbackHandlerMap
                = new HashMap<String, SqlRunnerCallbackHandler>();
        callbackHandlerMap.put("statement-a", mock(SqlRunnerCallbackHandler.class));
        instance.setCallbackHandlerMap(callbackHandlerMap);

        Map<String, SqlRunnerResultSetNextRowCallbackHandler> rsnrCallbackHandlerMap
                = new HashMap<String, SqlRunnerResultSetNextRowCallbackHandler>();
        rsnrCallbackHandlerMap.put("statement-b", mock(SqlRunnerResultSetNextRowCallbackHandler.class));
        instance.setRunnerResultSetNextRowCallbackHandlerMap(rsnrCallbackHandlerMap);

        // change the factory config
        DefaultFileReader fileReader = new DefaultFileReader();
        fileReader.setCharsetName("CharsetName");
        fileReader.setFilePathPrefix("FilePathPrefix");
        fileReader.setSingleLineCommentPrefix("SingleLineCommentPrefix");
        instance.setFileReader(fileReader);

        instance.setAttributeNamePrefix("AttributeNamePrefix");
        instance.setAttributeNamePostfix("AttributeNamePostfix");
        fileReader.setSingleLineCommentPrefix("SingleLineCommentPrefix");

        assertEquals("AttributeNamePrefix", instance.getAttributeNamePrefix());
        assertEquals("AttributeNamePostfix", instance.getAttributeNamePostfix());

        SqlRunner result = instance.newSqlRunner();

        assertEquals(dataSource,
                getFieldValue(SqlRunner.class, "dataSource", result));
        assertEquals(instance.getDefaultCallbackHandler(),
                getFieldValue(SqlRunner.class, "defaultCallbackHandler", result));
        assertEquals(instance.getDefaultResultSetNextRowCallbackHandler(),
                getFieldValue(SqlRunner.class, "defaultResultSetNextRowCallbackHandler", result));
        assertSame(fileReader,
                getFieldValue(SqlRunner.class, "fileReader", result));
        assertEquals("AttributeNamePrefix",
                getFieldValue(SqlRunner.class, "attributePrefix", result));
        assertEquals("AttributeNamePostfix",
                getFieldValue(SqlRunner.class, "attributePostfix", result));

        Map<String, String> resultAttributeMap =
                (Map<String, String>) getFieldValue(SqlRunner.class, "attributeMap", result);
        assertEquals("A", resultAttributeMap.get("a"));
        assertEquals("B", resultAttributeMap.get("b"));

        Map<String, SqlRunnerCallbackHandler> resultCallbackHandlerMap
                = (Map<String, SqlRunnerCallbackHandler>)
                getFieldValue(SqlRunner.class, "callbackHandlerMap", result);
        assertNotNull(resultCallbackHandlerMap.get("statement-a"));
        assertEquals(callbackHandlerMap.get("statement-a"), resultCallbackHandlerMap.get("statement-a"));

        Map<String, SqlRunnerResultSetNextRowCallbackHandler> resultRsnrCallbackHandlerMap
                = (Map<String, SqlRunnerResultSetNextRowCallbackHandler>)
                getFieldValue(SqlRunner.class, "rsnrCallbackHandlerMap", result);
        assertNotNull(resultRsnrCallbackHandlerMap.get("statement-b"));
        assertEquals(resultRsnrCallbackHandlerMap.get("statement-b"), resultRsnrCallbackHandlerMap.get("statement-b"));

    }

}
