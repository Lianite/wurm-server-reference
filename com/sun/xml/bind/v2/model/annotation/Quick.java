// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.annotation;

import com.sun.xml.bind.v2.runtime.Location;
import java.lang.annotation.Annotation;

public abstract class Quick implements Annotation, Locatable, Location
{
    private final Locatable upstream;
    
    protected Quick(final Locatable upstream) {
        this.upstream = upstream;
    }
    
    protected abstract Annotation getAnnotation();
    
    protected abstract Quick newInstance(final Locatable p0, final Annotation p1);
    
    public final Location getLocation() {
        return this;
    }
    
    public final Locatable getUpstream() {
        return this.upstream;
    }
    
    public final String toString() {
        return this.getAnnotation().toString();
    }
}
