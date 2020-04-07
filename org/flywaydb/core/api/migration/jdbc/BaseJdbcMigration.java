// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.api.migration.jdbc;

import org.flywaydb.core.api.configuration.FlywayConfiguration;
import org.flywaydb.core.api.configuration.ConfigurationAware;

public abstract class BaseJdbcMigration implements JdbcMigration, ConfigurationAware
{
    protected FlywayConfiguration flywayConfiguration;
    
    @Override
    public void setFlywayConfiguration(final FlywayConfiguration flywayConfiguration) {
        this.flywayConfiguration = flywayConfiguration;
    }
}
