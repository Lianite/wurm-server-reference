// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

import com.sun.xml.xsom.visitor.XSSimpleTypeFunction;
import com.sun.xml.xsom.visitor.XSSimpleTypeVisitor;

public interface XSSimpleType extends XSType, XSContentType
{
    XSSimpleType getSimpleBaseType();
    
    XSVariety getVariety();
    
    XSFacet getFacet(final String p0);
    
    void visit(final XSSimpleTypeVisitor p0);
    
    Object apply(final XSSimpleTypeFunction p0);
    
    boolean isRestriction();
    
    boolean isList();
    
    boolean isUnion();
    
    XSRestrictionSimpleType asRestriction();
    
    XSListSimpleType asList();
    
    XSUnionSimpleType asUnion();
}
