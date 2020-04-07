// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.api.resolver;

import java.util.Collection;

public interface MigrationResolver
{
    Collection<ResolvedMigration> resolveMigrations();
}
