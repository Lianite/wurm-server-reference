// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.host;

import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.builder.Annotations;

public class Base
{
    private static final AnnotationsHost nullAnnotations;
    private static final LocationHost nullLocation;
    
    protected AnnotationsHost cast(final Annotations ann) {
        if (ann == null) {
            return Base.nullAnnotations;
        }
        return (AnnotationsHost)ann;
    }
    
    protected LocationHost cast(final Location loc) {
        if (loc == null) {
            return Base.nullLocation;
        }
        return (LocationHost)loc;
    }
    
    static {
        nullAnnotations = new AnnotationsHost(null, null);
        nullLocation = new LocationHost(null, null);
    }
}
