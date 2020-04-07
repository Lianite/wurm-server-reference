// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.resolver;

import java.util.Set;
import java.util.HashSet;
import org.flywaydb.core.api.FlywayException;
import java.util.Comparator;
import java.util.Collections;
import java.util.Iterator;
import java.util.Arrays;
import org.flywaydb.core.internal.resolver.spring.SpringJdbcMigrationResolver;
import org.flywaydb.core.internal.util.FeatureDetector;
import org.flywaydb.core.internal.resolver.jdbc.JdbcMigrationResolver;
import org.flywaydb.core.internal.resolver.sql.SqlMigrationResolver;
import org.flywaydb.core.internal.util.Location;
import java.util.ArrayList;
import org.flywaydb.core.internal.util.PlaceholderReplacer;
import org.flywaydb.core.internal.util.Locations;
import org.flywaydb.core.api.configuration.FlywayConfiguration;
import org.flywaydb.core.internal.util.scanner.Scanner;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.api.resolver.ResolvedMigration;
import java.util.List;
import java.util.Collection;
import org.flywaydb.core.api.resolver.MigrationResolver;

public class CompositeMigrationResolver implements MigrationResolver
{
    private Collection<MigrationResolver> migrationResolvers;
    private List<ResolvedMigration> availableMigrations;
    
    public CompositeMigrationResolver(final DbSupport dbSupport, final Scanner scanner, final FlywayConfiguration config, final Locations locations, final String encoding, final String sqlMigrationPrefix, final String repeatableSqlMigrationPrefix, final String sqlMigrationSeparator, final String sqlMigrationSuffix, final PlaceholderReplacer placeholderReplacer, final MigrationResolver... customMigrationResolvers) {
        this.migrationResolvers = new ArrayList<MigrationResolver>();
        if (!config.isSkipDefaultResolvers()) {
            for (final Location location : locations.getLocations()) {
                this.migrationResolvers.add(new SqlMigrationResolver(dbSupport, scanner, location, placeholderReplacer, encoding, sqlMigrationPrefix, repeatableSqlMigrationPrefix, sqlMigrationSeparator, sqlMigrationSuffix));
                this.migrationResolvers.add(new JdbcMigrationResolver(scanner, location, config));
                if (new FeatureDetector(scanner.getClassLoader()).isSpringJdbcAvailable()) {
                    this.migrationResolvers.add(new SpringJdbcMigrationResolver(scanner, location, config));
                }
            }
        }
        this.migrationResolvers.addAll(Arrays.asList(customMigrationResolvers));
    }
    
    @Override
    public List<ResolvedMigration> resolveMigrations() {
        if (this.availableMigrations == null) {
            this.availableMigrations = this.doFindAvailableMigrations();
        }
        return this.availableMigrations;
    }
    
    private List<ResolvedMigration> doFindAvailableMigrations() throws FlywayException {
        final List<ResolvedMigration> migrations = new ArrayList<ResolvedMigration>(collectMigrations(this.migrationResolvers));
        Collections.sort(migrations, new ResolvedMigrationComparator());
        checkForIncompatibilities(migrations);
        return migrations;
    }
    
    static Collection<ResolvedMigration> collectMigrations(final Collection<MigrationResolver> migrationResolvers) {
        final Set<ResolvedMigration> migrations = new HashSet<ResolvedMigration>();
        for (final MigrationResolver migrationResolver : migrationResolvers) {
            migrations.addAll(migrationResolver.resolveMigrations());
        }
        return migrations;
    }
    
    static void checkForIncompatibilities(final List<ResolvedMigration> migrations) {
        int i = 0;
        while (i < migrations.size() - 1) {
            final ResolvedMigration current = migrations.get(i);
            final ResolvedMigration next = migrations.get(i + 1);
            if (new ResolvedMigrationComparator().compare(current, next) == 0) {
                if (current.getVersion() != null) {
                    throw new FlywayException(String.format("Found more than one migration with version %s\nOffenders:\n-> %s (%s)\n-> %s (%s)", current.getVersion(), current.getPhysicalLocation(), current.getType(), next.getPhysicalLocation(), next.getType()));
                }
                throw new FlywayException(String.format("Found more than one repeatable migration with description %s\nOffenders:\n-> %s (%s)\n-> %s (%s)", current.getDescription(), current.getPhysicalLocation(), current.getType(), next.getPhysicalLocation(), next.getType()));
            }
            else {
                ++i;
            }
        }
    }
}
