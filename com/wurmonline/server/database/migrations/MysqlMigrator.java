// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.database.migrations;

import java.io.File;
import org.flywaydb.core.Flyway;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import com.wurmonline.server.database.WurmDatabaseSchema;
import java.nio.file.Path;
import com.wurmonline.server.database.MysqlConnectionFactory;

public class MysqlMigrator extends Migrator<MysqlConnectionFactory>
{
    private static final Path DEFAULT_MIGRATIONS_DIR;
    
    public MysqlMigrator(final WurmDatabaseSchema migrationSchema, final MysqlConnectionFactory connectionFactory) {
        final WurmDatabaseSchema[] schemas;
        final ArrayList<String> migrationSchemas;
        final WurmDatabaseSchema[] array;
        int length;
        int i;
        WurmDatabaseSchema schema;
        super(connectionFactory, Collections.singletonList(MysqlMigrator.DEFAULT_MIGRATIONS_DIR), flyway -> {
            flyway.setDataSource(connectionFactory.getUrl(), connectionFactory.getUser(), connectionFactory.getPassword(), new String[0]);
            schemas = WurmDatabaseSchema.values();
            migrationSchemas = new ArrayList<String>(schemas.length);
            for (length = array.length; i < length; ++i) {
                schema = array[i];
                if (!schema.equals(migrationSchema)) {
                    migrationSchemas.add(schema.getDatabase());
                }
            }
            migrationSchemas.add(0, migrationSchema.getDatabase());
            flyway.setSchemas((String[])migrationSchemas.toArray(new String[migrationSchemas.size()]));
        });
    }
    
    @Nonnull
    @Override
    public MigrationResult migrate() {
        return super.migrate();
    }
    
    static {
        DEFAULT_MIGRATIONS_DIR = new File("migrations").toPath();
    }
}
