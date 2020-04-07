// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.db2zos;

import org.flywaydb.core.internal.dbsupport.Schema;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import java.sql.Connection;
import org.flywaydb.core.internal.dbsupport.DbSupport;

public class DB2zosDbSupport extends DbSupport
{
    public DB2zosDbSupport(final Connection connection) {
        super(new JdbcTemplate(connection, 12));
    }
    
    @Override
    public String getDbName() {
        return "db2zos";
    }
    
    @Override
    public SqlStatementBuilder createSqlStatementBuilder() {
        return new DB2zosSqlStatementBuilder();
    }
    
    public String getScriptLocation() {
        return "com/googlecode/flyway/core/dbsupport/db2zos/";
    }
    
    @Override
    protected String doGetCurrentSchemaName() throws SQLException {
        return this.jdbcTemplate.queryForString("select current_schema from sysibm.sysdummy1", new String[0]);
    }
    
    @Override
    protected void doChangeCurrentSchemaTo(final String schema) throws SQLException {
        this.jdbcTemplate.execute("SET SCHEMA " + schema, new Object[0]);
    }
    
    @Override
    public String getCurrentUserFunction() {
        return "USER";
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
    
    public String doQuote(final String identifier) {
        return "\"" + identifier + "\"";
    }
    
    @Override
    public Schema getSchema(final String name) {
        return new DB2zosSchema(this.jdbcTemplate, this, name);
    }
    
    @Override
    public boolean catalogIsSchema() {
        return false;
    }
}
