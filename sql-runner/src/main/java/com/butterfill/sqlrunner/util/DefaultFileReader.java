
package com.butterfill.sqlrunner.util;

import com.butterfill.sqlrunner.SqlRunnerException;
import com.butterfill.sqlrunner.SqlRunnerFileReader;
import com.butterfill.sqlrunner.SqlRunnerStatement;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class to read SQL files.
 *
 * <h3>File path prefix</h3>
 * The intended use of the file path prefix is to allow developers to work with file names
 * without having to know where the files are located.
 * e.g. To support different DBs we could have the following files
 * <ul>
 *   <li>/oracle/do-some-work.sql</li>
 *   <li>/mysql/do-some-work.sql</li>
 * </ul>
 * Developers could write
 * <p><code>sqlRunnerFactory.newSqlRunner().runFile("do-some-work.sql")</code></p>
 * and filePathPrefix ("/oracle/" or "/mysql/") could be set on the {@link SqlRunnerFileReader}
 * used by {@link com.butterfill.sqlrunner.SqlRunner} via dependency injection.
 *
 * @author Peter Butterfill
 */
public class DefaultFileReader implements SqlRunnerFileReader {

    /**
     * The name of this class.
     */
    public static final String CLASS_NAME = DefaultFileReader.class.getName();

    /**
     * The logger for this class.
     */
    private static final Logger logger = Logger.getLogger(CLASS_NAME);

    /**
     * Line separator on this platform.
     */
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    /**
     * The name of the character set of the SQL script file.
     */
    private final String charsetName;

    /**
     * The file path prefix.
     */
    private final String filePathPrefix;

    /**
     * Lines with this prefix will be treated as single line comments.
     */
    private final String singleLineCommentPrefix;

    /**
     * The sql-runner name comment prefix -
     * changing singleLineCommentPrefix will also change nameCommentPrefix.
     */
    private final String nameCommentPrefix;

    /**
     * The sql-runner fail fast comment prefix -
     * changing singleLineCommentPrefix will also change nameCommentPrefix.
     */
    private final String failFastCommentPrefix;

    /**
     * Creates a file reader that will use;
     * <ul>
     * <li>UTF-8 character set name,</li>
     * <li>"" as the file path prefix and </li>
     * <li>-- as the single line comment prefix.</li>
     * </ul>
     */
    public DefaultFileReader() {
        this.charsetName = "UTF-8";
        this.filePathPrefix = "";
        this.singleLineCommentPrefix = "--";
        this.nameCommentPrefix = "--sqlrunner.name:";
        this.failFastCommentPrefix = "--sqlrunner.failfast:";
    }

    /**
     * Creates a file reader that will use;
     * <ul>
     * <li>UTF-8 character set name,</li>
     * <li>the specified file path prefix and</li>
     * <li>-- as the single line comment prefix.</li>
     * </ul>
     * @param filePathPrefix
     *   The file path prefix.
     *   An empty string is the default.
     */
    public DefaultFileReader(final String filePathPrefix) {
        this.charsetName = "UTF-8";
        this.filePathPrefix = filePathPrefix;
        this.singleLineCommentPrefix = "--";
        this.nameCommentPrefix = "--sqlrunner.name:";
        this.failFastCommentPrefix = "--sqlrunner.failfast:";
    }

    /**
     * Creates a new file reader.
     * @param filePathPrefix
     *   The file path prefix.
     *   An empty string is the default.
     * @param charsetName
     *   The name of the character set of the SQL script file.
     *   UTF-8 is the default.
     * @param singleLineCommentPrefix
     *   The single line comment prefix, which must not be null.
     *   Double hyphen is the default. Note: MySQL allows # as well as --.
     */
    public DefaultFileReader(final String filePathPrefix, final String charsetName,
            final String singleLineCommentPrefix) {
        if (singleLineCommentPrefix == null) {
            throw new NullPointerException("singleLineCommentPrefix must not be null");
        }
        this.charsetName = charsetName;
        this.filePathPrefix = filePathPrefix;
        this.singleLineCommentPrefix = singleLineCommentPrefix;
        this.nameCommentPrefix = singleLineCommentPrefix + "sqlrunner.name:";
        this.failFastCommentPrefix = singleLineCommentPrefix + "sqlrunner.failfast:";
    }

    /**
     * Reads a file.
     * @param fileName
     *   Name of the file to read.
     * @return
     *   The statements from the file.
     */
    public List<SqlRunnerStatement> readFile(final String fileName) {
        if (fileName == null) {
            throw new NullPointerException("fileName must not be null");
        }

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                    this.getClass().getResourceAsStream(filePathPrefix + fileName), charsetName));

            final List<SqlRunnerStatement> sqlRunnerStatements =
                    new ArrayList<SqlRunnerStatement>();

            final StringBuilder sqlBuilder = new StringBuilder();
            String statementName = null;
            boolean failFast = true;
            boolean inMultiLineComment = false;

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                final String trimmedLine = line.trim();

                if (trimmedLine.startsWith(nameCommentPrefix)) {
                    // we've found a sql runner name comment - this gives us the statement name
                    statementName = trimmedLine.substring(nameCommentPrefix.length()).trim();
                    continue;
                }

                if (trimmedLine.startsWith(failFastCommentPrefix)) {
                    // we've found the fail fast comment
                    failFast = !"false"
                            .equals(trimmedLine.substring(failFastCommentPrefix.length()).trim());
                    continue;
                }

                if ("".equals(trimmedLine)
                        || trimmedLine.startsWith(singleLineCommentPrefix)) {
                    // skip single line comments and empty lines
                    continue;
                }

                // skip multi-line comments
                if (trimmedLine.startsWith("/*")) {
                    // we're only in a multi-line comment if it is not ended on this line
                    inMultiLineComment = !trimmedLine.endsWith("*/");
                    continue;
                } else if (trimmedLine.endsWith("*/")) {
                    inMultiLineComment = false;
                    continue;
                } else if (inMultiLineComment) {
                    continue;
                }

                // add the line to the statement builder
                sqlBuilder.append(line);

                if (trimmedLine.endsWith(";")) {
                    // statements are terminated with a semi-colon
                    final String sql = sqlBuilder.substring(0, sqlBuilder.length() - 1);
                    sqlRunnerStatements.add(new SqlRunnerStatement(statementName, sql, failFast));
                    statementName = null;
                    failFast = true;
                    sqlBuilder.setLength(0);

                } else {
                    sqlBuilder.append(LINE_SEPARATOR);

                }

            }

            return sqlRunnerStatements;

        } catch (Exception ex) {
            // we want to catch IOException and runtime exceptions such as NullPointerException
            // thrown by InputStreamReader when the resource is not found
            throw new SqlRunnerException(
                    "failed to read file [" + fileName
                    + "]. using filePathPrefix [" + filePathPrefix + "]",
                    ex);

        } finally {
            close(reader);

        }

    }

    /**
     * Closes a closeable without letting exceptions propagate.
     * @param closeable
     *   The closeable to close.
     */
    private void close(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ex) {
            logger.logp(Level.WARNING, CLASS_NAME, "close(java.io.Closeable)",
                    "failed to close closeable", ex);
        }
    }

}
