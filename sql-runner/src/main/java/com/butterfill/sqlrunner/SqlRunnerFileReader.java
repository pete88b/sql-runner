
package com.butterfill.sqlrunner;

import java.util.List;

/**
 * Handles reading files for sql runner.
 * @author Peter Butterfill
 */
public interface SqlRunnerFileReader {

    /**
     * Converts a file into a list of SqlRunnerStatements.
     * @param fileName
     *   Name of the file to read.
     * @return
     *   A list of SqlRunnerStatements.
     */
    List<SqlRunnerStatement> readFile(String fileName);

}
