// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.solid;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.Table;
import java.util.Iterator;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Schema;

public class SolidSchema extends Schema<SolidDbSupport>
{
    public SolidSchema(final JdbcTemplate jdbcTemplate, final SolidDbSupport dbSupport, final String name) {
        super(jdbcTemplate, dbSupport, name.toUpperCase());
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM _SYSTEM.SYS_SCHEMAS WHERE NAME = ?", this.name) > 0;
    }
    
    @Override
    protected boolean doEmpty() throws SQLException {
        int count = this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM _SYSTEM.SYS_TABLES WHERE TABLE_SCHEMA = ?", this.name);
        if (count > 0) {
            return false;
        }
        count = this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM _SYSTEM.SYS_TRIGGERS WHERE TRIGGER_SCHEMA = ?", this.name);
        if (count > 0) {
            return false;
        }
        count = this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM _SYSTEM.SYS_PROCEDURES WHERE PROCEDURE_SCHEMA = ?", this.name);
        if (count > 0) {
            return false;
        }
        count = this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM _SYSTEM.SYS_FORKEYS WHERE KEY_SCHEMA = ?", this.name);
        return count <= 0;
    }
    
    @Override
    protected void doCreate() throws SQLException {
        this.jdbcTemplate.execute("CREATE SCHEMA " + this.name, new Object[0]);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        this.clean();
        this.jdbcTemplate.execute("DROP SCHEMA " + this.name, new Object[0]);
    }
    
    @Override
    protected void doClean() throws SQLException {
        for (final String statement : this.dropTriggers()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.dropProcedures()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.dropConstraints()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.dropViews()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final Table table : this.allTables()) {
            table.drop();
        }
    }
    
    @Override
    protected Table[] doAllTables() throws SQLException {
        final List<String> tableNames = this.jdbcTemplate.queryForStringList("SELECT TABLE_NAME FROM _SYSTEM.SYS_TABLES WHERE TABLE_SCHEMA = ? AND TABLE_TYPE = 'BASE TABLE'", this.name);
        final Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new SolidTable(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }
    
    @Override
    public Table getTable(final String tableName) {
        return new SolidTable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
    
    private Iterable<String> dropTriggers() throws SQLException {
        final List<String> statements = new ArrayList<String>();
        for (final Map<String, String> item : this.jdbcTemplate.queryForList("SELECT TRIGGER_NAME FROM _SYSTEM.SYS_TRIGGERS WHERE TRIGGER_SCHEMA = ?", this.name)) {
            statements.add("DROP TRIGGER " + ((SolidDbSupport)this.dbSupport).quote(this.name, item.get("TRIGGER_NAME")));
        }
        return statements;
    }
    
    private Iterable<String> dropProcedures() throws SQLException {
        final List<String> statements = new ArrayList<String>();
        for (final Map<String, String> item : this.jdbcTemplate.queryForList("SELECT PROCEDURE_NAME FROM _SYSTEM.SYS_PROCEDURES WHERE PROCEDURE_SCHEMA = ?", this.name)) {
            statements.add("DROP PROCEDURE " + ((SolidDbSupport)this.dbSupport).quote(this.name, item.get("PROCEDURE_NAME")));
        }
        return statements;
    }
    
    private Iterable<String> dropConstraints() throws SQLException {
        final List<String> statements = new ArrayList<String>();
        for (final Map<String, String> item : this.jdbcTemplate.queryForList("SELECT TABLE_NAME, KEY_NAME FROM _SYSTEM.SYS_FORKEYS, _SYSTEM.SYS_TABLES WHERE SYS_FORKEYS.KEY_SCHEMA = ? AND SYS_FORKEYS.CREATE_REL_ID = SYS_FORKEYS.REF_REL_ID AND SYS_FORKEYS.CREATE_REL_ID = SYS_TABLES.ID", this.name)) {
            statements.add("ALTER TABLE " + ((SolidDbSupport)this.dbSupport).quote(this.name, item.get("TABLE_NAME")) + " DROP CONSTRAINT " + ((SolidDbSupport)this.dbSupport).quote(item.get("KEY_NAME")));
        }
        return statements;
    }
    
    private Iterable<String> dropViews() throws SQLException {
        final List<String> statements = new ArrayList<String>();
        for (final Map<String, String> item : this.jdbcTemplate.queryForList("SELECT TABLE_NAME FROM _SYSTEM.SYS_TABLES WHERE TABLE_TYPE = 'VIEW' AND TABLE_SCHEMA = ?", this.name)) {
            statements.add("DROP VIEW " + ((SolidDbSupport)this.dbSupport).quote(this.name, item.get("TABLE_NAME")));
        }
        return statements;
    }
    
    private void commitWork() throws SQLException {
        this.jdbcTemplate.executeStatement("COMMIT WORK");
    }
}
