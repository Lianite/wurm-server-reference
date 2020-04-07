// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.vertica;

import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.postgresql.PostgreSQLTable;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.internal.dbsupport.Type;
import org.flywaydb.core.internal.dbsupport.Table;
import java.util.Iterator;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Schema;

public class VerticaSchema extends Schema<VerticaDbSupport>
{
    public VerticaSchema(final JdbcTemplate jdbcTemplate, final VerticaDbSupport dbSupport, final String name) {
        super(jdbcTemplate, dbSupport, name);
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM v_catalog.schemata WHERE schema_name=?", this.name) > 0;
    }
    
    @Override
    protected boolean doEmpty() throws SQLException {
        final int objectCount = this.jdbcTemplate.queryForInt("SELECT count(*) FROM v_catalog.all_tables WHERE schema_name=? and table_type = 'TABLE'", this.name);
        return objectCount == 0;
    }
    
    @Override
    protected void doCreate() throws SQLException {
        this.jdbcTemplate.execute("CREATE SCHEMA " + ((VerticaDbSupport)this.dbSupport).quote(this.name), new Object[0]);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        this.jdbcTemplate.execute("DROP SCHEMA " + ((VerticaDbSupport)this.dbSupport).quote(this.name) + " CASCADE", new Object[0]);
    }
    
    @Override
    protected void doClean() throws SQLException {
        for (final String statement : this.generateDropStatementsForViews()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final Table table : this.allTables()) {
            table.drop();
        }
        for (final String statement : this.generateDropStatementsForSequences()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForFunctions()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final Type type : this.allTypes()) {
            type.drop();
        }
    }
    
    private List<String> generateDropStatementsForSequences() throws SQLException {
        final List<String> sequenceNames = this.jdbcTemplate.queryForStringList("SELECT sequence_name FROM v_catalog.sequences WHERE sequence_schema=?", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final String sequenceName : sequenceNames) {
            statements.add("DROP SEQUENCE IF EXISTS " + ((VerticaDbSupport)this.dbSupport).quote(this.name, sequenceName));
        }
        return statements;
    }
    
    private List<String> generateDropStatementsForFunctions() throws SQLException {
        final List<Map<String, String>> rows = this.jdbcTemplate.queryForList("select * from user_functions where schema_name = ? and procedure_type = 'User Defined Function'", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final Map<String, String> row : rows) {
            statements.add("DROP FUNCTION IF EXISTS " + ((VerticaDbSupport)this.dbSupport).quote(this.name, row.get("function_name")) + "(" + row.get("function_argument_type") + ")");
        }
        return statements;
    }
    
    private List<String> generateDropStatementsForViews() throws SQLException {
        final List<String> viewNames = this.jdbcTemplate.queryForStringList("SELECT t.table_name FROM v_catalog.all_tables t WHERE schema_name=? and table_type = 'VIEW'", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final String viewName : viewNames) {
            statements.add("DROP VIEW IF EXISTS " + ((VerticaDbSupport)this.dbSupport).quote(this.name, viewName));
        }
        return statements;
    }
    
    @Override
    protected Table[] doAllTables() throws SQLException {
        final List<String> tableNames = this.jdbcTemplate.queryForStringList("SELECT t.table_name FROM v_catalog.all_tables t WHERE schema_name=? and table_type =  'TABLE'", this.name);
        final Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new PostgreSQLTable(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }
    
    @Override
    public Table getTable(final String tableName) {
        return new PostgreSQLTable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
}
