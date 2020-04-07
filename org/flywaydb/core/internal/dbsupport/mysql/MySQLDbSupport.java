// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.mysql;

import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import java.util.UUID;
import org.flywaydb.core.internal.util.StringUtils;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.dbsupport.Schema;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import java.sql.Connection;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.dbsupport.DbSupport;

public class MySQLDbSupport extends DbSupport
{
    private static final Log LOG;
    
    public MySQLDbSupport(final Connection connection) {
        super(new JdbcTemplate(connection, 12));
    }
    
    @Override
    public String getDbName() {
        return "mysql";
    }
    
    @Override
    public String getCurrentUserFunction() {
        return "SUBSTRING_INDEX(USER(),'@',1)";
    }
    
    @Override
    protected String doGetCurrentSchemaName() throws SQLException {
        return this.jdbcTemplate.getConnection().getCatalog();
    }
    
    @Override
    public void changeCurrentSchemaTo(final Schema schema) {
        if (schema.getName().equals(this.originalSchema) || !schema.exists()) {
            return;
        }
        try {
            this.doChangeCurrentSchemaTo(schema.getName());
        }
        catch (SQLException e) {
            throw new FlywayException("Error setting current schema to " + schema, e);
        }
    }
    
    @Override
    protected void doChangeCurrentSchemaTo(final String schema) throws SQLException {
        if (!StringUtils.hasLength(schema)) {
            try {
                final String newDb = this.quote(UUID.randomUUID().toString());
                this.jdbcTemplate.execute("CREATE SCHEMA " + newDb, new Object[0]);
                this.jdbcTemplate.execute("USE " + newDb, new Object[0]);
                this.jdbcTemplate.execute("DROP SCHEMA " + newDb, new Object[0]);
            }
            catch (Exception e) {
                MySQLDbSupport.LOG.warn("Unable to restore connection to having no default schema: " + e.getMessage());
            }
        }
        else {
            this.jdbcTemplate.getConnection().setCatalog(schema);
        }
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
        return new MySQLSqlStatementBuilder();
    }
    
    public String doQuote(final String identifier) {
        return "`" + identifier + "`";
    }
    
    @Override
    public Schema getSchema(final String name) {
        return new MySQLSchema(this.jdbcTemplate, this, name);
    }
    
    @Override
    public boolean catalogIsSchema() {
        return true;
    }
    
    static {
        LOG = LogFactory.getLog(MySQLDbSupport.class);
    }
}
