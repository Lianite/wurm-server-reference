// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

public class DNotAllowedPattern extends DPattern
{
    public boolean isNullable() {
        return false;
    }
    
    public Object accept(final DPatternVisitor visitor) {
        return visitor.onNotAllowed(this);
    }
}
