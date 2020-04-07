// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import java.util.HashSet;
import com.sun.xml.xsom.XSParticle;
import java.util.Set;
import com.sun.tools.xjc.reader.gbind.Element;

abstract class GElement extends Element
{
    final Set<XSParticle> particles;
    
    GElement() {
        this.particles = new HashSet<XSParticle>();
    }
    
    abstract String getPropertyNameSeed();
}
