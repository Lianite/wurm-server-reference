// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.command;

import org.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.SQLException;
import org.flywaydb.core.internal.util.jdbc.TransactionCallback;
import org.flywaydb.core.internal.util.jdbc.TransactionTemplate;
import org.flywaydb.core.internal.metadatatable.MetaDataTable;
import org.flywaydb.core.internal.dbsupport.Schema;
import java.sql.Connection;
import org.flywaydb.core.internal.util.logging.Log;

public class DbSchemas
{
    private static final Log LOG;
    private final Connection connection;
    private final Schema[] schemas;
    private final MetaDataTable metaDataTable;
    
    public DbSchemas(final Connection connection, final Schema[] schemas, final MetaDataTable metaDataTable) {
        this.connection = connection;
        this.schemas = schemas;
        this.metaDataTable = metaDataTable;
    }
    
    public void create() {
        new TransactionTemplate(this.connection).execute((TransactionCallback<Object>)new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction() {
                for (final Schema schema : DbSchemas.this.schemas) {
                    if (schema.exists()) {
                        DbSchemas.LOG.debug("Schema " + schema + " already exists. Skipping schema creation.");
                        return null;
                    }
                }
                for (final Schema schema : DbSchemas.this.schemas) {
                    DbSchemas.LOG.info("Creating schema " + schema + " ...");
                    schema.create();
                }
                DbSchemas.this.metaDataTable.addSchemasMarker(DbSchemas.this.schemas);
                return null;
            }
        });
    }
    
    static {
        LOG = LogFactory.getLog(DbSchemas.class);
    }
}
