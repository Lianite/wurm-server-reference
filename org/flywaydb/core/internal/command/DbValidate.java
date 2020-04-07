// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.command;

import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.util.TimeFormat;
import org.flywaydb.core.internal.info.MigrationInfoServiceImpl;
import org.flywaydb.core.internal.util.Pair;
import org.flywaydb.core.internal.util.StopWatch;
import java.sql.SQLException;
import org.flywaydb.core.internal.util.jdbc.TransactionCallback;
import org.flywaydb.core.internal.util.jdbc.TransactionTemplate;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.api.callback.FlywayCallback;
import java.sql.Connection;
import org.flywaydb.core.api.resolver.MigrationResolver;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.metadatatable.MetaDataTable;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.internal.util.logging.Log;

public class DbValidate
{
    private static final Log LOG;
    private final MigrationVersion target;
    private final MetaDataTable metaDataTable;
    private final Schema schema;
    private final MigrationResolver migrationResolver;
    private final Connection connection;
    private final boolean outOfOrder;
    private final boolean pending;
    private final boolean future;
    private final FlywayCallback[] callbacks;
    private final DbSupport dbSupport;
    
    public DbValidate(final Connection connection, final DbSupport dbSupport, final MetaDataTable metaDataTable, final Schema schema, final MigrationResolver migrationResolver, final MigrationVersion target, final boolean outOfOrder, final boolean pending, final boolean future, final FlywayCallback[] callbacks) {
        this.connection = connection;
        this.dbSupport = dbSupport;
        this.metaDataTable = metaDataTable;
        this.schema = schema;
        this.migrationResolver = migrationResolver;
        this.target = target;
        this.outOfOrder = outOfOrder;
        this.pending = pending;
        this.future = future;
        this.callbacks = callbacks;
    }
    
    public String validate() {
        try {
            for (final FlywayCallback callback : this.callbacks) {
                new TransactionTemplate(this.connection).execute((TransactionCallback<Object>)new TransactionCallback<Object>() {
                    @Override
                    public Object doInTransaction() throws SQLException {
                        DbValidate.this.dbSupport.changeCurrentSchemaTo(DbValidate.this.schema);
                        callback.beforeValidate(DbValidate.this.connection);
                        return null;
                    }
                });
            }
            DbValidate.LOG.debug("Validating migrations ...");
            final StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            final Pair<Integer, String> result = new TransactionTemplate(this.connection).execute((TransactionCallback<Pair<Integer, String>>)new TransactionCallback<Pair<Integer, String>>() {
                @Override
                public Pair<Integer, String> doInTransaction() {
                    DbValidate.this.dbSupport.changeCurrentSchemaTo(DbValidate.this.schema);
                    final MigrationInfoServiceImpl migrationInfoService = new MigrationInfoServiceImpl(DbValidate.this.migrationResolver, DbValidate.this.metaDataTable, DbValidate.this.target, DbValidate.this.outOfOrder, DbValidate.this.pending, DbValidate.this.future);
                    migrationInfoService.refresh();
                    final int count = migrationInfoService.all().length;
                    final String validationError = migrationInfoService.validate();
                    return Pair.of(count, validationError);
                }
            });
            stopWatch.stop();
            final String error = result.getRight();
            if (error == null) {
                final int count = result.getLeft();
                if (count == 1) {
                    DbValidate.LOG.info(String.format("Successfully validated 1 migration (execution time %s)", TimeFormat.format(stopWatch.getTotalTimeMillis())));
                }
                else {
                    DbValidate.LOG.info(String.format("Successfully validated %d migrations (execution time %s)", count, TimeFormat.format(stopWatch.getTotalTimeMillis())));
                }
            }
            for (final FlywayCallback callback2 : this.callbacks) {
                new TransactionTemplate(this.connection).execute((TransactionCallback<Object>)new TransactionCallback<Object>() {
                    @Override
                    public Object doInTransaction() throws SQLException {
                        DbValidate.this.dbSupport.changeCurrentSchemaTo(DbValidate.this.schema);
                        callback2.afterValidate(DbValidate.this.connection);
                        return null;
                    }
                });
            }
            return error;
        }
        finally {
            this.dbSupport.restoreCurrentSchema();
        }
    }
    
    static {
        LOG = LogFactory.getLog(DbValidate.class);
    }
}
