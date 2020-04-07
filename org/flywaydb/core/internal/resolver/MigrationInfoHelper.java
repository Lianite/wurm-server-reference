// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.resolver;

import org.flywaydb.core.internal.util.StringUtils;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.internal.util.Pair;

public class MigrationInfoHelper
{
    public static Pair<MigrationVersion, String> extractVersionAndDescription(final String migrationName, final String prefix, final String separator, final String suffix) {
        final String cleanMigrationName = migrationName.substring(prefix.length(), migrationName.length() - suffix.length());
        final int descriptionPos = cleanMigrationName.indexOf(separator);
        if (descriptionPos < 0) {
            throw new FlywayException("Wrong migration name format: " + migrationName + "(It should look like this: " + prefix + "1_2" + separator + "Description" + suffix + ")");
        }
        final String version = cleanMigrationName.substring(0, descriptionPos);
        final String description = cleanMigrationName.substring(descriptionPos + separator.length()).replaceAll("_", " ");
        if (StringUtils.hasText(version)) {
            return Pair.of(MigrationVersion.fromVersion(version), description);
        }
        return Pair.of((MigrationVersion)null, description);
    }
}
