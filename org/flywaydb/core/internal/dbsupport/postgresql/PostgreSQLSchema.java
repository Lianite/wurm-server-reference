// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.postgresql;

import org.flywaydb.core.internal.dbsupport.Type;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import java.util.Arrays;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.internal.dbsupport.Table;
import java.util.Iterator;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Schema;

public class PostgreSQLSchema extends Schema<PostgreSQLDbSupport>
{
    public PostgreSQLSchema(final JdbcTemplate jdbcTemplate, final PostgreSQLDbSupport dbSupport, final String name) {
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
        this.jdbcTemplate.execute("CREATE SCHEMA " + ((PostgreSQLDbSupport)this.dbSupport).quote(this.name), new Object[0]);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        this.jdbcTemplate.execute("DROP SCHEMA " + ((PostgreSQLDbSupport)this.dbSupport).quote(this.name) + " CASCADE", new Object[0]);
    }
    
    @Override
    protected void doClean() throws SQLException {
        final int databaseMajorVersion = this.jdbcTemplate.getMetaData().getDatabaseMajorVersion();
        final int databaseMinorVersion = this.jdbcTemplate.getMetaData().getDatabaseMinorVersion();
        if (databaseMajorVersion > 9 || (databaseMajorVersion == 9 && databaseMinorVersion >= 3)) {
            for (final String statement : this.generateDropStatementsForMaterializedViews()) {
                this.jdbcTemplate.execute(statement, new Object[0]);
            }
        }
        for (final String statement : this.generateDropStatementsForViews()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final Table table : this.allTables()) {
            table.drop();
        }
        for (final String statement : this.generateDropStatementsForSequences()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForBaseTypes(true)) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForAggregates()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForRoutines()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForEnums()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForDomains()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForBaseTypes(false)) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
    }
    
    private List<String> generateDropStatementsForSequences() throws SQLException {
        final List<String> sequenceNames = this.jdbcTemplate.queryForStringList("SELECT sequence_name FROM information_schema.sequences WHERE sequence_schema=?", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final String sequenceName : sequenceNames) {
            statements.add("DROP SEQUENCE IF EXISTS " + ((PostgreSQLDbSupport)this.dbSupport).quote(this.name, sequenceName));
        }
        return statements;
    }
    
    private List<String> generateDropStatementsForBaseTypes(final boolean recreate) throws SQLException {
        final List<Map<String, String>> rows = this.jdbcTemplate.queryForList("select typname, typcategory from pg_catalog.pg_type t where (t.typrelid = 0 OR (SELECT c.relkind = 'c' FROM pg_catalog.pg_class c WHERE c.oid = t.typrelid)) and NOT EXISTS(SELECT 1 FROM pg_catalog.pg_type el WHERE el.oid = t.typelem AND el.typarray = t.oid) and t.typnamespace in (select oid from pg_catalog.pg_namespace where nspname = ?)", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final Map<String, String> row : rows) {
            statements.add("DROP TYPE IF EXISTS " + ((PostgreSQLDbSupport)this.dbSupport).quote(this.name, row.get("typname")) + " CASCADE");
        }
        if (recreate) {
            for (final Map<String, String> row : rows) {
                if (Arrays.asList("P", "U").contains(row.get("typcategory"))) {
                    statements.add("CREATE TYPE " + ((PostgreSQLDbSupport)this.dbSupport).quote(this.name, row.get("typname")));
                }
            }
        }
        return statements;
    }
    
    private List<String> generateDropStatementsForAggregates() throws SQLException {
        final List<Map<String, String>> rows = this.jdbcTemplate.queryForList("SELECT proname, oidvectortypes(proargtypes) AS args FROM pg_proc INNER JOIN pg_namespace ns ON (pg_proc.pronamespace = ns.oid) WHERE pg_proc.proisagg = true AND ns.nspname = ?", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final Map<String, String> row : rows) {
            statements.add("DROP AGGREGATE IF EXISTS " + ((PostgreSQLDbSupport)this.dbSupport).quote(this.name, row.get("proname")) + "(" + row.get("args") + ") CASCADE");
        }
        return statements;
    }
    
    private List<String> generateDropStatementsForRoutines() throws SQLException {
        final List<Map<String, String>> rows = this.jdbcTemplate.queryForList("SELECT proname, oidvectortypes(proargtypes) AS args FROM pg_proc INNER JOIN pg_namespace ns ON (pg_proc.pronamespace = ns.oid) LEFT JOIN pg_depend dep ON dep.objid = pg_proc.oid AND dep.deptype = 'e' WHERE pg_proc.proisagg = false AND ns.nspname = ? AND dep.objid IS NULL", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final Map<String, String> row : rows) {
            statements.add("DROP FUNCTION IF EXISTS " + ((PostgreSQLDbSupport)this.dbSupport).quote(this.name, row.get("proname")) + "(" + row.get("args") + ") CASCADE");
        }
        return statements;
    }
    
    private List<String> generateDropStatementsForEnums() throws SQLException {
        final List<String> enumNames = this.jdbcTemplate.queryForStringList("SELECT t.typname FROM pg_catalog.pg_type t INNER JOIN pg_catalog.pg_namespace n ON n.oid = t.typnamespace WHERE n.nspname = ? and t.typtype = 'e'", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final String enumName : enumNames) {
            statements.add("DROP TYPE " + ((PostgreSQLDbSupport)this.dbSupport).quote(this.name, enumName));
        }
        return statements;
    }
    
    private List<String> generateDropStatementsForDomains() throws SQLException {
        final List<String> domainNames = this.jdbcTemplate.queryForStringList("SELECT domain_name FROM information_schema.domains WHERE domain_schema=?", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final String domainName : domainNames) {
            statements.add("DROP DOMAIN " + ((PostgreSQLDbSupport)this.dbSupport).quote(this.name, domainName));
        }
        return statements;
    }
    
    private List<String> generateDropStatementsForMaterializedViews() throws SQLException {
        final List<String> viewNames = this.jdbcTemplate.queryForStringList("SELECT relname FROM pg_catalog.pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relkind = 'm' AND n.nspname = ?", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final String domainName : viewNames) {
            statements.add("DROP MATERIALIZED VIEW IF EXISTS " + ((PostgreSQLDbSupport)this.dbSupport).quote(this.name, domainName) + " CASCADE");
        }
        return statements;
    }
    
    private List<String> generateDropStatementsForViews() throws SQLException {
        final List<String> viewNames = this.jdbcTemplate.queryForStringList("SELECT relname FROM pg_catalog.pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace LEFT JOIN pg_depend dep ON dep.objid = c.oid AND dep.deptype = 'e' WHERE c.relkind = 'v' AND  n.nspname = ? AND dep.objid IS NULL", this.name);
        final List<String> statements = new ArrayList<String>();
        for (final String domainName : viewNames) {
            statements.add("DROP VIEW IF EXISTS " + ((PostgreSQLDbSupport)this.dbSupport).quote(this.name, domainName) + " CASCADE");
        }
        return statements;
    }
    
    @Override
    protected Table[] doAllTables() throws SQLException {
        final List<String> tableNames = this.jdbcTemplate.queryForStringList("SELECT t.table_name FROM information_schema.tables t WHERE table_schema=? AND table_type='BASE TABLE' AND NOT (SELECT EXISTS (SELECT inhrelid FROM pg_catalog.pg_inherits WHERE inhrelid = (quote_ident(t.table_schema)||'.'||quote_ident(t.table_name))::regclass::oid))", this.name);
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
    
    @Override
    protected Type getType(final String typeName) {
        return new PostgreSQLType(this.jdbcTemplate, this.dbSupport, this, typeName);
    }
}
