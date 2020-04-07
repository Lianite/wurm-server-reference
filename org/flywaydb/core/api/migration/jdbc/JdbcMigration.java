// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.api.migration.jdbc;

import java.sql.Connection;

public interface JdbcMigration
{
    void migrate(final Connection p0) throws Exception;
}
