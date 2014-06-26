
package com.butterfill.sqlrunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import oracle.jdbc.pool.OracleDataSource;

/**
 *
 * @author Butterp
 */
public class TestHelper {

    /**
     * The name of this class.
     */
    public static final String CLASS_NAME = TestHelper.class.getName();

    /**
     * The logger of this class.
     */
    private static final Logger logger = Logger.getLogger(CLASS_NAME);

    static {
        Logger.getLogger("").setLevel(Level.ALL);
        Logger.getLogger("").getHandlers()[0].setLevel(Level.ALL);
    }

    /**
     * Creates a new instance of TestHelper.
     */
    public TestHelper() {
        logger.entering(CLASS_NAME, "TestHelper()");

    }

    /**
     * Reads a file from the class path.
     * @param fileName
     *   Name of the file to read.
     * @return
     *   File contents as a string.
     */
    public static String readFile(String fileName) {
        try {
            final StringBuilder sb = new StringBuilder();
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                    TestHelper.class.getResourceAsStream(fileName)));

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                sb.append(line);
                sb.append("\n");

            }

            return sb.toString();

        } catch (Exception ex) {
            throw new RuntimeException("failed to read file " + fileName, ex);

        }

    }

    public static Field getField(Class clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;

        } catch (Exception ex) {
            throw new RuntimeException(
                    "failed to get field " + clazz.getName() + " '" + fieldName + "'",
                    ex);
        }

    }

    public static Object getFieldValue(Class clazz, String fieldName, Object instance) {
        try {
            return getField(clazz, fieldName).get(instance);
        } catch (Exception ex) {
            throw new RuntimeException("failed to get field value", ex);
        }
    }

    public static void setField(Class clazz, String fieldName, Object instance, Object value) {
        try {
            getField(clazz, fieldName).set(instance, value);
        } catch (Exception ex) {
            throw new RuntimeException("failed to set field", ex);
        }
    }

    public static Method getMethod(Class clazz, String methodName, Class... parameterTypes) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;

        } catch (Exception ex) {
            throw new RuntimeException(
                    "failed to get method " + clazz.getName() + " '" + methodName + "'",
                    ex);
        }

    }

    /**
     * Converts a string to a date.
     * @param date
     *   A string representation of a date in ddMMMyyyy format.
     * @return
     *   The date created from the specified string.
     */
    public static Date toDate(final String date) {
        try {
            return new SimpleDateFormat("ddMMMyyyy").parse(date);
        } catch (Exception ex) {
            throw new RuntimeException("failed to create date for " + date, ex);
        }
    }

    public static void integrationTestSetupOracle(DataSource dataSource) {
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            try {
                statement.execute("drop table study");
            } catch (SQLException ex) {
                // ignore the error - table probably didn't exist
            }
            try {
                statement.execute("drop sequence study_id");
            } catch (SQLException ex) {
                // ignore the error - seq probably didn't exist
            }
            statement.execute(
                    "create table study(" +
                    "  study_id integer constraint study_pk primary key," +
                    "  study_name varchar2(200))");
            statement.execute("create sequence study_id");
            statement.execute(
                    "create or replace trigger bi_fer_study before insert on study\n" +
                    "for each row\n" +
                    "begin\n" +
                    "  if (:new.study_id is null)\n" +
                    "  then\n" +
                    "    select study_id.nextval\n" +
                    "      into :new.study_id\n" +
                    "      from dual;\n" +
                    "  end if;\n" +
                    "end;");

        } catch (SQLException ex) {
            throw new RuntimeException("integrationTestSetup FAILED", ex);

        }

    }

    public static boolean isOracleDb(final DataSource dataSource) {
        return dataSource instanceof OracleDataSource;
    }

    public static boolean isDerbyDb(final DataSource dataSource) {
        return !(dataSource instanceof OracleDataSource);
    }

} // End of class TestHelper
