// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.database.migrations;

import com.wurmonline.server.database.MysqlConnectionFactory;
import com.wurmonline.server.database.WurmDatabaseSchema;

public class MysqlMigrationStrategy implements MigrationStrategy
{
    public static final WurmDatabaseSchema MIGRATION_SCHEMA;
    private final MysqlMigrator migrator;
    
    public MysqlMigrationStrategy(final MysqlConnectionFactory connectionFactory) {
        this.migrator = new MysqlMigrator(MysqlMigrationStrategy.MIGRATION_SCHEMA, connectionFactory);
    }
    
    @Override
    public MigrationResult migrate() {
        return this.migrator.migrate();
    }
    
    @Override
    public boolean hasPendingMigrations() {
        return this.migrator.hasPendingMigrations();
    }
    
    static {
        MIGRATION_SCHEMA = WurmDatabaseSchema.LOGIN;
    }
}
