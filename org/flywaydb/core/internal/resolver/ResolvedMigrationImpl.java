// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.resolver;

import org.flywaydb.core.internal.util.ObjectUtils;
import org.flywaydb.core.api.resolver.MigrationExecutor;
import org.flywaydb.core.api.MigrationType;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.resolver.ResolvedMigration;

public class ResolvedMigrationImpl implements ResolvedMigration
{
    private MigrationVersion version;
    private String description;
    private String script;
    private Integer checksum;
    private MigrationType type;
    private String physicalLocation;
    private MigrationExecutor executor;
    
    @Override
    public MigrationVersion getVersion() {
        return this.version;
    }
    
    public void setVersion(final MigrationVersion version) {
        this.version = version;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    @Override
    public String getScript() {
        return this.script;
    }
    
    public void setScript(final String script) {
        this.script = script;
    }
    
    @Override
    public Integer getChecksum() {
        return this.checksum;
    }
    
    public void setChecksum(final Integer checksum) {
        this.checksum = checksum;
    }
    
    @Override
    public MigrationType getType() {
        return this.type;
    }
    
    public void setType(final MigrationType type) {
        this.type = type;
    }
    
    @Override
    public String getPhysicalLocation() {
        return this.physicalLocation;
    }
    
    public void setPhysicalLocation(final String physicalLocation) {
        this.physicalLocation = physicalLocation;
    }
    
    @Override
    public MigrationExecutor getExecutor() {
        return this.executor;
    }
    
    public void setExecutor(final MigrationExecutor executor) {
        this.executor = executor;
    }
    
    public int compareTo(final ResolvedMigrationImpl o) {
        return this.version.compareTo(o.version);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ResolvedMigrationImpl migration = (ResolvedMigrationImpl)o;
        Label_0062: {
            if (this.checksum != null) {
                if (this.checksum.equals(migration.checksum)) {
                    break Label_0062;
                }
            }
            else if (migration.checksum == null) {
                break Label_0062;
            }
            return false;
        }
        Label_0095: {
            if (this.description != null) {
                if (this.description.equals(migration.description)) {
                    break Label_0095;
                }
            }
            else if (migration.description == null) {
                break Label_0095;
            }
            return false;
        }
        if (this.script != null) {
            if (this.script.equals(migration.script)) {
                return this.type == migration.type && ObjectUtils.nullSafeEquals(this.version, migration.version);
            }
        }
        else if (migration.script == null) {
            return this.type == migration.type && ObjectUtils.nullSafeEquals(this.version, migration.version);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = (this.version != null) ? this.version.hashCode() : 0;
        result = 31 * result + ((this.description != null) ? this.description.hashCode() : 0);
        result = 31 * result + ((this.script != null) ? this.script.hashCode() : 0);
        result = 31 * result + ((this.checksum != null) ? this.checksum.hashCode() : 0);
        result = 31 * result + this.type.hashCode();
        return result;
    }
}
