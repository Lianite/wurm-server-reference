// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.hsql;

import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.flywaydb.core.internal.util.jdbc.JdbcUtils;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import java.sql.Connection;
import org.flywaydb.core.internal.dbsupport.DbSupport;

public class HsqlDbSupport extends DbSupport
{
    public HsqlDbSupport(final Connection connection) {
        super(new JdbcTemplate(connection, 12));
    }
    
    @Override
    public String getDbName() {
        return "hsql";
    }
    
    @Override
    public String getCurrentUserFunction() {
        return "USER()";
    }
    
    @Override
    protected String doGetCurrentSchemaName() throws SQLException {
        ResultSet resultSet = null;
        String schema = null;
        try {
            resultSet = this.jdbcTemplate.getMetaData().getSchemas();
            while (resultSet.next()) {
                if (resultSet.getBoolean("IS_DEFAULT")) {
                    schema = resultSet.getString("TABLE_SCHEM");
                    break;
                }
            }
        }
        finally {
            JdbcUtils.closeResultSet(resultSet);
        }
        return schema;
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
        return new HsqlSqlStatementBuilder();
    }
    
    public String doQuote(final String identifier) {
        return "\"" + identifier + "\"";
    }
    
    @Override
    public Schema getSchema(final String name) {
        return new HsqlSchema(this.jdbcTemplate, this, name);
    }
    
    @Override
    public boolean catalogIsSchema() {
        return false;
    }
}
