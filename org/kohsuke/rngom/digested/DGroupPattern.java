// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

public class DGroupPattern extends DContainerPattern
{
    public boolean isNullable() {
        for (DPattern p = this.firstChild(); p != null; p = p.next) {
            if (!p.isNullable()) {
                return false;
            }
        }
        return true;
    }
    
    public <V> V accept(final DPatternVisitor<V> visitor) {
        return visitor.onGroup(this);
    }
}
