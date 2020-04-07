// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.resolver;

import org.flywaydb.core.api.resolver.ResolvedMigration;
import java.util.Comparator;

public class ResolvedMigrationComparator implements Comparator<ResolvedMigration>
{
    @Override
    public int compare(final ResolvedMigration o1, final ResolvedMigration o2) {
        if (o1.getVersion() != null && o2.getVersion() != null) {
            return o1.getVersion().compareTo(o2.getVersion());
        }
        if (o1.getVersion() != null) {
            return Integer.MIN_VALUE;
        }
        if (o2.getVersion() != null) {
            return Integer.MAX_VALUE;
        }
        return o1.getDescription().compareTo(o2.getDescription());
    }
}
