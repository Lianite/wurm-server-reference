// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.resolver.jdbc;

import org.flywaydb.core.api.FlywayException;
import java.sql.Connection;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.flywaydb.core.api.resolver.MigrationExecutor;

public class JdbcMigrationExecutor implements MigrationExecutor
{
    private final JdbcMigration jdbcMigration;
    
    public JdbcMigrationExecutor(final JdbcMigration jdbcMigration) {
        this.jdbcMigration = jdbcMigration;
    }
    
    @Override
    public void execute(final Connection connection) {
        try {
            this.jdbcMigration.migrate(connection);
        }
        catch (Exception e) {
            throw new FlywayException("Migration failed !", e);
        }
    }
    
    @Override
    public boolean executeInTransaction() {
        return true;
    }
}
