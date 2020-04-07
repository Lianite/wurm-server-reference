// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.h2;

import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import java.sql.Connection;
import org.flywaydb.core.internal.dbsupport.DbSupport;

public class H2DbSupport extends DbSupport
{
    public H2DbSupport(final Connection connection) {
        super(new JdbcTemplate(connection, 12));
    }
    
    @Override
    public String getDbName() {
        return "h2";
    }
    
    @Override
    public String getCurrentUserFunction() {
        return "USER()";
    }
    
    @Override
    protected String doGetCurrentSchemaName() throws SQLException {
        return this.jdbcTemplate.queryForString("CALL SCHEMA()", new String[0]);
    }
    
    @Override
    protected void doChangeCurrentSchemaTo(final String schema) throws SQLException {
        this.jdbcTemplate.execute("SET SCHEMA " + schema, new Object[0]);
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
    public SqlStatementBuilder createSqlStatementBuilder() {
        return new H2SqlStatementBuilder();
    }
    
    public String doQuote(final String identifier) {
        return "\"" + identifier + "\"";
    }
    
    @Override
    public Schema getSchema(final String name) {
        return new H2Schema(this.jdbcTemplate, this, name);
    }
    
    @Override
    public boolean catalogIsSchema() {
        return false;
    }
}
