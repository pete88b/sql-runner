package com.butterfill.sqlrunner;

import com.butterfill.sqlrunner.util.AttributeSettingResultSetNextRowCallbackHandlerImpl;
import com.butterfill.sqlrunner.util.DefaultCallbackHandlerImpl;
import java.util.Map;
import javax.sql.DataSource;

/**
 * Factory to help configure SqlScriptRunner via dependency injection.
 *
 * Please see {@link SqlRunner} for details of the configuration settings.
 *
 * @author Peter Butterfill
 */
public class SqlRunnerFactory {

    /**
     * The data source to use when executing scripts.
     */
    private DataSource dataSource;

    /**
     * We'll use this handler when no handler has been specified.
     */
    private SqlRunnerCallbackHandler defaultCallbackHandler = new DefaultCallbackHandlerImpl();

    /**
     * We'll use this handler when no result set handler has been specified.
     */
    private SqlRunnerResultSetNextRowCallbackHandler defaultResultSetNextRowCallbackHandler =
            new AttributeSettingResultSetNextRowCallbackHandlerImpl();

    /**
     * The name of the character set of the SQL script file.
     */
    private String charsetName = "UTF-8";

    /**
     * The file path prefix.
     */
    private String filePathPrefix = "";

    /**
     * The attribute name prefix.
     */
    private String attributeNamePrefix = "#{";

    /**
     * The attribute name postfix.
     */
    private String attributeNamePostfix = "}";

    /**
     * Lines with this prefix will be treated as single line comments.
     */
    private String singleLineCommentPrefix = "--";

    /**
     * Attributes that may be used in the SQL file.
     */
    private Map<String, String> attributeMap;

    /**
     * Map of callback handlers.
     * Key is a statement name (set via a sql-runner comment or null).
     */
    private Map<String, SqlRunnerCallbackHandler> callbackHandlerMap;

    /**
     * Map of result set next row callback handlers.
     * Key is a statement name (set via a sql-runner comment or null).
     */
    private Map<String, SqlRunnerResultSetNextRowCallbackHandler> rsnrCallbackHandlerMap;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public SqlRunnerCallbackHandler getDefaultCallbackHandler() {
        return defaultCallbackHandler;
    }

    public void setDefaultCallbackHandler(final SqlRunnerCallbackHandler defaultCallbackHandler) {
        this.defaultCallbackHandler = defaultCallbackHandler;
    }

    public SqlRunnerResultSetNextRowCallbackHandler getDefaultResultSetNextRowCallbackHandler() {
        return defaultResultSetNextRowCallbackHandler;
    }

    public void setDefaultResultSetNextRowCallbackHandler(
            final SqlRunnerResultSetNextRowCallbackHandler defaultResultSetNextRowCallbackHandler) {
        this.defaultResultSetNextRowCallbackHandler = defaultResultSetNextRowCallbackHandler;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(final String charsetName) {
        this.charsetName = charsetName;
    }

    public String getFilePathPrefix() {
        return filePathPrefix;
    }

    public void setFilePathPrefix(final String filePathPrefix) {
        this.filePathPrefix = filePathPrefix;
    }

    public String getAttributeNamePrefix() {
        return attributeNamePrefix;
    }

    public void setAttributeNamePrefix(final String attributeNamePrefix) {
        this.attributeNamePrefix = attributeNamePrefix;
    }

    public String getAttributeNamePostfix() {
        return attributeNamePostfix;
    }

    public void setAttributeNamePostfix(final String attributeNamePostfix) {
        this.attributeNamePostfix = attributeNamePostfix;
    }

    public String getSingleLineCommentPrefix() {
        return singleLineCommentPrefix;
    }

    public void setSingleLineCommentPrefix(String singleLineCommentPrefix) {
        this.singleLineCommentPrefix = singleLineCommentPrefix;
    }

    public Map<String, String> getAttributeMap() {
        return attributeMap;
    }

    public void setAttributeMap(final Map<String, String> attributeMap) {
        this.attributeMap = attributeMap;
    }

    public Map<String, SqlRunnerCallbackHandler> getCallbackHandlerMap() {
        return callbackHandlerMap;
    }

    public void setCallbackHandlerMap(
            final Map<String, SqlRunnerCallbackHandler> callbackHandlerMap) {
        this.callbackHandlerMap = callbackHandlerMap;
    }

    public Map<String, SqlRunnerResultSetNextRowCallbackHandler>
        getRunnerResultSetNextRowCallbackHandlerMap() {
        return rsnrCallbackHandlerMap;
    }

    public void setRunnerResultSetNextRowCallbackHandlerMap(
            final Map<String, SqlRunnerResultSetNextRowCallbackHandler> rsnrCallbackHandlerMap) {
        this.rsnrCallbackHandlerMap = rsnrCallbackHandlerMap;
    }


    /**
     * Returns a new SQL runner.
     * @return
     *   A new SQL runner.
     */
    public SqlRunner newSqlRunner() {
        final SqlRunner result = new SqlRunner(
                dataSource, defaultCallbackHandler, defaultResultSetNextRowCallbackHandler)
                .setCharsetName(charsetName)
                .setFilePathPrefix(filePathPrefix)
                .setAttributePrefixAndPostfix(attributeNamePrefix, attributeNamePostfix)
                .setSingleLineCommentPrefix(singleLineCommentPrefix);

        if (attributeMap != null) {
            for (Map.Entry<String, String> entry : attributeMap.entrySet()) {
                result.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        if (callbackHandlerMap != null) {
            for (Map.Entry<String, SqlRunnerCallbackHandler> entry
                    : callbackHandlerMap.entrySet()) {
                result.setCallbackHandler(entry.getKey(), entry.getValue());
            }
        }

        if (rsnrCallbackHandlerMap != null) {
            for (Map.Entry<String, SqlRunnerResultSetNextRowCallbackHandler> entry
                    : rsnrCallbackHandlerMap.entrySet()) {
                result.setResultSetNextRowCallbackHandler(entry.getKey(), entry.getValue());
            }
        }

        return result;

    }

}
