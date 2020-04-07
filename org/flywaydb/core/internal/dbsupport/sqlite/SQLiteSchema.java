// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.sqlite;

import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import java.util.Iterator;
import java.util.List;
import org.flywaydb.core.internal.dbsupport.Table;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.dbsupport.Schema;

public class SQLiteSchema extends Schema<SQLiteDbSupport>
{
    private static final Log LOG;
    
    public SQLiteSchema(final JdbcTemplate jdbcTemplate, final SQLiteDbSupport dbSupport, final String name) {
        super(jdbcTemplate, dbSupport, name);
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        try {
            this.doAllTables();
            return true;
        }
        catch (SQLException e) {
            return false;
        }
    }
    
    @Override
    protected boolean doEmpty() throws SQLException {
        final Table[] tables = this.allTables();
        return tables.length == 0 || (tables.length == 1 && "android_metadata".equals(tables[0].getName()));
    }
    
    @Override
    protected void doCreate() throws SQLException {
        SQLiteSchema.LOG.info("SQLite does not support creating schemas. Schema not created: " + this.name);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        SQLiteSchema.LOG.info("SQLite does not support dropping schemas. Schema not dropped: " + this.name);
    }
    
    @Override
    protected void doClean() throws SQLException {
        final List<String> viewNames = this.jdbcTemplate.queryForStringList("SELECT tbl_name FROM " + ((SQLiteDbSupport)this.dbSupport).quote(this.name) + ".sqlite_master WHERE type='view'", new String[0]);
        for (final String viewName : viewNames) {
            this.jdbcTemplate.executeStatement("DROP VIEW " + ((SQLiteDbSupport)this.dbSupport).quote(this.name, viewName));
        }
        for (final Table table : this.allTables()) {
            table.drop();
        }
    }
    
    @Override
    protected Table[] doAllTables() throws SQLException {
        final List<String> tableNames = this.jdbcTemplate.queryForStringList("SELECT tbl_name FROM " + ((SQLiteDbSupport)this.dbSupport).quote(this.name) + ".sqlite_master WHERE type='table'", new String[0]);
        final Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new SQLiteTable(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }
    
    @Override
    public Table getTable(final String tableName) {
        return new SQLiteTable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
    
    static {
        LOG = LogFactory.getLog(SQLiteSchema.class);
    }
}
