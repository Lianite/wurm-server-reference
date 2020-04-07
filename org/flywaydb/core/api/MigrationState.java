// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.api;

public enum MigrationState
{
    PENDING("Pending", true, false, false), 
    ABOVE_TARGET(">Target", true, false, false), 
    BELOW_BASELINE("<Baseln", true, false, false), 
    BASELINE("Baselin", true, true, false), 
    IGNORED("Ignored", true, false, false), 
    MISSING_SUCCESS("Missing", false, true, false), 
    MISSING_FAILED("MisFail", false, true, true), 
    SUCCESS("Success", true, true, false), 
    FAILED("Failed", true, true, true), 
    OUT_OF_ORDER("OutOrdr", true, true, false), 
    FUTURE_SUCCESS("Future", false, true, false), 
    FUTURE_FAILED("FutFail", false, true, true), 
    OUTDATED("Outdate", true, true, false), 
    SUPERSEEDED("Superse", true, true, false);
    
    private final String displayName;
    private final boolean resolved;
    private final boolean applied;
    private final boolean failed;
    
    private MigrationState(final String displayName, final boolean resolved, final boolean applied, final boolean failed) {
        this.displayName = displayName;
        this.resolved = resolved;
        this.applied = applied;
        this.failed = failed;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public boolean isApplied() {
        return this.applied;
    }
    
    public boolean isResolved() {
        return this.resolved;
    }
    
    public boolean isFailed() {
        return this.failed;
    }
}
