// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.db2zos;

import org.flywaydb.core.internal.dbsupport.Function;
import org.flywaydb.core.internal.dbsupport.Type;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.internal.dbsupport.Table;
import java.util.Iterator;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Schema;

public class DB2zosSchema extends Schema<DB2zosDbSupport>
{
    public DB2zosSchema(final JdbcTemplate jdbcTemplate, final DB2zosDbSupport dbSupport, final String name) {
        super(jdbcTemplate, dbSupport, name);
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM sysibm.sysdatabase WHERE name=?", this.name) > 0;
    }
    
    @Override
    protected boolean doEmpty() throws SQLException {
        int objectCount = this.jdbcTemplate.queryForInt("select count(*) from sysibm.systables where dbname = ?", this.name);
        objectCount += this.jdbcTemplate.queryForInt("select count(*) from sysibm.systables where creator = ?", this.name);
        objectCount += this.jdbcTemplate.queryForInt("select count(*) from sysibm.syssequences where schema = ?", this.name);
        objectCount += this.jdbcTemplate.queryForInt("select count(*) from sysibm.sysindexes where dbname = ?", this.name);
        objectCount += this.jdbcTemplate.queryForInt("select count(*) from sysibm.sysroutines where schema = ?", this.name);
        return objectCount == 0;
    }
    
    @Override
    protected void doCreate() throws SQLException {
        throw new UnsupportedOperationException("Create Schema - is not supported in db2 on zOS");
    }
    
    @Override
    protected void doDrop() throws SQLException {
        throw new UnsupportedOperationException("Drop Schema - is not supported in db2 on zOS");
    }
    
    @Override
    protected void doClean() throws SQLException {
        for (final String dropStatement : this.generateDropStatements(this.name, "V", "VIEW")) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
        for (final String dropStatement : this.generateDropStatements(this.name, "A", "ALIAS")) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
        for (final Table table : this.allTables()) {
            table.drop();
        }
        for (final String dropStatement : this.generateDropStatementsForTestTable(this.name, "T", "TABLE")) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
        for (final String dropStatement : this.generateDropStatementsForTablespace(this.name)) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
        for (final String dropStatement : this.generateDropStatementsForSequences(this.name)) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
        for (final String dropStatement : this.generateDropStatementsForProcedures(this.name)) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
        for (final String dropStatement : this.generateDropStatementsForFunctions(this.name)) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
        for (final String dropStatement : this.generateDropStatementsForUserTypes(this.name)) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
    }
    
    private List<String> generateDropStatementsForProcedures(final String schema) throws SQLException {
        final String dropProcGenQuery = "select rtrim(NAME) from SYSIBM.SYSROUTINES where CAST_FUNCTION = 'N'  and ROUTINETYPE  = 'P' and SCHEMA = '" + schema + "'";
        return this.buildDropStatements("DROP PROCEDURE", dropProcGenQuery, schema);
    }
    
    private List<String> generateDropStatementsForFunctions(final String schema) throws SQLException {
        final String dropProcGenQuery = "select rtrim(NAME) from SYSIBM.SYSROUTINES where CAST_FUNCTION = 'N'  and ROUTINETYPE  = 'F' and SCHEMA = '" + schema + "'";
        return this.buildDropStatements("DROP FUNCTION", dropProcGenQuery, schema);
    }
    
    private List<String> generateDropStatementsForSequences(final String schema) throws SQLException {
        final String dropSeqGenQuery = "select rtrim(NAME) from SYSIBM.SYSSEQUENCES where SCHEMA = '" + schema + "' and SEQTYPE='S'";
        return this.buildDropStatements("DROP SEQUENCE", dropSeqGenQuery, schema);
    }
    
    private List<String> generateDropStatementsForTablespace(final String schema) throws SQLException {
        final String dropTablespaceGenQuery = "select rtrim(NAME) FROM SYSIBM.SYSTABLESPACE where DBNAME = '" + schema + "'";
        return this.buildDropStatements("DROP TABLESPACE", dropTablespaceGenQuery, schema);
    }
    
    private List<String> generateDropStatementsForTestTable(final String schema, final String tableType, final String objectType) throws SQLException {
        final String dropTablesGenQuery = "select rtrim(NAME) from SYSIBM.SYSTABLES where TYPE='" + tableType + "' and creator = '" + schema + "'";
        return this.buildDropStatements("DROP " + objectType, dropTablesGenQuery, schema);
    }
    
    private List<String> generateDropStatementsForUserTypes(final String schema) throws SQLException {
        final String dropTablespaceGenQuery = "select rtrim(NAME) from SYSIBM.SYSDATATYPES where schema = '" + schema + "'";
        return this.buildDropStatements("DROP TYPE", dropTablespaceGenQuery, schema);
    }
    
    private List<String> generateDropStatements(final String schema, final String tableType, final String objectType) throws SQLException {
        final String dropTablesGenQuery = "select rtrim(NAME) from SYSIBM.SYSTABLES where TYPE='" + tableType + "' and (DBNAME = '" + schema + "' OR creator = '" + schema + "')";
        return this.buildDropStatements("DROP " + objectType, dropTablesGenQuery, schema);
    }
    
    private List<String> buildDropStatements(final String dropPrefix, final String query, final String schema) throws SQLException {
        final List<String> dropStatements = new ArrayList<String>();
        final List<String> dbObjects = this.jdbcTemplate.queryForStringList(query, new String[0]);
        for (final String dbObject : dbObjects) {
            dropStatements.add(dropPrefix + " " + ((DB2zosDbSupport)this.dbSupport).quote(schema, dbObject));
        }
        return dropStatements;
    }
    
    @Override
    protected Table[] doAllTables() throws SQLException {
        final List<String> tableNames = this.jdbcTemplate.queryForStringList("select rtrim(NAME) from SYSIBM.SYSTABLES where TYPE='T' and DBNAME = ?", this.name);
        final Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new DB2zosTable(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }
    
    @Override
    public Table getTable(final String tableName) {
        return new DB2zosTable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
    
    @Override
    protected Type getType(final String typeName) {
        return new DB2zosType(this.jdbcTemplate, this.dbSupport, this, typeName);
    }
    
    @Override
    public Function getFunction(final String functionName, final String... args) {
        return new DB2zosFunction(this.jdbcTemplate, this.dbSupport, this, functionName, args);
    }
}
