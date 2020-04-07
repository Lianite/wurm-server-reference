// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.database.migrations;

import org.flywaydb.core.Flyway;
import javax.sql.DataSource;
import java.util.Collections;
import java.nio.file.Path;
import com.wurmonline.server.database.WurmDatabaseSchema;
import com.wurmonline.server.database.SqliteConnectionFactory;

public class SqliteMigrator extends Migrator<SqliteConnectionFactory>
{
    private final WurmDatabaseSchema schema;
    
    public SqliteMigrator(final SqliteConnectionFactory connectionFactory, final Path migrationsDir) {
        super(connectionFactory, Collections.singletonList(migrationsDir), flyway -> flyway.setDataSource(new SqliteFlywayIssue1499Workaround(connectionFactory.getDataSource())));
        this.schema = connectionFactory.getSchema();
    }
    
    public WurmDatabaseSchema getSchema() {
        return this.schema;
    }
}
