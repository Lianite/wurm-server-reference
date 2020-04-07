// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.api.callback;

import org.flywaydb.core.api.MigrationInfo;
import java.sql.Connection;
import org.flywaydb.core.api.configuration.FlywayConfiguration;
import org.flywaydb.core.api.configuration.ConfigurationAware;

public abstract class BaseFlywayCallback implements FlywayCallback, ConfigurationAware
{
    protected FlywayConfiguration flywayConfiguration;
    
    @Override
    public void setFlywayConfiguration(final FlywayConfiguration flywayConfiguration) {
        this.flywayConfiguration = flywayConfiguration;
    }
    
    @Override
    public void beforeClean(final Connection connection) {
    }
    
    @Override
    public void afterClean(final Connection connection) {
    }
    
    @Override
    public void beforeMigrate(final Connection connection) {
    }
    
    @Override
    public void afterMigrate(final Connection connection) {
    }
    
    @Override
    public void beforeEachMigrate(final Connection connection, final MigrationInfo info) {
    }
    
    @Override
    public void afterEachMigrate(final Connection connection, final MigrationInfo info) {
    }
    
    @Override
    public void beforeValidate(final Connection connection) {
    }
    
    @Override
    public void afterValidate(final Connection connection) {
    }
    
    @Override
    public void beforeBaseline(final Connection connection) {
    }
    
    @Override
    public void afterBaseline(final Connection connection) {
    }
    
    @Override
    public void beforeRepair(final Connection connection) {
    }
    
    @Override
    public void afterRepair(final Connection connection) {
    }
    
    @Override
    public void beforeInfo(final Connection connection) {
    }
    
    @Override
    public void afterInfo(final Connection connection) {
    }
}
