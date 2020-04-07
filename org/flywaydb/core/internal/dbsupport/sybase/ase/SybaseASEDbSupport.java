// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.sybase.ase;

import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import java.sql.Connection;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.dbsupport.DbSupport;

public class SybaseASEDbSupport extends DbSupport
{
    private static final Log LOG;
    
    public SybaseASEDbSupport(final Connection connection) {
        super(new JdbcTemplate(connection, 0));
    }
    
    @Override
    public Schema getSchema(final String name) {
        Schema schema = new SybaseASESchema(this.jdbcTemplate, this, name) {
            @Override
            protected boolean doExists() throws SQLException {
                return false;
            }
        };
        try {
            final String currentName = this.doGetCurrentSchemaName();
            if (currentName.equals(name)) {
                schema = new SybaseASESchema(this.jdbcTemplate, this, name);
            }
        }
        catch (SQLException e) {
            SybaseASEDbSupport.LOG.error("Unable to obtain current schema, return non-existing schema", e);
        }
        return schema;
    }
    
    @Override
    public SqlStatementBuilder createSqlStatementBuilder() {
        return new SybaseASESqlStatementBuilder();
    }
    
    @Override
    public String getDbName() {
        return "sybaseASE";
    }
    
    @Override
    protected String doGetCurrentSchemaName() throws SQLException {
        return this.jdbcTemplate.queryForString("select USER_NAME()", new String[0]);
    }
    
    @Override
    protected void doChangeCurrentSchemaTo(final String schema) throws SQLException {
        SybaseASEDbSupport.LOG.info("Sybase does not support setting the schema for the current session. Default schema NOT changed to " + schema);
    }
    
    @Override
    public String getCurrentUserFunction() {
        return "user_name()";
    }
    
    @Override
    public boolean supportsDdlTransactions() {
        return false;
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
    protected String doQuote(final String identifier) {
        return identifier;
    }
    
    @Override
    public boolean catalogIsSchema() {
        return false;
    }
    
    static {
        LOG = LogFactory.getLog(SybaseASEDbSupport.class);
    }
}
