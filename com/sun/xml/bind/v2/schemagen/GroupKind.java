// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen;

import com.sun.xml.bind.v2.schemagen.xmlschema.Particle;
import com.sun.xml.bind.v2.schemagen.xmlschema.ContentModelContainer;

enum GroupKind
{
    ALL("all"), 
    SEQUENCE("sequence"), 
    CHOICE("choice");
    
    private final String name;
    
    private GroupKind(final String name) {
        this.name = name;
    }
    
    Particle write(final ContentModelContainer parent) {
        return parent._element(this.name, Particle.class);
    }
}
