// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import org.kohsuke.rngom.nc.NameClass;

public class DElementPattern extends DXmlTokenPattern
{
    public DElementPattern(final NameClass name) {
        super(name);
    }
    
    public Object accept(final DPatternVisitor visitor) {
        return visitor.onElement(this);
    }
}
