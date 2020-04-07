// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.metadatatable;

import org.flywaydb.core.internal.util.ObjectUtils;
import java.util.Date;
import org.flywaydb.core.api.MigrationType;
import org.flywaydb.core.api.MigrationVersion;

public class AppliedMigration implements Comparable<AppliedMigration>
{
    private int installedRank;
    private MigrationVersion version;
    private String description;
    private MigrationType type;
    private String script;
    private Integer checksum;
    private Date installedOn;
    private String installedBy;
    private int executionTime;
    private boolean success;
    
    public AppliedMigration(final int installedRank, final MigrationVersion version, final String description, final MigrationType type, final String script, final Integer checksum, final Date installedOn, final String installedBy, final int executionTime, final boolean success) {
        this.installedRank = installedRank;
        this.version = version;
        this.description = description;
        this.type = type;
        this.script = script;
        this.checksum = checksum;
        this.installedOn = installedOn;
        this.installedBy = installedBy;
        this.executionTime = executionTime;
        this.success = success;
    }
    
    public AppliedMigration(final MigrationVersion version, final String description, final MigrationType type, final String script, final Integer checksum, final int executionTime, final boolean success) {
        this.version = version;
        this.description = this.abbreviateDescription(description);
        this.type = type;
        this.script = this.abbreviateScript(script);
        this.checksum = checksum;
        this.executionTime = executionTime;
        this.success = success;
    }
    
    private String abbreviateDescription(final String description) {
        if (description == null) {
            return null;
        }
        if (description.length() <= 200) {
            return description;
        }
        return description.substring(0, 197) + "...";
    }
    
    private String abbreviateScript(final String script) {
        if (script == null) {
            return null;
        }
        if (script.length() <= 1000) {
            return script;
        }
        return "..." + script.substring(3, 1000);
    }
    
    public int getInstalledRank() {
        return this.installedRank;
    }
    
    public MigrationVersion getVersion() {
        return this.version;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public MigrationType getType() {
        return this.type;
    }
    
    public String getScript() {
        return this.script;
    }
    
    public Integer getChecksum() {
        return this.checksum;
    }
    
    public Date getInstalledOn() {
        return this.installedOn;
    }
    
    public String getInstalledBy() {
        return this.installedBy;
    }
    
    public int getExecutionTime() {
        return this.executionTime;
    }
    
    public boolean isSuccess() {
        return this.success;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final AppliedMigration that = (AppliedMigration)o;
        if (this.executionTime != that.executionTime) {
            return false;
        }
        if (this.installedRank != that.installedRank) {
            return false;
        }
        if (this.success != that.success) {
            return false;
        }
        Label_0101: {
            if (this.checksum != null) {
                if (this.checksum.equals(that.checksum)) {
                    break Label_0101;
                }
            }
            else if (that.checksum == null) {
                break Label_0101;
            }
            return false;
        }
        if (!this.description.equals(that.description)) {
            return false;
        }
        Label_0150: {
            if (this.installedBy != null) {
                if (this.installedBy.equals(that.installedBy)) {
                    break Label_0150;
                }
            }
            else if (that.installedBy == null) {
                break Label_0150;
            }
            return false;
        }
        if (this.installedOn != null) {
            if (this.installedOn.equals(that.installedOn)) {
                return this.script.equals(that.script) && this.type == that.type && ObjectUtils.nullSafeEquals(this.version, that.version);
            }
        }
        else if (that.installedOn == null) {
            return this.script.equals(that.script) && this.type == that.type && ObjectUtils.nullSafeEquals(this.version, that.version);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = this.installedRank;
        result = 31 * result + ((this.version != null) ? this.version.hashCode() : 0);
        result = 31 * result + this.description.hashCode();
        result = 31 * result + this.type.hashCode();
        result = 31 * result + this.script.hashCode();
        result = 31 * result + ((this.checksum != null) ? this.checksum.hashCode() : 0);
        result = 31 * result + ((this.installedOn != null) ? this.installedOn.hashCode() : 0);
        result = 31 * result + ((this.installedBy != null) ? this.installedBy.hashCode() : 0);
        result = 31 * result + this.executionTime;
        result = 31 * result + (this.success ? 1 : 0);
        return result;
    }
    
    @Override
    public int compareTo(final AppliedMigration o) {
        return this.installedRank - o.installedRank;
    }
}
