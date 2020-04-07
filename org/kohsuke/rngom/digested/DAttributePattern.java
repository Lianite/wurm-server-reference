// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import org.kohsuke.rngom.nc.NameClass;

public class DAttributePattern extends DXmlTokenPattern
{
    public DAttributePattern(final NameClass name) {
        super(name);
    }
    
    public Object accept(final DPatternVisitor visitor) {
        return visitor.onAttribute(this);
    }
}
