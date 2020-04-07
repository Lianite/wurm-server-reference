// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

public class DMixedPattern extends DUnaryPattern
{
    public boolean isNullable() {
        return this.getChild().isNullable();
    }
    
    public Object accept(final DPatternVisitor visitor) {
        return visitor.onMixed(this);
    }
}
