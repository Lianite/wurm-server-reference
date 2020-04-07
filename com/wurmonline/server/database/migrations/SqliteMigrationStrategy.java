// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.database.migrations;

import java.util.LinkedHashMap;
import org.flywaydb.core.api.MigrationVersion;
import com.wurmonline.server.database.WurmDatabaseSchema;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
import java.nio.file.Path;
import com.wurmonline.server.database.SqliteConnectionFactory;
import java.util.List;
import java.util.logging.Logger;

public class SqliteMigrationStrategy implements MigrationStrategy
{
    private static final Logger logger;
    private final List<SqliteMigrator> migrators;
    
    public SqliteMigrationStrategy(final List<SqliteConnectionFactory> connectionFactories, final Path migrationsDir) {
        final List<SqliteMigrator> migrators = new ArrayList<SqliteMigrator>();
        for (final SqliteConnectionFactory connectionFactory : connectionFactories) {
            final Path migrationsDirForSchema = migrationsDir.resolve(connectionFactory.getSchema().getMigration());
            migrators.add(new SqliteMigrator(connectionFactory, migrationsDirForSchema));
        }
        this.migrators = migrators;
    }
    
    private void logErrors(final Map<WurmDatabaseSchema, MigrationResult.MigrationSuccess> successfulMigrations) {
        if (successfulMigrations.size() == 0) {
            SqliteMigrationStrategy.logger.warning("Cannot perform migrations, error encounted. No migrations performed successfully.");
        }
        else {
            SqliteMigrationStrategy.logger.warning("Cannot continue migrations, error encountered. Migration in a partial state.");
            SqliteMigrationStrategy.logger.warning("The following migrations were performed successfully:");
            for (final Map.Entry<WurmDatabaseSchema, MigrationResult.MigrationSuccess> entry : successfulMigrations.entrySet()) {
                final MigrationResult.MigrationSuccess result = entry.getValue();
                String before = result.getVersionBefore().getVersion();
                if (before == null) {
                    before = "baseline";
                }
                SqliteMigrationStrategy.logger.warning(entry.getKey().name() + " : performed " + result.getNumMigrations() + " migrations from " + before + " to " + result.getVersionAfter());
            }
        }
    }
    
    @Override
    public boolean hasPendingMigrations() {
        for (final SqliteMigrator migrator : this.migrators) {
            if (migrator.hasPendingMigrations()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public MigrationResult migrate() {
        MigrationVersion earliestVersion = MigrationVersion.LATEST;
        MigrationVersion latestVersion = MigrationVersion.EMPTY;
        int numMigrations = 0;
        final LinkedHashMap<WurmDatabaseSchema, MigrationResult.MigrationSuccess> schemasMigrated = new LinkedHashMap<WurmDatabaseSchema, MigrationResult.MigrationSuccess>();
        for (final SqliteMigrator migrator : this.migrators) {
            final MigrationResult result = migrator.migrate();
            if (result.isError()) {
                this.logErrors(schemasMigrated);
                return result;
            }
            final MigrationResult.MigrationSuccess success = result.asSuccess();
            schemasMigrated.put(migrator.getSchema(), success);
            if (latestVersion.compareTo(success.getVersionAfter()) < 0) {
                latestVersion = success.getVersionAfter();
            }
            final MigrationVersion before = success.getVersionBefore();
            if (before == null) {
                earliestVersion = MigrationVersion.EMPTY;
            }
            else if (before.compareTo(earliestVersion) < 0) {
                earliestVersion = success.getVersionBefore();
            }
            numMigrations += success.getNumMigrations();
        }
        if (latestVersion == null) {
            final String errorMessage = "Error encountered after performing migrations: could not determine latest version";
            SqliteMigrationStrategy.logger.warning(errorMessage);
            return MigrationResult.newError(errorMessage);
        }
        return MigrationResult.newSuccess(earliestVersion, latestVersion, numMigrations);
    }
    
    static {
        logger = Logger.getLogger(SqliteMigrationStrategy.class.getName());
    }
}
