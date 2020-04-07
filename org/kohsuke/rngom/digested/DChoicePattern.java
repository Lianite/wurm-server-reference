// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

public class DChoicePattern extends DContainerPattern
{
    public boolean isNullable() {
        for (DPattern p = this.firstChild(); p != null; p = p.next) {
            if (p.isNullable()) {
                return true;
            }
        }
        return false;
    }
    
    public <V> V accept(final DPatternVisitor<V> visitor) {
        return visitor.onChoice(this);
    }
}
