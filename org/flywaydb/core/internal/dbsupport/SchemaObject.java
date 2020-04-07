// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport;

import java.sql.SQLException;
import org.flywaydb.core.api.FlywayException;

public abstract class SchemaObject
{
    protected final JdbcTemplate jdbcTemplate;
    protected final DbSupport dbSupport;
    protected final Schema schema;
    protected final String name;
    
    public SchemaObject(final JdbcTemplate jdbcTemplate, final DbSupport dbSupport, final Schema schema, final String name) {
        this.name = name;
        this.jdbcTemplate = jdbcTemplate;
        this.dbSupport = dbSupport;
        this.schema = schema;
    }
    
    public final Schema getSchema() {
        return this.schema;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final void drop() {
        try {
            this.doDrop();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to drop " + this, e);
        }
    }
    
    protected abstract void doDrop() throws SQLException;
    
    @Override
    public String toString() {
        return this.dbSupport.quote(this.schema.getName(), this.name);
    }
}
