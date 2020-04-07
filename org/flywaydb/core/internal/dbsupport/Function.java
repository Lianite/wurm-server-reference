// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport;

public abstract class Function extends SchemaObject
{
    protected String[] args;
    
    public Function(final JdbcTemplate jdbcTemplate, final DbSupport dbSupport, final Schema schema, final String name, final String... args) {
        super(jdbcTemplate, dbSupport, schema, name);
        this.args = args;
    }
}
