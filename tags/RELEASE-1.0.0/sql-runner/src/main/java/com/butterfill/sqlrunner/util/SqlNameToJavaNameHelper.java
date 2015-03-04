
package com.butterfill.sqlrunner.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Helper class for converting SQL names to Java names.
 *
 * @author Peter Butterfill
 */
public class SqlNameToJavaNameHelper {

    /**
     * The name of this class.
     */
    public static final String CLASS_NAME =
            SqlNameToJavaNameHelper.class.getName();

    /**
     * The logger for this class.
     */
    private static final Logger logger = Logger.getLogger(CLASS_NAME);

    /**
     * Creates a new instance of OpbSqlNameToJavaNameHelper.
     */
    public SqlNameToJavaNameHelper() {
        logger.entering(CLASS_NAME, "OpbSqlNameToJavaNameHelper()");

    }

    /**
     * Contains Java names for SQL names already converted by sqlNameToJavaName(String).
     */
    private static final Map<String, String> NAME_CACHE = new HashMap<String, String>();

    /**
     * Converts a SQL name to a Java name.
     *
     * @param sqlName
     *   The SQL name to convert.
     * @return
     *   The Java name for the specified SQL name.
     */
    public String sqlNameToJavaName(final String sqlName) {
        // if the name is already cached, we don't need to convert
        if (!NAME_CACHE.containsKey(sqlName)) {
            final String[] nameBits = sqlName.toLowerCase().split("_");

            final StringBuilder sb = new StringBuilder();

            boolean firstBit = true;

            for (String bit : nameBits) {
                if (firstBit) {
                    firstBit = false;
                    sb.append(bit);

                } else if (bit.length() > 0) {
                    // bit.length() will be 0 if the sql name contained
                    // consecutive underscores
                    sb.append(bit.substring(0, 1).toUpperCase());
                    if (bit.length() > 1) {
                        sb.append(bit.substring(1));

                    }

                }

            } // End of for (String bit : nameBits)

            NAME_CACHE.put(sqlName, sb.toString());

        } // End of if (!NAME_CACHE.containsKey(sqlName))

        return NAME_CACHE.get(sqlName);

    }

}
