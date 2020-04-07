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

public interface XSVisitor extends XSTermVisitor, XSContentTypeVisitor
{
    void annotation(final XSAnnotation p0);
    
    void attGroupDecl(final XSAttGroupDecl p0);
    
    void attributeDecl(final XSAttributeDecl p0);
    
    void attributeUse(final XSAttributeUse p0);
    
    void complexType(final XSComplexType p0);
    
    void schema(final XSSchema p0);
    
    void facet(final XSFacet p0);
    
    void notation(final XSNotation p0);
}
