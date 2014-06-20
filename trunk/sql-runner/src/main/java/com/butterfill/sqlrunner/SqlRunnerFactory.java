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
//TODO: make sure all config is included here
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
     * Attributes that may be used in the SQL file.
     */
    private Map<String, String> attributeMap;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public SqlRunnerCallbackHandler getDefaultCallbackHandler() {
        return defaultCallbackHandler;
    }

    public void setDefaultCallbackHandler(SqlRunnerCallbackHandler defaultCallbackHandler) {
        this.defaultCallbackHandler = defaultCallbackHandler;
    }

    public SqlRunnerResultSetNextRowCallbackHandler getDefaultResultSetNextRowCallbackHandler() {
        return defaultResultSetNextRowCallbackHandler;
    }

    public void setDefaultResultSetNextRowCallbackHandler(SqlRunnerResultSetNextRowCallbackHandler defaultResultSetNextRowCallbackHandler) {
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

    public Map<String, String> getAttributeMap() {
        return attributeMap;
    }

    public void setAttributeMap(final Map<String, String> attributeMap) {
        this.attributeMap = attributeMap;
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
                .setAttributePrefixAndPostfix(attributeNamePrefix, attributeNamePostfix);

        if (attributeMap != null) {
            for (Map.Entry<String, String> entry : attributeMap.entrySet()) {
                result.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        return result;

    }

}
