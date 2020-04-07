// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.redshift;

import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.dbsupport.postgresql.PostgreSQLSqlStatementBuilder;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import org.flywaydb.core.api.FlywayException;
import java.sql.SQLException;
import org.flywaydb.core.internal.util.StringUtils;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.dbsupport.DbSupport;

public abstract class RedshiftDbSupport extends DbSupport
{
    private static final Log LOG;
    
    public RedshiftDbSupport(final JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }
    
    @Override
    public String getDbName() {
        return "redshift";
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
            result = result.substring(2);
        }
        if (result.contains(",")) {
            return this.getSchema(result.substring(0, result.indexOf(",")));
        }
        return this.getSchema(result);
    }
    
    @Override
    protected String doGetCurrentSchemaName() throws SQLException {
        String searchPath = this.jdbcTemplate.queryForString("SHOW search_path", new String[0]);
        if (StringUtils.hasText(searchPath) && !searchPath.equals("unset") && searchPath.contains("$user") && !searchPath.contains(this.doQuote("$user"))) {
            searchPath = searchPath.replace("$user", this.doQuote("$user"));
        }
        return searchPath;
    }
    
    @Override
    public void changeCurrentSchemaTo(final Schema schema) {
        if (schema.getName().equals(this.originalSchema) || this.originalSchema.startsWith(schema.getName() + ",") || !schema.exists()) {
            return;
        }
        try {
            if (StringUtils.hasText(this.originalSchema) && !this.originalSchema.equals("unset")) {
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
        if (!StringUtils.hasLength(schema) || schema.equals("unset")) {
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
        return new RedshiftSchema(this.jdbcTemplate, this, name);
    }
    
    @Override
    public boolean catalogIsSchema() {
        return false;
    }
    
    public boolean detect() {
        try {
            return this.jdbcTemplate.queryForInt("select count(*) from information_schema.tables where table_schema = 'pg_catalog' and table_name = 'stl_s3client'", new String[0]) > 0;
        }
        catch (SQLException e) {
            RedshiftDbSupport.LOG.error("Unable to check whether this is a Redshift database", e);
            return false;
        }
    }
    
    static {
        LOG = LogFactory.getLog(RedshiftDbSupport.class);
    }
}
