// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.command;

import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.metadatatable.AppliedMigration;
import org.flywaydb.core.api.resolver.ResolvedMigration;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.internal.util.ObjectUtils;
import org.flywaydb.core.internal.info.MigrationInfoImpl;
import org.flywaydb.core.internal.util.TimeFormat;
import org.flywaydb.core.internal.util.StopWatch;
import java.sql.SQLException;
import org.flywaydb.core.internal.util.jdbc.TransactionCallback;
import org.flywaydb.core.internal.util.jdbc.TransactionTemplate;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.resolver.MigrationResolver;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.api.callback.FlywayCallback;
import org.flywaydb.core.internal.metadatatable.MetaDataTable;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.info.MigrationInfoServiceImpl;
import java.sql.Connection;
import org.flywaydb.core.internal.util.logging.Log;

public class DbRepair
{
    private static final Log LOG;
    private final Connection connection;
    private final MigrationInfoServiceImpl migrationInfoService;
    private final Schema schema;
    private final MetaDataTable metaDataTable;
    private final FlywayCallback[] callbacks;
    private final DbSupport dbSupport;
    
    public DbRepair(final DbSupport dbSupport, final Connection connection, final Schema schema, final MigrationResolver migrationResolver, final MetaDataTable metaDataTable, final FlywayCallback[] callbacks) {
        this.dbSupport = dbSupport;
        this.connection = connection;
        this.schema = schema;
        this.migrationInfoService = new MigrationInfoServiceImpl(migrationResolver, metaDataTable, MigrationVersion.LATEST, true, true, true);
        this.metaDataTable = metaDataTable;
        this.callbacks = callbacks;
    }
    
    public void repair() {
        try {
            for (final FlywayCallback callback : this.callbacks) {
                new TransactionTemplate(this.connection).execute((TransactionCallback<Object>)new TransactionCallback<Object>() {
                    @Override
                    public Object doInTransaction() throws SQLException {
                        DbRepair.this.dbSupport.changeCurrentSchemaTo(DbRepair.this.schema);
                        callback.beforeRepair(DbRepair.this.connection);
                        return null;
                    }
                });
            }
            final StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            new TransactionTemplate(this.connection).execute((TransactionCallback<Object>)new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction() {
                    DbRepair.this.dbSupport.changeCurrentSchemaTo(DbRepair.this.schema);
                    DbRepair.this.metaDataTable.removeFailedMigrations();
                    DbRepair.this.repairChecksums();
                    return null;
                }
            });
            stopWatch.stop();
            DbRepair.LOG.info("Successfully repaired metadata table " + this.metaDataTable + " (execution time " + TimeFormat.format(stopWatch.getTotalTimeMillis()) + ").");
            if (!this.dbSupport.supportsDdlTransactions()) {
                DbRepair.LOG.info("Manual cleanup of the remaining effects the failed migration may still be required.");
            }
            for (final FlywayCallback callback2 : this.callbacks) {
                new TransactionTemplate(this.connection).execute((TransactionCallback<Object>)new TransactionCallback<Object>() {
                    @Override
                    public Object doInTransaction() throws SQLException {
                        DbRepair.this.dbSupport.changeCurrentSchemaTo(DbRepair.this.schema);
                        callback2.afterRepair(DbRepair.this.connection);
                        return null;
                    }
                });
            }
        }
        finally {
            this.dbSupport.restoreCurrentSchema();
        }
    }
    
    public void repairChecksums() {
        this.migrationInfoService.refresh();
        for (final MigrationInfo migrationInfo : this.migrationInfoService.all()) {
            final MigrationInfoImpl migrationInfoImpl = (MigrationInfoImpl)migrationInfo;
            final ResolvedMigration resolved = migrationInfoImpl.getResolvedMigration();
            final AppliedMigration applied = migrationInfoImpl.getAppliedMigration();
            if (resolved != null && applied != null && !ObjectUtils.nullSafeEquals(resolved.getChecksum(), applied.getChecksum()) && resolved.getVersion() != null) {
                this.metaDataTable.updateChecksum(migrationInfoImpl.getVersion(), resolved.getChecksum());
            }
        }
    }
    
    static {
        LOG = LogFactory.getLog(DbRepair.class);
    }
}
