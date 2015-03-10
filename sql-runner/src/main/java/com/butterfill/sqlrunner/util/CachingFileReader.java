
package com.butterfill.sqlrunner.util;

import com.butterfill.sqlrunner.SqlRunnerFileReader;
import com.butterfill.sqlrunner.SqlRunnerStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wraps a {@link SqlRunnerFileReader} and caches the result of reading files as they are read.
 * Calling {@link #readFile(java.lang.String) } with the same fileName will return the same result.
 *
 * @author Peter Butterfill
 */
public class CachingFileReader implements SqlRunnerFileReader {

    /**
     * The file reader being wrapped by this instance.
     */
    private final SqlRunnerFileReader fileReader;

    /**
     * A cache of {@link #readFile(java.lang.String) } results.
     */
    private final Map<String, List<SqlRunnerStatement>> cache =
            new HashMap<String, List<SqlRunnerStatement>>();

    /**
     * Creates a new caching file reader.
     * @param fileReader
     *   The file reader to wrap.
     */
    public CachingFileReader(final SqlRunnerFileReader fileReader) {
        if (fileReader == null) {
            throw new NullPointerException("fileReader must not be null");
        }
        this.fileReader = fileReader;
    }

    /**
     * Reads a file -
     * calling this method, multiple times, with the same fileName will return the same result.
     * Results are cached by fileName.
     * @param fileName
     *   Name of the file to read.
     * @return
     *   The statements from the file.
     */
    public List<SqlRunnerStatement> readFile(final String fileName) {
        if (!cache.containsKey(fileName)) {
            cache.put(fileName, fileReader.readFile(fileName));
        }
        return cache.get(fileName);
    }

}
