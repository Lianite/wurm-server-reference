// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.api.callback;

import org.flywaydb.core.api.MigrationInfo;
import java.sql.Connection;

public interface FlywayCallback
{
    void beforeClean(final Connection p0);
    
    void afterClean(final Connection p0);
    
    void beforeMigrate(final Connection p0);
    
    void afterMigrate(final Connection p0);
    
    void beforeEachMigrate(final Connection p0, final MigrationInfo p1);
    
    void afterEachMigrate(final Connection p0, final MigrationInfo p1);
    
    void beforeValidate(final Connection p0);
    
    void afterValidate(final Connection p0);
    
    void beforeBaseline(final Connection p0);
    
    void afterBaseline(final Connection p0);
    
    void beforeRepair(final Connection p0);
    
    void afterRepair(final Connection p0);
    
    void beforeInfo(final Connection p0);
    
    void afterInfo(final Connection p0);
}
