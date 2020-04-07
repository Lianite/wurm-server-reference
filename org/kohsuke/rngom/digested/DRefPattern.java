// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

public class DRefPattern extends DPattern
{
    private final DDefine target;
    
    public DRefPattern(final DDefine target) {
        this.target = target;
    }
    
    public boolean isNullable() {
        return this.target.isNullable();
    }
    
    public DDefine getTarget() {
        return this.target;
    }
    
    public String getName() {
        return this.target.getName();
    }
    
    public Object accept(final DPatternVisitor visitor) {
        return visitor.onRef(this);
    }
}
