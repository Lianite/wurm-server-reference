// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.resolver.sql;

import java.util.Collection;
import java.io.IOException;
import org.flywaydb.core.api.FlywayException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.zip.CRC32;
import org.flywaydb.core.internal.callback.SqlScriptFlywayCallback;
import org.flywaydb.core.internal.util.Pair;
import org.flywaydb.core.internal.util.scanner.Resource;
import org.flywaydb.core.api.resolver.MigrationExecutor;
import org.flywaydb.core.api.MigrationType;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.internal.resolver.ResolvedMigrationImpl;
import org.flywaydb.core.internal.resolver.MigrationInfoHelper;
import java.util.Comparator;
import java.util.Collections;
import org.flywaydb.core.internal.resolver.ResolvedMigrationComparator;
import java.util.ArrayList;
import org.flywaydb.core.api.resolver.ResolvedMigration;
import java.util.List;
import org.flywaydb.core.internal.util.PlaceholderReplacer;
import org.flywaydb.core.internal.util.Location;
import org.flywaydb.core.internal.util.scanner.Scanner;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.api.resolver.MigrationResolver;

public class SqlMigrationResolver implements MigrationResolver
{
    private final DbSupport dbSupport;
    private final Scanner scanner;
    private final Location location;
    private final PlaceholderReplacer placeholderReplacer;
    private final String encoding;
    private final String sqlMigrationPrefix;
    private final String repeatableSqlMigrationPrefix;
    private final String sqlMigrationSeparator;
    private final String sqlMigrationSuffix;
    
    public SqlMigrationResolver(final DbSupport dbSupport, final Scanner scanner, final Location location, final PlaceholderReplacer placeholderReplacer, final String encoding, final String sqlMigrationPrefix, final String repeatableSqlMigrationPrefix, final String sqlMigrationSeparator, final String sqlMigrationSuffix) {
        this.dbSupport = dbSupport;
        this.scanner = scanner;
        this.location = location;
        this.placeholderReplacer = placeholderReplacer;
        this.encoding = encoding;
        this.sqlMigrationPrefix = sqlMigrationPrefix;
        this.repeatableSqlMigrationPrefix = repeatableSqlMigrationPrefix;
        this.sqlMigrationSeparator = sqlMigrationSeparator;
        this.sqlMigrationSuffix = sqlMigrationSuffix;
    }
    
    @Override
    public List<ResolvedMigration> resolveMigrations() {
        final List<ResolvedMigration> migrations = new ArrayList<ResolvedMigration>();
        this.scanForMigrations(migrations, this.sqlMigrationPrefix, this.sqlMigrationSeparator, this.sqlMigrationSuffix);
        this.scanForMigrations(migrations, this.repeatableSqlMigrationPrefix, this.sqlMigrationSeparator, this.sqlMigrationSuffix);
        Collections.sort(migrations, new ResolvedMigrationComparator());
        return migrations;
    }
    
    public void scanForMigrations(final List<ResolvedMigration> migrations, final String prefix, final String separator, final String suffix) {
        for (final Resource resource : this.scanner.scanForResources(this.location, prefix, suffix)) {
            final String filename = resource.getFilename();
            if (!isSqlCallback(filename, suffix)) {
                final Pair<MigrationVersion, String> info = MigrationInfoHelper.extractVersionAndDescription(filename, prefix, separator, suffix);
                final ResolvedMigrationImpl migration = new ResolvedMigrationImpl();
                migration.setVersion(info.getLeft());
                migration.setDescription(info.getRight());
                migration.setScript(this.extractScriptName(resource));
                migration.setChecksum(calculateChecksum(resource, resource.loadAsString(this.encoding)));
                migration.setType(MigrationType.SQL);
                migration.setPhysicalLocation(resource.getLocationOnDisk());
                migration.setExecutor(new SqlMigrationExecutor(this.dbSupport, resource, this.placeholderReplacer, this.encoding));
                migrations.add(migration);
            }
        }
    }
    
    static boolean isSqlCallback(final String filename, final String suffix) {
        final String baseName = filename.substring(0, filename.length() - suffix.length());
        return SqlScriptFlywayCallback.ALL_CALLBACKS.contains(baseName);
    }
    
    String extractScriptName(final Resource resource) {
        if (this.location.getPath().isEmpty()) {
            return resource.getLocation();
        }
        return resource.getLocation().substring(this.location.getPath().length() + 1);
    }
    
    static int calculateChecksum(final Resource resource, final String str) {
        final CRC32 crc32 = new CRC32();
        final BufferedReader bufferedReader = new BufferedReader(new StringReader(str));
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                crc32.update(line.getBytes("UTF-8"));
            }
        }
        catch (IOException e) {
            String message = "Unable to calculate checksum";
            if (resource != null) {
                message = message + " for " + resource.getLocation() + " (" + resource.getLocationOnDisk() + ")";
            }
            throw new FlywayException(message, e);
        }
        return (int)crc32.getValue();
    }
}
