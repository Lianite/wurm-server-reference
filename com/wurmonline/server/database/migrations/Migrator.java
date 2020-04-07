// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.database.migrations;

import javax.annotation.Nonnull;
import org.flywaydb.core.api.MigrationInfo;
import java.io.File;
import java.util.Iterator;
import java.util.Optional;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.MigrationInfoService;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.nio.file.Path;
import java.util.List;
import org.flywaydb.core.Flyway;
import java.util.logging.Logger;
import com.wurmonline.server.database.ConnectionFactory;

public abstract class Migrator<B extends ConnectionFactory>
{
    private static final Logger logger;
    private static final String FILESYSTEM_PREFIX = "filesystem:";
    private static final String VERSION_TABLE = "SCHEMA_VERSION";
    private static final String MIGRATION_PREFIX = "v";
    private static final String REPEATABLE_MIGRATION_PREFIX = "r";
    private static final String BASELINE_VERSION = "1";
    private final Flyway flyway;
    private final List<Path> sqlDirectories;
    
    Migrator(final B connectionFactory, final List<Path> sqlDirectories, final FlywayConfigurer configurer) {
        this(new Flyway(), connectionFactory, sqlDirectories, configurer);
    }
    
    Migrator(final Flyway flyway, final B connectionFactory, final List<Path> sqlDirectories, final FlywayConfigurer configurer) {
        this.flyway = flyway;
        this.sqlDirectories = Collections.unmodifiableList((List<? extends Path>)new ArrayList<Path>(sqlDirectories));
        flyway.setLocations(asFlywayLocations(sqlDirectories));
        flyway.setTable("SCHEMA_VERSION");
        flyway.setSqlMigrationPrefix("v");
        flyway.setRepeatableSqlMigrationPrefix("r");
        configurer.configureMigrations(flyway);
    }
    
    public boolean isCurrent() {
        return this.flyway.info().pending().length == 0;
    }
    
    private MigrationInfoService baseline() {
        Migrator.logger.info("No database migrations metadata found, creating baseline at version 1");
        this.flyway.setBaselineVersion(MigrationVersion.fromVersion("1"));
        this.flyway.baseline();
        return this.flyway.info();
    }
    
    private Optional<String> ensureDirsExist() {
        for (final Path path : this.sqlDirectories) {
            final File dir = path.toFile();
            if (!dir.exists() && !dir.mkdirs()) {
                final String errorMessage = "Could not find or create migrations directory at " + dir.getAbsolutePath();
                Migrator.logger.warning(errorMessage);
                Optional.of(errorMessage);
            }
        }
        return Optional.empty();
    }
    
    public boolean hasPendingMigrations() {
        final MigrationInfoService migrationInfoService = this.flyway.info();
        final MigrationInfo currentInfo = migrationInfoService.current();
        return currentInfo == null || migrationInfoService.pending().length > 0;
    }
    
    @Nonnull
    public MigrationResult migrate() {
        final Optional<String> optionalError = this.ensureDirsExist();
        if (optionalError.isPresent()) {
            return MigrationResult.newError(optionalError.get());
        }
        MigrationInfoService migrationInfoService = this.flyway.info();
        MigrationInfo currentInfo = migrationInfoService.current();
        MigrationVersion beforeVersion;
        if (currentInfo == null) {
            beforeVersion = MigrationVersion.EMPTY;
            migrationInfoService = this.baseline();
            currentInfo = migrationInfoService.current();
            if (currentInfo == null) {
                final String errorMessage = "No database versioning information found after creating baseline";
                Migrator.logger.warning(errorMessage);
                return MigrationResult.newError(errorMessage);
            }
            final int numMigrationsPending = migrationInfoService.pending().length;
            if (numMigrationsPending == 0) {
                Migrator.logger.info("Database baselined to version 1. No migrations pending.");
                return MigrationResult.newSuccess(beforeVersion, currentInfo.getVersion(), 0);
            }
        }
        else {
            beforeVersion = currentInfo.getVersion();
            if (this.isCurrent()) {
                Migrator.logger.info("No pending migrations, database is current");
                return MigrationResult.newSuccess(beforeVersion, beforeVersion, 0);
            }
        }
        Migrator.logger.info("Found " + migrationInfoService.pending().length + " pending database migrations, initiating now.");
        final int numMigrations = this.flyway.migrate();
        migrationInfoService = this.flyway.info();
        currentInfo = migrationInfoService.current();
        if (numMigrations == 0) {
            Migrator.logger.warning("Pending migrations found but none performed.");
        }
        else {
            if (currentInfo == null) {
                final String errorMessage2 = "Performed " + numMigrations + " migrations but no migrations metadata found afterwards.";
                Migrator.logger.warning(errorMessage2);
                return MigrationResult.newError(errorMessage2);
            }
            Migrator.logger.info("Performed " + numMigrations + " database migrations. Current version is " + currentInfo.getVersion());
        }
        return MigrationResult.newSuccess(beforeVersion, currentInfo.getVersion(), numMigrations);
    }
    
    protected Flyway getFlyway() {
        return this.flyway;
    }
    
    public static String asFlywayLocation(final Path dir) {
        return "filesystem:" + dir.toString();
    }
    
    public static String asFlywayLocations(final List<Path> paths) {
        final StringBuilder builder = new StringBuilder();
        if (paths.size() == 0) {
            return "";
        }
        builder.append("filesystem:");
        builder.append(paths.get(0).toString());
        for (final Path path : paths.subList(1, paths.size())) {
            builder.append(',');
            builder.append("filesystem:");
            builder.append(path.toString());
        }
        return builder.toString();
    }
    
    static {
        logger = Logger.getLogger(Migrator.class.getName());
    }
    
    interface FlywayConfigurer
    {
        void configureMigrations(final Flyway p0);
    }
}
