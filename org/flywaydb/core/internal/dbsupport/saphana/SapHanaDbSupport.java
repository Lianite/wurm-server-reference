// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.saphana;

import org.flywaydb.core.internal.dbsupport.Schema;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import java.sql.Connection;
import org.flywaydb.core.internal.dbsupport.DbSupport;

public class SapHanaDbSupport extends DbSupport
{
    public SapHanaDbSupport(final Connection connection) {
        super(new JdbcTemplate(connection, 12));
    }
    
    @Override
    public SqlStatementBuilder createSqlStatementBuilder() {
        return new SapHanaSqlStatementBuilder();
    }
    
    @Override
    public String getDbName() {
        return "saphana";
    }
    
    @Override
    protected String doGetCurrentSchemaName() throws SQLException {
        return this.jdbcTemplate.queryForString("SELECT CURRENT_SCHEMA FROM DUMMY", new String[0]);
    }
    
    @Override
    protected void doChangeCurrentSchemaTo(final String schema) throws SQLException {
        this.jdbcTemplate.execute("SET SCHEMA " + schema, new Object[0]);
    }
    
    @Override
    public String getCurrentUserFunction() {
        return "CURRENT_USER";
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
    
    public String doQuote(final String identifier) {
        return "\"" + identifier + "\"";
    }
    
    @Override
    public Schema getSchema(final String name) {
        return new SapHanaSchema(this.jdbcTemplate, this, name);
    }
    
    @Override
    public boolean catalogIsSchema() {
        return false;
    }
}
