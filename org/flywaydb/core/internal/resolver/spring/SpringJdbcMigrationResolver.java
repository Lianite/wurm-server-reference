// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.resolver.spring;

import org.flywaydb.core.internal.util.Pair;
import org.flywaydb.core.api.MigrationType;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.internal.resolver.MigrationInfoHelper;
import org.flywaydb.core.internal.util.StringUtils;
import org.flywaydb.core.api.migration.MigrationInfoProvider;
import org.flywaydb.core.api.migration.MigrationChecksumProvider;
import org.flywaydb.core.internal.resolver.ResolvedMigrationImpl;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;
import org.flywaydb.core.internal.resolver.ResolvedMigrationComparator;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.resolver.MigrationExecutor;
import org.flywaydb.core.internal.util.ConfigurationInjectionUtils;
import org.flywaydb.core.internal.util.ClassUtils;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import java.util.ArrayList;
import org.flywaydb.core.api.resolver.ResolvedMigration;
import java.util.Collection;
import org.flywaydb.core.api.configuration.FlywayConfiguration;
import org.flywaydb.core.internal.util.scanner.Scanner;
import org.flywaydb.core.internal.util.Location;
import org.flywaydb.core.api.resolver.MigrationResolver;

public class SpringJdbcMigrationResolver implements MigrationResolver
{
    private final Location location;
    private Scanner scanner;
    private FlywayConfiguration configuration;
    
    public SpringJdbcMigrationResolver(final Scanner scanner, final Location location, final FlywayConfiguration configuration) {
        this.location = location;
        this.scanner = scanner;
        this.configuration = configuration;
    }
    
    @Override
    public Collection<ResolvedMigration> resolveMigrations() {
        final List<ResolvedMigration> migrations = new ArrayList<ResolvedMigration>();
        if (!this.location.isClassPath()) {
            return migrations;
        }
        try {
            final Class<?>[] scanForClasses;
            final Class<?>[] classes = scanForClasses = this.scanner.scanForClasses(this.location, SpringJdbcMigration.class);
            for (final Class<?> clazz : scanForClasses) {
                final SpringJdbcMigration springJdbcMigration = ClassUtils.instantiate(clazz.getName(), this.scanner.getClassLoader());
                ConfigurationInjectionUtils.injectFlywayConfiguration(springJdbcMigration, this.configuration);
                final ResolvedMigrationImpl migrationInfo = this.extractMigrationInfo(springJdbcMigration);
                migrationInfo.setPhysicalLocation(ClassUtils.getLocationOnDisk(clazz));
                migrationInfo.setExecutor(new SpringJdbcMigrationExecutor(springJdbcMigration));
                migrations.add(migrationInfo);
            }
        }
        catch (Exception e) {
            throw new FlywayException("Unable to resolve Spring Jdbc Java migrations in location: " + this.location, e);
        }
        Collections.sort(migrations, new ResolvedMigrationComparator());
        return migrations;
    }
    
    ResolvedMigrationImpl extractMigrationInfo(final SpringJdbcMigration springJdbcMigration) {
        Integer checksum = null;
        if (springJdbcMigration instanceof MigrationChecksumProvider) {
            final MigrationChecksumProvider checksumProvider = (MigrationChecksumProvider)springJdbcMigration;
            checksum = checksumProvider.getChecksum();
        }
        MigrationVersion version;
        String description;
        if (springJdbcMigration instanceof MigrationInfoProvider) {
            final MigrationInfoProvider infoProvider = (MigrationInfoProvider)springJdbcMigration;
            version = infoProvider.getVersion();
            description = infoProvider.getDescription();
            if (!StringUtils.hasText(description)) {
                throw new FlywayException("Missing description for migration " + version);
            }
        }
        else {
            final String shortName = ClassUtils.getShortName(springJdbcMigration.getClass());
            if (!shortName.startsWith("V") && !shortName.startsWith("R")) {
                throw new FlywayException("Invalid Jdbc migration class name: " + springJdbcMigration.getClass().getName() + " => ensure it starts with V or R," + " or implement org.flywaydb.core.api.migration.MigrationInfoProvider for non-default naming");
            }
            final String prefix = shortName.substring(0, 1);
            final Pair<MigrationVersion, String> info = MigrationInfoHelper.extractVersionAndDescription(shortName, prefix, "__", "");
            version = info.getLeft();
            description = info.getRight();
        }
        final ResolvedMigrationImpl resolvedMigration = new ResolvedMigrationImpl();
        resolvedMigration.setVersion(version);
        resolvedMigration.setDescription(description);
        resolvedMigration.setScript(springJdbcMigration.getClass().getName());
        resolvedMigration.setChecksum(checksum);
        resolvedMigration.setType(MigrationType.SPRING_JDBC);
        return resolvedMigration;
    }
}
