// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport;

import java.sql.Connection;
import java.sql.SQLException;
import org.flywaydb.core.api.FlywayException;

public abstract class DbSupport
{
    protected final JdbcTemplate jdbcTemplate;
    protected final String originalSchema;
    
    public DbSupport(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.originalSchema = ((jdbcTemplate.getConnection() == null) ? null : this.getCurrentSchemaName());
    }
    
    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }
    
    public abstract Schema getSchema(final String p0);
    
    public abstract SqlStatementBuilder createSqlStatementBuilder();
    
    public abstract String getDbName();
    
    public Schema getOriginalSchema() {
        if (this.originalSchema == null) {
            return null;
        }
        return this.getSchema(this.originalSchema);
    }
    
    public String getCurrentSchemaName() {
        try {
            return this.doGetCurrentSchemaName();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to retrieve the current schema for the connection", e);
        }
    }
    
    protected abstract String doGetCurrentSchemaName() throws SQLException;
    
    public void changeCurrentSchemaTo(final Schema schema) {
        if (schema.getName().equals(this.originalSchema) || !schema.exists()) {
            return;
        }
        try {
            this.doChangeCurrentSchemaTo(schema.toString());
        }
        catch (SQLException e) {
            throw new FlywayException("Error setting current schema to " + schema, e);
        }
    }
    
    public void restoreCurrentSchema() {
        try {
            this.doChangeCurrentSchemaTo(this.originalSchema);
        }
        catch (SQLException e) {
            throw new FlywayException("Error restoring current schema to its original setting", e);
        }
    }
    
    protected abstract void doChangeCurrentSchemaTo(final String p0) throws SQLException;
    
    public abstract String getCurrentUserFunction();
    
    public abstract boolean supportsDdlTransactions();
    
    public abstract String getBooleanTrue();
    
    public abstract String getBooleanFalse();
    
    public String quote(final String... identifiers) {
        String result = "";
        boolean first = true;
        for (final String identifier : identifiers) {
            if (!first) {
                result += ".";
            }
            first = false;
            result += this.doQuote(identifier);
        }
        return result;
    }
    
    protected abstract String doQuote(final String p0);
    
    public abstract boolean catalogIsSchema();
    
    public void executePgCopy(final Connection connection, final String sql) throws SQLException {
    }
}
