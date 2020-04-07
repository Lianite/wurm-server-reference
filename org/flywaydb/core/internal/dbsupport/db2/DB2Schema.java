// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.db2;

import org.flywaydb.core.internal.util.StringUtils;
import java.util.Map;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.internal.dbsupport.Type;
import org.flywaydb.core.internal.dbsupport.Function;
import org.flywaydb.core.internal.dbsupport.Table;
import java.util.Iterator;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Schema;

public class DB2Schema extends Schema<DB2DbSupport>
{
    public DB2Schema(final JdbcTemplate jdbcTemplate, final DB2DbSupport dbSupport, final String name) {
        super(jdbcTemplate, dbSupport, name);
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM syscat.schemata WHERE schemaname=?", this.name) > 0;
    }
    
    @Override
    protected boolean doEmpty() throws SQLException {
        int objectCount = this.jdbcTemplate.queryForInt("select count(*) from syscat.tables where tabschema = ?", this.name);
        objectCount += this.jdbcTemplate.queryForInt("select count(*) from syscat.views where viewschema = ?", this.name);
        objectCount += this.jdbcTemplate.queryForInt("select count(*) from syscat.sequences where seqschema = ?", this.name);
        objectCount += this.jdbcTemplate.queryForInt("select count(*) from syscat.indexes where indschema = ?", this.name);
        objectCount += this.jdbcTemplate.queryForInt("select count(*) from syscat.procedures where procschema = ?", this.name);
        objectCount += this.jdbcTemplate.queryForInt("select count(*) from syscat.functions where funcschema = ?", this.name);
        objectCount += this.jdbcTemplate.queryForInt("select count(*) from syscat.triggers where trigschema = ?", this.name);
        return objectCount == 0;
    }
    
    @Override
    protected void doCreate() throws SQLException {
        this.jdbcTemplate.execute("CREATE SCHEMA " + ((DB2DbSupport)this.dbSupport).quote(this.name), new Object[0]);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        this.clean();
        this.jdbcTemplate.execute("DROP SCHEMA " + ((DB2DbSupport)this.dbSupport).quote(this.name) + " RESTRICT", new Object[0]);
    }
    
    @Override
    protected void doClean() throws SQLException {
        if (((DB2DbSupport)this.dbSupport).getDb2MajorVersion() >= 10) {
            for (final String dropVersioningStatement : this.generateDropVersioningStatement()) {
                this.jdbcTemplate.execute(dropVersioningStatement, new Object[0]);
            }
        }
        for (final String dropStatement : this.generateDropStatementsForViews()) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
        for (final String dropStatement : this.generateDropStatements("A", "ALIAS")) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
        for (final Table table : this.allTables()) {
            table.drop();
        }
        for (final String dropStatement : this.generateDropStatementsForSequences()) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
        for (final String dropStatement : this.generateDropStatementsForProcedures()) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
        for (final String dropStatement : this.generateDropStatementsForTriggers()) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
        for (final Function function : this.allFunctions()) {
            function.drop();
        }
        for (final Type type : this.allTypes()) {
            type.drop();
        }
    }
    
    private List<String> generateDropStatementsForProcedures() throws SQLException {
        final String dropProcGenQuery = "select PROCNAME from SYSCAT.PROCEDURES where PROCSCHEMA = '" + this.name + "'";
        return this.buildDropStatements("DROP PROCEDURE", dropProcGenQuery);
    }
    
    private List<String> generateDropStatementsForTriggers() throws SQLException {
        final String dropTrigGenQuery = "select TRIGNAME from SYSCAT.TRIGGERS where TRIGSCHEMA = '" + this.name + "'";
        return this.buildDropStatements("DROP TRIGGER", dropTrigGenQuery);
    }
    
    private List<String> generateDropStatementsForSequences() throws SQLException {
        final String dropSeqGenQuery = "select SEQNAME from SYSCAT.SEQUENCES where SEQSCHEMA = '" + this.name + "' and SEQTYPE='S'";
        return this.buildDropStatements("DROP SEQUENCE", dropSeqGenQuery);
    }
    
    private List<String> generateDropStatementsForViews() throws SQLException {
        final String dropSeqGenQuery = "select TABNAME from SYSCAT.TABLES where TABSCHEMA = '" + this.name + "' and TABNAME NOT LIKE '%_V' and TYPE='V'";
        return this.buildDropStatements("DROP VIEW", dropSeqGenQuery);
    }
    
    private List<String> generateDropStatements(final String tableType, final String objectType) throws SQLException {
        final String dropTablesGenQuery = "select TABNAME from SYSCAT.TABLES where TYPE='" + tableType + "' and TABSCHEMA = '" + this.name + "'";
        return this.buildDropStatements("DROP " + objectType, dropTablesGenQuery);
    }
    
    private List<String> buildDropStatements(final String dropPrefix, final String query) throws SQLException {
        final List<String> dropStatements = new ArrayList<String>();
        final List<String> dbObjects = this.jdbcTemplate.queryForStringList(query, new String[0]);
        for (final String dbObject : dbObjects) {
            dropStatements.add(dropPrefix + " " + ((DB2DbSupport)this.dbSupport).quote(this.name, dbObject));
        }
        return dropStatements;
    }
    
    private List<String> generateDropVersioningStatement() throws SQLException {
        final List<String> dropVersioningStatements = new ArrayList<String>();
        final Table[] tables;
        final Table[] versioningTables = tables = this.findTables("select TABNAME from SYSCAT.TABLES where TEMPORALTYPE <> 'N' and TABSCHEMA = ?", this.name);
        for (final Table table : tables) {
            dropVersioningStatements.add("ALTER TABLE " + table.toString() + " DROP VERSIONING");
        }
        return dropVersioningStatements;
    }
    
    private Table[] findTables(final String sqlQuery, final String... params) throws SQLException {
        final List<String> tableNames = this.jdbcTemplate.queryForStringList(sqlQuery, params);
        final Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new DB2Table(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }
    
    @Override
    protected Table[] doAllTables() throws SQLException {
        return this.findTables("select TABNAME from SYSCAT.TABLES where TYPE='T' and TABSCHEMA = ?", this.name);
    }
    
    @Override
    protected Function[] doAllFunctions() throws SQLException {
        final List<Map<String, String>> rows = this.jdbcTemplate.queryForList("select p.SPECIFICNAME, p.FUNCNAME, substr( xmlserialize( xmlagg( xmltext( concat( ', ', TYPENAME ) ) ) as varchar( 1024 ) ), 3 ) as PARAMS from SYSCAT.FUNCTIONS f inner join SYSCAT.FUNCPARMS p on f.SPECIFICNAME = p.SPECIFICNAME where f.ORIGIN = 'Q' and p.FUNCSCHEMA = ? and p.ROWTYPE = 'P' group by p.SPECIFICNAME, p.FUNCNAME order by p.SPECIFICNAME", this.name);
        final List<Function> functions = new ArrayList<Function>();
        for (final Map<String, String> row : rows) {
            functions.add(this.getFunction(row.get("FUNCNAME"), StringUtils.tokenizeToStringArray(row.get("PARAMS"), ",")));
        }
        return functions.toArray(new Function[functions.size()]);
    }
    
    @Override
    public Table getTable(final String tableName) {
        return new DB2Table(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
    
    @Override
    protected Type getType(final String typeName) {
        return new DB2Type(this.jdbcTemplate, this.dbSupport, this, typeName);
    }
    
    @Override
    public Function getFunction(final String functionName, final String... args) {
        return new DB2Function(this.jdbcTemplate, this.dbSupport, this, functionName, args);
    }
}
