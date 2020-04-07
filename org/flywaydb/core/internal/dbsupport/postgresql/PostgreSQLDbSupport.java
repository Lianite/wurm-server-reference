// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.postgresql;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.util.StringUtils;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import java.sql.Connection;
import org.flywaydb.core.internal.dbsupport.DbSupport;

public class PostgreSQLDbSupport extends DbSupport
{
    public PostgreSQLDbSupport(final Connection connection) {
        super(new JdbcTemplate(connection, 0));
    }
    
    @Override
    public String getDbName() {
        return "postgresql";
    }
    
    @Override
    public String getCurrentUserFunction() {
        return "current_user";
    }
    
    @Override
    public Schema getOriginalSchema() {
        if (this.originalSchema == null) {
            return null;
        }
        return this.getSchema(this.getFirstSchemaFromSearchPath(this.originalSchema));
    }
    
    String getFirstSchemaFromSearchPath(final String searchPath) {
        String result = searchPath.replace(this.doQuote("$user"), "").trim();
        if (result.startsWith(",")) {
            result = result.substring(1);
        }
        if (result.contains(",")) {
            result = result.substring(0, result.indexOf(","));
        }
        result = result.trim();
        if (result.startsWith("\"") && result.endsWith("\"") && !result.endsWith("\\\"") && result.length() > 1) {
            result = result.substring(1, result.length() - 1);
        }
        return result;
    }
    
    @Override
    protected String doGetCurrentSchemaName() throws SQLException {
        return this.jdbcTemplate.queryForString("SHOW search_path", new String[0]);
    }
    
    @Override
    public void changeCurrentSchemaTo(final Schema schema) {
        if (schema.getName().equals(this.originalSchema) || this.originalSchema.startsWith(schema.getName() + ",") || !schema.exists()) {
            return;
        }
        try {
            if (StringUtils.hasText(this.originalSchema)) {
                this.doChangeCurrentSchemaTo(schema.toString() + "," + this.originalSchema);
            }
            else {
                this.doChangeCurrentSchemaTo(schema.toString());
            }
        }
        catch (SQLException e) {
            throw new FlywayException("Error setting current schema to " + schema, e);
        }
    }
    
    @Override
    protected void doChangeCurrentSchemaTo(final String schema) throws SQLException {
        if (!StringUtils.hasLength(schema)) {
            this.jdbcTemplate.execute("SELECT set_config('search_path', '', false)", new Object[0]);
            return;
        }
        this.jdbcTemplate.execute("SET search_path = " + schema, new Object[0]);
    }
    
    @Override
    public boolean supportsDdlTransactions() {
        return true;
    }
    
    @Override
    public String getBooleanTrue() {
        return "TRUE";
    }
    
    @Override
    public String getBooleanFalse() {
        return "FALSE";
    }
    
    @Override
    public SqlStatementBuilder createSqlStatementBuilder() {
        return new PostgreSQLSqlStatementBuilder();
    }
    
    public String doQuote(final String identifier) {
        return "\"" + StringUtils.replaceAll(identifier, "\"", "\"\"") + "\"";
    }
    
    @Override
    public Schema getSchema(final String name) {
        return new PostgreSQLSchema(this.jdbcTemplate, this, name);
    }
    
    @Override
    public boolean catalogIsSchema() {
        return false;
    }
    
    @Override
    public void executePgCopy(final Connection connection, final String sql) throws SQLException {
        final int split = sql.indexOf(";");
        final String statement = sql.substring(0, split);
        final String data = sql.substring(split + 1).trim();
        final CopyManager copyManager = new CopyManager((BaseConnection)connection.unwrap(BaseConnection.class));
        try {
            copyManager.copyIn(statement, (Reader)new StringReader(data));
        }
        catch (IOException e) {
            throw new SQLException("Unable to execute COPY operation", e);
        }
    }
}
