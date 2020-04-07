// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.redshift;

import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import java.util.Iterator;
import org.flywaydb.core.internal.dbsupport.Table;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Schema;

public class RedshiftSchema extends Schema<RedshiftDbSupport>
{
    public RedshiftSchema(final JdbcTemplate jdbcTemplate, final RedshiftDbSupport dbSupport, final String name) {
        super(jdbcTemplate, dbSupport, name);
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM pg_namespace WHERE nspname=?", this.name) > 0;
    }
    
    @Override
    protected boolean doEmpty() throws SQLException {
        final int objectCount = this.jdbcTemplate.queryForInt("SELECT count(*) FROM information_schema.tables WHERE table_schema=? AND table_type='BASE TABLE'", this.name);
        return objectCount == 0;
    }
    
    @Override
    protected void doCreate() throws SQLException {
        this.jdbcTemplate.execute("CREATE SCHEMA " + ((RedshiftDbSupport)this.dbSupport).quote(this.name), new Object[0]);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        this.jdbcTemplate.execute("DROP SCHEMA " + ((RedshiftDbSupport)this.dbSupport).quote(this.name) + " CASCADE", new Object[0]);
    }
    
    @Override
    protected void doClean() throws SQLException {
        for (final Table table : this.allTables()) {
            table.drop();
        }
        for (final String statement : this.generateDropStatementsForViews()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
    }
    
    @Override
    protected Table[] doAllTables() throws SQLException {
        final List<String> tableNames = this.jdbcTemplate.queryForStringList("SELECT t.table_name FROM information_schema.tables t WHERE table_schema=? AND table_type = 'BASE TABLE'", this.name);
        final Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new RedshiftTable(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }
    
    protected List<String> generateDropStatementsForViews() throws SQLException {
        final List<String> viewNames = this.jdbcTemplate.queryForStringList("SELECT t.table_name FROM information_schema.tables t WHERE table_schema=? AND table_type = 'VIEW'", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final String viewName : viewNames) {
            statements.add("DROP VIEW " + ((RedshiftDbSupport)this.dbSupport).quote(this.name, viewName) + " CASCADE");
        }
        return statements;
    }
    
    @Override
    public Table getTable(final String tableName) {
        return new RedshiftTable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
}
