// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.database;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.sql.Connection;
import javax.annotation.Nonnull;

public abstract class ConnectionFactory
{
    private final String url;
    private final WurmDatabaseSchema schema;
    
    ConnectionFactory(@Nonnull final String url, @Nonnull final WurmDatabaseSchema schema) {
        this.schema = schema;
        this.url = url;
    }
    
    public final String getUrl() {
        return this.url;
    }
    
    public abstract Connection createConnection() throws SQLException;
    
    public abstract boolean isValid(@Nullable final Connection p0) throws SQLException;
    
    public abstract boolean isStale(final long p0, @Nullable final Connection p1) throws SQLException;
    
    public WurmDatabaseSchema getSchema() {
        return this.schema;
    }
}
