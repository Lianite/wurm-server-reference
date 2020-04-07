// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.solid;

import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import java.sql.Connection;
import org.flywaydb.core.internal.dbsupport.DbSupport;

public class SolidDbSupport extends DbSupport
{
    public SolidDbSupport(final Connection connection) {
        super(new JdbcTemplate(connection, 0));
    }
    
    @Override
    public Schema getSchema(final String name) {
        return new SolidSchema(this.jdbcTemplate, this, name);
    }
    
    @Override
    public SqlStatementBuilder createSqlStatementBuilder() {
        return new SolidSqlStatementBuilder();
    }
    
    @Override
    public String getDbName() {
        return "solid";
    }
    
    @Override
    protected String doGetCurrentSchemaName() throws SQLException {
        return this.jdbcTemplate.queryForString("SELECT CURRENT_SCHEMA()", new String[0]);
    }
    
    @Override
    protected void doChangeCurrentSchemaTo(final String schema) throws SQLException {
        this.jdbcTemplate.execute("SET SCHEMA " + schema, new Object[0]);
    }
    
    @Override
    public String getCurrentUserFunction() {
        return "LOGIN_SCHEMA()";
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
    protected String doQuote(final String identifier) {
        return "\"" + identifier + "\"";
    }
    
    @Override
    public boolean catalogIsSchema() {
        return false;
    }
}
