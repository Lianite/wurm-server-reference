// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.saphana;

import org.flywaydb.core.internal.dbsupport.DbSupport;
import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.internal.dbsupport.Table;
import java.util.Iterator;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Schema;

public class SapHanaSchema extends Schema<SapHanaDbSupport>
{
    public SapHanaSchema(final JdbcTemplate jdbcTemplate, final SapHanaDbSupport dbSupport, final String name) {
        super(jdbcTemplate, dbSupport, name);
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM SYS.SCHEMAS WHERE SCHEMA_NAME=?", this.name) > 0;
    }
    
    @Override
    protected boolean doEmpty() throws SQLException {
        int objectCount = this.jdbcTemplate.queryForInt("select count(*) from sys.tables where schema_name = ?", this.name);
        objectCount += this.jdbcTemplate.queryForInt("select count(*) from sys.views where schema_name = ?", this.name);
        objectCount += this.jdbcTemplate.queryForInt("select count(*) from sys.sequences where schema_name = ?", this.name);
        objectCount += this.jdbcTemplate.queryForInt("select count(*) from sys.synonyms where schema_name = ?", this.name);
        return objectCount == 0;
    }
    
    @Override
    protected void doCreate() throws SQLException {
        this.jdbcTemplate.execute("CREATE SCHEMA " + ((SapHanaDbSupport)this.dbSupport).quote(this.name), new Object[0]);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        this.clean();
        this.jdbcTemplate.execute("DROP SCHEMA " + ((SapHanaDbSupport)this.dbSupport).quote(this.name) + " RESTRICT", new Object[0]);
    }
    
    @Override
    protected void doClean() throws SQLException {
        for (final String dropStatement : this.generateDropStatements("SYNONYM")) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
        for (final String dropStatement : this.generateDropStatements("VIEW")) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
        for (final Table table : this.allTables()) {
            table.drop();
        }
        for (final String dropStatement : this.generateDropStatements("SEQUENCE")) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
    }
    
    private List<String> generateDropStatements(final String objectType) throws SQLException {
        final List<String> dropStatements = new ArrayList<String>();
        final List<String> dbObjects = this.jdbcTemplate.queryForStringList("select " + objectType + "_NAME from SYS." + objectType + "S where SCHEMA_NAME = '" + this.name + "'", new String[0]);
        for (final String dbObject : dbObjects) {
            dropStatements.add("DROP " + objectType + " " + ((SapHanaDbSupport)this.dbSupport).quote(this.name, dbObject) + " CASCADE");
        }
        return dropStatements;
    }
    
    @Override
    protected Table[] doAllTables() throws SQLException {
        final List<String> tableNames = this.jdbcTemplate.queryForStringList("select TABLE_NAME from SYS.TABLES where SCHEMA_NAME = ?", this.name);
        final Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new SapHanaTable(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }
    
    @Override
    public Table getTable(final String tableName) {
        return new SapHanaTable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
}
