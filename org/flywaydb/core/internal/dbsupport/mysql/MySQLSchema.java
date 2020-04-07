// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.mysql;

import org.flywaydb.core.internal.dbsupport.DbSupport;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.internal.dbsupport.Table;
import java.util.Iterator;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Schema;

public class MySQLSchema extends Schema<MySQLDbSupport>
{
    public MySQLSchema(final JdbcTemplate jdbcTemplate, final MySQLDbSupport dbSupport, final String name) {
        super(jdbcTemplate, dbSupport, name);
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name=?", this.name) > 0;
    }
    
    @Override
    protected boolean doEmpty() throws SQLException {
        final int objectCount = this.jdbcTemplate.queryForInt("Select (Select count(*) from information_schema.TABLES Where TABLE_SCHEMA=?) + (Select count(*) from information_schema.VIEWS Where TABLE_SCHEMA=?) + (Select count(*) from information_schema.TABLE_CONSTRAINTS Where TABLE_SCHEMA=?) + (Select count(*) from information_schema.EVENTS Where EVENT_SCHEMA=?) + (Select count(*) from information_schema.TRIGGERS Where TRIGGER_SCHEMA=?) + (Select count(*) from information_schema.ROUTINES Where ROUTINE_SCHEMA=?)", this.name, this.name, this.name, this.name, this.name, this.name);
        return objectCount == 0;
    }
    
    @Override
    protected void doCreate() throws SQLException {
        this.jdbcTemplate.execute("CREATE SCHEMA " + ((MySQLDbSupport)this.dbSupport).quote(this.name), new Object[0]);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        this.jdbcTemplate.execute("DROP SCHEMA " + ((MySQLDbSupport)this.dbSupport).quote(this.name), new Object[0]);
    }
    
    @Override
    protected void doClean() throws SQLException {
        for (final String statement : this.cleanEvents()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.cleanRoutines()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.cleanViews()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        this.jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0", new Object[0]);
        for (final Table table : this.allTables()) {
            table.drop();
        }
        this.jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1", new Object[0]);
    }
    
    private List<String> cleanEvents() throws SQLException {
        final List<Map<String, String>> eventNames = this.jdbcTemplate.queryForList("SELECT event_name FROM information_schema.events WHERE event_schema=?", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final Map<String, String> row : eventNames) {
            statements.add("DROP EVENT " + ((MySQLDbSupport)this.dbSupport).quote(this.name, row.get("event_name")));
        }
        return statements;
    }
    
    private List<String> cleanRoutines() throws SQLException {
        final List<Map<String, String>> routineNames = this.jdbcTemplate.queryForList("SELECT routine_name, routine_type FROM information_schema.routines WHERE routine_schema=?", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final Map<String, String> row : routineNames) {
            final String routineName = row.get("routine_name");
            final String routineType = row.get("routine_type");
            statements.add("DROP " + routineType + " " + ((MySQLDbSupport)this.dbSupport).quote(this.name, routineName));
        }
        return statements;
    }
    
    private List<String> cleanViews() throws SQLException {
        final List<String> viewNames = this.jdbcTemplate.queryForStringList("SELECT table_name FROM information_schema.views WHERE table_schema=?", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final String viewName : viewNames) {
            statements.add("DROP VIEW " + ((MySQLDbSupport)this.dbSupport).quote(this.name, viewName));
        }
        return statements;
    }
    
    @Override
    protected Table[] doAllTables() throws SQLException {
        final List<String> tableNames = this.jdbcTemplate.queryForStringList("SELECT table_name FROM information_schema.tables WHERE table_schema=? AND table_type='BASE TABLE'", this.name);
        final Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new MySQLTable(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }
    
    @Override
    public Table getTable(final String tableName) {
        return new MySQLTable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
}
