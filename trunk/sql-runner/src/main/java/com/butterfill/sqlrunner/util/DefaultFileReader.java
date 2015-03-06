
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
    private String charsetName = "UTF-8";

    /**
     * The file path prefix.
     */
    private String filePathPrefix = "";

    /**
     * Lines with this prefix will be treated as single line comments.
     */
    private String singleLineCommentPrefix = "--";

    /**
     * The sql-runner name comment prefix -
     * changing singleLineCommentPrefix will also change nameCommentPrefix.
     */
    private String nameCommentPrefix = "--sqlrunner.name:";

    /**
     * The sql-runner fail fast comment prefix -
     * changing singleLineCommentPrefix will also change nameCommentPrefix.
     */
    private String failFastCommentPrefix = "--sqlrunner.failfast:";

    /**
     * Sets the name of the character set of the SQL script file - UTF-8 by default.
     * @param charsetName
     *   The name of the character set of the SQL script file.
     * @return
     *   this instance.
     */
    public DefaultFileReader setCharsetName(final String charsetName) {
        this.charsetName = charsetName;
        return this;
    }

    /**
     * Sets the file path prefix.
     * The intended use of the file path prefix is to allow developers to work with file names
     * without having to know where the files are located.
     * e.g. To support different DBs we could have the following files
     * <ul>
     *   <li>/oracle/do-some-work.sql</li>
     *   <li>/mysql/do-some-work.sql</li>
     * </ul>
     * Developers could write
     * <p><code>new SqlScriptRunner(dataSource).setFileName("do-some-work.sql")</code></p>
     * and filePathPrefix ("/oracle/" or "/mysql/") could be set on the SqlScriptRunner via
     * dependency injection.
     *
     * @param filePathPrefix
     *   The file path prefix.
     * @return
     *   this instance.
     */
    public DefaultFileReader setFilePathPrefix(final String filePathPrefix) {
        this.filePathPrefix = filePathPrefix;
        return this;
    }

    /**
     * Sets the single line comment prefix. Double hyphen is the default.
     * Note: MySQL allows # as well as --.
     * @param singleLineCommentPrefix
     *   The single line comment prefix, which must not be null.
     * @return
     *   this instance.
     */
    public DefaultFileReader setSingleLineCommentPrefix(final String singleLineCommentPrefix) {
        if (singleLineCommentPrefix == null) {
            throw new NullPointerException("singleLineCommentPrefix must not be null");
        }
        this.singleLineCommentPrefix = singleLineCommentPrefix;
        this.nameCommentPrefix = singleLineCommentPrefix + "sqlrunner.name:";
        this.failFastCommentPrefix = singleLineCommentPrefix + "sqlrunner.failfast:";
        return this;
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
