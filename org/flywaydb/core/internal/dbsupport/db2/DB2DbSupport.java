// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.db2;

import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import java.sql.SQLException;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import java.sql.Connection;
import org.flywaydb.core.internal.dbsupport.DbSupport;

public class DB2DbSupport extends DbSupport
{
    private final int majorVersion;
    
    public DB2DbSupport(final Connection connection) {
        super(new JdbcTemplate(connection, 12));
        try {
            this.majorVersion = connection.getMetaData().getDatabaseMajorVersion();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to determine DB2 major version", e);
        }
    }
    
    @Override
    public SqlStatementBuilder createSqlStatementBuilder() {
        return new DB2SqlStatementBuilder();
    }
    
    @Override
    public String getDbName() {
        return "db2";
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
        return "CURRENT_USER";
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
        return new DB2Schema(this.jdbcTemplate, this, name);
    }
    
    @Override
    public boolean catalogIsSchema() {
        return false;
    }
    
    public int getDb2MajorVersion() {
        return this.majorVersion;
    }
}
