// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.info;

import java.util.Date;
import org.flywaydb.core.internal.util.ObjectUtils;
import org.flywaydb.core.api.MigrationState;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.MigrationType;
import org.flywaydb.core.internal.metadatatable.AppliedMigration;
import org.flywaydb.core.api.resolver.ResolvedMigration;
import org.flywaydb.core.api.MigrationInfo;

public class MigrationInfoImpl implements MigrationInfo
{
    private final ResolvedMigration resolvedMigration;
    private final AppliedMigration appliedMigration;
    private final MigrationInfoContext context;
    private final boolean outOfOrder;
    
    public MigrationInfoImpl(final ResolvedMigration resolvedMigration, final AppliedMigration appliedMigration, final MigrationInfoContext context, final boolean outOfOrder) {
        this.resolvedMigration = resolvedMigration;
        this.appliedMigration = appliedMigration;
        this.context = context;
        this.outOfOrder = outOfOrder;
    }
    
    public ResolvedMigration getResolvedMigration() {
        return this.resolvedMigration;
    }
    
    public AppliedMigration getAppliedMigration() {
        return this.appliedMigration;
    }
    
    @Override
    public MigrationType getType() {
        if (this.appliedMigration != null) {
            return this.appliedMigration.getType();
        }
        return this.resolvedMigration.getType();
    }
    
    @Override
    public Integer getChecksum() {
        if (this.appliedMigration != null) {
            return this.appliedMigration.getChecksum();
        }
        return this.resolvedMigration.getChecksum();
    }
    
    @Override
    public MigrationVersion getVersion() {
        if (this.appliedMigration != null) {
            return this.appliedMigration.getVersion();
        }
        return this.resolvedMigration.getVersion();
    }
    
    @Override
    public String getDescription() {
        if (this.appliedMigration != null) {
            return this.appliedMigration.getDescription();
        }
        return this.resolvedMigration.getDescription();
    }
    
    @Override
    public String getScript() {
        if (this.appliedMigration != null) {
            return this.appliedMigration.getScript();
        }
        return this.resolvedMigration.getScript();
    }
    
    @Override
    public MigrationState getState() {
        if (this.appliedMigration == null) {
            if (this.resolvedMigration.getVersion() != null) {
                if (this.resolvedMigration.getVersion().compareTo(this.context.baseline) < 0) {
                    return MigrationState.BELOW_BASELINE;
                }
                if (this.resolvedMigration.getVersion().compareTo(this.context.target) > 0) {
                    return MigrationState.ABOVE_TARGET;
                }
                if (this.resolvedMigration.getVersion().compareTo(this.context.lastApplied) < 0 && !this.context.outOfOrder) {
                    return MigrationState.IGNORED;
                }
            }
            return MigrationState.PENDING;
        }
        if (this.resolvedMigration == null) {
            if (MigrationType.SCHEMA == this.appliedMigration.getType()) {
                return MigrationState.SUCCESS;
            }
            if (MigrationType.BASELINE == this.appliedMigration.getType()) {
                return MigrationState.BASELINE;
            }
            if (this.appliedMigration.getVersion() == null || this.getVersion().compareTo(this.context.lastResolved) < 0) {
                if (this.appliedMigration.isSuccess()) {
                    return MigrationState.MISSING_SUCCESS;
                }
                return MigrationState.MISSING_FAILED;
            }
            else {
                if (this.appliedMigration.isSuccess()) {
                    return MigrationState.FUTURE_SUCCESS;
                }
                return MigrationState.FUTURE_FAILED;
            }
        }
        else {
            if (!this.appliedMigration.isSuccess()) {
                return MigrationState.FAILED;
            }
            if (this.appliedMigration.getVersion() == null) {
                if (ObjectUtils.nullSafeEquals(this.appliedMigration.getChecksum(), this.resolvedMigration.getChecksum())) {
                    return MigrationState.SUCCESS;
                }
                if (this.appliedMigration.getInstalledRank() == this.context.latestRepeatableRuns.get(this.appliedMigration.getDescription())) {
                    return MigrationState.OUTDATED;
                }
                return MigrationState.SUPERSEEDED;
            }
            else {
                if (this.outOfOrder) {
                    return MigrationState.OUT_OF_ORDER;
                }
                return MigrationState.SUCCESS;
            }
        }
    }
    
    @Override
    public Date getInstalledOn() {
        if (this.appliedMigration != null) {
            return this.appliedMigration.getInstalledOn();
        }
        return null;
    }
    
    @Override
    public String getInstalledBy() {
        if (this.appliedMigration != null) {
            return this.appliedMigration.getInstalledBy();
        }
        return null;
    }
    
    @Override
    public Integer getInstalledRank() {
        if (this.appliedMigration != null) {
            return this.appliedMigration.getInstalledRank();
        }
        return null;
    }
    
    @Override
    public Integer getExecutionTime() {
        if (this.appliedMigration != null) {
            return this.appliedMigration.getExecutionTime();
        }
        return null;
    }
    
    public String validate() {
        if (this.resolvedMigration == null && this.appliedMigration.getType() != MigrationType.SCHEMA && this.appliedMigration.getType() != MigrationType.BASELINE && this.appliedMigration.getVersion() != null && (!this.context.future || (MigrationState.FUTURE_SUCCESS != this.getState() && MigrationState.FUTURE_FAILED != this.getState()))) {
            return "Detected applied migration not resolved locally: " + this.getVersion();
        }
        if (!this.context.pending) {
            if (MigrationState.PENDING == this.getState() || MigrationState.IGNORED == this.getState()) {
                if (this.getVersion() != null) {
                    return "Detected resolved migration not applied to database: " + this.getVersion();
                }
                return "Detected resolved repeatable migration not applied to database: " + this.getDescription();
            }
            else if (MigrationState.OUTDATED == this.getState()) {
                return "Detected outdated resolved repeatable migration that should be re-applied to database: " + this.getDescription();
            }
        }
        if (this.resolvedMigration != null && this.appliedMigration != null) {
            Object migrationIdentifier = this.appliedMigration.getVersion();
            if (migrationIdentifier == null) {
                migrationIdentifier = this.appliedMigration.getScript();
            }
            if (this.getVersion() == null || this.getVersion().compareTo(this.context.baseline) > 0) {
                if (this.resolvedMigration.getType() != this.appliedMigration.getType()) {
                    return this.createMismatchMessage("type", migrationIdentifier, this.appliedMigration.getType(), this.resolvedMigration.getType());
                }
                if ((this.resolvedMigration.getVersion() != null || (this.context.pending && MigrationState.OUTDATED != this.getState() && MigrationState.SUPERSEEDED != this.getState())) && !ObjectUtils.nullSafeEquals(this.resolvedMigration.getChecksum(), this.appliedMigration.getChecksum())) {
                    return this.createMismatchMessage("checksum", migrationIdentifier, this.appliedMigration.getChecksum(), this.resolvedMigration.getChecksum());
                }
                if (!this.resolvedMigration.getDescription().equals(this.appliedMigration.getDescription())) {
                    return this.createMismatchMessage("description", migrationIdentifier, this.appliedMigration.getDescription(), this.resolvedMigration.getDescription());
                }
            }
        }
        return null;
    }
    
    private String createMismatchMessage(final String mismatch, final Object migrationIdentifier, final Object applied, final Object resolved) {
        return String.format("Migration " + mismatch + " mismatch for migration %s\n" + "-> Applied to database : %s\n" + "-> Resolved locally    : %s", migrationIdentifier, applied, resolved);
    }
    
    @Override
    public int compareTo(final MigrationInfo o) {
        if (this.getInstalledRank() != null && o.getInstalledRank() != null) {
            return this.getInstalledRank() - o.getInstalledRank();
        }
        final MigrationState state = this.getState();
        final MigrationState oState = o.getState();
        if ((this.getInstalledRank() != null || o.getInstalledRank() != null) && state != MigrationState.BELOW_BASELINE && oState != MigrationState.BELOW_BASELINE && state != MigrationState.IGNORED && oState != MigrationState.IGNORED) {
            if (this.getInstalledRank() != null) {
                return Integer.MIN_VALUE;
            }
            if (o.getInstalledRank() != null) {
                return Integer.MAX_VALUE;
            }
        }
        if (this.getVersion() != null && o.getVersion() != null) {
            return this.getVersion().compareTo(o.getVersion());
        }
        if (this.getVersion() != null) {
            return Integer.MIN_VALUE;
        }
        if (o.getVersion() != null) {
            return Integer.MAX_VALUE;
        }
        return this.getDescription().compareTo(o.getDescription());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final MigrationInfoImpl that = (MigrationInfoImpl)o;
        Label_0062: {
            if (this.appliedMigration != null) {
                if (this.appliedMigration.equals(that.appliedMigration)) {
                    break Label_0062;
                }
            }
            else if (that.appliedMigration == null) {
                break Label_0062;
            }
            return false;
        }
        if (!this.context.equals(that.context)) {
            return false;
        }
        if (this.resolvedMigration != null) {
            if (!this.resolvedMigration.equals(that.resolvedMigration)) {
                return false;
            }
        }
        else if (that.resolvedMigration != null) {
            return false;
        }
        return true;
        b = false;
        return b;
    }
    
    @Override
    public int hashCode() {
        int result = (this.resolvedMigration != null) ? this.resolvedMigration.hashCode() : 0;
        result = 31 * result + ((this.appliedMigration != null) ? this.appliedMigration.hashCode() : 0);
        result = 31 * result + this.context.hashCode();
        return result;
    }
}
