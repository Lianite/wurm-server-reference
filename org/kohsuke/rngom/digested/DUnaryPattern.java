// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

public abstract class DUnaryPattern extends DPattern
{
    private DPattern child;
    
    public DPattern getChild() {
        return this.child;
    }
    
    public void setChild(final DPattern child) {
        this.child = child;
    }
}
