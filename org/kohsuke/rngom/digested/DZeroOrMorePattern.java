// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

public class DZeroOrMorePattern extends DUnaryPattern
{
    public boolean isNullable() {
        return true;
    }
    
    public Object accept(final DPatternVisitor visitor) {
        return visitor.onZeroOrMore(this);
    }
}
