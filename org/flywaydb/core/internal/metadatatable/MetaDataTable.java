// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.metadatatable;

import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.api.MigrationVersion;
import java.util.List;

public interface MetaDataTable
{
    void lock();
    
    void addAppliedMigration(final AppliedMigration p0);
    
    boolean hasAppliedMigrations();
    
    List<AppliedMigration> allAppliedMigrations();
    
    void addBaselineMarker(final MigrationVersion p0, final String p1);
    
    boolean hasBaselineMarker();
    
    AppliedMigration getBaselineMarker();
    
    void removeFailedMigrations();
    
    void addSchemasMarker(final Schema[] p0);
    
    boolean hasSchemasMarker();
    
    void updateChecksum(final MigrationVersion p0, final Integer p1);
    
    boolean upgradeIfNecessary();
}
