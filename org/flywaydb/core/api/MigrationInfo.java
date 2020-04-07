// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.api;

import java.util.Date;

public interface MigrationInfo extends Comparable<MigrationInfo>
{
    MigrationType getType();
    
    Integer getChecksum();
    
    MigrationVersion getVersion();
    
    String getDescription();
    
    String getScript();
    
    MigrationState getState();
    
    Date getInstalledOn();
    
    String getInstalledBy();
    
    Integer getInstalledRank();
    
    Integer getExecutionTime();
}
