// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.derby;

import org.flywaydb.core.internal.util.StringUtils;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import java.util.Map;
import java.util.ArrayList;
import org.flywaydb.core.internal.dbsupport.Table;
import java.util.Iterator;
import java.util.List;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Schema;

public class DerbySchema extends Schema<DerbyDbSupport>
{
    public DerbySchema(final JdbcTemplate jdbcTemplate, final DerbyDbSupport dbSupport, final String name) {
        super(jdbcTemplate, dbSupport, name);
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT (*) FROM sys.sysschemas WHERE schemaname=?", this.name) > 0;
    }
    
    @Override
    protected boolean doEmpty() throws SQLException {
        return this.allTables().length == 0;
    }
    
    @Override
    protected void doCreate() throws SQLException {
        this.jdbcTemplate.execute("CREATE SCHEMA " + ((DerbyDbSupport)this.dbSupport).quote(this.name), new Object[0]);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        this.clean();
        this.jdbcTemplate.execute("DROP SCHEMA " + ((DerbyDbSupport)this.dbSupport).quote(this.name) + " RESTRICT", new Object[0]);
    }
    
    @Override
    protected void doClean() throws SQLException {
        final List<String> triggerNames = this.listObjectNames("TRIGGER", "");
        for (final String statement : this.generateDropStatements("TRIGGER", triggerNames, "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForConstraints()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        final List<String> viewNames = this.listObjectNames("TABLE", "TABLETYPE='V'");
        for (final String statement2 : this.generateDropStatements("VIEW", viewNames, "")) {
            this.jdbcTemplate.execute(statement2, new Object[0]);
        }
        for (final Table table : this.allTables()) {
            table.drop();
        }
        final List<String> sequenceNames = this.listObjectNames("SEQUENCE", "");
        for (final String statement3 : this.generateDropStatements("SEQUENCE", sequenceNames, "RESTRICT")) {
            this.jdbcTemplate.execute(statement3, new Object[0]);
        }
    }
    
    private List<String> generateDropStatementsForConstraints() throws SQLException {
        final List<Map<String, String>> results = this.jdbcTemplate.queryForList("SELECT c.constraintname, t.tablename FROM sys.sysconstraints c INNER JOIN sys.systables t ON c.tableid = t.tableid INNER JOIN sys.sysschemas s ON c.schemaid = s.schemaid WHERE c.type = 'F' AND s.schemaname = ?", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final Map<String, String> result : results) {
            final String dropStatement = "ALTER TABLE " + ((DerbyDbSupport)this.dbSupport).quote(this.name, result.get("TABLENAME")) + " DROP CONSTRAINT " + ((DerbyDbSupport)this.dbSupport).quote(result.get("CONSTRAINTNAME"));
            statements.add(dropStatement);
        }
        return statements;
    }
    
    private List<String> generateDropStatements(final String objectType, final List<String> objectNames, final String dropStatementSuffix) {
        final List<String> statements = new ArrayList<String>();
        for (final String objectName : objectNames) {
            final String dropStatement = "DROP " + objectType + " " + ((DerbyDbSupport)this.dbSupport).quote(this.name, objectName) + " " + dropStatementSuffix;
            statements.add(dropStatement);
        }
        return statements;
    }
    
    @Override
    protected Table[] doAllTables() throws SQLException {
        final List<String> tableNames = this.listObjectNames("TABLE", "TABLETYPE='T'");
        final Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new DerbyTable(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }
    
    private List<String> listObjectNames(final String objectType, final String querySuffix) throws SQLException {
        String query = "SELECT " + objectType + "name FROM sys.sys" + objectType + "s WHERE schemaid in (SELECT schemaid FROM sys.sysschemas where schemaname = ?)";
        if (StringUtils.hasLength(querySuffix)) {
            query = query + " AND " + querySuffix;
        }
        return this.jdbcTemplate.queryForStringList(query, this.name);
    }
    
    @Override
    public Table getTable(final String tableName) {
        return new DerbyTable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
}
