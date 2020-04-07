// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.resolver.spring;

import org.flywaydb.core.api.FlywayException;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import java.sql.Connection;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.flywaydb.core.api.resolver.MigrationExecutor;

public class SpringJdbcMigrationExecutor implements MigrationExecutor
{
    private final SpringJdbcMigration springJdbcMigration;
    
    public SpringJdbcMigrationExecutor(final SpringJdbcMigration springJdbcMigration) {
        this.springJdbcMigration = springJdbcMigration;
    }
    
    @Override
    public void execute(final Connection connection) {
        try {
            this.springJdbcMigration.migrate(new JdbcTemplate((DataSource)new SingleConnectionDataSource(connection, true)));
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
