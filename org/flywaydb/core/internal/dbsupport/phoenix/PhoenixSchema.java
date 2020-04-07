// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.phoenix;

import org.flywaydb.core.internal.util.logging.LogFactory;
import java.util.Set;
import org.flywaydb.core.internal.util.jdbc.RowMapper;
import java.util.Collection;
import java.util.HashSet;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.Table;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.dbsupport.Schema;

public class PhoenixSchema extends Schema<PhoenixDbSupport>
{
    private static final Log LOG;
    
    public PhoenixSchema(final JdbcTemplate jdbcTemplate, final PhoenixDbSupport dbSupport, final String name) {
        super(jdbcTemplate, dbSupport, name);
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        final ResultSet rs = this.jdbcTemplate.getMetaData().getSchemas();
        while (rs.next()) {
            final String schemaName = rs.getString("TABLE_SCHEM");
            if (schemaName == null) {
                if (this.name == null) {
                    return true;
                }
                continue;
            }
            else {
                if (this.name != null && schemaName.equals(this.name)) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    @Override
    protected boolean doEmpty() throws SQLException {
        return this.allTables().length == 0;
    }
    
    @Override
    protected void doCreate() throws SQLException {
        PhoenixSchema.LOG.info("Phoenix does not support creating schemas. Schema not created: " + this.name);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        PhoenixSchema.LOG.info("Phoenix does not support dropping schemas directly. Running clean of objects instead");
        this.doClean();
    }
    
    @Override
    protected void doClean() throws SQLException {
        final List<String> sequenceNames = this.listObjectsOfType("sequence");
        for (final String statement : this.generateDropStatements("SEQUENCE", sequenceNames, "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        final List<String> viewNames = this.listObjectsOfType("view");
        for (final String statement2 : this.generateDropStatements("VIEW", viewNames, "")) {
            this.jdbcTemplate.execute(statement2, new Object[0]);
        }
        final List<String> indexPairs = this.listObjectsOfType("index");
        final List<String> indexNames = new ArrayList<String>();
        final List<String> indexTables = new ArrayList<String>();
        for (final String indexPair : indexPairs) {
            final String[] splits = indexPair.split(",");
            indexNames.add(splits[0]);
            indexTables.add("ON " + ((PhoenixDbSupport)this.dbSupport).quote(this.name, splits[1]));
        }
        final List<String> statements = this.generateDropIndexStatements(indexNames, indexTables);
        for (final String statement3 : statements) {
            this.jdbcTemplate.execute(statement3, new Object[0]);
        }
        final List<String> tableNames = this.listObjectsOfType("table");
        for (final String statement4 : this.generateDropStatements("TABLE", tableNames, "")) {
            this.jdbcTemplate.execute(statement4, new Object[0]);
        }
    }
    
    private List<String> generateDropStatements(final String objectType, final List<String> objectNames, final String dropStatementSuffix) {
        final List<String> statements = new ArrayList<String>();
        for (final String objectName : objectNames) {
            final String dropStatement = "DROP " + objectType + " " + ((PhoenixDbSupport)this.dbSupport).quote(this.name, objectName) + " " + dropStatementSuffix;
            statements.add(dropStatement);
        }
        return statements;
    }
    
    private List<String> generateDropIndexStatements(final List<String> objectNames, final List<String> dropStatementSuffixes) {
        final List<String> statements = new ArrayList<String>();
        for (int i = 0; i < objectNames.size(); ++i) {
            final String dropStatement = "DROP INDEX " + ((PhoenixDbSupport)this.dbSupport).quote(objectNames.get(i)) + " " + dropStatementSuffixes.get(i);
            statements.add(dropStatement);
        }
        return statements;
    }
    
    @Override
    protected Table[] doAllTables() throws SQLException {
        final List<String> tableNames = this.listObjectsOfType("table");
        final Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new PhoenixTable(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }
    
    protected List<String> listObjectsOfType(final String type) throws SQLException {
        List<String> retVal = new ArrayList<String>();
        final String finalName = (this.name == null) ? "" : this.name;
        if (type.equalsIgnoreCase("view")) {
            final ResultSet rs = this.jdbcTemplate.getConnection().getMetaData().getTables(null, finalName, null, new String[] { "VIEW" });
            while (rs.next()) {
                final String viewName = rs.getString("TABLE_NAME");
                if (viewName != null) {
                    retVal.add(viewName);
                }
            }
        }
        else if (type.equalsIgnoreCase("table")) {
            final ResultSet rs = this.jdbcTemplate.getMetaData().getTables(null, finalName, null, new String[] { "TABLE" });
            while (rs.next()) {
                final String tableName = rs.getString("TABLE_NAME");
                final Set<String> tables = new HashSet<String>();
                if (tableName != null) {
                    tables.add(tableName);
                }
                retVal.addAll(tables);
            }
        }
        else if (type.equalsIgnoreCase("sequence")) {
            if (this.name == null) {
                final String query = "SELECT SEQUENCE_NAME FROM SYSTEM.\"SEQUENCE\" WHERE SEQUENCE_SCHEMA IS NULL";
                return this.jdbcTemplate.queryForStringList(query, new String[0]);
            }
            final String query = "SELECT SEQUENCE_NAME FROM SYSTEM.\"SEQUENCE\" WHERE SEQUENCE_SCHEMA = ?";
            return this.jdbcTemplate.queryForStringList(query, this.name);
        }
        else if (type.equalsIgnoreCase("index")) {
            String query = "SELECT TABLE_NAME, DATA_TABLE_NAME FROM SYSTEM.CATALOG WHERE TABLE_SCHEM";
            if (this.name == null) {
                query += " IS NULL";
            }
            else {
                query += " = ?";
            }
            query += " AND TABLE_TYPE = 'i'";
            final String finalQuery = query.replaceFirst("\\?", "'" + this.name + "'");
            retVal = this.jdbcTemplate.query(finalQuery, (RowMapper<String>)new RowMapper<String>() {
                @Override
                public String mapRow(final ResultSet rs) throws SQLException {
                    return rs.getString("TABLE_NAME") + "," + rs.getString("DATA_TABLE_NAME");
                }
            });
        }
        return retVal;
    }
    
    @Override
    public Table getTable(final String tableName) {
        return new PhoenixTable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Schema schema = (Schema)o;
        if (this.name == null) {
            return this.name == schema.getName();
        }
        return this.name.equals(schema.getName());
    }
    
    static {
        LOG = LogFactory.getLog(PhoenixSchema.class);
    }
}
