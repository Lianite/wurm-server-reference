// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.vertica;

import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.util.StringUtils;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.flywaydb.core.internal.util.jdbc.RowMapper;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import java.sql.Connection;
import org.flywaydb.core.internal.dbsupport.DbSupport;

public class VerticaDbSupport extends DbSupport
{
    public VerticaDbSupport(final Connection connection) {
        super(new JdbcTemplate(connection, 0));
    }
    
    @Override
    public String getDbName() {
        return "vertica";
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
        String result = this.originalSchema.replace(this.doQuote("$user"), "").trim();
        if (result.startsWith(",")) {
            result = result.substring(1);
        }
        if (result.contains(",")) {
            return this.getSchema(result.substring(0, result.indexOf(",")));
        }
        return this.getSchema(result);
    }
    
    @Override
    protected String doGetCurrentSchemaName() throws SQLException {
        return this.jdbcTemplate.query("SHOW search_path", (RowMapper<String>)new RowMapper<String>() {
            @Override
            public String mapRow(final ResultSet rs) throws SQLException {
                return rs.getString("setting");
            }
        }).get(0);
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
            this.jdbcTemplate.execute("SET search_path = v_catalog", new Object[0]);
            return;
        }
        this.jdbcTemplate.execute("SET search_path = " + schema, new Object[0]);
    }
    
    @Override
    public boolean supportsDdlTransactions() {
        return false;
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
        return new VerticaStatementBuilder();
    }
    
    public String doQuote(final String identifier) {
        return "\"" + StringUtils.replaceAll(identifier, "\"", "\"\"") + "\"";
    }
    
    @Override
    public Schema getSchema(final String name) {
        return new VerticaSchema(this.jdbcTemplate, this, name);
    }
    
    @Override
    public boolean catalogIsSchema() {
        return false;
    }
}
