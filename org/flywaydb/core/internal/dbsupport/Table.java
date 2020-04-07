// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport;

import org.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.ResultSet;
import org.flywaydb.core.internal.util.jdbc.JdbcUtils;
import java.sql.SQLException;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.util.logging.Log;

public abstract class Table extends SchemaObject
{
    private static final Log LOG;
    
    public Table(final JdbcTemplate jdbcTemplate, final DbSupport dbSupport, final Schema schema, final String name) {
        super(jdbcTemplate, dbSupport, schema, name);
    }
    
    public boolean exists() {
        try {
            return this.doExists();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to check whether table " + this + " exists", e);
        }
    }
    
    protected abstract boolean doExists() throws SQLException;
    
    protected boolean exists(final Schema catalog, final Schema schema, final String table, final String... tableTypes) throws SQLException {
        String[] types = tableTypes;
        if (types.length == 0) {
            types = null;
        }
        ResultSet resultSet = null;
        boolean found;
        try {
            resultSet = this.jdbcTemplate.getMetaData().getTables((catalog == null) ? null : catalog.getName(), (schema == null) ? null : schema.getName(), table, types);
            found = resultSet.next();
        }
        finally {
            JdbcUtils.closeResultSet(resultSet);
        }
        return found;
    }
    
    public boolean hasPrimaryKey() {
        ResultSet resultSet = null;
        boolean found;
        try {
            if (this.dbSupport.catalogIsSchema()) {
                resultSet = this.jdbcTemplate.getMetaData().getPrimaryKeys(this.schema.getName(), null, this.name);
            }
            else {
                resultSet = this.jdbcTemplate.getMetaData().getPrimaryKeys(null, this.schema.getName(), this.name);
            }
            found = resultSet.next();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to check whether table " + this + " has a primary key", e);
        }
        finally {
            JdbcUtils.closeResultSet(resultSet);
        }
        return found;
    }
    
    public boolean hasColumn(final String column) {
        ResultSet resultSet = null;
        boolean found;
        try {
            if (this.dbSupport.catalogIsSchema()) {
                resultSet = this.jdbcTemplate.getMetaData().getColumns(this.schema.getName(), null, this.name, column);
            }
            else {
                resultSet = this.jdbcTemplate.getMetaData().getColumns(null, this.schema.getName(), this.name, column);
            }
            found = resultSet.next();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to check whether table " + this + " has a column named " + column, e);
        }
        finally {
            JdbcUtils.closeResultSet(resultSet);
        }
        return found;
    }
    
    public int getColumnSize(final String column) {
        ResultSet resultSet = null;
        int columnSize;
        try {
            if (this.dbSupport.catalogIsSchema()) {
                resultSet = this.jdbcTemplate.getMetaData().getColumns(this.schema.getName(), null, this.name, column);
            }
            else {
                resultSet = this.jdbcTemplate.getMetaData().getColumns(null, this.schema.getName(), this.name, column);
            }
            resultSet.next();
            columnSize = resultSet.getInt("COLUMN_SIZE");
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to check the size of column " + column + " in table " + this, e);
        }
        finally {
            JdbcUtils.closeResultSet(resultSet);
        }
        return columnSize;
    }
    
    public void lock() {
        try {
            Table.LOG.debug("Locking table " + this + "...");
            this.doLock();
            Table.LOG.debug("Lock acquired for table " + this);
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to lock table " + this, e);
        }
    }
    
    protected abstract void doLock() throws SQLException;
    
    static {
        LOG = LogFactory.getLog(Table.class);
    }
}
