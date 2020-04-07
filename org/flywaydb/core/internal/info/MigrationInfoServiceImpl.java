// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.info;

import org.flywaydb.core.api.MigrationState;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.util.Collections;
import org.flywaydb.core.internal.util.ObjectUtils;
import java.util.HashSet;
import org.flywaydb.core.api.MigrationType;
import java.util.ArrayList;
import org.flywaydb.core.internal.util.Pair;
import java.util.TreeMap;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.internal.metadatatable.AppliedMigration;
import org.flywaydb.core.api.resolver.ResolvedMigration;
import java.util.Collection;
import java.util.List;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.internal.metadatatable.MetaDataTable;
import org.flywaydb.core.api.resolver.MigrationResolver;
import org.flywaydb.core.api.MigrationInfoService;

public class MigrationInfoServiceImpl implements MigrationInfoService
{
    private final MigrationResolver migrationResolver;
    private final MetaDataTable metaDataTable;
    private MigrationVersion target;
    private boolean outOfOrder;
    private final boolean pending;
    private final boolean future;
    private List<MigrationInfoImpl> migrationInfos;
    
    public MigrationInfoServiceImpl(final MigrationResolver migrationResolver, final MetaDataTable metaDataTable, final MigrationVersion target, final boolean outOfOrder, final boolean pending, final boolean future) {
        this.migrationResolver = migrationResolver;
        this.metaDataTable = metaDataTable;
        this.target = target;
        this.outOfOrder = outOfOrder;
        this.pending = pending;
        this.future = future;
    }
    
    public void refresh() {
        final Collection<ResolvedMigration> availableMigrations = this.migrationResolver.resolveMigrations();
        final List<AppliedMigration> appliedMigrations = this.metaDataTable.allAppliedMigrations();
        this.migrationInfos = this.mergeAvailableAndAppliedMigrations(availableMigrations, appliedMigrations);
        if (MigrationVersion.CURRENT == this.target) {
            final MigrationInfo current = this.current();
            if (current == null) {
                this.target = MigrationVersion.EMPTY;
            }
            else {
                this.target = current.getVersion();
            }
        }
    }
    
    private List<MigrationInfoImpl> mergeAvailableAndAppliedMigrations(final Collection<ResolvedMigration> resolvedMigrations, final List<AppliedMigration> appliedMigrations) {
        final MigrationInfoContext context = new MigrationInfoContext();
        context.outOfOrder = this.outOfOrder;
        context.pending = this.pending;
        context.future = this.future;
        context.target = this.target;
        final Map<MigrationVersion, ResolvedMigration> resolvedMigrationsMap = new TreeMap<MigrationVersion, ResolvedMigration>();
        final Map<String, ResolvedMigration> resolvedRepeatableMigrationsMap = new TreeMap<String, ResolvedMigration>();
        for (final ResolvedMigration resolvedMigration : resolvedMigrations) {
            final MigrationVersion version = resolvedMigration.getVersion();
            if (version != null) {
                if (version.compareTo(context.lastResolved) > 0) {
                    context.lastResolved = version;
                }
                resolvedMigrationsMap.put(version, resolvedMigration);
            }
            else {
                resolvedRepeatableMigrationsMap.put(resolvedMigration.getDescription(), resolvedMigration);
            }
        }
        final Map<MigrationVersion, Pair<AppliedMigration, Boolean>> appliedMigrationsMap = new TreeMap<MigrationVersion, Pair<AppliedMigration, Boolean>>();
        final List<AppliedMigration> appliedRepeatableMigrations = new ArrayList<AppliedMigration>();
        for (final AppliedMigration appliedMigration : appliedMigrations) {
            final MigrationVersion version2 = appliedMigration.getVersion();
            boolean outOfOrder = false;
            if (version2 != null) {
                if (version2.compareTo(context.lastApplied) > 0) {
                    context.lastApplied = version2;
                }
                else {
                    outOfOrder = true;
                }
            }
            if (appliedMigration.getType() == MigrationType.SCHEMA) {
                context.schema = version2;
            }
            if (appliedMigration.getType() == MigrationType.BASELINE) {
                context.baseline = version2;
            }
            if (version2 != null) {
                appliedMigrationsMap.put(version2, Pair.of(appliedMigration, outOfOrder));
            }
            else {
                appliedRepeatableMigrations.add(appliedMigration);
            }
        }
        final Set<MigrationVersion> allVersions = new HashSet<MigrationVersion>();
        allVersions.addAll(resolvedMigrationsMap.keySet());
        allVersions.addAll(appliedMigrationsMap.keySet());
        final List<MigrationInfoImpl> migrationInfos = new ArrayList<MigrationInfoImpl>();
        for (final MigrationVersion version3 : allVersions) {
            final ResolvedMigration resolvedMigration2 = resolvedMigrationsMap.get(version3);
            final Pair<AppliedMigration, Boolean> appliedMigrationInfo = appliedMigrationsMap.get(version3);
            if (appliedMigrationInfo == null) {
                migrationInfos.add(new MigrationInfoImpl(resolvedMigration2, null, context, false));
            }
            else {
                migrationInfos.add(new MigrationInfoImpl(resolvedMigration2, appliedMigrationInfo.getLeft(), context, appliedMigrationInfo.getRight()));
            }
        }
        final Set<ResolvedMigration> pendingResolvedRepeatableMigrations = new HashSet<ResolvedMigration>(resolvedRepeatableMigrationsMap.values());
        for (final AppliedMigration appliedRepeatableMigration : appliedRepeatableMigrations) {
            final ResolvedMigration resolvedMigration3 = resolvedRepeatableMigrationsMap.get(appliedRepeatableMigration.getDescription());
            if (resolvedMigration3 != null && ObjectUtils.nullSafeEquals(appliedRepeatableMigration.getChecksum(), resolvedMigration3.getChecksum())) {
                pendingResolvedRepeatableMigrations.remove(resolvedMigration3);
            }
            if (!context.latestRepeatableRuns.containsKey(appliedRepeatableMigration.getDescription()) || appliedRepeatableMigration.getInstalledRank() > context.latestRepeatableRuns.get(appliedRepeatableMigration.getDescription())) {
                context.latestRepeatableRuns.put(appliedRepeatableMigration.getDescription(), appliedRepeatableMigration.getInstalledRank());
            }
            migrationInfos.add(new MigrationInfoImpl(resolvedMigration3, appliedRepeatableMigration, context, false));
        }
        for (final ResolvedMigration pendingResolvedRepeatableMigration : pendingResolvedRepeatableMigrations) {
            migrationInfos.add(new MigrationInfoImpl(pendingResolvedRepeatableMigration, null, context, false));
        }
        Collections.sort(migrationInfos);
        return migrationInfos;
    }
    
    @Override
    public MigrationInfo[] all() {
        return this.migrationInfos.toArray(new MigrationInfoImpl[this.migrationInfos.size()]);
    }
    
    @Override
    public MigrationInfo current() {
        for (int i = this.migrationInfos.size() - 1; i >= 0; --i) {
            final MigrationInfo migrationInfo = this.migrationInfos.get(i);
            if (migrationInfo.getState().isApplied() && migrationInfo.getVersion() != null) {
                return migrationInfo;
            }
        }
        return null;
    }
    
    @Override
    public MigrationInfoImpl[] pending() {
        final List<MigrationInfoImpl> pendingMigrations = new ArrayList<MigrationInfoImpl>();
        for (final MigrationInfoImpl migrationInfo : this.migrationInfos) {
            if (MigrationState.PENDING == migrationInfo.getState()) {
                pendingMigrations.add(migrationInfo);
            }
        }
        return pendingMigrations.toArray(new MigrationInfoImpl[pendingMigrations.size()]);
    }
    
    @Override
    public MigrationInfo[] applied() {
        final List<MigrationInfo> appliedMigrations = new ArrayList<MigrationInfo>();
        for (final MigrationInfo migrationInfo : this.migrationInfos) {
            if (migrationInfo.getState().isApplied()) {
                appliedMigrations.add(migrationInfo);
            }
        }
        return appliedMigrations.toArray(new MigrationInfo[appliedMigrations.size()]);
    }
    
    public MigrationInfo[] resolved() {
        final List<MigrationInfo> resolvedMigrations = new ArrayList<MigrationInfo>();
        for (final MigrationInfo migrationInfo : this.migrationInfos) {
            if (migrationInfo.getState().isResolved()) {
                resolvedMigrations.add(migrationInfo);
            }
        }
        return resolvedMigrations.toArray(new MigrationInfo[resolvedMigrations.size()]);
    }
    
    public MigrationInfo[] failed() {
        final List<MigrationInfo> failedMigrations = new ArrayList<MigrationInfo>();
        for (final MigrationInfo migrationInfo : this.migrationInfos) {
            if (migrationInfo.getState().isFailed()) {
                failedMigrations.add(migrationInfo);
            }
        }
        return failedMigrations.toArray(new MigrationInfo[failedMigrations.size()]);
    }
    
    public MigrationInfo[] future() {
        final List<MigrationInfo> futureMigrations = new ArrayList<MigrationInfo>();
        for (final MigrationInfo migrationInfo : this.migrationInfos) {
            if (migrationInfo.getState() == MigrationState.FUTURE_SUCCESS || migrationInfo.getState() == MigrationState.FUTURE_FAILED) {
                futureMigrations.add(migrationInfo);
            }
        }
        return futureMigrations.toArray(new MigrationInfo[futureMigrations.size()]);
    }
    
    public MigrationInfo[] outOfOrder() {
        final List<MigrationInfo> outOfOrderMigrations = new ArrayList<MigrationInfo>();
        for (final MigrationInfo migrationInfo : this.migrationInfos) {
            if (migrationInfo.getState() == MigrationState.OUT_OF_ORDER) {
                outOfOrderMigrations.add(migrationInfo);
            }
        }
        return outOfOrderMigrations.toArray(new MigrationInfo[outOfOrderMigrations.size()]);
    }
    
    public String validate() {
        for (final MigrationInfoImpl migrationInfo : this.migrationInfos) {
            final String message = migrationInfo.validate();
            if (message != null) {
                return message;
            }
        }
        return null;
    }
}
