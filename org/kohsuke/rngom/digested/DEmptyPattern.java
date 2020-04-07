// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

public class DEmptyPattern extends DPattern
{
    public boolean isNullable() {
        return true;
    }
    
    public Object accept(final DPatternVisitor visitor) {
        return visitor.onEmpty(this);
    }
}
