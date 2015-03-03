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
     * Name of the character set of SQL files.
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

    /**
     * Returns the datasource to be used by SqlRunners created by this factory.
     * @return
     *   The datasource to be used by SqlRunners created by this factory.
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Sets the datasource to be used by SqlRunners created by this factory.
     * @param dataSource
     *   The datasource to be used by SqlRunners created by this factory.
     */
    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns the default callback handler to be used by SqlRunners created by this factory.
     * @return
     *   The default callback handler to be used by SqlRunners created by this factory.
     */
    public SqlRunnerCallbackHandler getDefaultCallbackHandler() {
        return defaultCallbackHandler;
    }

    /**
     * Sets the default callback handler to be used by SqlRunners created by this factory.
     * @param defaultCallbackHandler
     *   The default callback handler to be used by SqlRunners created by this factory.
     */
    public void setDefaultCallbackHandler(final SqlRunnerCallbackHandler defaultCallbackHandler) {
        this.defaultCallbackHandler = defaultCallbackHandler;
    }

    /**
     * Returns the default result set handler to be used by SqlRunners created by this factory.
     * @return
     *   The default result set handler to be used by SqlRunners created by this factory.
     */
    public SqlRunnerResultSetNextRowCallbackHandler getDefaultResultSetNextRowCallbackHandler() {
        return defaultResultSetNextRowCallbackHandler;
    }

    /**
     * Sets the default result set handler to be used by SqlRunners created by this factory.
     * @param defaultResultSetNextRowCallbackHandler
     *   The default result set handler to be used by SqlRunners created by this factory.
     */
    public void setDefaultResultSetNextRowCallbackHandler(
            final SqlRunnerResultSetNextRowCallbackHandler defaultResultSetNextRowCallbackHandler) {
        this.defaultResultSetNextRowCallbackHandler = defaultResultSetNextRowCallbackHandler;
    }

    /**
     * Returns the name of the character set to be used by SqlRunners created by this factory.
     * SqlRunners use the character set name when reading files.
     * @return
     *   The name of the character set to be used by SqlRunners created by this factory.
     */
    public String getCharsetName() {
        return charsetName;
    }

    /**
     * Sets the name of the character set to be used by SqlRunners created by this factory.
     * SqlRunners use the character set name when reading files.
     * @param charsetName
     *   The name of the character set to be used by SqlRunners created by this factory.
     */
    public void setCharsetName(final String charsetName) {
        this.charsetName = charsetName;
    }

    /**
     * Returns the file path prefix to be used by SqlRunners created by this factory.
     * SqlRunners use the file path prefix when reading files.
     * @return
     *   The file path prefix to be used by SqlRunners created by this factory.
     */
    public String getFilePathPrefix() {
        return filePathPrefix;
    }

    /**
     * Sets the file path prefix to be used by SqlRunners created by this factory.
     * SqlRunners use the file path prefix when reading files.
     * @param filePathPrefix
     *   The file path prefix to be used by SqlRunners created by this factory.
     */
    public void setFilePathPrefix(final String filePathPrefix) {
        this.filePathPrefix = filePathPrefix;
    }

    /**
     * Returns the attribute name prefix to be used by SqlRunners created by this factory.
     * SqlRunners use the attribute name prefix when replacing attributes files.
     * @return
     *   The attribute name prefix to be used by SqlRunners created by this factory.
     */
    public String getAttributeNamePrefix() {
        return attributeNamePrefix;
    }

    /**
     * Sets the attribute name prefix to be used by SqlRunners created by this factory.
     * SqlRunners use the attribute name prefix when replacing attributes files.
     * @param attributeNamePrefix
     *   The attribute name prefix to be used by SqlRunners created by this factory.
     */
    public void setAttributeNamePrefix(final String attributeNamePrefix) {
        this.attributeNamePrefix = attributeNamePrefix;
    }

    /**
     * Returns the attribute name postfix to be used by SqlRunners created by this factory.
     * SqlRunners use the attribute name postfix when replacing attributes files.
     * @return
     *   The attribute name postfix to be used by SqlRunners created by this factory.
     */
    public String getAttributeNamePostfix() {
        return attributeNamePostfix;
    }

    /**
     * Sets the attribute name postfix to be used by SqlRunners created by this factory.
     * SqlRunners use the attribute name postfix when replacing attributes files.
     * @param attributeNamePostfix
     *   The attribute name postfix to be used by SqlRunners created by this factory.
     */
    public void setAttributeNamePostfix(final String attributeNamePostfix) {
        this.attributeNamePostfix = attributeNamePostfix;
    }

    /**
     * Returns the single line comment prefix to be used by SqlRunners created by this factory.
     * SqlRunners use the single line comment prefix when reading files.
     * @return
     *   The single line comment prefix to be used by SqlRunners created by this factory.
     */
    public String getSingleLineCommentPrefix() {
        return singleLineCommentPrefix;
    }

    /**
     * Sets the single line comment prefix to be used by SqlRunners created by this factory.
     * SqlRunners use the single line comment prefix when reading files.
     * @param singleLineCommentPrefix
     *   The single line comment prefix to be used by SqlRunners created by this factory.
     */
    public void setSingleLineCommentPrefix(final String singleLineCommentPrefix) {
        this.singleLineCommentPrefix = singleLineCommentPrefix;
    }

    /**
     * Returns the attribute map of this instance - all attributes in this map are set on
     * SqlRunners by {@link #newSqlRunner() }.
     * @return
     *   The attribute map of this instance.
     */
    public Map<String, String> getAttributeMap() {
        return attributeMap;
    }

    /**
     * Sets the attribute map of this instance - all attributes in this map are set on
     * SqlRunners by {@link #newSqlRunner() }.
     * @param attributeMap
     *   The attribute map of this instance.
     */
    public void setAttributeMap(final Map<String, String> attributeMap) {
        this.attributeMap = attributeMap;
    }

    /**
     * Returns the callback handler map to be used by SqlRunners created by this factory.
     * SqlRunners use callback handlers when executing SQL statements.
     * @return
     *   The callback handler map to be used by SqlRunners created by this factory.
     */
    public Map<String, SqlRunnerCallbackHandler> getCallbackHandlerMap() {
        return callbackHandlerMap;
    }

    /**
     * Sets the callback handler map to be used by SqlRunners created by this factory.
     * SqlRunners use callback handlers when executing SQL statements.
     * @param callbackHandlerMap
     *   The callback handler map to be used by SqlRunners created by this factory.
     */
    public void setCallbackHandlerMap(
            final Map<String, SqlRunnerCallbackHandler> callbackHandlerMap) {
        this.callbackHandlerMap = callbackHandlerMap;
    }

    /**
     * Returns the result set handler map to be used by SqlRunners created by this factory.
     * SqlRunners use result set handlers when processing result sets.
     * @return
     *   The result set handler map to be used by SqlRunners created by this factory.
     */
    public Map<String, SqlRunnerResultSetNextRowCallbackHandler>
        getRunnerResultSetNextRowCallbackHandlerMap() {
        return rsnrCallbackHandlerMap;
    }

    /**
     * Sets the result set handler map to be used by SqlRunners created by this factory.
     * SqlRunners use result set handlers when processing result sets.
     * @param rsnrCallbackHandlerMap
     *   The result set handler map to be used by SqlRunners created by this factory.
     */
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
