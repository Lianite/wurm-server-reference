// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

import com.sun.xml.xsom.visitor.XSContentTypeVisitor;
import com.sun.xml.xsom.visitor.XSContentTypeFunction;

public interface XSContentType extends XSComponent
{
    XSSimpleType asSimpleType();
    
    XSParticle asParticle();
    
    XSContentType asEmpty();
    
    Object apply(final XSContentTypeFunction p0);
    
    void visit(final XSContentTypeVisitor p0);
}
