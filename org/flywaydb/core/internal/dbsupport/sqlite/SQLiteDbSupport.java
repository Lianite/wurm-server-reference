// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.sqlite;

import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import java.sql.Connection;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.dbsupport.DbSupport;

public class SQLiteDbSupport extends DbSupport
{
    private static final Log LOG;
    
    public SQLiteDbSupport(final Connection connection) {
        super(new JdbcTemplate(connection, 12));
    }
    
    @Override
    public String getDbName() {
        return "sqlite";
    }
    
    @Override
    public String getCurrentUserFunction() {
        return "''";
    }
    
    @Override
    protected String doGetCurrentSchemaName() throws SQLException {
        return "main";
    }
    
    @Override
    protected void doChangeCurrentSchemaTo(final String schema) throws SQLException {
        SQLiteDbSupport.LOG.info("SQLite does not support setting the schema. Default schema NOT changed to " + schema);
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
        return new SQLiteSqlStatementBuilder();
    }
    
    public String doQuote(final String identifier) {
        return "\"" + identifier + "\"";
    }
    
    @Override
    public Schema getSchema(final String name) {
        return new SQLiteSchema(this.jdbcTemplate, this, name);
    }
    
    @Override
    public boolean catalogIsSchema() {
        return true;
    }
    
    static {
        LOG = LogFactory.getLog(SQLiteDbSupport.class);
    }
}
