// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.visitor;

import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;

public interface XSContentTypeVisitor
{
    void simpleType(final XSSimpleType p0);
    
    void particle(final XSParticle p0);
    
    void empty(final XSContentType p0);
}
