// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.sqlserver;

import org.flywaydb.core.internal.dbsupport.DbSupport;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.internal.dbsupport.Table;
import java.util.Iterator;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Schema;

public class SQLServerSchema extends Schema<SQLServerDbSupport>
{
    public SQLServerSchema(final JdbcTemplate jdbcTemplate, final SQLServerDbSupport dbSupport, final String name) {
        super(jdbcTemplate, dbSupport, name);
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME=?", this.name) > 0;
    }
    
    @Override
    protected boolean doEmpty() throws SQLException {
        final int objectCount = this.jdbcTemplate.queryForInt("Select count(*) FROM ( Select TABLE_NAME as OBJECT_NAME, TABLE_SCHEMA as OBJECT_SCHEMA from INFORMATION_SCHEMA.TABLES Union Select TABLE_NAME as OBJECT_NAME, TABLE_SCHEMA as OBJECT_SCHEMA from INFORMATION_SCHEMA.VIEWS Union Select CONSTRAINT_NAME as OBJECT_NAME, TABLE_SCHEMA as OBJECT_SCHEMA from INFORMATION_SCHEMA.TABLE_CONSTRAINTS Union Select ROUTINE_NAME as OBJECT_NAME, ROUTINE_SCHEMA as OBJECT_SCHEMA from INFORMATION_SCHEMA.ROUTINES ) R where OBJECT_SCHEMA = ?", this.name);
        return objectCount == 0;
    }
    
    @Override
    protected void doCreate() throws SQLException {
        this.jdbcTemplate.execute("CREATE SCHEMA " + ((SQLServerDbSupport)this.dbSupport).quote(this.name), new Object[0]);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        this.clean();
        this.jdbcTemplate.execute("DROP SCHEMA " + ((SQLServerDbSupport)this.dbSupport).quote(this.name), new Object[0]);
    }
    
    @Override
    protected void doClean() throws SQLException {
        for (final String statement : this.cleanForeignKeys()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.cleanDefaultConstraints()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.cleanRoutines("PROCEDURE")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.cleanViews()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final Table table : this.allTables()) {
            table.drop();
        }
        for (final String statement : this.cleanRoutines("FUNCTION")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.cleanTypes()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.cleanSynonyms()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        if (this.jdbcTemplate.getMetaData().getDatabaseMajorVersion() >= 11) {
            for (final String statement : this.cleanSequences()) {
                this.jdbcTemplate.execute(statement, new Object[0]);
            }
        }
    }
    
    private List<String> cleanForeignKeys() throws SQLException {
        final List<Map<String, String>> constraintNames = this.jdbcTemplate.queryForList("SELECT table_name, constraint_name FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE constraint_type in ('FOREIGN KEY','CHECK') and table_schema=?", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final Map<String, String> row : constraintNames) {
            final String tableName = row.get("table_name");
            final String constraintName = row.get("constraint_name");
            statements.add("ALTER TABLE " + ((SQLServerDbSupport)this.dbSupport).quote(this.name, tableName) + " DROP CONSTRAINT " + ((SQLServerDbSupport)this.dbSupport).quote(constraintName));
        }
        return statements;
    }
    
    private List<String> cleanDefaultConstraints() throws SQLException {
        final List<Map<String, String>> constraintNames = this.jdbcTemplate.queryForList("select t.name as table_name, d.name as constraint_name from sys.tables t inner join sys.default_constraints d on d.parent_object_id = t.object_id\n inner join sys.schemas s on s.schema_id = t.schema_id\n where s.name = ?", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final Map<String, String> row : constraintNames) {
            final String tableName = row.get("table_name");
            final String constraintName = row.get("constraint_name");
            statements.add("ALTER TABLE " + ((SQLServerDbSupport)this.dbSupport).quote(this.name, tableName) + " DROP CONSTRAINT " + ((SQLServerDbSupport)this.dbSupport).quote(constraintName));
        }
        return statements;
    }
    
    private List<String> cleanRoutines(final String routineType) throws SQLException {
        final List<Map<String, String>> routineNames = this.jdbcTemplate.queryForList("SELECT routine_name FROM INFORMATION_SCHEMA.ROUTINES WHERE routine_schema=? AND routine_type=?", this.name, routineType);
        final List<String> statements = new ArrayList<String>();
        for (final Map<String, String> row : routineNames) {
            final String routineName = row.get("routine_name");
            statements.add("DROP " + routineType + " " + ((SQLServerDbSupport)this.dbSupport).quote(this.name, routineName));
        }
        return statements;
    }
    
    private List<String> cleanViews() throws SQLException {
        final List<String> viewNames = this.jdbcTemplate.queryForStringList("SELECT table_name FROM INFORMATION_SCHEMA.VIEWS WHERE table_schema=?", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final String viewName : viewNames) {
            statements.add("DROP VIEW " + ((SQLServerDbSupport)this.dbSupport).quote(this.name, viewName));
        }
        return statements;
    }
    
    private List<String> cleanTypes() throws SQLException {
        final List<String> typeNames = this.jdbcTemplate.queryForStringList("SELECT t.name FROM sys.types t INNER JOIN sys.schemas s ON t.schema_id = s.schema_id WHERE t.is_user_defined = 1 AND s.name = ?", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final String typeName : typeNames) {
            statements.add("DROP TYPE " + ((SQLServerDbSupport)this.dbSupport).quote(this.name, typeName));
        }
        return statements;
    }
    
    private List<String> cleanSynonyms() throws SQLException {
        final List<String> synonymNames = this.jdbcTemplate.queryForStringList("SELECT sn.name FROM sys.synonyms sn INNER JOIN sys.schemas s ON sn.schema_id = s.schema_id WHERE s.name = ?", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final String synonymName : synonymNames) {
            statements.add("DROP SYNONYM " + ((SQLServerDbSupport)this.dbSupport).quote(this.name, synonymName));
        }
        return statements;
    }
    
    private List<String> cleanSequences() throws SQLException {
        final List<String> names = this.jdbcTemplate.queryForStringList("SELECT sequence_name FROM INFORMATION_SCHEMA.SEQUENCES WHERE sequence_schema=?", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final String sequenceName : names) {
            statements.add("DROP SEQUENCE " + ((SQLServerDbSupport)this.dbSupport).quote(this.name, sequenceName));
        }
        return statements;
    }
    
    @Override
    protected Table[] doAllTables() throws SQLException {
        final List<String> tableNames = this.jdbcTemplate.queryForStringList("SELECT table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_type='BASE TABLE' and table_schema=?", this.name);
        final Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new SQLServerTable(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }
    
    @Override
    public Table getTable(final String tableName) {
        return new SQLServerTable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
}
