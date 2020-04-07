// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.api.configuration;

import java.util.Map;
import org.flywaydb.core.api.callback.FlywayCallback;
import org.flywaydb.core.api.resolver.MigrationResolver;
import org.flywaydb.core.api.MigrationVersion;
import javax.sql.DataSource;

public interface FlywayConfiguration
{
    ClassLoader getClassLoader();
    
    DataSource getDataSource();
    
    MigrationVersion getBaselineVersion();
    
    String getBaselineDescription();
    
    MigrationResolver[] getResolvers();
    
    boolean isSkipDefaultResolvers();
    
    FlywayCallback[] getCallbacks();
    
    boolean isSkipDefaultCallbacks();
    
    String getSqlMigrationSuffix();
    
    String getRepeatableSqlMigrationPrefix();
    
    String getSqlMigrationSeparator();
    
    String getSqlMigrationPrefix();
    
    boolean isPlaceholderReplacement();
    
    String getPlaceholderSuffix();
    
    String getPlaceholderPrefix();
    
    Map<String, String> getPlaceholders();
    
    MigrationVersion getTarget();
    
    String getTable();
    
    String[] getSchemas();
    
    String getEncoding();
    
    String[] getLocations();
}
