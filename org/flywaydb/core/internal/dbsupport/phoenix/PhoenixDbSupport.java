// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.phoenix;

import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import java.sql.Connection;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.dbsupport.DbSupport;

public class PhoenixDbSupport extends DbSupport
{
    private static final Log LOG;
    
    public PhoenixDbSupport(final Connection connection) {
        super(new JdbcTemplate(connection, 12));
    }
    
    @Override
    public String getDbName() {
        return "phoenix";
    }
    
    @Override
    public Schema getOriginalSchema() {
        return this.getSchema(this.originalSchema);
    }
    
    @Override
    public String quote(final String... identifiers) {
        String result = "";
        boolean first = true;
        boolean lastNull = false;
        for (final String identifier : identifiers) {
            if (!first && !lastNull) {
                result += ".";
            }
            first = false;
            if (identifier == null) {
                lastNull = true;
            }
            else {
                result += this.doQuote(identifier);
                lastNull = false;
            }
        }
        return result;
    }
    
    @Override
    protected String doGetCurrentSchemaName() throws SQLException {
        return null;
    }
    
    @Override
    public void changeCurrentSchemaTo(final Schema schema) {
        PhoenixDbSupport.LOG.info("Phoenix does not support setting the schema. Default schema NOT changed to " + schema);
    }
    
    @Override
    protected void doChangeCurrentSchemaTo(final String schema) throws SQLException {
        PhoenixDbSupport.LOG.info("Phoenix does not support setting the schema. Default schema NOT changed to " + schema);
    }
    
    @Override
    public String getCurrentUserFunction() {
        String userName = null;
        try {
            userName = this.jdbcTemplate.getMetaData().getUserName();
        }
        catch (SQLException ex) {}
        return userName;
    }
    
    @Override
    public boolean supportsDdlTransactions() {
        return false;
    }
    
    @Override
    public String getBooleanTrue() {
        return "TRUE";
    }
    
    @Override
    public String getBooleanFalse() {
        return "FALSE";
    }
    
    @Override
    public SqlStatementBuilder createSqlStatementBuilder() {
        return new SqlStatementBuilder();
    }
    
    public String doQuote(final String identifier) {
        return "\"" + identifier + "\"";
    }
    
    @Override
    public Schema getSchema(final String name) {
        return new PhoenixSchema(this.jdbcTemplate, this, name);
    }
    
    @Override
    public boolean catalogIsSchema() {
        return false;
    }
    
    static {
        LOG = LogFactory.getLog(PhoenixDbSupport.class);
    }
}
