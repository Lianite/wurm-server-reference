// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import org.kohsuke.rngom.nc.NameClass;

public abstract class DXmlTokenPattern extends DUnaryPattern
{
    private final NameClass name;
    
    public DXmlTokenPattern(final NameClass name) {
        this.name = name;
    }
    
    public NameClass getName() {
        return this.name;
    }
    
    public final boolean isNullable() {
        return false;
    }
}
