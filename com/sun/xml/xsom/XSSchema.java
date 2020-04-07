// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

import java.util.Iterator;

public interface XSSchema extends XSComponent
{
    String getTargetNamespace();
    
    Iterator iterateAttributeDecls();
    
    XSAttributeDecl getAttributeDecl(final String p0);
    
    Iterator iterateElementDecls();
    
    XSElementDecl getElementDecl(final String p0);
    
    Iterator iterateAttGroupDecls();
    
    XSAttGroupDecl getAttGroupDecl(final String p0);
    
    Iterator iterateModelGroupDecls();
    
    XSModelGroupDecl getModelGroupDecl(final String p0);
    
    Iterator iterateTypes();
    
    XSType getType(final String p0);
    
    Iterator iterateSimpleTypes();
    
    XSSimpleType getSimpleType(final String p0);
    
    Iterator iterateComplexTypes();
    
    XSComplexType getComplexType(final String p0);
    
    Iterator iterateNotations();
    
    XSNotation getNotation(final String p0);
}
