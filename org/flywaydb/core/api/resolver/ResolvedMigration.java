// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.api.resolver;

import org.flywaydb.core.api.MigrationType;
import org.flywaydb.core.api.MigrationVersion;

public interface ResolvedMigration
{
    MigrationVersion getVersion();
    
    String getDescription();
    
    String getScript();
    
    Integer getChecksum();
    
    MigrationType getType();
    
    String getPhysicalLocation();
    
    MigrationExecutor getExecutor();
}
