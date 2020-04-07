// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.sqlserver;

import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.util.StringUtils;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import java.sql.Connection;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.dbsupport.DbSupport;

public class SQLServerDbSupport extends DbSupport
{
    private static final Log LOG;
    private static boolean schemaMessagePrinted;
    
    public SQLServerDbSupport(final Connection connection) {
        super(new JdbcTemplate(connection, 12));
    }
    
    @Override
    public String getDbName() {
        return "sqlserver";
    }
    
    @Override
    public String getCurrentUserFunction() {
        return "SUSER_SNAME()";
    }
    
    @Override
    protected String doGetCurrentSchemaName() throws SQLException {
        return this.jdbcTemplate.queryForString("SELECT SCHEMA_NAME()", new String[0]);
    }
    
    @Override
    protected void doChangeCurrentSchemaTo(final String schema) throws SQLException {
        if (!SQLServerDbSupport.schemaMessagePrinted) {
            SQLServerDbSupport.LOG.info("SQLServer does not support setting the schema for the current session. Default schema NOT changed to " + schema);
            SQLServerDbSupport.schemaMessagePrinted = true;
        }
    }
    
    @Override
    public boolean supportsDdlTransactions() {
        return true;
    }
    
    @Override
    public String getBooleanTrue() {
        return "1";
    }
    
    @Override
    public String getBooleanFalse() {
        return "0";
    }
    
    @Override
    public SqlStatementBuilder createSqlStatementBuilder() {
        return new SQLServerSqlStatementBuilder();
    }
    
    private String escapeIdentifier(final String identifier) {
        return StringUtils.replaceAll(identifier, "]", "]]");
    }
    
    public String doQuote(final String identifier) {
        return "[" + this.escapeIdentifier(identifier) + "]";
    }
    
    @Override
    public Schema getSchema(final String name) {
        return new SQLServerSchema(this.jdbcTemplate, this, name);
    }
    
    @Override
    public boolean catalogIsSchema() {
        return false;
    }
    
    static {
        LOG = LogFactory.getLog(SQLServerDbSupport.class);
    }
}
