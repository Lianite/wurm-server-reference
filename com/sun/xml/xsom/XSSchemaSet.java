// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

import java.util.Iterator;

public interface XSSchemaSet
{
    XSSchema getSchema(final String p0);
    
    XSSchema getSchema(final int p0);
    
    int getSchemaSize();
    
    Iterator iterateSchema();
    
    XSSimpleType getSimpleType(final String p0, final String p1);
    
    XSAttributeDecl getAttributeDecl(final String p0, final String p1);
    
    XSElementDecl getElementDecl(final String p0, final String p1);
    
    XSModelGroupDecl getModelGroupDecl(final String p0, final String p1);
    
    XSAttGroupDecl getAttGroupDecl(final String p0, final String p1);
    
    XSComplexType getComplexType(final String p0, final String p1);
    
    Iterator iterateElementDecls();
    
    Iterator iterateTypes();
    
    Iterator iterateAttributeDecls();
    
    Iterator iterateAttGroupDecls();
    
    Iterator iterateModelGroupDecls();
    
    Iterator iterateSimpleTypes();
    
    Iterator iterateComplexTypes();
    
    Iterator iterateNotations();
    
    XSComplexType getAnyType();
    
    XSSimpleType getAnySimpleType();
    
    XSContentType getEmpty();
}
