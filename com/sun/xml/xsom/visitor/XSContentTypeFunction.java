// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.visitor;

import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;

public interface XSContentTypeFunction
{
    Object simpleType(final XSSimpleType p0);
    
    Object particle(final XSParticle p0);
    
    Object empty(final XSContentType p0);
}
