// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport;

import java.util.List;
import java.sql.ResultSet;
import org.flywaydb.core.internal.util.jdbc.JdbcUtils;
import java.util.ArrayList;
import java.sql.SQLException;
import org.flywaydb.core.api.FlywayException;

public abstract class Schema<S extends DbSupport>
{
    protected final JdbcTemplate jdbcTemplate;
    protected final S dbSupport;
    protected final String name;
    
    public Schema(final JdbcTemplate jdbcTemplate, final S dbSupport, final String name) {
        this.jdbcTemplate = jdbcTemplate;
        this.dbSupport = dbSupport;
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean exists() {
        try {
            return this.doExists();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to check whether schema " + this + " exists", e);
        }
    }
    
    protected abstract boolean doExists() throws SQLException;
    
    public boolean empty() {
        try {
            return this.doEmpty();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to check whether schema " + this + " is empty", e);
        }
    }
    
    protected abstract boolean doEmpty() throws SQLException;
    
    public void create() {
        try {
            this.doCreate();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to create schema " + this, e);
        }
    }
    
    protected abstract void doCreate() throws SQLException;
    
    public void drop() {
        try {
            this.doDrop();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to drop schema " + this, e);
        }
    }
    
    protected abstract void doDrop() throws SQLException;
    
    public void clean() {
        try {
            this.doClean();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to clean schema " + this, e);
        }
    }
    
    protected abstract void doClean() throws SQLException;
    
    public Table[] allTables() {
        try {
            return this.doAllTables();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to retrieve all tables in schema " + this, e);
        }
    }
    
    protected abstract Table[] doAllTables() throws SQLException;
    
    public final Type[] allTypes() {
        ResultSet resultSet = null;
        try {
            resultSet = this.jdbcTemplate.getMetaData().getUDTs(null, this.name, null, null);
            final List<Type> types = new ArrayList<Type>();
            while (resultSet.next()) {
                types.add(this.getType(resultSet.getString("TYPE_NAME")));
            }
            return types.toArray(new Type[types.size()]);
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to retrieve all types in schema " + this, e);
        }
        finally {
            JdbcUtils.closeResultSet(resultSet);
        }
    }
    
    protected Type getType(final String typeName) {
        return null;
    }
    
    public abstract Table getTable(final String p0);
    
    public Function getFunction(final String functionName, final String... args) {
        throw new UnsupportedOperationException("getFunction()");
    }
    
    public final Function[] allFunctions() {
        try {
            return this.doAllFunctions();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to retrieve all functions in schema " + this, e);
        }
    }
    
    protected Function[] doAllFunctions() throws SQLException {
        return new Function[0];
    }
    
    @Override
    public String toString() {
        return this.dbSupport.quote(this.name);
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
        return this.name.equals(schema.name);
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
