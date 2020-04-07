// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.h2;

import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.util.StringUtils;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.flywaydb.core.internal.dbsupport.Table;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.dbsupport.Schema;

public class H2Schema extends Schema<H2DbSupport>
{
    private static final Log LOG;
    
    public H2Schema(final JdbcTemplate jdbcTemplate, final H2DbSupport dbSupport, final String name) {
        super(jdbcTemplate, dbSupport, name);
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM INFORMATION_SCHEMA.schemata WHERE schema_name=?", this.name) > 0;
    }
    
    @Override
    protected boolean doEmpty() throws SQLException {
        return this.allTables().length == 0;
    }
    
    @Override
    protected void doCreate() throws SQLException {
        this.jdbcTemplate.execute("CREATE SCHEMA " + ((H2DbSupport)this.dbSupport).quote(this.name), new Object[0]);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        this.jdbcTemplate.execute("DROP SCHEMA " + ((H2DbSupport)this.dbSupport).quote(this.name), new Object[0]);
    }
    
    @Override
    protected void doClean() throws SQLException {
        for (final Table table : this.allTables()) {
            table.drop();
        }
        final List<String> sequenceNames = this.listObjectNames("SEQUENCE", "IS_GENERATED = false");
        for (final String statement : this.generateDropStatements("SEQUENCE", sequenceNames, "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        final List<String> constantNames = this.listObjectNames("CONSTANT", "");
        for (final String statement2 : this.generateDropStatements("CONSTANT", constantNames, "")) {
            this.jdbcTemplate.execute(statement2, new Object[0]);
        }
        final List<String> domainNames = this.listObjectNames("DOMAIN", "");
        if (!domainNames.isEmpty()) {
            if (this.name.equals(((H2DbSupport)this.dbSupport).getCurrentSchemaName())) {
                for (final String statement3 : this.generateDropStatementsForCurrentSchema("DOMAIN", domainNames, "")) {
                    this.jdbcTemplate.execute(statement3, new Object[0]);
                }
            }
            else {
                H2Schema.LOG.error("Unable to drop DOMAIN objects in schema " + ((H2DbSupport)this.dbSupport).quote(this.name) + " due to H2 bug! (More info: http://code.google.com/p/h2database/issues/detail?id=306)");
            }
        }
    }
    
    private List<String> generateDropStatements(final String objectType, final List<String> objectNames, final String dropStatementSuffix) {
        final List<String> statements = new ArrayList<String>();
        for (final String objectName : objectNames) {
            final String dropStatement = "DROP " + objectType + ((H2DbSupport)this.dbSupport).quote(this.name, objectName) + " " + dropStatementSuffix;
            statements.add(dropStatement);
        }
        return statements;
    }
    
    private List<String> generateDropStatementsForCurrentSchema(final String objectType, final List<String> objectNames, final String dropStatementSuffix) {
        final List<String> statements = new ArrayList<String>();
        for (final String objectName : objectNames) {
            final String dropStatement = "DROP " + objectType + ((H2DbSupport)this.dbSupport).quote(objectName) + " " + dropStatementSuffix;
            statements.add(dropStatement);
        }
        return statements;
    }
    
    @Override
    protected Table[] doAllTables() throws SQLException {
        final List<String> tableNames = this.listObjectNames("TABLE", "TABLE_TYPE = 'TABLE'");
        final Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new H2Table(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }
    
    private List<String> listObjectNames(final String objectType, final String querySuffix) throws SQLException {
        String query = "SELECT " + objectType + "_NAME FROM INFORMATION_SCHEMA." + objectType + "s WHERE " + objectType + "_schema = ?";
        if (StringUtils.hasLength(querySuffix)) {
            query = query + " AND " + querySuffix;
        }
        return this.jdbcTemplate.queryForStringList(query, this.name);
    }
    
    @Override
    public Table getTable(final String tableName) {
        return new H2Table(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
    
    static {
        LOG = LogFactory.getLog(H2Schema.class);
    }
}
