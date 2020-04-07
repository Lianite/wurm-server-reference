// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.command;

import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.util.TimeFormat;
import org.flywaydb.core.internal.util.StopWatch;
import java.sql.SQLException;
import org.flywaydb.core.internal.util.jdbc.TransactionCallback;
import org.flywaydb.core.internal.util.jdbc.TransactionTemplate;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.api.callback.FlywayCallback;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.metadatatable.MetaDataTable;
import java.sql.Connection;
import org.flywaydb.core.internal.util.logging.Log;

public class DbClean
{
    private static final Log LOG;
    private final Connection connection;
    private final MetaDataTable metaDataTable;
    private final Schema[] schemas;
    private final FlywayCallback[] callbacks;
    private boolean cleanDisabled;
    private final DbSupport dbSupport;
    
    public DbClean(final Connection connection, final DbSupport dbSupport, final MetaDataTable metaDataTable, final Schema[] schemas, final FlywayCallback[] callbacks, final boolean cleanDisabled) {
        this.connection = connection;
        this.dbSupport = dbSupport;
        this.metaDataTable = metaDataTable;
        this.schemas = schemas;
        this.callbacks = callbacks;
        this.cleanDisabled = cleanDisabled;
    }
    
    public void clean() throws FlywayException {
        if (this.cleanDisabled) {
            throw new FlywayException("Unable to execute clean as it has been disabled with the \"flyway.cleanDisabled\" property.");
        }
        try {
            for (final FlywayCallback callback : this.callbacks) {
                new TransactionTemplate(this.connection).execute((TransactionCallback<Object>)new TransactionCallback<Object>() {
                    @Override
                    public Object doInTransaction() throws SQLException {
                        DbClean.this.dbSupport.changeCurrentSchemaTo(DbClean.this.schemas[0]);
                        callback.beforeClean(DbClean.this.connection);
                        return null;
                    }
                });
            }
            this.dbSupport.changeCurrentSchemaTo(this.schemas[0]);
            boolean dropSchemas = false;
            try {
                dropSchemas = this.metaDataTable.hasSchemasMarker();
            }
            catch (Exception e) {
                DbClean.LOG.error("Error while checking whether the schemas should be dropped", e);
            }
            for (final Schema schema : this.schemas) {
                if (!schema.exists()) {
                    DbClean.LOG.warn("Unable to clean unknown schema: " + schema);
                }
                else if (dropSchemas) {
                    this.dropSchema(schema);
                }
                else {
                    this.cleanSchema(schema);
                }
            }
            for (final FlywayCallback callback2 : this.callbacks) {
                new TransactionTemplate(this.connection).execute((TransactionCallback<Object>)new TransactionCallback<Object>() {
                    @Override
                    public Object doInTransaction() throws SQLException {
                        DbClean.this.dbSupport.changeCurrentSchemaTo(DbClean.this.schemas[0]);
                        callback2.afterClean(DbClean.this.connection);
                        return null;
                    }
                });
            }
        }
        finally {
            this.dbSupport.restoreCurrentSchema();
        }
    }
    
    private void dropSchema(final Schema schema) {
        DbClean.LOG.debug("Dropping schema " + schema + " ...");
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        new TransactionTemplate(this.connection).execute((TransactionCallback<Object>)new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction() {
                schema.drop();
                return null;
            }
        });
        stopWatch.stop();
        DbClean.LOG.info(String.format("Successfully dropped schema %s (execution time %s)", schema, TimeFormat.format(stopWatch.getTotalTimeMillis())));
    }
    
    private void cleanSchema(final Schema schema) {
        DbClean.LOG.debug("Cleaning schema " + schema + " ...");
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        new TransactionTemplate(this.connection).execute((TransactionCallback<Object>)new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction() {
                schema.clean();
                return null;
            }
        });
        stopWatch.stop();
        DbClean.LOG.info(String.format("Successfully cleaned schema %s (execution time %s)", schema, TimeFormat.format(stopWatch.getTotalTimeMillis())));
    }
    
    static {
        LOG = LogFactory.getLog(DbClean.class);
    }
}
