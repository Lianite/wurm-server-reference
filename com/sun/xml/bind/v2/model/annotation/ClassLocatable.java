// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.annotation;

import com.sun.xml.bind.v2.runtime.Location;
import com.sun.xml.bind.v2.model.nav.Navigator;

public class ClassLocatable<C> implements Locatable
{
    private final Locatable upstream;
    private final C clazz;
    private final Navigator<?, C, ?, ?> nav;
    
    public ClassLocatable(final Locatable upstream, final C clazz, final Navigator<?, C, ?, ?> nav) {
        this.upstream = upstream;
        this.clazz = clazz;
        this.nav = nav;
    }
    
    public Locatable getUpstream() {
        return this.upstream;
    }
    
    public Location getLocation() {
        return this.nav.getClassLocation(this.clazz);
    }
}
