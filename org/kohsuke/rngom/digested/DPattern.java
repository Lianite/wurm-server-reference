// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import org.kohsuke.rngom.parse.Parseable;
import org.xml.sax.Locator;
import org.kohsuke.rngom.ast.om.ParsedPattern;

public abstract class DPattern implements ParsedPattern
{
    Locator location;
    DAnnotation annotation;
    DPattern next;
    DPattern prev;
    
    public Locator getLocation() {
        return this.location;
    }
    
    public DAnnotation getAnnotation() {
        if (this.annotation == null) {
            return DAnnotation.EMPTY;
        }
        return this.annotation;
    }
    
    public abstract boolean isNullable();
    
    public abstract <V> V accept(final DPatternVisitor<V> p0);
    
    public Parseable createParseable() {
        return new PatternParseable(this);
    }
    
    public final boolean isElement() {
        return this instanceof DElementPattern;
    }
    
    public final boolean isAttribute() {
        return this instanceof DAttributePattern;
    }
}
