// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

public class DTextPattern extends DPattern
{
    public boolean isNullable() {
        return true;
    }
    
    public Object accept(final DPatternVisitor visitor) {
        return visitor.onText(this);
    }
}
