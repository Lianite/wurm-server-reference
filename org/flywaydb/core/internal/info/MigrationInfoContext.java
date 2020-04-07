// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.info;

import java.util.HashMap;
import java.util.Map;
import org.flywaydb.core.api.MigrationVersion;

public class MigrationInfoContext
{
    public boolean outOfOrder;
    public boolean pending;
    public boolean future;
    public MigrationVersion target;
    public MigrationVersion schema;
    public MigrationVersion baseline;
    public MigrationVersion lastResolved;
    public MigrationVersion lastApplied;
    public Map<String, Integer> latestRepeatableRuns;
    
    public MigrationInfoContext() {
        this.lastResolved = MigrationVersion.EMPTY;
        this.lastApplied = MigrationVersion.EMPTY;
        this.latestRepeatableRuns = new HashMap<String, Integer>();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final MigrationInfoContext that = (MigrationInfoContext)o;
        if (this.outOfOrder != that.outOfOrder) {
            return false;
        }
        if (this.pending != that.pending) {
            return false;
        }
        if (this.future != that.future) {
            return false;
        }
        Label_0101: {
            if (this.target != null) {
                if (this.target.equals(that.target)) {
                    break Label_0101;
                }
            }
            else if (that.target == null) {
                break Label_0101;
            }
            return false;
        }
        Label_0134: {
            if (this.schema != null) {
                if (this.schema.equals(that.schema)) {
                    break Label_0134;
                }
            }
            else if (that.schema == null) {
                break Label_0134;
            }
            return false;
        }
        Label_0167: {
            if (this.baseline != null) {
                if (this.baseline.equals(that.baseline)) {
                    break Label_0167;
                }
            }
            else if (that.baseline == null) {
                break Label_0167;
            }
            return false;
        }
        Label_0200: {
            if (this.lastResolved != null) {
                if (this.lastResolved.equals(that.lastResolved)) {
                    break Label_0200;
                }
            }
            else if (that.lastResolved == null) {
                break Label_0200;
            }
            return false;
        }
        if (this.lastApplied != null) {
            if (this.lastApplied.equals(that.lastApplied)) {
                return this.latestRepeatableRuns.equals(that.latestRepeatableRuns);
            }
        }
        else if (that.lastApplied == null) {
            return this.latestRepeatableRuns.equals(that.latestRepeatableRuns);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = this.outOfOrder ? 1 : 0;
        result = 31 * result + (this.pending ? 1 : 0);
        result = 31 * result + (this.future ? 1 : 0);
        result = 31 * result + ((this.target != null) ? this.target.hashCode() : 0);
        result = 31 * result + ((this.schema != null) ? this.schema.hashCode() : 0);
        result = 31 * result + ((this.baseline != null) ? this.baseline.hashCode() : 0);
        result = 31 * result + ((this.lastResolved != null) ? this.lastResolved.hashCode() : 0);
        result = 31 * result + ((this.lastApplied != null) ? this.lastApplied.hashCode() : 0);
        result = 31 * result + this.latestRepeatableRuns.hashCode();
        return result;
    }
}
