// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.command;

import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.metadatatable.AppliedMigration;
import org.flywaydb.core.api.resolver.MigrationExecutor;
import org.flywaydb.core.internal.util.TimeFormat;
import org.flywaydb.core.internal.info.MigrationInfoImpl;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationState;
import org.flywaydb.core.internal.info.MigrationInfoServiceImpl;
import org.flywaydb.core.internal.util.StopWatch;
import java.sql.SQLException;
import org.flywaydb.core.internal.util.jdbc.TransactionCallback;
import org.flywaydb.core.internal.util.jdbc.TransactionTemplate;
import org.flywaydb.core.internal.dbsupport.DbSupportFactory;
import org.flywaydb.core.api.callback.FlywayCallback;
import java.sql.Connection;
import org.flywaydb.core.api.resolver.MigrationResolver;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.metadatatable.MetaDataTable;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.internal.util.logging.Log;

public class DbMigrate
{
    private static final Log LOG;
    private final MigrationVersion target;
    private final DbSupport dbSupport;
    private final MetaDataTable metaDataTable;
    private final Schema schema;
    private final MigrationResolver migrationResolver;
    private final Connection connectionMetaDataTable;
    private final Connection connectionUserObjects;
    private final boolean ignoreFutureMigrations;
    private final boolean ignoreFailedFutureMigration;
    private final boolean outOfOrder;
    private final FlywayCallback[] callbacks;
    private final DbSupport dbSupportUserObjects;
    
    public DbMigrate(final Connection connectionMetaDataTable, final Connection connectionUserObjects, final DbSupport dbSupport, final MetaDataTable metaDataTable, final Schema schema, final MigrationResolver migrationResolver, final MigrationVersion target, final boolean ignoreFutureMigrations, final boolean ignoreFailedFutureMigration, final boolean outOfOrder, final FlywayCallback[] callbacks) {
        this.connectionMetaDataTable = connectionMetaDataTable;
        this.connectionUserObjects = connectionUserObjects;
        this.dbSupport = dbSupport;
        this.metaDataTable = metaDataTable;
        this.schema = schema;
        this.migrationResolver = migrationResolver;
        this.target = target;
        this.ignoreFutureMigrations = ignoreFutureMigrations;
        this.ignoreFailedFutureMigration = ignoreFailedFutureMigration;
        this.outOfOrder = outOfOrder;
        this.callbacks = callbacks;
        this.dbSupportUserObjects = DbSupportFactory.createDbSupport(connectionUserObjects, false);
    }
    
    public int migrate() throws FlywayException {
        try {
            for (final FlywayCallback callback : this.callbacks) {
                new TransactionTemplate(this.connectionUserObjects).execute((TransactionCallback<Object>)new TransactionCallback<Object>() {
                    @Override
                    public Object doInTransaction() throws SQLException {
                        DbMigrate.this.dbSupportUserObjects.changeCurrentSchemaTo(DbMigrate.this.schema);
                        callback.beforeMigrate(DbMigrate.this.connectionUserObjects);
                        return null;
                    }
                });
            }
            final StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            int migrationSuccessCount = 0;
            while (true) {
                final boolean firstRun = migrationSuccessCount == 0;
                final boolean done = new TransactionTemplate(this.connectionMetaDataTable, false).execute((TransactionCallback<Boolean>)new TransactionCallback<Boolean>() {
                    @Override
                    public Boolean doInTransaction() {
                        DbMigrate.this.metaDataTable.lock();
                        final MigrationInfoServiceImpl infoService = new MigrationInfoServiceImpl(DbMigrate.this.migrationResolver, DbMigrate.this.metaDataTable, DbMigrate.this.target, DbMigrate.this.outOfOrder, true, true);
                        infoService.refresh();
                        MigrationVersion currentSchemaVersion = MigrationVersion.EMPTY;
                        if (infoService.current() != null) {
                            currentSchemaVersion = infoService.current().getVersion();
                        }
                        if (firstRun) {
                            DbMigrate.LOG.info("Current version of schema " + DbMigrate.this.schema + ": " + currentSchemaVersion);
                            if (DbMigrate.this.outOfOrder) {
                                DbMigrate.LOG.warn("outOfOrder mode is active. Migration of schema " + DbMigrate.this.schema + " may not be reproducible.");
                            }
                        }
                        final MigrationInfo[] future = infoService.future();
                        if (future.length > 0) {
                            final MigrationInfo[] resolved = infoService.resolved();
                            if (resolved.length == 0) {
                                DbMigrate.LOG.warn("Schema " + DbMigrate.this.schema + " has version " + currentSchemaVersion + ", but no migration could be resolved in the configured locations !");
                            }
                            else {
                                int offset;
                                for (offset = resolved.length - 1; resolved[offset].getVersion() == null; --offset) {}
                                DbMigrate.LOG.warn("Schema " + DbMigrate.this.schema + " has a version (" + currentSchemaVersion + ") that is newer than the latest available migration (" + resolved[offset].getVersion() + ") !");
                            }
                        }
                        final MigrationInfo[] failed = infoService.failed();
                        if (failed.length > 0) {
                            if (failed.length != 1 || failed[0].getState() != MigrationState.FUTURE_FAILED || (!DbMigrate.this.ignoreFutureMigrations && !DbMigrate.this.ignoreFailedFutureMigration)) {
                                throw new FlywayException("Schema " + DbMigrate.this.schema + " contains a failed migration to version " + failed[0].getVersion() + " !");
                            }
                            DbMigrate.LOG.warn("Schema " + DbMigrate.this.schema + " contains a failed future migration to version " + failed[0].getVersion() + " !");
                        }
                        final MigrationInfoImpl[] pendingMigrations = infoService.pending();
                        if (pendingMigrations.length == 0) {
                            return true;
                        }
                        final boolean isOutOfOrder = pendingMigrations[0].getVersion() != null && pendingMigrations[0].getVersion().compareTo(currentSchemaVersion) < 0;
                        return DbMigrate.this.applyMigration(pendingMigrations[0], isOutOfOrder);
                    }
                });
                if (done) {
                    break;
                }
                ++migrationSuccessCount;
            }
            stopWatch.stop();
            this.logSummary(migrationSuccessCount, stopWatch.getTotalTimeMillis());
            for (final FlywayCallback callback2 : this.callbacks) {
                new TransactionTemplate(this.connectionUserObjects).execute((TransactionCallback<Object>)new TransactionCallback<Object>() {
                    @Override
                    public Object doInTransaction() throws SQLException {
                        DbMigrate.this.dbSupportUserObjects.changeCurrentSchemaTo(DbMigrate.this.schema);
                        callback2.afterMigrate(DbMigrate.this.connectionUserObjects);
                        return null;
                    }
                });
            }
            return migrationSuccessCount;
        }
        finally {
            this.dbSupportUserObjects.restoreCurrentSchema();
        }
    }
    
    private void logSummary(final int migrationSuccessCount, final long executionTime) {
        if (migrationSuccessCount == 0) {
            DbMigrate.LOG.info("Schema " + this.schema + " is up to date. No migration necessary.");
            return;
        }
        if (migrationSuccessCount == 1) {
            DbMigrate.LOG.info("Successfully applied 1 migration to schema " + this.schema + " (execution time " + TimeFormat.format(executionTime) + ").");
        }
        else {
            DbMigrate.LOG.info("Successfully applied " + migrationSuccessCount + " migrations to schema " + this.schema + " (execution time " + TimeFormat.format(executionTime) + ").");
        }
    }
    
    private Boolean applyMigration(final MigrationInfoImpl migration, final boolean isOutOfOrder) {
        final MigrationVersion version = migration.getVersion();
        String migrationText;
        if (version != null) {
            migrationText = "schema " + this.schema + " to version " + version + " - " + migration.getDescription() + (isOutOfOrder ? " (out of order)" : "");
        }
        else {
            migrationText = "schema " + this.schema + " with repeatable migration " + migration.getDescription();
        }
        DbMigrate.LOG.info("Migrating " + migrationText);
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            final MigrationExecutor migrationExecutor = migration.getResolvedMigration().getExecutor();
            if (migrationExecutor.executeInTransaction()) {
                new TransactionTemplate(this.connectionUserObjects).execute((TransactionCallback<Object>)new TransactionCallback<Object>() {
                    @Override
                    public Object doInTransaction() throws SQLException {
                        DbMigrate.this.doMigrate(migration, migrationExecutor, migrationText);
                        return null;
                    }
                });
            }
            else {
                try {
                    this.doMigrate(migration, migrationExecutor, migrationText);
                }
                catch (SQLException e) {
                    throw new FlywayException("Unable to apply migration", e);
                }
            }
        }
        catch (FlywayException e2) {
            final String failedMsg = "Migration of " + migrationText + " failed!";
            if (this.dbSupport.supportsDdlTransactions()) {
                DbMigrate.LOG.error(failedMsg + " Changes successfully rolled back.");
            }
            else {
                DbMigrate.LOG.error(failedMsg + " Please restore backups and roll back database and code!");
                stopWatch.stop();
                final int executionTime = (int)stopWatch.getTotalTimeMillis();
                final AppliedMigration appliedMigration = new AppliedMigration(version, migration.getDescription(), migration.getType(), migration.getScript(), migration.getResolvedMigration().getChecksum(), executionTime, false);
                this.metaDataTable.addAppliedMigration(appliedMigration);
            }
            throw e2;
        }
        stopWatch.stop();
        final int executionTime2 = (int)stopWatch.getTotalTimeMillis();
        final AppliedMigration appliedMigration2 = new AppliedMigration(version, migration.getDescription(), migration.getType(), migration.getScript(), migration.getResolvedMigration().getChecksum(), executionTime2, true);
        this.metaDataTable.addAppliedMigration(appliedMigration2);
        return false;
    }
    
    private void doMigrate(final MigrationInfoImpl migration, final MigrationExecutor migrationExecutor, final String migrationText) throws SQLException {
        this.dbSupportUserObjects.changeCurrentSchemaTo(this.schema);
        for (final FlywayCallback callback : this.callbacks) {
            callback.beforeEachMigrate(this.connectionUserObjects, migration);
        }
        migrationExecutor.execute(this.connectionUserObjects);
        DbMigrate.LOG.debug("Successfully completed migration of " + migrationText);
        for (final FlywayCallback callback : this.callbacks) {
            callback.afterEachMigrate(this.connectionUserObjects, migration);
        }
    }
    
    static {
        LOG = LogFactory.getLog(DbMigrate.class);
    }
}
