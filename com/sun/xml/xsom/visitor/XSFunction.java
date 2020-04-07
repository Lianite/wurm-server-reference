// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.visitor;

import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAnnotation;

public interface XSFunction extends XSContentTypeFunction, XSTermFunction
{
    Object annotation(final XSAnnotation p0);
    
    Object attGroupDecl(final XSAttGroupDecl p0);
    
    Object attributeDecl(final XSAttributeDecl p0);
    
    Object attributeUse(final XSAttributeUse p0);
    
    Object complexType(final XSComplexType p0);
    
    Object schema(final XSSchema p0);
    
    Object facet(final XSFacet p0);
    
    Object notation(final XSNotation p0);
}
