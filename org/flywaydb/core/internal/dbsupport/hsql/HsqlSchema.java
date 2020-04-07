// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.hsql;

import org.flywaydb.core.internal.dbsupport.DbSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.flywaydb.core.internal.dbsupport.Table;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Schema;

public class HsqlSchema extends Schema<HsqlDbSupport>
{
    public HsqlSchema(final JdbcTemplate jdbcTemplate, final HsqlDbSupport dbSupport, final String name) {
        super(jdbcTemplate, dbSupport, name);
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT (*) FROM information_schema.system_schemas WHERE table_schem=?", this.name) > 0;
    }
    
    @Override
    protected boolean doEmpty() throws SQLException {
        return this.allTables().length == 0;
    }
    
    @Override
    protected void doCreate() throws SQLException {
        final String user = this.jdbcTemplate.queryForString("SELECT USER() FROM (VALUES(0))", new String[0]);
        this.jdbcTemplate.execute("CREATE SCHEMA " + ((HsqlDbSupport)this.dbSupport).quote(this.name) + " AUTHORIZATION " + user, new Object[0]);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        this.jdbcTemplate.execute("DROP SCHEMA " + ((HsqlDbSupport)this.dbSupport).quote(this.name) + " CASCADE", new Object[0]);
    }
    
    @Override
    protected void doClean() throws SQLException {
        for (final Table table : this.allTables()) {
            table.drop();
        }
        for (final String statement : this.generateDropStatementsForSequences()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
    }
    
    private List<String> generateDropStatementsForSequences() throws SQLException {
        final List<String> sequenceNames = this.jdbcTemplate.queryForStringList("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SYSTEM_SEQUENCES where SEQUENCE_SCHEMA = ?", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final String seqName : sequenceNames) {
            statements.add("DROP SEQUENCE " + ((HsqlDbSupport)this.dbSupport).quote(this.name, seqName));
        }
        return statements;
    }
    
    @Override
    protected Table[] doAllTables() throws SQLException {
        final List<String> tableNames = this.jdbcTemplate.queryForStringList("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.SYSTEM_TABLES where TABLE_SCHEM = ? AND TABLE_TYPE = 'TABLE'", this.name);
        final Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new HsqlTable(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }
    
    @Override
    public Table getTable(final String tableName) {
        return new HsqlTable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
}
